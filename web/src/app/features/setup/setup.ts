import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-setup',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './setup.html',
  styleUrls: ['./setup.css', '../../components/login-cms/login-cms.css']
})
export class Setup {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  setupForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  showPassword = false;
  userCodeGenerated = '';

  constructor() {
    this.setupForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  onSubmit() {
    if (this.setupForm.invalid) {
      this.setupForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    
    const { name, password } = this.setupForm.value;

    this.authService.bootstrapSystem(name, password).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.userCodeGenerated = res.userCode;
      },
      error: (err) => {
        this.isLoading = false;
        if (err.status === 409) {
          this.errorMessage = 'O sistema já possui um proprietário inicializado. Acesse o login.';
        } else {
          this.errorMessage = err.error?.message || 'Erro ao configurar o sistema. Verifique a conexão.';
        }
      }
    });
  }

  goToLogin() {
    this.router.navigate(['/login-cms']);
  }
}
