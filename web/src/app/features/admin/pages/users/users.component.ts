import { Component, ChangeDetectionStrategy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../../core/services/auth.service';
import { ModalComponent } from '../../../../shared/components/modal/modal.component';
import { User, Role } from '../../../../core/models/user.model';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule, ModalComponent],
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UsersComponent {
  authService = inject(AuthService);
  currentUser = this.authService.currentUser;
  
  users: User[] = []; // In a real scenario, this comes from a service
  
  isEditModalOpen = false;
  isDeleteModalOpen = false;
  
  editingUser: Partial<User> = {};
  showPassword = false;
  
  // Two factor delete
  deleteUsernameConfirm = '';
  deleteOwnerPasswordConfirm = '';
  userToDelete: User | null = null;
  
  get isOwner() {
    return this.currentUser?.role === 'Owner';
  }
  
  canEditUser(targetUser: User) {
    if (this.isOwner) return true;
    return this.currentUser?.id === targetUser.id; // Any user can edit their own profile
  }
  
  canEditAttributes(targetUser: User) {
    // Only owner can edit attributes of OTHER users.
    // However, even the owner cannot edit their OWN status/role.
    return this.isOwner;
  }
  
  openEditModal(user: User) {
    if (!this.canEditUser(user)) return;
    this.editingUser = { ...user };
    this.showPassword = false;
    this.isEditModalOpen = true;
  }
  
  togglePassword() {
    this.showPassword = !this.showPassword;
  }
  
  saveUser() {
    // Save logic goes here
    this.isEditModalOpen = false;
  }
  
  openDeleteModal(user: Partial<User>) {
    if (!this.isOwner || user.id === this.currentUser?.id) return;
    this.userToDelete = user as User;
    this.deleteUsernameConfirm = '';
    this.deleteOwnerPasswordConfirm = '';
    this.isDeleteModalOpen = true;
  }
  
  confirmDelete() {
    if (this.deleteUsernameConfirm !== this.userToDelete?.name) return;
    if (!this.deleteOwnerPasswordConfirm) return; // Must validate password via API
    
    // Delete logic goes here
    this.isDeleteModalOpen = false;
    this.userToDelete = null;
  }
}
