import { Component, ChangeDetectionStrategy, inject, OnInit, OnDestroy, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../../services/auth.service';
import { ModalCategoriaComponent } from '../../../../shared/modal-categoria/modal-categoria.component';
import { RoleTranslations } from '../../../../models/user.model';
import { BehaviorSubject } from 'rxjs';

// ModalProdutoComponent removido do layout global:
// agora e controlado diretamente pelo CatalogoComponent.

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, ModalCategoriaComponent],
  templateUrl: './admin-layout.component.html',
  styleUrls: ['./admin-layout.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AdminLayoutComponent implements OnInit, OnDestroy {
  authService = inject(AuthService);
  router = inject(Router);

  readonly RoleTranslations = RoleTranslations;

  private readonly isDropdownOpenSubject = new BehaviorSubject<boolean>(false);
  readonly isDropdownOpen$ = this.isDropdownOpenSubject.asObservable();

  toggleDropdown(): void {
    this.isDropdownOpenSubject.next(!this.isDropdownOpenSubject.value);
  }

  closeDropdown(): void {
    this.isDropdownOpenSubject.next(false);
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.account-actions')) {
      this.closeDropdown();
    }
  }

  ngOnInit(): void {
    document.body.classList.add('admin-layout');
  }

  ngOnDestroy(): void {
    document.body.classList.remove('admin-layout');
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login-cms']);
  }
}
