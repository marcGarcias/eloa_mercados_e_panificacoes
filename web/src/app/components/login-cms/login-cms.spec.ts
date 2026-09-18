import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { LoginCms } from './login-cms';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../services/toast.service';
import { of, throwError } from 'rxjs';

describe('LoginCms Component', () => {
  let component: LoginCms;
  let fixture: ComponentFixture<LoginCms>;
  let mockAuthService: { login: ReturnType<typeof vi.fn>; checkAuthStatus: ReturnType<typeof vi.fn> };
  let mockToastService: { success: ReturnType<typeof vi.fn>; error: ReturnType<typeof vi.fn> };
  let mockRouter: { navigate: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    mockAuthService = {
      login: vi.fn(),
      checkAuthStatus: vi.fn().mockReturnValue(of(false))
    };
    mockToastService = {
      success: vi.fn(),
      error: vi.fn()
    };
    mockRouter = {
      navigate: vi.fn().mockResolvedValue(true)
    };

    await TestBed.configureTestingModule({
      imports: [LoginCms, ReactiveFormsModule],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: ToastService, useValue: mockToastService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginCms);
    component = fixture.componentInstance;
  });

  it('deve redirecionar para /admin no ngOnInit se o usuário já estiver autenticado', () => {
    mockAuthService.checkAuthStatus.mockReturnValue(of(true));
    fixture.detectChanges();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/admin']);
  });

  it('deve inicializar o formulário vazio e inválido', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(component.loginForm.valid).toBeFalsy();
    expect(component.loginForm.get('userCode')?.value).toBe('');
    expect(component.loginForm.get('password')?.value).toBe('');
  });

  it('deve alternar a visibilidade da senha com togglePassword()', () => {
    expect(component.showPassword).toBeFalsy();
    component.togglePassword();
    expect(component.showPassword).toBeTruthy();
    component.togglePassword();
    expect(component.showPassword).toBeFalsy();
  });

  it('não deve enviar login se o formulário for inválido', () => {
    component.onSubmit();
    expect(mockAuthService.login).not.toHaveBeenCalled();
    expect(component.loginForm.get('userCode')?.touched).toBeTruthy();
    expect(component.loginForm.get('password')?.touched).toBeTruthy();
  });

  it('deve realizar login com sucesso e navegar para /admin', async () => {
    mockAuthService.login.mockReturnValue(of({ token: 'jwt-token' }));

    component.loginForm.setValue({
      userCode: 'ADM-001',
      password: 'secretPassword123'
    });

    component.onSubmit();

    expect(component.isLoading).toBeFalsy(); // finalize executado
    expect(mockAuthService.login).toHaveBeenCalledWith('ADM-001', 'secretPassword123');
    expect(mockToastService.success).toHaveBeenCalledWith('Bem-vindo de volta ao painel!', 'Login Realizado');
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/admin']);
  });

  it('deve redirecionar para /admin/catalog se a rota /admin não navegar', async () => {
    mockAuthService.login.mockReturnValue(of({ token: 'jwt-token' }));
    mockRouter.navigate.mockResolvedValue(false);

    component.loginForm.setValue({
      userCode: 'ADM-001',
      password: 'secretPassword123'
    });

    component.onSubmit();
    await Promise.resolve(); // aguarda o .then()

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/admin/catalog']);
  });

  it('deve tratar erro 401 de credenciais inválidas', () => {
    mockAuthService.login.mockReturnValue(throwError(() => ({ status: 401 })));

    component.loginForm.setValue({
      userCode: 'ADM-001',
      password: 'wrongPassword'
    });

    component.onSubmit();

    expect(component.isLoading).toBeFalsy();
    expect(component.loginError).toBe('Código de acesso ou senha incorretos.');
    expect(mockToastService.error).toHaveBeenCalledWith('Verifique suas credenciais e tente novamente.', 'Erro de Acesso');
  });

  it('deve tratar erro 500 genérico de falha de conexão', () => {
    mockAuthService.login.mockReturnValue(throwError(() => ({ status: 500 })));

    component.loginForm.setValue({
      userCode: 'ADM-001',
      password: 'anyPassword'
    });

    component.onSubmit();

    expect(component.isLoading).toBeFalsy();
    expect(component.loginError).toBe('Ocorreu um erro ao tentar fazer login. Tente novamente mais tarde.');
    expect(mockToastService.error).toHaveBeenCalledWith('Erro de conexão com o servidor. Tente novamente.', 'Falha no Login');
  });

  it('deve tratar falha na promessa de navegação no catch', async () => {
    const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {});
    mockAuthService.login.mockReturnValue(of({ token: 'jwt-token' }));
    mockRouter.navigate.mockRejectedValue(new Error('Navigation cancelled'));

    component.loginForm.setValue({
      userCode: 'ADM-001',
      password: 'secretPassword123'
    });

    component.onSubmit();
    await new Promise(r => setTimeout(r, 10));

    expect(consoleSpy).toHaveBeenCalledWith('Falha na navegação pós-login:', expect.any(Error));
    consoleSpy.mockRestore();
  });

  it('deve desinscrever subscriptions no ngOnDestroy', () => {
    const unsubSpy = vi.spyOn((component as unknown as { subs: { unsubscribe: () => void } }).subs, 'unsubscribe');
    component.ngOnDestroy();
    expect(unsubSpy).toHaveBeenCalled();
  });
});
