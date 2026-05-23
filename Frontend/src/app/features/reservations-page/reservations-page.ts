import { ReservationsInterface } from './../../core/Models/reservations-interface';
import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { UserService } from '../../core/Services/user-service';

@Component({
  selector: 'app-reservations-page',
  imports: [CommonModule, RouterLink],
  templateUrl: './reservations-page.html',
  styleUrl: './reservations-page.css',
})
export class ReservationsPage implements OnInit {
  userService = inject(UserService);

  reservations = signal<ReservationsInterface[]>([]);
  ngOnInit(): void {
    this.getReservations();
  }
  getReservations() {
    this.userService.getUserReservations().subscribe({
      next: (res) => {
        console.log(res);
        this.reservations.set(res)
      },
      error: (err) => {
        console.log(err);
      },
    });
  }
}
