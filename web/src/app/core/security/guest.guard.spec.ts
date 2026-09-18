import { TestBed } from '@angular/core/testing';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, provideRouter, GuardResult, MaybeAsync } from '@angular/router';
import { of, firstValueFrom, isObservable, from, Observable } from 'rxjs';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { guestGuard } from './guest.guard';
import { AuthService } from '../../services/auth.service';

function resolveGuardResult(result: MaybeAsync<GuardResult>): Observable<GuardResult> {
  if (isObservable(result)) {
    return result;
  }
  if (result instanceof Promise) {
    return from(result);
  }
  return of(result);
}

describe('guestGuard (CanActivateFn)', () => {
  let router: Router;
  let authServiceMock: {
    checkAuthStatus: ReturnType<typeof vi.fn>;
  };

  beforeEach(() => {
    authServiceMock = {
      checkAuthStatus: vi.fn()
    };

    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authServiceMock }
      ]
    });

    router = TestBed.inject(Router);
  });

  const dummyRoute = {} as ActivatedRouteSnapshot;
  const dummyState = {} as RouterStateSnapshot;

  it('deve redirecionar para /admin quando o usuário já estiver autenticado', async () => {
    authServiceMock.checkAuthStatus.mockReturnValue(of(true));

    const rawGuard = TestBed.runInInjectionContext(() => guestGuard(dummyRoute, dummyState));
    const result = await firstValueFrom(resolveGuardResult(rawGuard));

    expect(result instanceof UrlTree).toBe(true);
    expect(router.serializeUrl(result as UrlTree)).toBe('/admin');
    expect(result).toEqual(router.parseUrl('/admin'));
  });

  it('deve permitir acesso (true) quando o usuário não estiver autenticado', async () => {
    authServiceMock.checkAuthStatus.mockReturnValue(of(false));

    const rawGuard = TestBed.runInInjectionContext(() => guestGuard(dummyRoute, dummyState));
    const result = await firstValueFrom(resolveGuardResult(rawGuard));

    expect(result).toBe(true);
  });
});
