import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ResturantCard } from '../../../shared/UI/resturant-card/resturant-card';
import { RouterLink } from '@angular/router';
import { RestaurantService } from '../../../core/Services/restaurant-service';
import { RestauratsInterface } from '../../../core/Models/restaurats-interface';

@Component({
  selector: 'app-top-rated-section',
  standalone: true,
  imports: [CommonModule, ResturantCard, RouterLink],
  templateUrl: './top-rated-section.html',
  styleUrl: './top-rated-section.css',
})
export class TopRatedSection implements OnInit {
  private restaurantService = inject(RestaurantService);

  restaurants = signal<RestauratsInterface[]>([]);

  ngOnInit() {
    this.restaurantService.getRestaurants().subscribe({
      next: (list: RestauratsInterface[]) => {
        // Show last 5 restaurants as "top picks" (most recently added)
        const all = [...list].reverse();
        this.restaurants.set(all.slice(0, 5));
      },
      error: () => {
        this.restaurants.set([]);
      },
    });
  }
}
