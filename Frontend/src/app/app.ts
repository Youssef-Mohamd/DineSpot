import { Component, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavBar } from './features/nav-bar/nav-bar';
import { Footer } from './features/footer/footer';
import { ToastService } from './core/Services/toast-service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavBar, Footer,CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('DineSpot');
  toastService = inject(ToastService)
}
