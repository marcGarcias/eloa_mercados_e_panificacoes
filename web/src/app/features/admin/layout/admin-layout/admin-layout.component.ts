import { Component, ChangeDetectionStrategy, inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../../services/auth.service';
import { ModalCategoriaComponent } from '../../../../shared/modal-categoria/modal-categoria.component';
import { RoleTranslations } from '../../../../models/user.model';

// ModalProdutoComponent removido do layout global:
// agora e controlado diretamente pelo CatalogoComponent.

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, ModalCategoriaComponent],
  templateUrl: './admin-layout.component.html',
  styleUrls: ['./admin-layout.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AdminLayoutComponent implements OnInit, OnDestroy {
  authService = inject(AuthService);
  router = inject(Router);
  
  readonly RoleTranslations = RoleTranslations;

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
