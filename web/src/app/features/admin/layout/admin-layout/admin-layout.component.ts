import { Component, ChangeDetectionStrategy, inject, OnInit, OnDestroy, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../../services/auth.service';
import { ModalCategoriaComponent } from '../../../../shared/modal-categoria/modal-categoria.component';
import { RoleTranslations } from '../../../../models/user.model';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, ModalCategoriaComponent],
  templateUrl: './admin-layout.component.html',
  styleUrls: ['./admin-layout.component.css', './admin-theme.css'],
  encapsulation: ViewEncapsulation.None,
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

