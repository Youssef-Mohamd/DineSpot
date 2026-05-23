import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RestaurantService } from '../../core/Services/restaurant-service';
import { environment } from '../../../environments/environment';
import { RestauratsInterface } from '../../core/Models/restaurats-interface';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ToastService } from '../../core/Services/toast-service';

@Component({
  selector: 'app-booking-page',
  imports: [ReactiveFormsModule],
  templateUrl: './booking-page.html',
  styleUrl: './booking-page.css',
})
export class BookingPage implements OnInit {
  restaurantService = inject(RestaurantService);
  route = inject(ActivatedRoute);
  router = inject(Router);
  form = inject(FormBuilder);
  toast = inject(ToastService);

  restaurantId = signal<number>(0);
  date = signal('');
  guests = signal<number>(0);
  slotTime = signal('');
  tableNumber = signal<number | null>(null);
  tableLocation = signal<string | null>(null);
  restaurantDetails = signal<RestauratsInterface | null>(null);
  loading = signal<boolean>(true);
  submitting = signal<boolean>(false);

  bookingForm: FormGroup = this.form.group({
    restaurantId: ['', Validators.required],
    reservationDate: ['', Validators.required],
    timeSlotId: ['', Validators.required],
    guestCount: ['', [Validators.required, Validators.min(1), Validators.max(20)]],
    specialRequest: [''],
  });

  ngOnInit() {
    this.route.queryParams.subscribe((params) => {
      const restaurantId = Number(params['restaurantId']);
      const timeSlotId = Number(params['timeSlotId']);

      this.restaurantId.set(restaurantId);
      this.date.set(params['date'] ?? '');
      this.guests.set(Number(params['guests']));
      this.slotTime.set(params['slotTime'] ?? '');

      if (params['tableNumber']) {
        this.tableNumber.set(Number(params['tableNumber']));
      } else {
        this.tableNumber.set(null);
      }

      if (params['location']) {
        this.tableLocation.set(params['location']);
      } else {
        this.tableLocation.set(null);
      }

      this.bookingForm.patchValue({
        restaurantId,
        reservationDate: params['date'],
        timeSlotId,
        guestCount: Number(params['guests']),
      });

      if (restaurantId) {
        this.getRestaurantDetails(restaurantId);
      }
    });
  }

  getRestaurantDetails(restaurantId: number) {
    this.loading.set(true);

    this.restaurantService.getSpecificRestaurant(restaurantId).subscribe({
      next: (res) => {
        this.restaurantDetails.set(res);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.toast.show('Could not load restaurant details', 'error');
      },
    });
  }

  confirmBooking() {
    if (this.bookingForm.invalid) {
      this.bookingForm.markAllAsTouched();
      this.toast.show('Please complete all booking details', 'error');
      return;
    }

    const raw = this.bookingForm.getRawValue();
    const payload = {
      restaurantId: Number(raw.restaurantId),
      reservationDate: raw.reservationDate,
      timeSlotId: Number(raw.timeSlotId),
      guestCount: Number(raw.guestCount),
      specialRequest: raw.specialRequest?.trim() || undefined,
    };

    this.submitting.set(true);

    this.restaurantService.bookRestaurant(payload).subscribe({
      next: () => {
        this.submitting.set(false);
        this.toast.show('Booking confirmed successfully', 'success');
        this.router.navigate(['/my-reservations']);
      },
      error: () => {
        this.submitting.set(false);
        this.toast.show('Booking failed. Please log in and try again.', 'error');
      },
    });
  }

  cancelBooking() {
    this.router.navigate(['/restaurants']);
  }

  getImageUrl(): string {
    const res = this.restaurantDetails();
    if (!res || !res.imageUrl) {
      return 'images/hero-1.jpg';
    }
    if (res.imageUrl.startsWith('http')) {
      return res.imageUrl;
    }
    const base = environment.imageBase;
    return `${base}/${res.imageUrl}`;
  }
}
