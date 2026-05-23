import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule, NgClass } from '@angular/common';

import { RestaurantService } from '../../core/Services/restaurant-service';
import { AuthenticationService } from '../../core/Auth/services/authentication-service';
import { environment } from '../../../environments/environment';
import { RestauratsInterface } from '../../core/Models/restaurats-interface';
import { AvailableSlot } from '../../core/Models/available-slot-interface';

@Component({
  standalone: true,
  selector: 'app-restaurant-details-page',
  imports: [CommonModule, ReactiveFormsModule, NgClass],
  templateUrl: './restaurant-details-page.html',
  styleUrl: './restaurant-details-page.css',
})
export class RestaurantDetailsPage implements OnInit {
  // injects
  form = inject(FormBuilder);
  route = inject(ActivatedRoute);
  router = inject(Router);
  restaurantService = inject(RestaurantService);
  authService = inject(AuthenticationService);

  // signals
  restaurantId = signal<number>(0);
  restaurant = signal<RestauratsInterface | null>(null);
  availableSlots = signal<AvailableSlot[]>([]);
  loading = signal<boolean>(true);
  checking = signal<boolean>(false);
  loadingSlots = signal<boolean>(false);
  modal = signal<boolean>(false);
  availabilityError = signal<string | null>(null);
  hasChecked = signal<boolean>(false);
  selectedSlot = signal<AvailableSlot | null>(null);
  assignmentResult = signal<any | null>(null);

  // form
  availabilityForm: FormGroup = this.form.group({
    restaurantId: ['', Validators.required],
    date: ['', Validators.required],
    guests: [null, [Validators.required, Validators.min(1), Validators.max(20)]],
  });

  // lifecycle
  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      const restaurantId = +id;
      this.restaurantId.set(restaurantId);

      // patch restaurant id into form
      this.availabilityForm.patchValue({
        restaurantId: restaurantId,
      });

      // load restaurant details
      this.getRestaurantDetails(restaurantId);

      // Subscribe to date change to trigger Step 1: load slots
      this.availabilityForm.get('date')?.valueChanges.subscribe((newDate) => {
        if (newDate) {
          this.loadTimeSlotsForDate(newDate);
        }
      });

      // Reset selection when guests change
      this.availabilityForm.get('guests')?.valueChanges.subscribe(() => {
        this.selectedSlot.set(null);
        this.assignmentResult.set(null);
      });
    }
  }

  // get restaurant details
  getRestaurantDetails(id: number) {
    this.loading.set(true);
    this.restaurantService.getSpecificRestaurant(id).subscribe({
      next: (res) => {
        this.restaurant.set(res);
        this.loading.set(false);
        console.log(res);
      },
      error: (err) => {
        console.log(err);
        this.loading.set(false);
      },
    });
  }

  // Validate form and load slots
  checkAvailability() {
    if (this.availabilityForm.invalid) {
      this.availabilityForm.markAllAsTouched();
      return;
    }
    const dateStr = this.availabilityForm.value.date;
    if (dateStr) {
      this.loadTimeSlotsForDate(dateStr);
    }
  }

  // load time slots based on day of week template (Step 1)
  loadTimeSlotsForDate(dateStr: string) {
    const restaurantId = this.restaurantId();
    if (!restaurantId || !dateStr) return;

    this.loadingSlots.set(true);
    this.selectedSlot.set(null);
    this.assignmentResult.set(null);
    this.availabilityError.set(null);
    this.hasChecked.set(false);

    this.restaurantService.getAvailableTimeSlots(restaurantId, dateStr).subscribe({
      next: (slots) => {
        const list: AvailableSlot[] = Array.isArray(slots) ? slots : [];
        this.availableSlots.set(list);
        this.hasChecked.set(true);
        this.loadingSlots.set(false);
      },
      error: (err) => {
        console.error(err);
        this.availableSlots.set([]);
        this.hasChecked.set(true);
        this.loadingSlots.set(false);
        this.availabilityError.set('Failed to load slots for this date.');
      }
    });
  }

  // modal
  toggleModal() {
    // Check if user is logged in
    if (!this.authService.isLogged()) {
      this.router.navigate(['/login']);
      return;
    }

    const opening = !this.modal();
    this.modal.set(opening);

    if (opening) {
      this.availableSlots.set([]);
      this.selectedSlot.set(null);
      this.assignmentResult.set(null);
      this.availabilityError.set(null);
      this.hasChecked.set(false);

      if (this.availabilityForm.get('guests')?.value == null) {
        this.availabilityForm.patchValue({ guests: 4 });
      }

      const currentDate = this.availabilityForm.get('date')?.value;
      if (currentDate) {
        this.loadTimeSlotsForDate(currentDate);
      }
    }
  }

  // select slot and trigger table assignment verification (Step 2)
  selectSlotAndCheck(slot: AvailableSlot) {
    if (this.availabilityForm.invalid) {
      this.availabilityForm.markAllAsTouched();
      return;
    }

    this.selectedSlot.set(slot);
    this.checking.set(true);
    this.assignmentResult.set(null);
    this.availabilityError.set(null);

    const raw = this.availabilityForm.getRawValue();
    const payload = {
      restaurantId: Number(raw.restaurantId),
      reservationDate: raw.date,
      timeSlotId: slot.id,
      guestCount: Number(raw.guests || 4),
    };

    this.restaurantService.checkAvailabilityWithAssignment(payload).subscribe({
      next: (res) => {
        this.checking.set(false);
        const isAvail = res.isAvailable !== undefined ? res.isAvailable : res.available;
        if (isAvail) {
          this.assignmentResult.set(res);
        } else {
          this.assignmentResult.set(null);
          this.availabilityError.set(res.message || 'No suitable table available.');
        }
      },
      error: () => {
        this.checking.set(false);
        this.assignmentResult.set(null);
        this.availabilityError.set('Could not allocate table. Make sure the server is running.');
      },
    });
  }

  // Proceed to final confirmation page
  proceedToBooking() {
    const slot = this.selectedSlot();
    const assignment = this.assignmentResult();
    if (!slot || !assignment) return;

    this.router.navigate(['/booking'], {
      queryParams: {
        restaurantId: this.restaurantId(),
        date: this.availabilityForm.value.date,
        guests: this.availabilityForm.value.guests,
        timeSlotId: slot.id,
        slotTime: slot.slotTime,
        tableNumber: assignment.tableNumber,
        location: assignment.location,
      },
    });
  }

  getImageUrl(): string {
    const res = this.restaurant();
    if (!res || !res.imageUrl) {
      return 'images/hero-1.jpg';
    }
    if (res.imageUrl.startsWith('http')) {
      return res.imageUrl;
    }
    return `${environment.imageBase}/${res.imageUrl}`;
  }
}
