import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, catchError, of, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { User, UserRole } from '../models/user.model';

export interface LoginResponse {
  accessToken: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private readonly apiUrl = (environment?.apiUrl ?? '') + '/api/auth';
  
  private accessToken: string | null = null;
  private loggedInSubject = new BehaviorSubject<boolean>(false);
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  
  isLoggedIn$ = this.loggedInSubject.asObservable();
  currentUser$ = this.currentUserSubject.asObservable();

  private authInitialized = false;

  constructor() {
    // Na inicialização, não fazemos a chamada aqui. Deixamos o guard ou APP_INITIALIZER fazer para podermos aguardar.
  }

  get currentUser(): User | null {
    return this.currentUserSubject.value;
  }

  login(userCode: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, { userCode, password }).pipe(
      tap(response => {
        if (response.accessToken) {
          this.setToken(response.accessToken);
          this.loadCurrentUser().subscribe();
        }
      })
    );
  }

  logout(): void {
    // Chama o backend para invalidar o cookie
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
    // Rota que será implementada no backend para ler o token e retornar os dados do usuário
    return this.http.get<User>(`${this.apiUrl}/me`).pipe(
      tap(user => this.currentUserSubject.next(user)),
      catchError(() => {
        // Se der erro (ex: 401 ou rota não existe ainda), limpamos o user e token local
        this.currentUserSubject.next(null);
        this.accessToken = null;
        this.loggedInSubject.next(false);
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
      tap(response => {
        if (response.accessToken) {
          this.setToken(response.accessToken);
          this.loadCurrentUser().subscribe();
        }
      }),
      map(() => true),
      catchError(() => {
        this.accessToken = null;
        this.loggedInSubject.next(false);
        this.currentUserSubject.next(null);
        return of(false);
      })
    );
  }

  private setToken(token: string): void {
    this.accessToken = token;
    this.loggedInSubject.next(true);
  }
}
