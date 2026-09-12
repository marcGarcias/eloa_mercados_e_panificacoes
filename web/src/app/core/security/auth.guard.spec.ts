import { TestBed } from '@angular/core/testing';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot, Route, UrlSegment, UrlTree, provideRouter, GuardResult, MaybeAsync } from '@angular/router';
import { of, BehaviorSubject, firstValueFrom, isObservable, from, Observable } from 'rxjs';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { authGuard, authMatchGuard } from './auth.guard';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';
import { createMockUser, createMockSuperAdmin, createMockEditor } from '../../../testing';

function resolveGuardResult(result: MaybeAsync<GuardResult>): Observable<GuardResult> {
  if (isObservable(result)) {
    return result;
  }
  if (result instanceof Promise) {
    return from(result);
  }
  return of(result);
}

describe('Guards de Autenticação e Autorização', () => {
  let router: Router;
  let authServiceMock: {
    checkAuthStatus: ReturnType<typeof vi.fn>;
    currentUser$: BehaviorSubject<User | null>;
  };

  beforeEach(() => {
    authServiceMock = {
      checkAuthStatus: vi.fn(),
      currentUser$: new BehaviorSubject<User | null>(null)
    };

    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authServiceMock }
      ]
    });

    router = TestBed.inject(Router);
  });

  describe('authGuard (CanActivateFn)', () => {
    const dummyState = {} as RouterStateSnapshot;

    it('deve redirecionar para /login-cms quando o usuário não estiver autenticado', async () => {

      authServiceMock.checkAuthStatus.mockReturnValue(of(false));
      const route = { data: {} } as unknown as ActivatedRouteSnapshot;

      const rawGuard = TestBed.runInInjectionContext(() => authGuard(route, dummyState));
      const result = await firstValueFrom(resolveGuardResult(rawGuard));

      expect(result instanceof UrlTree).toBe(true);
      expect(router.serializeUrl(result as UrlTree)).toBe('/login-cms');
      expect(result).toEqual(router.parseUrl('/login-cms'));
    });

    it('deve permitir acesso (true) quando autenticado e a rota não exigir roles específicas', async () => {

      authServiceMock.checkAuthStatus.mockReturnValue(of(true));
      const route = { data: {} } as unknown as ActivatedRouteSnapshot;

      const rawGuard = TestBed.runInInjectionContext(() => authGuard(route, dummyState));
      const result = await firstValueFrom(resolveGuardResult(rawGuard));

      expect(result).toBe(true);
    });

    it('deve permitir acesso (true) quando o usuário possui uma das roles requeridas', async () => {

      authServiceMock.checkAuthStatus.mockReturnValue(of(true));
      authServiceMock.currentUser$.next(createMockUser({ role: 'ADMIN' }));
      const route = {
        data: { roles: ['ADMIN', 'SUPER_ADMIN'] }
      } as unknown as ActivatedRouteSnapshot;

      const rawGuard = TestBed.runInInjectionContext(() => authGuard(route, dummyState));
      const result = await firstValueFrom(resolveGuardResult(rawGuard));

      expect(result).toBe(true);
    });

    it('deve redirecionar para /admin quando a role do usuário não for permitida', async () => {

      authServiceMock.checkAuthStatus.mockReturnValue(of(true));
      authServiceMock.currentUser$.next(createMockEditor());
      const route = {
        data: { roles: ['SUPER_ADMIN'] }
      } as unknown as ActivatedRouteSnapshot;

      const rawGuard = TestBed.runInInjectionContext(() => authGuard(route, dummyState));
      const result = await firstValueFrom(resolveGuardResult(rawGuard));

      expect(result instanceof UrlTree).toBe(true);
      expect(router.serializeUrl(result as UrlTree)).toBe('/admin');
      expect(result).toEqual(router.parseUrl('/admin'));
    });

    it('deve redirecionar para /admin em caso de timeout aguardando currentUser', async () => {

      vi.useFakeTimers();
      authServiceMock.checkAuthStatus.mockReturnValue(of(true));

      const route = {
        data: { roles: ['ADMIN'] }
      } as unknown as ActivatedRouteSnapshot;

      let resolvedResult: unknown = null;
      const rawGuard = TestBed.runInInjectionContext(() => authGuard(route, dummyState));
      resolveGuardResult(rawGuard).subscribe(res => {
        resolvedResult = res;
      });

      vi.advanceTimersByTime(3500);

      expect(resolvedResult instanceof UrlTree).toBe(true);
      expect(router.serializeUrl(resolvedResult as UrlTree)).toBe('/admin');

      vi.useRealTimers();
    });
  });

  describe('authMatchGuard (CanMatchFn)', () => {
    const dummySegments: UrlSegment[] = [];

    it('deve retornar false para usuário anônimo (não autenticado)', async () => {

      authServiceMock.checkAuthStatus.mockReturnValue(of(false));
      const route: Route = { path: 'admin' };

      const rawGuard = TestBed.runInInjectionContext(() => authMatchGuard(route, dummySegments));
      const result = await firstValueFrom(resolveGuardResult(rawGuard));

      expect(result).toBe(false);
    });

    it('deve retornar true para usuário autenticado quando não há restrição de role', async () => {

      authServiceMock.checkAuthStatus.mockReturnValue(of(true));
      const route: Route = { path: 'admin' };

      const rawGuard = TestBed.runInInjectionContext(() => authMatchGuard(route, dummySegments));
      const result = await firstValueFrom(resolveGuardResult(rawGuard));

      expect(result).toBe(true);
    });

    it('deve retornar true para SUPER_ADMIN em rota restrita a SUPER_ADMIN', async () => {

      authServiceMock.checkAuthStatus.mockReturnValue(of(true));
      authServiceMock.currentUser$.next(createMockSuperAdmin());
      const route: Route = {
        path: 'admin/users',
        data: { roles: ['SUPER_ADMIN'] }
      };

      const rawGuard = TestBed.runInInjectionContext(() => authMatchGuard(route, dummySegments));
      const result = await firstValueFrom(resolveGuardResult(rawGuard));

      expect(result).toBe(true);
    });

    it('deve retornar false para ADMIN em rota exclusiva de SUPER_ADMIN', async () => {

      authServiceMock.checkAuthStatus.mockReturnValue(of(true));
      authServiceMock.currentUser$.next(createMockUser({ role: 'ADMIN' }));
      const route: Route = {
        path: 'admin/users',
        data: { roles: ['SUPER_ADMIN'] }
      };

      const rawGuard = TestBed.runInInjectionContext(() => authMatchGuard(route, dummySegments));
      const result = await firstValueFrom(resolveGuardResult(rawGuard));

      expect(result).toBe(false);
    });

    it('deve retornar false para EDITOR em rota restrita a ADMIN', async () => {

      authServiceMock.checkAuthStatus.mockReturnValue(of(true));
      authServiceMock.currentUser$.next(createMockEditor());
      const route: Route = {
        path: 'admin/products',
        data: { roles: ['ADMIN', 'SUPER_ADMIN'] }
      };

      const rawGuard = TestBed.runInInjectionContext(() => authMatchGuard(route, dummySegments));
      const result = await firstValueFrom(resolveGuardResult(rawGuard));

      expect(result).toBe(false);
    });
  });
});

