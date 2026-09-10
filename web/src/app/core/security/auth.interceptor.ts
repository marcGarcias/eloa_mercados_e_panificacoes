import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { catchError, switchMap, take, finalize } from 'rxjs/operators';
import { Observable, Subject, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';

let isRefreshing = false;
let refreshTokenSubject = new Subject<string>();

/**
 * Interceptor de autenticação HTTP.
 * 
 * Responsabilidades:
 * 1. Anexar credenciais (cookies HttpOnly) e token JWT Bearer nas requisições à API.
 * 2. Identificar respostas 401 Unauthorized e coordenar um único refresh concorrente.
 * 3. Enfileirar requisições simultâneas durante a rotação do token.
 * 4. Repetir as requisições originais com o novo Access Token após renovação bem-sucedida.
 * 5. Prevenir loops infinitos ignorando rotas de autenticação (/api/auth/*).
 * 6. Efetuar logout somente em caso de falha definitiva de autenticação.
 */
export const authInterceptor: HttpInterceptorFn = (req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  const token = authService.getToken();
  const apiUrl = environment?.apiUrl ?? '';

  // Habilita withCredentials para envio seguro do cookie HttpOnly (refresh_token)
  let authReq = req.clone({
    withCredentials: true
  });

  // Anexa X-Requested-With e o token Bearer para requisições à nossa própria API
  if (req.url.startsWith(`${apiUrl}/api/`) || req.url.startsWith('/api/')) {
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
    catchError((error: unknown) => {
      if (!(error instanceof HttpErrorResponse)) {
        return throwError(() => error);
      }

      // Extrai pathname preciso para evitar falsos positivos
      const pathname = req.url.startsWith('http')
        ? new URL(req.url).pathname
        : req.url;

      const isAuthRoute =
        pathname.startsWith('/api/auth/login') ||
        pathname.startsWith('/api/auth/refresh') ||
        pathname.startsWith('/api/auth/logout') ||
        pathname.startsWith('/api/auth/bootstrap');

      // Se for 401 e não for rota de autenticação
      if (error.status === 401 && !isAuthRoute) {
        if (isRefreshing) {
          // Requisições concorrentes aguardam o novo token emitido pelo refresh principal
          return refreshTokenSubject.pipe(
            take(1),
            switchMap((newToken: string) => {
              const retryReq = authReq.clone({
                withCredentials: true,
                setHeaders: {
                  'Authorization': `Bearer ${newToken}`,
                  'X-Requested-With': 'XMLHttpRequest'
                }
              });
              return next(retryReq);
            })
          );
        }

        isRefreshing = true;

        return authService.silentRefresh().pipe(
          switchMap((success: boolean) => {
            if (success) {
              const newToken = authService.getToken();
              if (newToken) {
                // Notifica todas as requisições em espera na fila
                refreshTokenSubject.next(newToken);
                refreshTokenSubject.complete();
                refreshTokenSubject = new Subject<string>();

                const retryReq = authReq.clone({
                  withCredentials: true,
                  setHeaders: {
                    'Authorization': `Bearer ${newToken}`,
                    'X-Requested-With': 'XMLHttpRequest'
                  }
                });
                return next(retryReq);
              }
            }

            // Falha na renovação: notifica a fila e executa logout
            refreshTokenSubject.error(error);
            refreshTokenSubject = new Subject<string>();
            authService.logout();
            router.navigate(['/login-cms']);
            return throwError(() => error);
          }),
          catchError((refreshErr: unknown) => {
            // Em caso de erro definitivo de autenticação no refresh
            refreshTokenSubject.error(refreshErr);
            refreshTokenSubject = new Subject<string>();
            
            // Só desloga se o erro de refresh for 401/403 (token inválido/expirado)
            if (refreshErr instanceof HttpErrorResponse && (refreshErr.status === 401 || refreshErr.status === 403)) {
              authService.logout();
              router.navigate(['/login-cms']);
            }
            return throwError(() => refreshErr);
          }),
          finalize(() => {
            // Garante que a trava seja sempre liberada
            isRefreshing = false;
          })
        );
      }

      // Propaga o erro normalmente para outras situações (400, 403, 404, 500, etc.)
      return throwError(() => error);
    })
  );
};

