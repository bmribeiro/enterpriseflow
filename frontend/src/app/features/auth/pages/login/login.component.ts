
import { Component, inject, signal } from '@angular/core';
import { AuthService } from '../../../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  private readonly authService = inject(AuthService);

  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  
async login(): Promise<void> {
  this.loading.set(true);
  this.error.set(null);

  try {
    console.log('[OIDC] A iniciar autenticação...');

    await this.authService.login();

  } catch (error: unknown) {
    console.error('[OIDC] Falha ao iniciar login:', error);

    this.error.set(
      error instanceof Error
        ? error.message
        : 'Erro desconhecido ao iniciar autenticação.'
    );

    this.loading.set(false);
  }
}

}
