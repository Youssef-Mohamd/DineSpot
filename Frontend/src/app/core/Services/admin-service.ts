import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { catchError, map, Observable, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import { RestauratsInterface } from '../Models/restaurats-interface';
import { AdminRestaurantInterface } from '../Models/admin-restaurant-interface';
import { AdminStatisticsInterface } from '../Models/admin-statistics-interface';
import { ReservationsInterface } from '../Models/reservations-interface';
import { TableInterface } from '../Models/table-interface';
import { TimeSlotInterface } from '../Models/time-slot-interface';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private http = inject(HttpClient);
  private api = environment.baseUrl;
  private admin = `${environment.baseUrl}/admin`;

  getStatistics(): Observable<AdminStatisticsInterface | null> {
    return this.http
      .get<AdminStatisticsInterface>(`${this.admin}/statistics`)
      .pipe(catchError(() => of(null)));
  }

  getRestaurants(): Observable<AdminRestaurantInterface[]> {
    return this.http.get<AdminRestaurantInterface[]>(`${this.admin}/restaurants`).pipe(
      catchError(() =>
        this.http.get<RestauratsInterface[]>(`${this.api}/restaurants`).pipe(
          map((list) =>
            list.map((r) => ({
              id: r.id,
              name: r.name,
              description: r.description ?? '',
              address: r.address ?? '',
              city: r.city ?? '',
              phone: r.phone ?? '',
              cuisineType: r.cuisineType ?? '',
              imageUrl: r.imageUrl ?? '',
              openingTime: r.openingTime ?? '',
              closingTime: r.closingTime ?? '',
              isActive: r.isActive ?? true,
              adminName: '',
              adminEmail: '',
              totalTables: 0,
              totalReservations: 0,
              activeReservations: 0,
            })),
          ),
          catchError(() => of([])),
        ),
      ),
    );
  }

  createRestaurant(body: {
    name: string;
    description?: string;
    address: string;
    city: string;
    phone: string;
    cuisineType: string;
    imageUrl?: string;
    openingTime: string;
    closingTime: string;
  }): Observable<RestauratsInterface> {
    return this.http.post<RestauratsInterface>(`${this.api}/restaurants`, body);
  }

  uploadRestaurantImage(restaurantId: number, file: File): Observable<RestauratsInterface> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<RestauratsInterface>(`${this.api}/restaurants/${restaurantId}/image`, formData);
  }

  toggleRestaurantStatus(restaurantId: number): Observable<AdminRestaurantInterface> {
    return this.http.put<AdminRestaurantInterface>(
      `${this.admin}/restaurants/${restaurantId}/toggle-status`,
      {},
    );
  }

  getReservations(): Observable<ReservationsInterface[]> {
    return this.http
      .get<ReservationsInterface[]>(`${this.admin}/reservations`)
      .pipe(catchError(() => of([])));
  }

  confirmReservation(reservationId: number): Observable<ReservationsInterface> {
    return this.http.put<ReservationsInterface>(
      `${this.admin}/reservations/${reservationId}/confirm`,
      {},
    );
  }

  cancelReservation(reservationId: number): Observable<ReservationsInterface> {
    return this.http.put<ReservationsInterface>(
      `${this.admin}/reservations/${reservationId}/cancel`,
      {},
    );
  }

  /** Public GET — no /admin prefix (avoids 403 when token role is stale for reads). */
  getTables(restaurantId: number): Observable<TableInterface[]> {
    return this.http.get<TableInterface[]>(`${this.api}/restaurants/${restaurantId}/tables`);
  }

  createTable(
    restaurantId: number,
    body: { tableNumber: number; capacity: number; location: string },
  ): Observable<TableInterface> {
    return this.http.post<TableInterface>(`${this.api}/restaurants/${restaurantId}/tables`, body);
  }

  updateTable(
    restaurantId: number,
    tableId: number,
    body: Partial<{ tableNumber: number; capacity: number; location: string; isAvailable: boolean }>,
  ): Observable<TableInterface> {
    return this.http.put<TableInterface>(
      `${this.api}/restaurants/${restaurantId}/tables/${tableId}`,
      body,
    );
  }

  deleteTable(restaurantId: number, tableId: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/restaurants/${restaurantId}/tables/${tableId}`);
  }

  /** Requires ADMIN in JWT — includes inactive slots. */
  getSlots(restaurantId: number): Observable<TimeSlotInterface[]> {
    return this.http
      .get<TimeSlotInterface[]>(`${this.api}/restaurants/${restaurantId}/slots/all`)
      .pipe(
        catchError(() =>
          this.http.get<TimeSlotInterface[]>(`${this.api}/restaurants/${restaurantId}/slots`),
        ),
      );
  }

  createSlot(
    restaurantId: number,
    body: { slotTime: string; dayOfWeek?: string },
  ): Observable<TimeSlotInterface> {
    return this.http.post<TimeSlotInterface>(`${this.api}/restaurants/${restaurantId}/slots`, body);
  }

  updateSlot(
    restaurantId: number,
    slotId: number,
    body: Partial<{ slotTime: string; dayOfWeek: string; isActive: boolean }>,
  ): Observable<TimeSlotInterface> {
    return this.http.put<TimeSlotInterface>(
      `${this.api}/restaurants/${restaurantId}/slots/${slotId}`,
      body,
    );
  }

  deleteSlot(restaurantId: number, slotId: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/restaurants/${restaurantId}/slots/${slotId}`);
  }

  updateRestaurant(
    restaurantId: number,
    body: {
      name: string;
      description?: string;
      address: string;
      city: string;
      phone: string;
      cuisineType: string;
      openingTime: string;
      closingTime: string;
    },
  ): Observable<RestauratsInterface> {
    return this.http.put<RestauratsInterface>(`${this.api}/restaurants/${restaurantId}`, body);
  }

  deleteRestaurant(restaurantId: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/restaurants/${restaurantId}`);
  }
}
