import { Component, inject, OnInit, signal } from '@angular/core';
import { UserService } from '../../core/Services/user-service';
import { NotificationsInterface } from '../../core/Models/notifications-interface';
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-notifications-page',
  imports: [RouterLink],
  templateUrl: './notifications-page.html',
  styleUrl: './notifications-page.css',
})
export class NotificationsPage implements OnInit {
  userService = inject(UserService);
  notifications = signal<NotificationsInterface[]>([])
  ngOnInit(): void {
    this.getNotifications();
  }

  getNotifications() {
    this.userService.getUserNotifications().subscribe({
      next: (res) => {
        console.log(res);
        this.notifications.set(res)
      },
      error: (err) => {
        console.log(err);
      },
    });
  }

  markAsRead(id: number) {
    this.userService.markNotificationAsRead(id).subscribe({
      next: () => {
        this.notifications.update((list) =>
          list.map((n) => (n.id === id ? { ...n, isRead: true } : n)),
        );
      },
      error: (err) => {
        console.log(err);
      },
    });
  }

  // markAllAsRead() {
  //   this.notifications.update((list) => list.map((n) => ({ ...n, isRead: true })));
  // }

  // notifications = signal([
  //   {
  //     id: 1,
  //     title: 'Reservation Confirmed',
  //     message: 'Your table at La Bella Italia has been confirmed.',
  //     createdAt: '2 hours ago',
  //     isRead: false,
  //   },
  //   {
  //     id: 2,
  //     title: 'Special Offer',
  //     message: 'Get 20% off your next booking this weekend.',
  //     createdAt: 'Yesterday',
  //     isRead: false,
  //   },
  //   {
  //     id: 3,
  //     title: 'Reminder',
  //     message: 'You have a reservation tomorrow at 8:00 PM.',
  //     createdAt: '2 days ago',
  //     isRead: true,
  //   },
  // ]);
}
