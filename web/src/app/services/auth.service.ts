import { Injectable, inject, OnDestroy } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap, catchError, of, map, switchMap, throwError } from 'rxjs';
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
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private readonly apiUrl = (environment?.apiUrl ?? '') + '/api/auth';

  private accessToken: string | null = null;
  private loggedInSubject = new BehaviorSubject<boolean>(false);
  private currentUserSubject = new BehaviorSubject<User | null>(null);

  isLoggedIn$ = this.loggedInSubject.asObservable();
  currentUser$ = this.currentUserSubject.asObservable();

  private authInitialized = false;

  constructor() {}

  get currentUser(): User | null {
    return this.currentUserSubject.value;
  }

  login(userCode: string, password: string): Observable<User> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, { userCode, password }).pipe(
      switchMap(response => {
        if (response.accessToken) {
          this.setToken(response.accessToken);
          this.authInitialized = true; // Evita refresh redundante no guard
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
      catchError((error: unknown) => {
        // Apenas limpa a autenticação se for erro explícito de autenticação (401/403)
        if (error instanceof HttpErrorResponse && (error.status === 401 || error.status === 403)) {
          this.currentUserSubject.next(null);
          this.accessToken = null;
          this.loggedInSubject.next(false);
        }
        return of(null);
      })
    );
  }

  checkAuthStatus(): Observable<boolean> {
    if (this.authInitialized) {
      return of(this.isLoggedIn());
    }

    return this.silentRefresh().pipe(
      tap(() => this.authInitialized = true),
      catchError(() => of(false))
    );
  }

  silentRefresh(): Observable<boolean> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/refresh`, {}).pipe(
      switchMap(response => {
        if (response.accessToken) {
          this.setToken(response.accessToken);
          return this.loadCurrentUser().pipe(
            map(() => true),
            catchError(() => of(true))
          );
        }
        return of(false);
      }),
      catchError((error: unknown) => {
        // Só limpa a sessão e credenciais se o refresh for explicitamente 401 ou 403
        if (error instanceof HttpErrorResponse && (error.status === 401 || error.status === 403)) {
          this.accessToken = null;
          this.loggedInSubject.next(false);
          this.currentUserSubject.next(null);
          return of(false);
        }
        // Para oscilações temporárias de rede (status 0) ou erro 5xx do servidor:
        // NÃO limpa as credenciais! Propaga o erro para não deslogar injustamente.
        return throwError(() => error);
      })
    );
  }

  private setToken(token: string): void {
    this.accessToken = token;
    this.loggedInSubject.next(true);
  }
}

