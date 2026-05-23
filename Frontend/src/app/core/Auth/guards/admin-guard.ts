import { isPlatformBrowser } from '@angular/common';
import { inject, PLATFORM_ID } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const adminGuard: CanActivateFn = () => {
  const router = inject(Router);
  const platform = inject(PLATFORM_ID);

  if (isPlatformBrowser(platform)) {
    const token = localStorage.getItem('userToken');
    const role = localStorage.getItem('userRole');

    if (token && role === 'ADMIN') {
      return true;
    }

    return router.parseUrl('/login');
  }

  return true;
};
