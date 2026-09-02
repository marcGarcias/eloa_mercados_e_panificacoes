import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-login-cms',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './login-cms.html',
  styleUrl: './login-cms.css',
})
export class LoginCms {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private toastService = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  showPassword = false;
  loginError = '';
  isLoading = false;

  loginForm = this.fb.group({
    userCode: ['', [Validators.required]],
    password: ['', [Validators.required]]
  });

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      return;
    }

    this.isLoading = true;
    this.loginError = '';
    
    const { userCode, password } = this.loginForm.value;

    this.authService.login(userCode!, password!).subscribe({
      next: () => {
        this.toastService.success('Bem-vindo de volta ao painel!', 'Login Realizado');
        this.router.navigate(['/admin']).then(navigated => {
          if (!navigated) {
            this.isLoading = false;
            this.cdr.detectChanges();
          }
        }).catch(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        });
      },
      error: (err) => {
        this.isLoading = false;
        if (err.status === 401 || err.status === 403) {
          this.loginError = 'Código de acesso ou senha incorretos.';
          this.toastService.error('Verifique suas credenciais e tente novamente.', 'Erro de Acesso');
        } else {
          this.loginError = 'Ocorreu um erro ao tentar fazer login. Tente novamente mais tarde.';
          this.toastService.error('Erro de conexão com o servidor. Tente novamente.', 'Falha no Login');
        }
        this.cdr.detectChanges();
      }
    });
  }
}
