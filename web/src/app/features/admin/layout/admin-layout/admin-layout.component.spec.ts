import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminLayoutComponent } from './admin-layout.component';
import { AuthService } from '../../../../services/auth.service';
import { Router, provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { User } from '../../../../models/user.model';

describe('AdminLayoutComponent', () => {
  let component: AdminLayoutComponent;
  let fixture: ComponentFixture<AdminLayoutComponent>;
  let mockAuthService: any;
  let router: Router;

  const mockUser: User = {
    id: 'u-1',
    name: 'Admin Teste',
    userCode: 'ADM-01',
    role: 'SUPER_ADMIN',
    status: 'ACTIVE'
  };

  beforeEach(async () => {
    mockAuthService = {
      currentUser$: of(mockUser),
      currentUser: mockUser,
      logout: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [AdminLayoutComponent],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: mockAuthService }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);

    fixture = TestBed.createComponent(AdminLayoutComponent);
    component = fixture.componentInstance;
  });

  it('deve adicionar a classe admin-layout no body ao inicializar e remover ao destruir', () => {
    component.ngOnInit();
    expect(document.body.classList.contains('admin-layout')).toBeTruthy();

    component.ngOnDestroy();
    expect(document.body.classList.contains('admin-layout')).toBeFalsy();
  });

  it('deve deslogar e redirecionar para /login-cms no logout', () => {
    component.logout();
    expect(mockAuthService.logout).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/login-cms']);
  });
});
