import { Component, ChangeDetectionStrategy, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../../services/auth.service';
import { UserService } from '../../../../services/user.service';
import { ModalComponent } from '../../../../shared/components/modal/modal.component';
import { User, UserRole, UserStatus, RoleTranslations, StatusTranslations } from '../../../../models/user.model';
import { catchError, of } from 'rxjs';
import { SpringPage } from '../../../../models/page.model';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule, ModalComponent],
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class UsersComponent implements OnInit {
  authService = inject(AuthService);
  userService = inject(UserService);
  
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
  
  // Confirmação de exclusão simplificada (apenas nome)
  deleteUsernameConfirm = '';
  userToDelete: User | null = null;
  
  readonly RoleTranslations = RoleTranslations;
  readonly StatusTranslations = StatusTranslations;

  get currentUser(): User | null {
    return this.authService.currentUser;
  }

  get isOwner(): boolean {
    return this.authService.hasRole(['SUPER_ADMIN']);
  }
  
  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
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
    });
  }

  canEditUser(targetUser: User): boolean {
    if (this.isOwner) return true;
    return this.currentUser?.id === targetUser.id; // Usuário pode editar próprio perfil (ex: senha)
  }
  
  openCreateModal(): void {
    if (!this.isOwner) return;
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
    this.isCreateMode = false;
    this.editingUser = { ...user, password: '' }; // Não carrega a senha real
    this.showPassword = false;
    this.isModalOpen = true;
  }
  
  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }
  
  saveUser(): void {
    if (this.isCreateMode) {
      // Create user (Requer name, password, role, status)
      this.userService.create(this.editingUser).subscribe({
        next: () => {
          this.isModalOpen = false;
          this.loadUsers();
        },
        error: (err) => console.error('Erro ao criar usuário', err)
      });
    } else {
      // Update user
      const id = this.editingUser.id!;
      
      // Se informou senha nova, chama o endpoint de troca de senha
      if (this.editingUser.password && this.editingUser.password.trim() !== '') {
        this.userService.changePassword(id, this.editingUser.password).subscribe({
          error: (err) => console.error('Erro ao trocar senha', err)
        });
      }
      
      // Atualiza os dados (name, role, status)
      this.userService.updateData(id, this.editingUser).subscribe({
        next: () => {
          this.isModalOpen = false;
          this.loadUsers();
        },
        error: (err) => console.error('Erro ao atualizar usuário', err)
      });
    }
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
    
    this.userService.delete(this.userToDelete.id).subscribe({
      next: () => {
        this.isDeleteModalOpen = false;
        this.userToDelete = null;
        this.loadUsers();
      },
      error: (err) => console.error('Erro ao excluir usuário', err)
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
