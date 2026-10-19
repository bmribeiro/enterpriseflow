
import {
  Component,
  OnInit,
  inject,
  signal
} from '@angular/core';

import { RouterLink } from '@angular/router';
import { AuthService } from '../../../../core/auth/auth.service';

@Component({
  selector: 'app-callback',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './callback.component.html',
  styleUrl: './callback.component.scss'
})
export class CallbackComponent implements OnInit {

  private readonly authService = inject(AuthService);

  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    void this.authService.completeLogin()
      .catch(() => {
        this.error.set(
          'Não foi possível concluir a autenticação.'
        );
      });
  }
}
