import { Component, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthenticationService } from '../../core/Auth/services/authentication-service';
import { isPlatformBrowser, NgClass } from '@angular/common';

@Component({
  selector: 'app-nav-bar',
  imports: [RouterLink, NgClass],
  templateUrl: './nav-bar.html',
  styleUrl: './nav-bar.css',
})
export class NavBar {
  platform = inject(PLATFORM_ID);
  router = inject(Router);
  authService = inject(AuthenticationService);

  menuOpen = signal(false);
  toggleMenu() {
    this.menuOpen.update((v) => !v);
  }

  // dropdown states //
  desktopDropDown = signal(false);
  mobileDropDown = signal(false);
  toggleDesktop() {
    this.desktopDropDown.set(!this.desktopDropDown());
  }
  toggleMobile() {
    this.mobileDropDown.set(!this.mobileDropDown());
  }
  // ------------------------------ //
}
