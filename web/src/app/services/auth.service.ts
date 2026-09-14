import { Injectable, inject, OnDestroy } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap, catchError, of, map, switchMap } from 'rxjs';
import { environment } from '../../environments/environment';
import { User, UserRole } from '../models/user.model';

export interface LoginResponse {
  accessToken: string;
}

export interface BootstrapUserResponse {
  id: string;
  name: string;
  userCode: string;
  role: string;
  status: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService implements OnDestroy {
  private http = inject(HttpClient);
  private router = inject(Router);
  private readonly apiUrl = (environment?.apiUrl ?? '') + '/api/auth';

  private accessToken: string | null = null;
  private loggedInSubject = new BehaviorSubject<boolean>(false);
  private currentUserSubject = new BehaviorSubject<User | null>(null);

  isLoggedIn$ = this.loggedInSubject.asObservable();
  currentUser$ = this.currentUserSubject.asObservable();

  private authInitialized = false;
  private pingIntervalId: any = null;
  private readonly PING_INTERVAL_MS = 10 * 60 * 1000; // 10 minutos

  constructor() {

  }

  ngOnDestroy(): void {
    this.stopPingTimer();
  }

  get currentUser(): User | null {
    return this.currentUserSubject.value;
  }

  login(userCode: string, password: string): Observable<User> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, { userCode, password }).pipe(
      switchMap(response => {
        if (response.accessToken) {
          this.setToken(response.accessToken);
          this.authInitialized = true; // Evita refresh redundante no guard
          this.startPingTimer();
          return this.loadCurrentUser().pipe(
            map(user => {
              if (!user) {
                throw new Error('Falha ao carregar perfil do usuário.');
              }
              return user;
            })
          );
        } else {
          throw new Error('Token de acesso ausente.');
        }
      })
    );
  }

  bootstrapSystem(name: string, password: string, accessKey: string, cpf: string): Observable<BootstrapUserResponse> {
    return this.http.post<BootstrapUserResponse>(`${this.apiUrl}/bootstrap`, { name, password, accessKey, cpf });
  }

  logout(): void {
    this.stopPingTimer();

    // Chama o backend para invalidar a sessão e o cookie no Redis
    this.http.post(`${this.apiUrl}/logout`, {}).pipe(
      catchError(() => of(null)) // Ignora erro de rede no logout
    ).subscribe(() => {
      this.accessToken = null;
      this.loggedInSubject.next(false);
      this.currentUserSubject.next(null);
    });
  }

  getToken(): string | null {
    return this.accessToken;
  }

  isLoggedIn(): boolean {
    return !!this.accessToken;
  }

  hasRole(roles: UserRole[]): boolean {
    const user = this.currentUser;
    if (!user) return false;
    return roles.includes(user.role);
  }

  loadCurrentUser(): Observable<User | null> {
    return this.http.get<User>(`${this.apiUrl}/me`).pipe(
      tap(user => this.currentUserSubject.next(user)),
      catchError(() => {
        this.currentUserSubject.next(null);
        this.accessToken = null;
        this.loggedInSubject.next(false);
        this.stopPingTimer();
        return of(null);
      })
    );
  }

  checkAuthStatus(): Observable<boolean> {
    if (this.authInitialized) {
      return of(this.isLoggedIn());
    }

    return this.silentRefresh().pipe(
      tap(() => this.authInitialized = true)
    );
  }

  silentRefresh(): Observable<boolean> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/refresh`, {}).pipe(
      switchMap(response => {
        if (response.accessToken) {
          this.setToken(response.accessToken);
          this.startPingTimer();
          return this.loadCurrentUser().pipe(
            map(() => true),
            catchError(() => of(true))
          );
        }
        return of(false);
      }),
      catchError(() => {
        this.accessToken = null;
        this.loggedInSubject.next(false);
        this.currentUserSubject.next(null);
        this.stopPingTimer();
        return of(false);
      })
    );
  }

  pingSession(): Observable<boolean> {
    if (!this.isLoggedIn()) {
      this.stopPingTimer();
      return of(false);
    }

    return this.http.get(`${this.apiUrl}/ping`, { observe: 'response' }).pipe(
      map(res => res.status === 204 || res.status === 200),
      catchError((error: unknown) => {
        if (error instanceof HttpErrorResponse) {
          // Sessão revogada / inválida no servidor
          if (error.status === 401) {
            this.logout();
            this.router.navigate(['/login-cms']);
            return of(false);
          }
          // Erros de infraestrutura temporários (500, timeout) -> mantém usuário logado
          console.warn('[AuthService] Falha temporária no ping de sessão:', error.message);
          return of(true);
        }
        return of(true);
      })
    );
  }

  startPingTimer(): void {
    this.stopPingTimer();
    this.pingIntervalId = setInterval(() => {
      this.pingSession().subscribe();
    }, this.PING_INTERVAL_MS);
  }

  stopPingTimer(): void {
    if (this.pingIntervalId) {
      clearInterval(this.pingIntervalId);
      this.pingIntervalId = null;
    }
  }

  private setToken(token: string): void {
    this.accessToken = token;
    this.loggedInSubject.next(true);
  }
}

