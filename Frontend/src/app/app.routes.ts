import { Routes } from '@angular/router';
import { adminGuard } from './core/Auth/guards/admin-guard';
import { guestGuard } from './core/Auth/guards/guest-guard';
import { userGuard } from './core/Auth/guards/user-guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./features/home-page/home-page').then((c) => c.HomePage),
    title: 'Home',
  },

  {
    path: 'login',
    loadComponent: () => import('./features/login-page/login-page').then((c) => c.LoginPage),
    title: 'LogIn',
    canActivate: [userGuard]
  },

  {
    path: 'register',
    loadComponent: () =>
      import('./features/register-page/register-page').then((c) => c.RegisterPage),
    title: 'SignUp',
    canActivate: [userGuard]
  },

  {
    path: 'home',
    loadComponent: () => import('./features/home-page/home-page').then((c) => c.HomePage),
    title: 'Home',
  },

  {
    path: 'restaurants',
    loadComponent: () =>
      import('./features/resturants-page/resturants-page').then((c) => c.ResturantsPage),
    title: 'All Resturants',
  },

  {
    path: 'my-notifications',
    loadComponent: () =>
      import('./features/notifications-page/notifications-page').then((c) => c.NotificationsPage),
    title: 'My Notifications',
    canActivate: [guestGuard]
  },

  {
    path: 'my-reservations',
    loadComponent: () =>
      import('./features/reservations-page/reservations-page').then((c) => c.ReservationsPage),
    title: 'My Reservations',
    canActivate: [guestGuard]
  },

  {
    path: 'booking',
    loadComponent: () =>
      import('./features/booking-page/booking-page').then((c) => c.BookingPage),
    title: 'Confirm Booking',
    canActivate: [guestGuard]
  },

  {
    path: 'dashboard',
    loadComponent: () =>
      import('./features/dash-board/dash-board').then((c) => c.DashBoard),
    title: 'Management',
    canActivate: [adminGuard],
  },

  {
    path: 'restaurant-details/:id',
    loadComponent: () =>
      import('./features/restaurant-details-page/restaurant-details-page').then((c) => c.RestaurantDetailsPage),
    title: 'Resturant',
  },

  {
    path: 'about-us',
    loadComponent: () => import('./features/about-page/about-page').then((c) => c.AboutPage),
    title: 'About Us',
  },

  {
    path: '**',
    loadComponent: () => import('./features/not-found-page/not-found-page').then((c)=> c.NotFoundPage)
  }
];
