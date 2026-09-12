import { TestBed } from '@angular/core/testing';
import { HttpClient, HttpErrorResponse, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Router, provideRouter } from '@angular/router';
import { of, throwError, Subject } from 'rxjs';
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { authInterceptor } from './auth.interceptor';
import { AuthService } from '../../services/auth.service';

describe('authInterceptor (HttpInterceptorFn)', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let router: Router;
  let authServiceMock: {
    getToken: ReturnType<typeof vi.fn>;
    silentRefresh: ReturnType<typeof vi.fn>;
    logout: ReturnType<typeof vi.fn>;
  };

  beforeEach(() => {
    authServiceMock = {
      getToken: vi.fn(),
      silentRefresh: vi.fn(),
      logout: vi.fn()
    };

    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: authServiceMock }
      ]
    });

    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate');
  });

  afterEach(() => {
    httpMock.verify();
    vi.clearAllMocks();
  });

  describe('withCredentials e Headers', () => {
    it('deve anexar withCredentials: true, X-Requested-With e Bearer token para chamadas da API interna', () => {
      // Arrange
      authServiceMock.getToken.mockReturnValue('valid-token-abc');

      // Act
      http.get('/api/products').subscribe();

      // Assert
      const req = httpMock.expectOne('/api/products');
      expect(req.request.withCredentials).toBe(true);
      expect(req.request.headers.get('X-Requested-With')).toBe('XMLHttpRequest');
      expect(req.request.headers.get('Authorization')).toBe('Bearer valid-token-abc');

      req.flush([]);
    });

    it('deve garantir withCredentials: true em chamadas de login, refresh e logout', () => {
      // Arrange
      authServiceMock.getToken.mockReturnValue(null);

      // Act & Assert para login
      http.post('/api/auth/login', {}).subscribe();
      const reqLogin = httpMock.expectOne('/api/auth/login');
      expect(reqLogin.request.withCredentials).toBe(true);
      reqLogin.flush({});

      // Act & Assert para refresh
      http.post('/api/auth/refresh', {}).subscribe();
      const reqRefresh = httpMock.expectOne('/api/auth/refresh');
      expect(reqRefresh.request.withCredentials).toBe(true);
      reqRefresh.flush({});

      // Act & Assert para logout
      http.post('/api/auth/logout', {}).subscribe();
      const reqLogout = httpMock.expectOne('/api/auth/logout');
      expect(reqLogout.request.withCredentials).toBe(true);
      reqLogout.flush({});
    });

    it('não deve anexar Authorization nem X-Requested-With para URLs de terceiros', () => {
      // Arrange
      authServiceMock.getToken.mockReturnValue('secret-token');

      // Act
      http.get('https://external-api.com/data').subscribe();

      // Assert
      const req = httpMock.expectOne('https://external-api.com/data');
      expect(req.request.withCredentials).toBe(true);
      expect(req.request.headers.has('Authorization')).toBe(false);
      expect(req.request.headers.has('X-Requested-With')).toBe(false);

      req.flush({});
    });
  });

  describe('Prevenção de Loops Infinitos em Rotas de Autenticação', () => {
    it.each([
      '/api/auth/login',
      '/api/auth/refresh',
      '/api/auth/logout',
      '/api/auth/bootstrap'
    ])('não deve disparar silentRefresh quando %s responder 401', (authUrl) => {
      // Arrange
      let caughtError: unknown = null;

      // Act
      http.post(authUrl, {}).subscribe({
        error: (err) => (caughtError = err)
      });

      const req = httpMock.expectOne(authUrl);
      req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

      // Assert
      expect(authServiceMock.silentRefresh).not.toHaveBeenCalled();
      expect(caughtError).toBeInstanceOf(HttpErrorResponse);
    });
  });

  describe('Concorrência de 401 e Rotação de Refresh Token', () => {
    it('deve disparar silentRefresh único para requisições concorrentes e repetir ambas com o novo token', () => {
      // Arrange
      authServiceMock.getToken.mockReturnValue('expired-token');
      const refreshSubject = new Subject<boolean>();
      authServiceMock.silentRefresh.mockReturnValue(refreshSubject.asObservable());

      let response1: unknown = null;
      let response2: unknown = null;

      // Act - Dispara duas requisições simultâneas
      http.get('/api/products/1').subscribe(res => (response1 = res));
      http.get('/api/products/2').subscribe(res => (response2 = res));

      // Ambas partem com o token expirado
      const req1 = httpMock.expectOne('/api/products/1');
      const req2 = httpMock.expectOne('/api/products/2');

      // Primeira requisição recebe 401
      req1.flush('Expired token', { status: 401, statusText: 'Unauthorized' });

      // Segunda requisição recebe 401 enquanto o refresh está em andamento
      req2.flush('Expired token', { status: 401, statusText: 'Unauthorized' });

      // Assert - Apenas 1 refresh deve ter sido iniciado
      expect(authServiceMock.silentRefresh).toHaveBeenCalledTimes(1);

      // Agora o refresh é concluído com sucesso e emite um novo token
      authServiceMock.getToken.mockReturnValue('new-fresh-token-xyz');
      refreshSubject.next(true);
      refreshSubject.complete();

      // Assert - Ambas as requisições devem ter sido repetidas com o novo token
      const retry1 = httpMock.expectOne('/api/products/1');
      const retry2 = httpMock.expectOne('/api/products/2');

      expect(retry1.request.headers.get('Authorization')).toBe('Bearer new-fresh-token-xyz');
      expect(retry2.request.headers.get('Authorization')).toBe('Bearer new-fresh-token-xyz');

      // Responde com sucesso aos retries
      retry1.flush({ id: 1, name: 'Produto 1' });
      retry2.flush({ id: 2, name: 'Produto 2' });

      expect(response1).toEqual({ id: 1, name: 'Produto 1' });
      expect(response2).toEqual({ id: 2, name: 'Produto 2' });
      expect(authServiceMock.logout).not.toHaveBeenCalled();
    });

    it('deve propagar erro para todas as requisições, chamar logout 1 única vez e redirecionar se o refresh falhar', () => {
      // Arrange
      authServiceMock.getToken.mockReturnValue('expired-token');
      const refreshSubject = new Subject<boolean>();
      authServiceMock.silentRefresh.mockReturnValue(refreshSubject.asObservable());

      let error1: unknown = null;
      let error2: unknown = null;

      // Act
      http.get('/api/categories').subscribe({
        error: (err) => (error1 = err)
      });
      http.get('/api/users').subscribe({
        error: (err) => (error2 = err)
      });

      const req1 = httpMock.expectOne('/api/categories');
      const req2 = httpMock.expectOne('/api/users');

      // Ambas recebem 401 enquanto o refresh está em andamento
      req1.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });
      req2.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

      // O refresh emite falha definitiva (401)
      const refreshError = new HttpErrorResponse({
        status: 401,
        statusText: 'Refresh Token Expired',
        url: '/api/auth/refresh'
      });
      refreshSubject.error(refreshError);

      // Assert
      expect(authServiceMock.silentRefresh).toHaveBeenCalledTimes(1);
      expect(authServiceMock.logout).toHaveBeenCalledTimes(1);
      expect(router.navigate).toHaveBeenCalledWith(['/login-cms']);
      expect(error1).toBeDefined();
      expect(error2).toBeDefined();
    });

    it('deve propagar erro normalmente para outros status HTTP (400, 403, 404, 500) sem chamar refresh', () => {
      // Arrange
      let error404: unknown = null;

      // Act
      http.get('/api/products/9999').subscribe({
        error: (err) => (error404 = err)
      });

      const req = httpMock.expectOne('/api/products/9999');
      req.flush('Not Found', { status: 404, statusText: 'Not Found' });

      // Assert
      expect(authServiceMock.silentRefresh).not.toHaveBeenCalled();
      expect(authServiceMock.logout).not.toHaveBeenCalled();
      expect(error404).toBeInstanceOf(HttpErrorResponse);
    });
  });
});
