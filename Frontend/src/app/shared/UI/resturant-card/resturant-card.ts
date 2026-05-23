import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { RestauratsInterface } from '../../../core/Models/restaurats-interface';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-resturant-card',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './resturant-card.html',
  styleUrl: './resturant-card.css',
})
export class ResturantCard {
  @Input() restaurant?: RestauratsInterface;

  @Output() reserve = new EventEmitter<number>();
  @Output() showRestaurantDetailsEvent = new EventEmitter<number>();

  router = inject(Router);

  showRestaurant() {
    if (!this.restaurant) {
      console.log('restaurant undefined');
      return;
    }

    const id = this.restaurant.id;

    this.showRestaurantDetailsEvent.emit(id);

    this.router.navigate(['/restaurant-details', id]);
  }

  getImageUrl(): string {
    if (!this.restaurant || !this.restaurant.imageUrl) {
      return 'images/hero-1.jpg';
    }
    const img = this.restaurant.imageUrl;
    if (img.startsWith('http')) {
      return img;
    }
    // Use imageBase (localhost:8081) so images always load regardless of which IP serves the API
    return `${environment.imageBase}/${img}`;
  }
}
