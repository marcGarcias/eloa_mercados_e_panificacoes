import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { catchError } from 'rxjs/operators';
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
      // If unauthorized, log out and redirect
      if (error.status === 401 || error.status === 403) {
        authService.logout();
        router.navigate(['/login-cms']);
      }
      return throwError(() => error);
    })
  );
};
