
import { inject } from '@angular/core';
import {
  HttpInterceptorFn
} from '@angular/common/http';

import { from, switchMap } from 'rxjs';
import { AuthService } from './auth.service';

export const authInterceptor: HttpInterceptorFn =
  (req, next) => {

    // Aplicar apenas aos endpoints da API EnterpriseFlow.
    if (!req.url.startsWith('/api/v1/')) {
      return next(req);
    }

    const authService = inject(AuthService);

    return from(authService.getAccessToken()).pipe(
      switchMap(token => {

        if (!token) {
          return next(req);
        }

        const authenticatedRequest = req.clone({
          setHeaders: {
            Authorization: `Bearer ${token}`
          }
        });

        return next(authenticatedRequest);
      })
    );
  };
