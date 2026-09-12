import { Component, OnInit, OnDestroy, inject, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../services/toast.service';
import { finalize, Subscription } from 'rxjs';

@Component({
  selector: 'app-login-cms',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './login-cms.html',
  styleUrl: './login-cms.css',
})
export class LoginCms implements OnDestroy {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly toastService = inject(ToastService);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly subs = new Subscription();

  showPassword = false;
  loginError = '';
  isLoading = false;

  loginForm = this.fb.group({
    userCode: ['', [Validators.required]],
    password: ['', [Validators.required]]
  });

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
    this.cdr.markForCheck();
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      this.cdr.markForCheck();
      return;
    }

    this.isLoading = true;
    this.loginError = '';
    this.cdr.detectChanges();
    
    const { userCode, password } = this.loginForm.value;

    this.subs.add(
      this.authService.login(userCode!, password!).pipe(
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        })
      ).subscribe({
        next: () => {
          this.toastService.success('Bem-vindo de volta ao painel!', 'Login Realizado');
          this.router.navigate(['/admin']).then(navigated => {
            if (!navigated) {
              this.router.navigate(['/admin/catalog']);
            }
          }).catch(err => {
            console.error('Falha na navegação pós-login:', err);
          });
          this.cdr.detectChanges();
        },
        error: (err) => {
          if (err.status === 401 || err.status === 403) {
            this.loginError = 'Código de acesso ou senha incorretos.';
            this.toastService.error('Verifique suas credenciais e tente novamente.', 'Erro de Acesso');
          } else {
            this.loginError = 'Ocorreu um erro ao tentar fazer login. Tente novamente mais tarde.';
            this.toastService.error('Erro de conexão com o servidor. Tente novamente.', 'Falha no Login');
          }
          this.cdr.detectChanges();
        }
      })
    );
  }
}
