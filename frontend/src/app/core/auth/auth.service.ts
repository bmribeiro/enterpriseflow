
import {
  Injectable,
  computed,
  inject,
  signal
} from '@angular/core';

import { Router } from '@angular/router';

import {
  User,
  UserManager,
  WebStorageStateStore
} from 'oidc-client-ts';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly router = inject(Router);

  private readonly userManager = new UserManager({
    authority: 'http://localhost:9000',
    client_id: 'enterpriseflow-projects',
    redirect_uri: `${window.location.origin}/auth/callback`,
    response_type: 'code',
    scope: 'openid profile projects.read projects.write',

    // Nesta fase não há renovação automática.
    automaticSilentRenew: false,
    loadUserInfo: false,

    userStore: new WebStorageStateStore({
      store: sessionStorage
    }),

    stateStore: new WebStorageStateStore({
      store: sessionStorage
    })
  });

  private readonly currentUser = signal<User | null>(null);

  readonly isAuthenticated = computed(() => {
    const user = this.currentUser();
    return user !== null && !user.expired;
  });

  constructor() {
    this.userManager.events.addAccessTokenExpired(() => {
      this.currentUser.set(null);
    });
  }

  async restoreSession(): Promise<void> {
    const user = await this.userManager.getUser();

    this.currentUser.set(
      user && !user.expired ? user : null
    );
  }

  async login(): Promise<void> {
    await this.userManager.signinRedirect();
  }

  async completeLogin(): Promise<void> {
    const user =
      await this.userManager.signinRedirectCallback();

    this.currentUser.set(user);

    await this.router.navigateByUrl('/dashboard', {
      replaceUrl: true
    });
  }

  
async getAccessToken(): Promise<string | null> {
  const user = await this.userManager.getUser();

  if (!user || user.expired) {
    this.currentUser.set(null);
    return null;
  }

  this.currentUser.set(user);
  return user.access_token;
}

}
