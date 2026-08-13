import { Routes } from '@angular/router';

export const routes: Routes = [
    {
        path: '',
        loadComponent: () => import('./components/home/home').then(m => m.Home)
    },
    {
        path: 'login-cms',
        loadComponent: () => import('./components/login-cms/login-cms').then(m => m.LoginCms)
    }
];
