import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ResturantCard } from './../../../shared/UI/resturant-card/resturant-card';
import { RestaurantService } from '../../../core/Services/restaurant-service';
import { RestauratsInterface } from '../../../core/Models/restaurats-interface';

@Component({
  selector: 'app-featured-section',
  standalone: true,
  imports: [CommonModule, ResturantCard, RouterLink],
  templateUrl: './featured-section.html',
  styleUrl: './featured-section.css',
})
export class FeaturedSection implements OnInit {
  private restaurantService = inject(RestaurantService);

  restaurants = signal<RestauratsInterface[]>([]);

  ngOnInit() {
    this.restaurantService.getRestaurants().subscribe({
      next: (list: RestauratsInterface[]) => {
        // Show first 5 restaurants as "featured"
        this.restaurants.set(list.slice(0, 5));
      },
      error: () => {
        this.restaurants.set([]);
      },
    });
  }
}
