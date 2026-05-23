import { isPlatformBrowser } from '@angular/common';
import { inject, PLATFORM_ID } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

/** Any logged-in user can open Management (no extra session refresh). */
export const managementGuard: CanActivateFn = () => {
  const router = inject(Router);
  const platform = inject(PLATFORM_ID);

  if (isPlatformBrowser(platform)) {
    if (localStorage.getItem('userToken')) {
      return true;
    }
    return router.parseUrl('/login');
  }

  return true;
};
