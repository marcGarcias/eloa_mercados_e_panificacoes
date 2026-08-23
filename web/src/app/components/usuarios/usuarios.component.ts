import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';
import { ModalUsuarioComponent } from '../../shared/modal-usuario/modal-usuario.component';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [CommonModule, ModalUsuarioComponent],
  templateUrl: './usuarios.component.html',
  styleUrls: ['./usuarios.component.css']
})
export class UsuariosComponent implements OnInit {
  users: User[] = [];
  visiblePasswords: Set<string> = new Set();
  userToDelete: User | null = null;
  isUserModalOpen = false;
  userToEdit: User | null = null;

  constructor(private userService: UserService) {}

  ngOnInit() {
    this.userService.getAll().subscribe(data => {
      this.users = data;
    });
  }
  openDeleteConfirm(user: User) {
    this.userToDelete = user;
  }

  cancelDelete() {
    this.userToDelete = null;
  }

  deleteUser() {
    if (this.userToDelete) {
      this.users = this.users.filter(u => u.code !== this.userToDelete!.code);
      this.userToDelete = null;
    }
  }

  openCreateModal() {
    this.userToEdit = null;
    this.isUserModalOpen = true;
  }

  openEditModal(user: User) {
    this.userToEdit = user;
    this.isUserModalOpen = true;
  }

  closeUserModal() {
    this.isUserModalOpen = false;
    this.userToEdit = null;
  }

  onUserSaved(userData: Partial<User>) {
    if (this.userToEdit) {
      this.userToEdit.name = userData.name!;
      this.userToEdit.role = userData.role! as any;
      this.userToEdit.password = userData.password!;
    } else {
      const newCode = (1000 + this.users.length + 1).toString();
      const newUser = {
        name: userData.name!,
        code: newCode,
        role: userData.role! as any,
        password: userData.password!,
        status: 'ativo' as const,
        access: 'Nunca'
      };
      this.users = [...this.users, newUser];
    }
    this.closeUserModal();
  }

  togglePasswordVisibility(code: string) {
    if (this.visiblePasswords.has(code)) {
      this.visiblePasswords.delete(code);
    } else {
      this.visiblePasswords.add(code);
    }
  }
}
