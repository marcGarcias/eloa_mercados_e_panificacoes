import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { catchError, switchMap } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { environment } from '../../../environments/environment';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  const token = authService.getToken();
  const apiUrl = environment?.apiUrl ?? '';

  // Habilitamos withCredentials para enviar o cookie HttpOnly (refresh_token) para o backend
  let authReq = req.clone({
    withCredentials: true
  });

  // Anexa o token apenas se a requisição for para a nossa própria API, evitando vazamentos
  if (token && req.url.startsWith(`${apiUrl}/api/`)) {
    authReq = authReq.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(authReq).pipe(
    catchError((error) => {
      if (error.status === 401) {
        const isAuthRoute = req.url.includes('/api/auth/login') || req.url.includes('/api/auth/logout') || req.url.includes('/api/auth/refresh');
        
        if (!isAuthRoute) {
          return authService.silentRefresh().pipe(
            switchMap((success) => {
              if (success) {
                const newToken = authService.getToken();
                const newReq = req.clone({
                  withCredentials: true,
                  setHeaders: { Authorization: `Bearer ${newToken}` }
                });
                return next(newReq);
              } else {
                authService.logout();
                router.navigate(['/login-cms']);
                return throwError(() => error);
              }
            })
          );
        }
      }
      return throwError(() => error);
    })
  );
};
