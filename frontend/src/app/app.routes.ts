
import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'login',
    title: 'Login | EnterpriseFlow',
    loadComponent: () =>
      import('./features/auth/pages/login/login.component')
        .then(m => m.LoginComponent)
  },
  {
    path: '',
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
