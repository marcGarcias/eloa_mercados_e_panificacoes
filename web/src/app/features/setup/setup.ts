import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

function cpfValidator(control: AbstractControl): ValidationErrors | null {
  const cpf = control.value?.replace(/\D/g, '');
  if (!cpf) return null;
  if (cpf.length !== 11 || /^(\d)\1{10}$/.test(cpf)) return { cpfInvalid: true };

  let sum = 0;
  let rest;

  for (let i = 1; i <= 9; i++) sum = sum + parseInt(cpf.substring(i - 1, i)) * (11 - i);
  rest = (sum * 10) % 11;
  if ((rest === 10) || (rest === 11)) rest = 0;
  if (rest !== parseInt(cpf.substring(9, 10))) return { cpfInvalid: true };

  sum = 0;
  for (let i = 1; i <= 10; i++) sum = sum + parseInt(cpf.substring(i - 1, i)) * (12 - i);
  rest = (sum * 10) % 11;
  if ((rest === 10) || (rest === 11)) rest = 0;
  if (rest !== parseInt(cpf.substring(10, 11))) return { cpfInvalid: true };

  return null;
}

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
      cpf: ['', [Validators.required, cpfValidator]],
      accessKey: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });

    // Mascara simples de CPF no subscribe
    this.setupForm.get('cpf')?.valueChanges.subscribe(value => {
      if (value) {
        let cleanValue = value.replace(/\D/g, '').substring(0, 11);
        if (cleanValue.length > 9) {
          cleanValue = cleanValue.replace(/^(\d{3})(\d{3})(\d{3})(\d{2}).*/, '$1.$2.$3-$4');
        } else if (cleanValue.length > 6) {
          cleanValue = cleanValue.replace(/^(\d{3})(\d{3})(\d{1,3}).*/, '$1.$2.$3');
        } else if (cleanValue.length > 3) {
          cleanValue = cleanValue.replace(/^(\d{3})(\d{1,3}).*/, '$1.$2');
        }
        if (value !== cleanValue) {
          this.setupForm.get('cpf')?.setValue(cleanValue, { emitEvent: false });
        }
      }
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
    
    const { name, password, accessKey, cpf } = this.setupForm.value;

    this.authService.bootstrapSystem(name, password, accessKey, cpf).subscribe({
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
