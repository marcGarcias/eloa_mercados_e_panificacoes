import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { catchError, switchMap, take } from 'rxjs/operators';
import { Subject, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';

let isRefreshing = false;
let refreshTokenSubject = new Subject<string>();

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  const token = authService.getToken();
  const apiUrl = environment?.apiUrl ?? '';

  // Habilita withCredentials para envio do cookie HttpOnly (refresh_token)
  let authReq = req.clone({
    withCredentials: true
  });

  // Anexa X-Requested-With e o token Bearer para requisições à nossa própria API
  if (req.url.startsWith(`${apiUrl}/api/`)) {
    const headers: { [key: string]: string } = {
      'X-Requested-With': 'XMLHttpRequest'
    };
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    authReq = authReq.clone({
      setHeaders: headers
    });
  }

  return next(authReq).pipe(
    catchError((error) => {
      // Extrai pathname preciso para evitar falsos positivos
      const pathname = req.url.startsWith('http')
        ? new URL(req.url).pathname
        : req.url;
      const isAuthRoute = pathname.startsWith('/api/auth/');

      // Se for 401 e não for rota de autenticação (/api/auth/login, /refresh, /logout)
      if (error.status === 401 && !isAuthRoute) {
        if (isRefreshing) {
          // Requisições concorrentes aguardam o novo token emitido pelo refresh principal
          return refreshTokenSubject.pipe(
            take(1),
            switchMap((newToken) => {
              const retryReq = req.clone({
                withCredentials: true,
                setHeaders: { Authorization: `Bearer ${newToken}` }
              });
              return next(retryReq);
            })
          );
        }

        isRefreshing = true;

        return authService.silentRefresh().pipe(
          switchMap((success) => {
            if (success) {
              const newToken = authService.getToken();
              if (newToken) {
                // Notifica todas as requisições em espera com o novo token
                refreshTokenSubject.next(newToken);
                refreshTokenSubject.complete();
                isRefreshing = false;
                refreshTokenSubject = new Subject<string>();

                const retryReq = req.clone({
                  withCredentials: true,
                  setHeaders: { Authorization: `Bearer ${newToken}` }
                });
                return next(retryReq);
              }
            }

            // Se o refresh não tiver sucesso, notifica os followers com erro e desloga
            refreshTokenSubject.error(error);
            isRefreshing = false;
            refreshTokenSubject = new Subject<string>();
            authService.logout();
            router.navigate(['/login-cms']);
            return throwError(() => error);
          }),
          catchError((refreshErr) => {
            // Em caso de exceção de rede ou erro na chamada de refresh
            refreshTokenSubject.error(refreshErr);
            isRefreshing = false;
            refreshTokenSubject = new Subject<string>();
            authService.logout();
            router.navigate(['/login-cms']);
            return throwError(() => refreshErr);
          })
        );
      }

      // Se for 403 Forbidden ou qualquer outro erro, apenas propaga adiante (NÃO faz refresh)
      return throwError(() => error);
    })
  );
};

