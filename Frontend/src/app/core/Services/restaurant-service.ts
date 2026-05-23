import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class RestaurantService {
  private myHttp = inject(HttpClient);
  private baseUrl = environment.baseUrl;

  getRestaurants(): Observable<any> {
    return this.myHttp.get(`${this.baseUrl}/restaurants`);
  }

  getSpecificRestaurant(id: number): Observable<any> {
    return this.myHttp.get(`${this.baseUrl}/restaurants/${id}`);
  }

  checkAvailability(data: {
    restaurantId: number;
    date: string;
    guests: number;
  }): Observable<any> {
    return this.myHttp.post(`${this.baseUrl}/availability`, data);
  }

  getAvailableTimeSlots(restaurantId: number, date: string): Observable<any> {
    return this.myHttp.get(`${this.baseUrl}/timeslots/available`, {
      params: { restaurantId: restaurantId.toString(), date }
    });
  }

  checkAvailabilityWithAssignment(data: {
    restaurantId: number;
    reservationDate: string;
    timeSlotId: number;
    guestCount: number;
  }): Observable<any> {
    return this.myHttp.post(`${this.baseUrl}/reservations/check-availability`, data);
  }

  bookRestaurant(data: {
    restaurantId: number;
    reservationDate: string;
    timeSlotId: number;
    guestCount: number;
    specialRequest?: string;
  }): Observable<any> {
    return this.myHttp.post(`${this.baseUrl}/reservations`, data);
  }
}
