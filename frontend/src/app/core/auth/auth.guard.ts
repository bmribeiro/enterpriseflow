
import { inject } from '@angular/core';
import {
  CanActivateFn,
  Router
} from '@angular/router';

import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = async () => {

  const authService = inject(AuthService);
  const router = inject(Router);

  try {
    await authService.restoreSession();

    if (authService.isAuthenticated()) {
      return true;
    }
  } catch {
    // Sessão inválida ou indisponível.
  }

  return router.createUrlTree(['/login']);
};
