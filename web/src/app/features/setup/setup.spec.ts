import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Setup } from './setup';
import { AuthService } from '../../services/auth.service';
import { of, throwError } from 'rxjs';

describe('Setup Component', () => {
  let component: Setup;
  let fixture: ComponentFixture<Setup>;
  let mockAuthService: { bootstrapSystem: ReturnType<typeof vi.fn> };
  let mockRouter: { navigate: ReturnType<typeof vi.fn> };

  const validCpf = '52998224725';

  beforeEach(async () => {
    mockAuthService = {
      bootstrapSystem: vi.fn()
    };
    mockRouter = {
      navigate: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [Setup, ReactiveFormsModule],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Setup);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve inicializar o formulário inválido', () => {
    expect(component).toBeTruthy();
    expect(component.setupForm.valid).toBeFalsy();
  });

  it('deve alternar a visibilidade da senha', () => {
    expect(component.showPassword).toBeFalsy();
    component.togglePassword();
    expect(component.showPassword).toBeTruthy();
    component.togglePassword();
    expect(component.showPassword).toBeFalsy();
  });

  it('deve formatar máscara do CPF automaticamente ao digitar', () => {
    const cpfControl = component.setupForm.get('cpf');

    // 4 dígitos -> formata 000.0
    cpfControl?.setValue('1234');
    expect(cpfControl?.value).toBe('123.4');

    // 7 dígitos -> formata 000.000.0
    cpfControl?.setValue('1234567');
    expect(cpfControl?.value).toBe('123.456.7');

    // 11 dígitos -> formata 000.000.000-00
    cpfControl?.setValue('12345678901');
    expect(cpfControl?.value).toBe('123.456.789-01');
  });

  it('deve validar CPF e rejeitar inválidos com sequências repetidas ou dígitos errados', () => {
    const cpfControl = component.setupForm.get('cpf');

    // Dígitos repetidos
    cpfControl?.setValue('11111111111');
    expect(cpfControl?.hasError('cpfInvalid')).toBeTruthy();

    // Dígitos de verificação incorretos
    cpfControl?.setValue('12345678900');
    expect(cpfControl?.hasError('cpfInvalid')).toBeTruthy();

    // CPF válido
    cpfControl?.setValue(validCpf);
    expect(cpfControl?.hasError('cpfInvalid')).toBeFalsy();
  });

  it('não deve submeter se o formulário for inválido', () => {
    component.onSubmit();
    expect(mockAuthService.bootstrapSystem).not.toHaveBeenCalled();
    expect(component.setupForm.touched).toBeTruthy();
  });

  it('deve executar o bootstrapSystem com sucesso e receber o userCode', () => {
    mockAuthService.bootstrapSystem.mockReturnValue(of({ userCode: 'ADM-9999' }));

    component.setupForm.patchValue({
      name: 'Administrador Inicial',
      cpf: validCpf,
      accessKey: 'MASTER_KEY_123',
      password: 'StrongPassword123'
    });

    component.onSubmit();

    expect(mockAuthService.bootstrapSystem).toHaveBeenCalledWith(
      'Administrador Inicial',
      'StrongPassword123',
      'MASTER_KEY_123',
      expect.stringContaining('529')
    );
    expect(component.userCodeGenerated).toBe('ADM-9999');
    expect(component.errorMessage).toBe('');
    expect(component.isLoading).toBeFalsy();
  });

  it('deve tratar caso onde a resposta de sucesso não contém userCode', () => {
    mockAuthService.bootstrapSystem.mockReturnValue(of({ userCode: '' }));

    component.setupForm.patchValue({
      name: 'Administrador',
      cpf: validCpf,
      accessKey: 'KEY',
      password: 'Password123'
    });

    component.onSubmit();

    expect(component.errorMessage).toContain('Não foi possível gerar o código de acesso');
  });

  it('deve tratar erro 409 de sistema já inicializado', () => {
    mockAuthService.bootstrapSystem.mockReturnValue(throwError(() => ({ status: 409 })));

    component.setupForm.patchValue({
      name: 'Administrador',
      cpf: validCpf,
      accessKey: 'KEY',
      password: 'Password123'
    });

    component.onSubmit();

    expect(component.errorMessage).toContain('O sistema já possui um proprietário inicializado');
  });

  it('deve tratar erro genérico na configuração', () => {
    mockAuthService.bootstrapSystem.mockReturnValue(throwError(() => ({
      status: 500,
      error: { message: 'Chave mestra inválida.' }
    })));

    component.setupForm.patchValue({
      name: 'Administrador',
      cpf: validCpf,
      accessKey: 'WRONG_KEY',
      password: 'Password123'
    });

    component.onSubmit();

    expect(component.errorMessage).toBe('Chave mestra inválida.');
  });

  it('deve navegar para a tela de login ao chamar goToLogin()', () => {
    component.goToLogin();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login-cms']);
  });

  it('deve cancelar inscrições no ngOnDestroy', () => {
    const unsubSpy = vi.spyOn((component as unknown as { subs: { unsubscribe: () => void } }).subs, 'unsubscribe');
    component.ngOnDestroy();
    expect(unsubSpy).toHaveBeenCalled();
  });
});
