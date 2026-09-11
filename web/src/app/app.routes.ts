import { Routes } from '@angular/router';
import { authGuard } from './core/security/auth.guard';
import { Home } from './components/home/home';

export const routes: Routes = [
    {
        path: '',
        component: Home
    },
    {
        path: 'admin',
        canActivate: [authGuard],
        loadChildren: () => import('./features/admin/admin.routes').then(m => m.ADMIN_ROUTES)
    },
    {
        path: 'setup',
        loadComponent: () => import('./features/setup/setup').then(m => m.Setup)
    },
    {
        path: 'login-cms',
        loadComponent: () => import('./components/login-cms/login-cms').then(m => m.LoginCms)
    }
];
