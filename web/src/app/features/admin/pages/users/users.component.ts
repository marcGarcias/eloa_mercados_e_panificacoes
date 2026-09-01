import { Component, ChangeDetectionStrategy, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../../services/auth.service';
import { UserService } from '../../../../services/user.service';
import { ToastService } from '../../../../services/toast.service';
import { ModalComponent } from '../../../../shared/components/modal/modal.component';
import { User, UserRole, UserStatus, RoleTranslations, StatusTranslations } from '../../../../models/user.model';
import { catchError, of } from 'rxjs';
import { SpringPage } from '../../../../models/page.model';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule, ModalComponent],
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {
  authService = inject(AuthService);
  userService = inject(UserService);
  cdr = inject(ChangeDetectorRef);
  toastService = inject(ToastService);
  
  currentUser$ = this.authService.currentUser$;
  
  users: User[] = [];
  page = 0;
  size = 10;
  totalPages = 0;
  
  isModalOpen = false;
  isDeleteModalOpen = false;
  isCreateMode = false;
  
  editingUser: Partial<User> = {};
  showPassword = false;
  isLoading = false;
  errorMessage: string | null = null;
  firstName = '';
  lastName = '';
  
  // Confirmação de exclusão simplificada (apenas nome)
  deleteUsernameConfirm = '';
  userToDelete: User | null = null;
  
  readonly RoleTranslations = RoleTranslations;
  readonly StatusTranslations = StatusTranslations;

  sortField: keyof User | 'lastLoginAt' | null = null;
  sortDirection: 'asc' | 'desc' = 'asc';

  get currentUser(): User | null {
    return this.authService.currentUser;
  }

  get isOwner(): boolean {
    return this.authService.hasRole(['SUPER_ADMIN']);
  }

  sortBy(field: keyof User | 'lastLoginAt'): void {
    if (this.sortField === field) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortField = field;
      this.sortDirection = 'asc';
    }
    this.applySort();
    this.cdr.markForCheck();
  }

  private applySort(): void {
    if (!this.sortField) return;
    const field = this.sortField;
    const dir = this.sortDirection === 'asc' ? 1 : -1;
    this.users = [...this.users].sort((a, b) => {
      const aVal = a[field as keyof User] ?? '';
      const bVal = b[field as keyof User] ?? '';
      if (aVal < bVal) return -1 * dir;
      if (aVal > bVal) return 1 * dir;
      return 0;
    });
  }
  
  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        this.loadUsers();
      }
    });
  }

  loadUsers(): void {
    if (!this.isOwner) {
      if (this.currentUser) {
        this.users = [this.currentUser];
        this.totalPages = 1;
        this.cdr.markForCheck();
      }
      return;
    }

    this.isLoading = true;
    this.userService.getAll(this.page, this.size).pipe(
      catchError(() => {
        // Fallback em caso de erro (ex: rota GET /admin/users não implementada ainda)
        const emptyPage: SpringPage<User> = { content: [], totalElements: 0, totalPages: 0, number: 0, size: this.size };
        return of(emptyPage);
      })
    ).subscribe(pageData => {
      this.users = pageData.content;
      this.totalPages = pageData.totalPages;
      this.isLoading = false;
      this.cdr.markForCheck();
    });
  }

  canEditUser(targetUser: User): boolean {
    if (this.isOwner) return true;
    return this.currentUser?.id === targetUser.id; // Usuário pode editar próprio perfil (ex: senha)
  }
  
  openCreateModal(): void {
    if (!this.isOwner) return;
    this.errorMessage = null;
    this.firstName = '';
    this.lastName = '';
    this.isCreateMode = true;
    this.editingUser = {
      name: '',
      password: '',
      role: 'EDITOR',
      status: 'ACTIVE'
    };
    this.showPassword = false;
    this.isModalOpen = true;
  }

  openEditModal(user: User): void {
    if (!this.canEditUser(user)) return;
    this.errorMessage = null;
    this.isCreateMode = false;
    this.editingUser = { ...user, password: '' }; // Não carrega a senha real
    this.showPassword = false;
    this.isModalOpen = true;
  }
  
  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  validateLocalData(): boolean {
    this.errorMessage = null;
    
    if (this.isCreateMode) {
      const firstName = this.firstName.trim();
      const lastName = this.lastName.trim();
      
      if (!firstName) {
        this.errorMessage = 'O nome é obrigatório.';
        return false;
      }
      if (!lastName) {
        this.errorMessage = 'O sobrenome é obrigatório.';
        return false;
      }
      
      const fullName = `${firstName} ${lastName}`;
      if (fullName.length > 150) {
        this.errorMessage = 'A combinação de nome e sobrenome não pode exceder 150 caracteres.';
        return false;
      }
      
      const password = this.editingUser.password?.trim();
      if (!password) {
        this.errorMessage = 'A senha é obrigatória para novos usuários.';
        return false;
      }
    } else {
      const name = this.editingUser.name?.trim();
      if (!name) {
        this.errorMessage = 'O nome do usuário é obrigatório.';
        return false;
      }
      if (name.length > 150) {
        this.errorMessage = 'O nome do usuário não pode exceder 150 caracteres.';
        return false;
      }
    }
    
    return true;
  }

  translateErrorMessage(err: any): string {
    const rawMessage = err.error?.message;
    if (!rawMessage) {
      return 'Ocorreu um erro inesperado. Por favor, tente novamente.';
    }

    if (rawMessage.includes('New password cannot be the same as current password')) {
      return 'A nova senha não pode ser igual à senha atual.';
    }
    if (rawMessage.includes('User name cannot be empty')) {
      return 'O nome do usuário não pode ficar em branco.';
    }
    if (rawMessage.includes('User name exceeds maximum limit')) {
      return 'O nome do usuário não pode exceder 150 caracteres.';
    }
    if (rawMessage.includes('SUPER_ADMIN user. Only one owner is allowed')) {
      return 'Já existe um Proprietário cadastrado no sistema.';
    }
    if (rawMessage.includes('Creating a SUPER_ADMIN user is not allowed')) {
      return 'Não é permitido criar usuários com perfil de Proprietário.';
    }
    if (rawMessage.includes('Modifying the role to/from SUPER_ADMIN')) {
      return 'Não é permitido alterar ou promover usuários para a função de Proprietário.';
    }
    if (rawMessage.includes('User not found')) {
      return 'Usuário não encontrado.';
    }

    return rawMessage;
  }
  
  saveUser(): void {
    if (!this.validateLocalData()) {
      this.cdr.markForCheck();
      return;
    }

    this.isLoading = true;
    this.cdr.markForCheck();

    if (this.isCreateMode) {
      this.editingUser.name = `${this.firstName.trim()} ${this.lastName.trim()}`;
      const createdUserName = this.editingUser.name;
      this.userService.create(this.editingUser).subscribe({
        next: () => {
          this.isModalOpen = false;
          this.isLoading = false;
          this.toastService.success(`O usuário "${createdUserName}" foi criado com sucesso.`, 'Usuário Criado');
          this.loadUsers();
        },
        error: (err) => {
          this.errorMessage = this.translateErrorMessage(err);
          this.isLoading = false;
          this.cdr.markForCheck();
        }
      });
    } else {
      const id = this.editingUser.id!;
      const hasNewPassword = this.editingUser.password && this.editingUser.password.trim() !== '';

      if (hasNewPassword) {
        // Encadeamento sequencial: troca a senha primeiro
        this.userService.changePassword(id, this.editingUser.password!).subscribe({
          next: () => {
            // Se a senha foi alterada com sucesso, atualiza o restante dos dados
            this.updateUserDataOnly(id);
          },
          error: (err) => {
            this.errorMessage = this.translateErrorMessage(err);
            this.isLoading = false;
            this.cdr.markForCheck();
          }
        });
      } else {
        this.updateUserDataOnly(id);
      }
    }
  }

  private updateUserDataOnly(id: string): void {
    const updatedUserName = this.editingUser.name;
    this.userService.updateData(id, this.editingUser).subscribe({
      next: () => {
        this.isModalOpen = false;
        this.isLoading = false;
        this.toastService.success(`Os dados do usuário "${updatedUserName}" foram atualizados.`, 'Usuário Atualizado');
        this.loadUsers();
      },
      error: (err) => {
        this.errorMessage = this.translateErrorMessage(err);
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }
  
  openDeleteModal(user: Partial<User>): void {
    if (!this.isOwner || user.id === this.currentUser?.id) return;
    this.userToDelete = user as User;
    this.deleteUsernameConfirm = '';
    this.isDeleteModalOpen = true;
    this.isModalOpen = false; // Fecha a modal de edição se estiver aberta
  }
  
  confirmDelete(): void {
    if (this.deleteUsernameConfirm !== this.userToDelete?.name) return;
    if (!this.userToDelete?.id) return;
    
    this.isLoading = true;
    this.cdr.markForCheck();
    
    const deletedUserName = this.userToDelete.name;
    
    this.userService.delete(this.userToDelete.id).subscribe({
      next: () => {
        this.isDeleteModalOpen = false;
        this.isLoading = false;
        this.toastService.success(`O usuário "${deletedUserName}" foi excluído com sucesso.`, 'Usuário Excluído');
        this.userToDelete = null;
        this.loadUsers();
      },
      error: (err) => {
        this.isLoading = false;
        const errorMsg = this.translateErrorMessage(err);
        this.toastService.error(errorMsg, 'Erro ao Excluir');
        this.cdr.markForCheck();
      }
    });
  }
  
  nextPage(): void {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadUsers();
    }
  }
  
  prevPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadUsers();
    }
  }
}
