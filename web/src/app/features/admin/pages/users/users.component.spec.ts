import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UsersComponent } from './users.component';
import { AuthService } from '../../../../services/auth.service';
import { UserService } from '../../../../services/user.service';
import { ToastService } from '../../../../services/toast.service';
import { of, throwError } from 'rxjs';
import { User, UserRole, UserStatus } from '../../../../models/user.model';

describe('UsersComponent (Admin)', () => {
  let component: UsersComponent;
  let fixture: ComponentFixture<UsersComponent>;
  let mockAuthService: any;
  let mockUserService: any;
  let mockToastService: any;

  const mockOwnerUser: User = {
    id: 'u-admin',
    name: 'Proprietário',
    userCode: '1001',
    role: 'SUPER_ADMIN',
    status: 'ACTIVE'
  };

  const mockEditorUser: User = {
    id: 'u-editor',
    name: 'Editor Um',
    userCode: '1002',
    role: 'EDITOR',
    status: 'ACTIVE'
  };

  const mockPageData = {
    content: [mockOwnerUser, mockEditorUser],
    totalElements: 2,
    totalPages: 1,
    number: 0,
    size: 10
  };

  beforeEach(async () => {
    mockAuthService = {
      currentUser$: of(mockOwnerUser),
      currentUser: mockOwnerUser,
      hasRole: vi.fn().mockImplementation((roles: string[]) => roles.includes('SUPER_ADMIN'))
    };

    mockUserService = {
      getAll: vi.fn().mockReturnValue(of(mockPageData)),
      create: vi.fn().mockReturnValue(of(mockEditorUser)),
      updateData: vi.fn().mockReturnValue(of(mockEditorUser)),
      changePassword: vi.fn().mockReturnValue(of(null)),
      delete: vi.fn().mockReturnValue(of(null))
    };

    mockToastService = {
      success: vi.fn(),
      error: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [UsersComponent],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: UserService, useValue: mockUserService },
        { provide: ToastService, useValue: mockToastService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(UsersComponent);
    component = fixture.componentInstance;
  });

  it('deve carregar todos os usuários se o usuário logado for SUPER_ADMIN', () => {
    fixture.detectChanges();

    expect(component).toBeTruthy();
    expect(mockUserService.getAll).toHaveBeenCalledWith(0, 10);
    expect(component.users.length).toBe(2);
  });

  it('deve carregar apenas o próprio usuário se não for proprietário', () => {
    mockAuthService.hasRole.mockReturnValue(false);
    mockAuthService.currentUser = mockEditorUser;

    component.loadUsers();

    expect(component.users).toEqual([mockEditorUser]);
    expect(component.totalPages).toBe(1);
  });

  it('deve filtrar usuários por busca de texto e por papel', () => {
    fixture.detectChanges();

    component.searchQuery = 'Editor';
    expect(component.filteredUsers.length).toBe(1);
    expect(component.filteredUsers[0].name).toBe('Editor Um');

    component.searchQuery = '1001';
    expect(component.filteredUsers.length).toBe(1);
    expect(component.filteredUsers[0].name).toBe('Proprietário');

    // Desmarcar papel EDITOR
    const dummyEvent = { stopPropagation: vi.fn() } as any;
    component.toggleRoleFilter('EDITOR', dummyEvent);
    expect(component.isRoleFilterActive).toBeTruthy();

    component.searchQuery = '';
    expect(component.filteredUsers.length).toBe(1);
    expect(component.filteredUsers[0].role).toBe('SUPER_ADMIN');

    component.clearRoleFilter();
    expect(component.isRoleFilterActive).toBeFalsy();
  });

  it('deve ordenar usuários por diferentes campos', () => {
    fixture.detectChanges();

    component.sortBy('name');
    expect(component.sortField).toBe('name');
    expect(component.sortDirection).toBe('asc');

    // Inverter direção
    component.sortBy('name');
    expect(component.sortDirection).toBe('desc');

    component.sortBy('userCode');
    expect(component.sortField).toBe('userCode');

    component.sortBy('status');
    expect(component.sortField).toBe('status');
  });

  it('deve abrir modal de criação e validar dados locais', () => {
    component.openCreateModal();
    expect(component.isModalOpen).toBeTruthy();
    expect(component.isCreateMode).toBeTruthy();

    // Sem nome
    component.firstName = '';
    component.lastName = 'Silva';
    expect(component.validateLocalData()).toBeFalsy();
    expect(component.errorMessage).toBe('O nome é obrigatório.');

    // Sem sobrenome
    component.firstName = 'João';
    component.lastName = '';
    expect(component.validateLocalData()).toBeFalsy();
    expect(component.errorMessage).toBe('O sobrenome é obrigatório.');

    // Sem senha
    component.lastName = 'Silva';
    component.editingUser.password = '';
    expect(component.validateLocalData()).toBeFalsy();
    expect(component.errorMessage).toBe('A senha é obrigatória para novos usuários.');

    // Válido
    component.editingUser.password = 'StrongPass123!';
    expect(component.validateLocalData()).toBeTruthy();
  });

  it('deve rejeitar senha fraca ao validar formulário', () => {
    component.openCreateModal();
    component.firstName = 'Maria';
    component.lastName = 'Souza';
    component.editingUser = {
      password: 'fraca',
      role: 'EDITOR',
      status: 'ACTIVE'
    };

    expect(component.validateLocalData()).toBeFalsy();
    expect(component.errorMessage).toContain('A senha deve conter no mínimo 8 caracteres');
  });

  it('deve salvar novo usuário com sucesso', () => {
    component.openCreateModal();
    component.firstName = 'Maria';
    component.lastName = 'Souza';
    component.editingUser = {
      password: 'PassWord123!',
      role: 'EDITOR',
      status: 'ACTIVE'
    };

    component.saveUser();

    expect(mockUserService.create).toHaveBeenCalledWith(expect.objectContaining({
      name: 'Maria Souza',
      role: 'EDITOR'
    }));
    expect(mockToastService.success).toHaveBeenCalledWith(expect.stringContaining('Maria Souza'), 'Usuário Criado');
    expect(component.isModalOpen).toBeFalsy();
  });

  it('deve editar usuário existente com alteração de senha', () => {
    component.openEditModal(mockEditorUser);
    expect(component.isModalOpen).toBeTruthy();
    expect(component.isCreateMode).toBeFalsy();

    component.editingUser.password = 'NovaSenhaSegura123!';
    component.saveUser();

    expect(mockUserService.changePassword).toHaveBeenCalledWith('u-editor', 'NovaSenhaSegura123!', undefined, undefined);
    expect(mockUserService.updateData).toHaveBeenCalledWith('u-editor', expect.any(Object));
    expect(mockToastService.success).toHaveBeenCalledWith(expect.any(String), 'Usuário Atualizado');
  });

  it('deve exigir CPF e código de acesso ao alterar a senha de usuário SUPER_ADMIN', () => {
    component.openEditModal(mockOwnerUser);
    expect(component.isModalOpen).toBeTruthy();

    component.editingUser.password = 'NovaSenhaOwner123!';
    component.ownerCpf = '';
    component.ownerAccessKey = '';

    expect(component.validateLocalData()).toBeFalsy();
    expect(component.errorMessage).toBe('O CPF do Proprietário é obrigatório para alterar a senha.');

    component.ownerCpf = '123.456.789-09';
    expect(component.validateLocalData()).toBeFalsy();
    expect(component.errorMessage).toBe('O Código de Acesso é obrigatório para alterar a senha.');

    component.ownerAccessKey = 'AAA-111-BBB-!';
    expect(component.validateLocalData()).toBeTruthy();

    component.saveUser();
    expect(mockUserService.changePassword).toHaveBeenCalledWith(
      'u-admin',
      'NovaSenhaOwner123!',
      'AAA-111-BBB-!',
      '123.456.789-09'
    );
  });

  it('deve traduzir mensagens de erro conhecidas da API', () => {
    expect(component.translateErrorMessage({ error: { message: 'New password cannot be the same as current password' } }))
      .toBe('A nova senha não pode ser igual à senha atual.');

    expect(component.translateErrorMessage({ error: { message: 'CPF de setup inválido.' } }))
      .toBe('CPF do Proprietário inválido.');

    expect(component.translateErrorMessage({ error: { message: 'Código de acesso incorreto.' } }))
      .toBe('Código de acesso incorreto.');

    expect(component.translateErrorMessage({ error: { message: 'SUPER_ADMIN user. Only one owner is allowed' } }))
      .toBe('Já existe um Proprietário cadastrado no sistema.');

    expect(component.translateErrorMessage({ error: { message: 'Creating a SUPER_ADMIN user is not allowed' } }))
      .toBe('Não é permitido criar usuários com perfil de Proprietário.');

    expect(component.translateErrorMessage({ error: { message: 'Modifying the role to/from SUPER_ADMIN' } }))
      .toBe('Não é permitido alterar ou promover usuários para a função de Proprietário.');

    expect(component.translateErrorMessage({ error: { message: 'User not found' } }))
      .toBe('Usuário não encontrado.');

    expect(component.translateErrorMessage({ error: { message: 'Erro desconhecido' } }))
      .toBe('Erro desconhecido');
  });

  it('deve tratar erro ao atualizar dados de usuário e exibir mensagem', () => {
    mockUserService.updateData.mockReturnValue(throwError(() => ({ error: { message: 'User name cannot be empty' } })));
    component.openEditModal(mockEditorUser);

    component.saveUser();

    expect(component.errorMessage).toBe('O nome do usuário não pode ficar em branco.');
  });

  it('deve controlar modal de exclusão e confirmar deleção com match exato de nome', () => {
    component.openDeleteModal(mockEditorUser);
    expect(component.isDeleteModalOpen).toBeTruthy();

    // Confirmação incorreta -> não deleta
    component.deleteUsernameConfirm = 'Nome Errado';
    component.confirmDelete();
    expect(mockUserService.delete).not.toHaveBeenCalled();

    // Confirmação correta com erro da API
    mockUserService.delete.mockReturnValue(throwError(() => ({ error: { message: 'Erro ao deletar' } })));
    component.deleteUsernameConfirm = 'Editor Um';
    component.confirmDelete();
    expect(mockToastService.error).toHaveBeenCalledWith('Erro ao deletar', 'Erro ao Excluir');

    // Confirmação correta com sucesso
    mockUserService.delete.mockReturnValue(of(null));
    component.deleteUsernameConfirm = 'Editor Um';
    component.confirmDelete();
    expect(mockToastService.success).toHaveBeenCalledWith(expect.any(String), 'Usuário Excluído');
    expect(component.isDeleteModalOpen).toBeFalsy();
  });

  it('não deve permitir abrir modal de exclusão para o próprio usuário logado', () => {
    component.openDeleteModal(mockOwnerUser);
    expect(component.isDeleteModalOpen).toBeFalsy();
  });

  it('deve alternar páginas com nextPage e prevPage', () => {
    component.totalPages = 3;
    component.page = 0;

    component.nextPage();
    expect(component.page).toBe(1);

    component.prevPage();
    expect(component.page).toBe(0);
  });

  it('deve alternar showPassword com togglePassword', () => {
    expect(component.showPassword).toBeFalsy();
    component.togglePassword();
    expect(component.showPassword).toBeTruthy();
  });

  it('deve cancelar inscrições no ngOnDestroy', () => {
    fixture.detectChanges();
    const unsubSpy = vi.spyOn((component as unknown as { subs: { unsubscribe: () => void } }).subs, 'unsubscribe');
    component.ngOnDestroy();
    expect(unsubSpy).toHaveBeenCalled();
  });
});
