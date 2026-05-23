import { inject, Injectable, PLATFORM_ID, signal } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';
import { UserInterface } from '../../Models/user-interface';
import { Router } from '@angular/router';

export type AppRole = 'ADMIN' | 'CUSTOMER';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {
  private myHttp = inject(HttpClient);
  private baseUrl = environment.baseUrl;
  private platformId = inject(PLATFORM_ID);
  private router = inject(Router);

  isLogged = signal<boolean>(false);
  userData = signal<UserInterface | null>(null);
  userRole = signal<AppRole | null>(null);
  signingOut = signal<boolean>(false);

  constructor() {
    if (isPlatformBrowser(this.platformId)) {
      const token = localStorage.getItem('userToken');
      const user = localStorage.getItem('userData');
      const role = localStorage.getItem('userRole') as AppRole | null;

      if (token) {
        this.isLogged.set(true);
      }

      if (user) {
        this.userData.set(JSON.parse(user));
      }

      if (role === 'ADMIN' || role === 'CUSTOMER') {
        this.userRole.set(role);
      }
    }
  }

  register(form: {
    fullName: string;
    email: string;
    password: string;
    phone?: string;
  }): Observable<UserInterface> {
    return this.myHttp.post<UserInterface>(`${this.baseUrl}/auth/register`, form);
  }

  login(form: { email: string; password: string }): Observable<UserInterface> {
    return this.myHttp.post<UserInterface>(`${this.baseUrl}/auth/login`, form);
  }

  /** Reload JWT from database (fixes 403 on /api/admin after role change). */
  refreshToken(): Observable<UserInterface> {
    return this.myHttp.post<UserInterface>(`${this.baseUrl}/auth/refresh-token`, {});
  }

  getMe(): Observable<{ role: string; email: string; fullName: string }> {
    return this.myHttp.get<{ role: string; email: string; fullName: string }>(`${this.baseUrl}/auth/me`);
  }

  setSession(res: UserInterface) {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('userToken', res.token);
      localStorage.setItem('userRole', res.role);
      localStorage.setItem('userData', JSON.stringify(res));
    }

    this.isLogged.set(true);
    this.userData.set(res);

    if (res.role === 'ADMIN' || res.role === 'CUSTOMER') {
      this.userRole.set(res.role);
    }
  }

  isAdmin(): boolean {
    return this.userRole() === 'ADMIN';
  }

  isCustomer(): boolean {
    return this.userRole() === 'CUSTOMER';
  }

  signOut() {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('userToken');
      localStorage.removeItem('userData');
      localStorage.removeItem('userRole');
    }

    this.signingOut.set(true);

    setTimeout(() => {
      this.signingOut.set(false);
      this.isLogged.set(false);
      this.userData.set(null);
      this.userRole.set(null);
      this.router.navigate(['/home']);
    }, 800);
  }
}
