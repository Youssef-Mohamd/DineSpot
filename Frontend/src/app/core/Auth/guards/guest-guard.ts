import { isPlatformBrowser } from '@angular/common';
import { inject, PLATFORM_ID } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const guestGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const platform = inject(PLATFORM_ID);

  if (isPlatformBrowser(platform)) {
    const token = localStorage.getItem('userToken');
    const userRole = localStorage.getItem('userRole');

    if (token && userRole) {
      return true;
    }

    return router.parseUrl('/login');
  }

  return true;
};