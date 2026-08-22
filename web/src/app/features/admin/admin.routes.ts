import { Routes } from '@angular/router';
import { AdminLayoutComponent } from './layout/admin-layout/admin-layout.component';

export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    component: AdminLayoutComponent,
    children: [
      {
        path: 'home',
        loadComponent: () => import('../../components/conteudo/conteudo.component').then(m => m.ConteudoComponent)
      },
      {
        path: 'catalog',
        loadComponent: () => import('../../components/catalogo/catalogo.component').then(m => m.CatalogoComponent)
      },
      {
        path: 'users',
        loadComponent: () => import('../../components/usuarios/usuarios.component').then(m => m.UsuariosComponent)
      },
      {
        path: '',
        redirectTo: 'catalog',
        pathMatch: 'full'
      }
    ]
  }
];
