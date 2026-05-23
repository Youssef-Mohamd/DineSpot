import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private myHttp = inject(HttpClient);
  private baseUrl = environment.baseUrl;

  getUserNotifications(): Observable<any> {
    return this.myHttp.get(`${this.baseUrl}/notifications`);
  }

  markNotificationAsRead(notificationId: number): Observable<any> {
    return this.myHttp.put(`${this.baseUrl}/notifications/${notificationId}/read`, {});
  }

  getUserReservations(): Observable<any> {
    return this.myHttp.get(`${this.baseUrl}/reservations/me`);
  }
}
