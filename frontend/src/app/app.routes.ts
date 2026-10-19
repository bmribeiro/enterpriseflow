
import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    title: 'Login | EnterpriseFlow',
    loadComponent: () =>
      import('./features/auth/pages/login/login.component')
        .then(m => m.LoginComponent)
  },
  {
    path: 'auth/callback',
    title: 'Autenticação | EnterpriseFlow',
    loadComponent: () =>
      import('./features/auth/pages/callback/callback.component')
        .then(m => m.CallbackComponent)
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./core/layout/layout.component')
        .then(m => m.LayoutComponent),
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'dashboard'
      },
      {
        path: 'dashboard',
        title: 'Dashboard | EnterpriseFlow',
        loadComponent: () =>
          import('./features/dashboard/pages/dashboard/dashboard.component')
            .then(m => m.DashboardComponent)
      },
      {
        path: 'tasks',
        title: 'Tarefas | EnterpriseFlow',
        loadComponent: () =>
          import('./features/tasks/pages/task-list/task-list.component')
            .then(m => m.TaskListComponent)
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'dashboard'
  }
];
