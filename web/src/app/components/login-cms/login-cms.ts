import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

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
        this.router.navigate(['/admin']);
      },
      error: (err) => {
        this.isLoading = false;
        if (err.status === 401 || err.status === 403) {
          this.loginError = 'Código de acesso ou senha incorretos.';
        } else {
          this.loginError = 'Ocorreu um erro ao tentar fazer login. Tente novamente mais tarde.';
        }
      }
    });
  }
}
