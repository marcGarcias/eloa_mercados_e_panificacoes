import { Routes } from '@angular/router';
import { authGuard } from './core/security/auth.guard';

export const routes: Routes = [
    {
        path: 'admin',
        canActivate: [authGuard],
        loadChildren: () => import('./features/admin/admin.routes').then(m => m.ADMIN_ROUTES)
    },

    {
        path: '',
        loadComponent: () => import('./components/home/home').then(m => m.Home)
    },
    {
        path: 'login-cms',
        loadComponent: () => import('./components/login-cms/login-cms').then(m => m.LoginCms)
    }
];
