import { inject } from '@angular/core';
import { Router, type CanActivateFn, type CanMatchFn } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { UserRole } from '../../models/user.model';
import { map, filter, take, switchMap, of } from 'rxjs';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.checkAuthStatus().pipe(
    switchMap(isAuthenticated => {
      if (!isAuthenticated) {
        return of(router.parseUrl('/login-cms'));
      }

      const requiredRoles = route.data['roles'] as UserRole[];
      if (!requiredRoles || requiredRoles.length === 0) {
        return of(true); // Nenhuma restrição de role
      }

      // Se temos permissões exigidas, aguardamos o currentUser ser carregado pela chamada /me
      return authService.currentUser$.pipe(
        filter(user => user !== null), // Aguarda até que não seja null
        take(1), // Pega apenas o primeiro valor válido e completa
        map(user => {
          if (requiredRoles.includes(user!.role)) {
            return true; // Tem permissão
          }
          // Se não tem permissão, volta pra home do admin
          return router.parseUrl('/admin'); 
        })
      );
    })
  );
};

export const authMatchGuard: CanMatchFn = (route, segments) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.checkAuthStatus().pipe(
    switchMap(isAuthenticated => {
      if (!isAuthenticated) {
        return of(false); // Não combina a rota se não estiver logado
      }

      const requiredRoles = route.data?.['roles'] as UserRole[];
      if (!requiredRoles || requiredRoles.length === 0) {
        return of(true); // Nenhuma restrição
      }

      return authService.currentUser$.pipe(
        filter(user => user !== null),
        take(1),
        map(user => {
          return requiredRoles.includes(user!.role);
        })
      );
    })
  );
};
