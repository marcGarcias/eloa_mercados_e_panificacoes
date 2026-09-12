import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { AuthService, LoginResponse } from './auth.service';
import { createMockUser, createMockLoginResponse } from '../../testing';
import { UserRole } from '../models/user.model';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('Estado Inicial', () => {
    it('deve inicializar deslogado e sem usuário ativo', () => {
      expect(service.isLoggedIn()).toBe(false);
      expect(service.getToken()).toBeNull();
      expect(service.currentUser).toBeNull();
    });
  });

  describe('login()', () => {
    it('deve autenticar com sucesso, armazenar o token e carregar o perfil do usuário', () => {
      // Arrange
      const mockUser = createMockUser({ userCode: 'ADM001', role: 'ADMIN' });
      const mockLogin = createMockLoginResponse('valid-jwt-token');

      let loggedUser: unknown = null;
      let stateIsLoggedIn: boolean | undefined;

      service.isLoggedIn$.subscribe(state => (stateIsLoggedIn = state));

      // Act
      service.login('ADM001', 'senha123').subscribe(user => {
        loggedUser = user;
      });

      // 1. Responde a chamada de login
      const reqLogin = httpMock.expectOne('/api/auth/login');
      expect(reqLogin.request.method).toBe('POST');
      expect(reqLogin.request.body).toEqual({ userCode: 'ADM001', password: 'senha123' });
      reqLogin.flush(mockLogin);

      // 2. Responde a chamada sequencial de carregar usuário (/api/auth/me)
      const reqMe = httpMock.expectOne('/api/auth/me');
      expect(reqMe.request.method).toBe('GET');
      reqMe.flush(mockUser);

      // Assert
      expect(loggedUser).toEqual(mockUser);
      expect(service.getToken()).toBe('valid-jwt-token');
      expect(service.isLoggedIn()).toBe(true);
      expect(stateIsLoggedIn).toBe(true);
      expect(service.currentUser).toEqual(mockUser);
    });

    it('deve propagar erro caso a chamada de login falhe', () => {
      // Arrange
      let caughtError: unknown = null;

      // Act
      service.login('ERR001', 'senhaErrada').subscribe({
        error: (err) => (caughtError = err)
      });

      const req = httpMock.expectOne('/api/auth/login');
      req.flush('Credenciais inválidas', { status: 401, statusText: 'Unauthorized' });

      // Assert
      expect(caughtError).toBeDefined();
      expect(service.isLoggedIn()).toBe(false);
      expect(service.getToken()).toBeNull();
    });
  });

  describe('silentRefresh()', () => {
    it('deve renovar o token e atualizar o estado da sessão com sucesso', () => {
      // Arrange
      const mockUser = createMockUser();
      let refreshSuccess: boolean | undefined;

      // Act
      service.silentRefresh().subscribe(success => {
        refreshSuccess = success;
      });

      const reqRefresh = httpMock.expectOne('/api/auth/refresh');
      expect(reqRefresh.request.method).toBe('POST');
      reqRefresh.flush({ accessToken: 'renewed-jwt-token' });

      const reqMe = httpMock.expectOne('/api/auth/me');
      reqMe.flush(mockUser);

      // Assert
      expect(refreshSuccess).toBe(true);
      expect(service.getToken()).toBe('renewed-jwt-token');
      expect(service.isLoggedIn()).toBe(true);
      expect(service.currentUser).toEqual(mockUser);
    });

    it('deve limpar token e estado caso a renovação falhe', () => {
      // Arrange
      let refreshSuccess: boolean | undefined;

      // Act
      service.silentRefresh().subscribe(success => {
        refreshSuccess = success;
      });

      const reqRefresh = httpMock.expectOne('/api/auth/refresh');
      reqRefresh.flush('Refresh Token Expirado', { status: 401, statusText: 'Unauthorized' });

      // Assert
      expect(refreshSuccess).toBe(false);
      expect(service.getToken()).toBeNull();
      expect(service.isLoggedIn()).toBe(false);
      expect(service.currentUser).toBeNull();
    });
  });

  describe('logout()', () => {
    it('deve chamar o backend e limpar token e usuário mesmo se houver falha de rede', () => {
      // Arrange
      (service as any).setToken('token-ativo');
      (service as any).currentUserSubject.next(createMockUser());

      expect(service.isLoggedIn()).toBe(true);

      // Act
      service.logout();

      const reqLogout = httpMock.expectOne('/api/auth/logout');
      expect(reqLogout.request.method).toBe('POST');
      reqLogout.error(new ProgressEvent('Network Error'));

      // Assert
      expect(service.getToken()).toBeNull();
      expect(service.isLoggedIn()).toBe(false);
      expect(service.currentUser).toBeNull();
    });
  });

  describe('hasRole()', () => {
    it('deve retornar true quando o usuário possui uma das roles solicitadas', () => {
      (service as any).currentUserSubject.next(createMockUser({ role: 'ADMIN' }));

      expect(service.hasRole(['ADMIN', 'SUPER_ADMIN'])).toBe(true);
      expect(service.hasRole(['EDITOR'])).toBe(false);
    });

    it('deve retornar false quando não há usuário logado', () => {
      expect(service.hasRole(['ADMIN'])).toBe(false);
    });
  });

  describe('checkAuthStatus()', () => {
    it('deve ser idempotente quando a autenticação já tiver sido inicializada', () => {
      // Arrange
      (service as any).authInitialized = true;
      (service as any).setToken('existing-token');

      // Act
      let status: boolean | undefined;
      service.checkAuthStatus().subscribe(res => (status = res));

      // Assert: nenhuma chamada HTTP é necessária
      expect(status).toBe(true);
      httpMock.expectNone('/api/auth/refresh');
    });
  });
});
