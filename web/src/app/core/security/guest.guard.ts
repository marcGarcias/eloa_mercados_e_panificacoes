import { inject } from '@angular/core';
import { Router, type CanActivateFn } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { map, of, switchMap } from 'rxjs';

export const guestGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.checkAuthStatus().pipe(
    switchMap(isAuthenticated => {
      if (isAuthenticated) {
        return of(router.parseUrl('/admin'));
      }
      return of(true);
    })
  );
};
