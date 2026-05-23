import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { RestaurantService } from '../../core/Services/restaurant-service';
import { RestauratsInterface } from '../../core/Models/restaurats-interface';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ResturantCard } from '../../shared/UI/resturant-card/resturant-card';

@Component({
  selector: 'app-resturants-page',
  imports: [CommonModule, ResturantCard],
  templateUrl: './resturants-page.html',
  styleUrl: './resturants-page.css',
})
export class ResturantsPage implements OnInit {
  restaurantService = inject(RestaurantService);
  router = inject(Router);

  allRestaurants = signal<RestauratsInterface[]>([]);
  loading = signal(false);

  selectedCity = signal('');
  selectedCuisine = signal('');

  restaurants = computed(() => {
    let filtered = this.allRestaurants();
    if (this.selectedCity()) {
      filtered = filtered.filter((r) => r.city === this.selectedCity());
    }
    if (this.selectedCuisine()) {
      filtered = filtered.filter((r) => r.cuisineType === this.selectedCuisine());
    }
    return filtered;
  });

  cities = signal<string[]>([
    'Cairo',
    'Giza',
    'Alexandria',
    'Dubai',
    'Abu Dhabi',
    'Riyadh',
    'Jeddah',
    'Doha',
    'Kuwait City',
    'Casablanca',
    'Istanbul',
    'Amman',
    'Beirut',
    'London',
    'Paris',
    'Rome',
    'Berlin',
    'Madrid',
    'Barcelona',
    'Amsterdam',
    'Vienna',
    'Prague',
    'Athens',
    'Lisbon',
    'Zurich',
    'Munich',
    'New York',
    'Los Angeles',
    'Chicago',
    'San Francisco',
    'Toronto',
    'Vancouver',
    'Miami',
    'Washington DC',
    'Boston',
    'Las Vegas',
    'Houston',
    'Seattle',
    'Tokyo',
    'Osaka',
    'Kyoto',
    'Seoul',
    'Beijing',
    'Shanghai',
    'Hong Kong',
    'Singapore',
    'Bangkok',
    'Kuala Lumpur',
    'Mumbai',
    'Delhi',
    'Manila',
    'São Paulo',
    'Rio de Janeiro',
    'Buenos Aires',
    'Lima',
    'Bogotá',
    'Santiago',
    'Caracas',
  ]);

  cuisines = signal<string[]>([
    'Egyptian',
    'Levantine',
    'Lebanese',
    'Syrian',
    'Turkish',
    'Moroccan',
    'Persian',
    'Israeli',
    'Sudanese',
    'Italian',
    'French',
    'Spanish',
    'Greek',
    'German',
    'British',
    'Mediterranean',
    'European',
    'Swiss',
    'Portuguese',
    'American',
    'Fast Food',
    'BBQ',
    'Steakhouse',
    'Burgers',
    'Tex-Mex',
    'Cajun',
    'Soul Food',
    'Chinese',
    'Japanese',
    'Sushi',
    'Korean',
    'Thai',
    'Vietnamese',
    'Indian',
    'Pakistani',
    'Indonesian',
    'Malaysian',
    'Mexican',
    'Brazilian',
    'Argentinian',
    'Peruvian',
    'Colombian',
    'Caribbean',
    'Cuban',
    'Cafe',
    'Desserts',
    'Ice Cream',
    'Bakery',
    'Healthy',
    'Vegan',
    'Vegetarian',
    'Seafood',
    'Fusion',
    'Street Food',
  ]);

  ngOnInit() {
    this.loadRestaurants();
  }

  loadRestaurants() {
    this.loading.set(true);

    this.restaurantService.getRestaurants().subscribe({
      next: (res) => {
        this.allRestaurants.set(res);
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(false);
      },
    });
  }

  onCityChange(city: string) {
    this.selectedCity.set(city);
  }

  onCuisineChange(cuisine: string) {
    this.selectedCuisine.set(cuisine);
  }

  clearFilters() {
    this.selectedCity.set('');
    this.selectedCuisine.set('');
  }

}
