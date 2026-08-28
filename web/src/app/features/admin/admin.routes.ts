import { Routes } from '@angular/router';
import { AdminLayoutComponent } from './layout/admin-layout/admin-layout.component';
import { authGuard, authMatchGuard } from '../../core/security/auth.guard';

export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    component: AdminLayoutComponent,
    canActivate: [authGuard], // Protege todas as rotas filhas para exigir login
    children: [
      {
        path: 'home',
        loadComponent: () => import('./pages/content/content.component').then(m => m.ContentComponent),
        canMatch: [authMatchGuard],
        data: { roles: ['SUPER_ADMIN', 'ADMIN'] }
      },
      {
        path: 'catalog',
        loadComponent: () => import('./pages/catalog/catalog.component').then(m => m.CatalogComponent)
      },
      {
        path: 'users',
        loadComponent: () => import('./pages/users/users.component').then(m => m.UsersComponent),
        canMatch: [authMatchGuard],
        data: { roles: ['SUPER_ADMIN'] }
      },
      {
        path: '',
        redirectTo: 'catalog',
        pathMatch: 'full'
      }
    ]
  }
];
