import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal, ViewChild, ElementRef } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminRestaurantInterface } from '../../core/Models/admin-restaurant-interface';
import { AdminStatisticsInterface } from '../../core/Models/admin-statistics-interface';
import { ReservationsInterface } from '../../core/Models/reservations-interface';
import { TableInterface } from '../../core/Models/table-interface';
import { TimeSlotInterface } from '../../core/Models/time-slot-interface';
import { AdminService } from '../../core/Services/admin-service';
import { ToastService } from '../../core/Services/toast-service';
import { environment } from '../../../environments/environment';

type ManagementTab = 'overview' | 'restaurants' | 'setup' | 'reservations';

/** State for the custom confirmation dialog */
interface ConfirmState {
  visible: boolean;
  title: string;
  message: string;
  confirmLabel: string;
  danger: boolean;
  onConfirm: () => void;
}

@Component({
  selector: 'app-dash-board',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './dash-board.html',
  styleUrl: './dash-board.css',
})
export class DashBoard implements OnInit {
  private adminService = inject(AdminService);
  private form = inject(FormBuilder);
  private toast = inject(ToastService);

  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;
  @ViewChild('editFileInput') editFileInput!: ElementRef<HTMLInputElement>;

  activeTab = signal<ManagementTab>('overview');
  loading = signal(true);
  savingRestaurant = signal(false);
  savingRestaurantEdit = signal(false);

  editingRestaurant = signal<AdminRestaurantInterface | null>(null);
  selectedEditFile = signal<File | null>(null);


  stats = signal<AdminStatisticsInterface | null>(null);
  restaurants = signal<AdminRestaurantInterface[]>([]);
  reservations = signal<ReservationsInterface[]>([]);
  tables = signal<TableInterface[]>([]);
  slots = signal<TimeSlotInterface[]>([]);

  selectedRestaurantId = signal<number | null>(null);
  editingTableId = signal<number | null>(null);
  editingSlotId = signal<number | null>(null);

  readonly locations = ['INDOOR', 'OUTDOOR', 'TERRACE'];
  readonly daysOfWeek = [
    '',
    'MONDAY',
    'TUESDAY',
    'WEDNESDAY',
    'THURSDAY',
    'FRIDAY',
    'SATURDAY',
    'SUNDAY',
  ];

  selectedFile = signal<File | null>(null);

  // ── Custom confirm modal state ──────────────────────────────────────────────
  confirmState = signal<ConfirmState>({
    visible: false,
    title: '',
    message: '',
    confirmLabel: 'Confirm',
    danger: true,
    onConfirm: () => {},
  });

  /** Show the custom confirm dialog. Returns a promise that resolves to true/false. */
  private triggerConfirm(
    title: string,
    message: string,
    confirmLabel: string,
    danger: boolean,
    action: () => void,
  ): void {
    this.confirmState.set({ visible: true, title, message, confirmLabel, danger, onConfirm: action });
  }

  closeConfirm() {
    this.confirmState.update((s) => ({ ...s, visible: false }));
  }

  executeConfirm() {
    const action = this.confirmState().onConfirm;
    this.closeConfirm();
    action();
  }
  // ───────────────────────────────────────────────────────────────────────────

  restaurantForm: FormGroup = this.form.group({
    name: ['', Validators.required],
    description: [''],
    address: ['', Validators.required],
    city: ['', Validators.required],
    phone: ['', Validators.required],
    cuisineType: ['', Validators.required],
    openingTime: ['09:00', Validators.required],
    closingTime: ['22:00', Validators.required],
  });

  editRestaurantForm: FormGroup = this.form.group({
    name: ['', Validators.required],
    description: [''],
    address: ['', Validators.required],
    city: ['', Validators.required],
    phone: ['', Validators.required],
    cuisineType: ['', Validators.required],
    openingTime: ['09:00', Validators.required],
    closingTime: ['22:00', Validators.required],
  });

  tableForm: FormGroup = this.form.group({
    tableNumber: [1, [Validators.required, Validators.min(1)]],
    capacity: [4, [Validators.required, Validators.min(1)]],
    location: ['INDOOR', Validators.required],
    isAvailable: [true],
  });

  slotForm: FormGroup = this.form.group({
    slotTime: ['18:00', Validators.required],
    dayOfWeek: [''],
    isActive: [true],
  });

  ngOnInit() {
    this.loadOverview();
  }

  loadOverview(silent = false) {
    if (!silent) {
      this.loading.set(true);
    }

    this.adminService.getStatistics().subscribe({
      next: (stats) => {
        this.stats.set(stats);
        if (!silent) {
          this.loading.set(false);
        }
      },
      error: () => {
        if (!silent) {
          this.loading.set(false);
        }
      },
    });

    this.adminService.getRestaurants().subscribe({
      next: (list) => {
        this.restaurants.set(list);
        if (!this.selectedRestaurantId() && list.length > 0) {
          this.selectRestaurant(list[0].id);
        }
        if (!silent) {
          this.loading.set(false);
        }
      },
      error: () => {
        if (!silent) {
          this.loading.set(false);
        }
        this.toast.show('Could not load restaurants', 'error');
      },
    });

    this.adminService.getReservations().subscribe({
      next: (list) => this.reservations.set(list),
    });
  }

  setTab(tab: ManagementTab) {
    this.activeTab.set(tab);
    if (tab === 'setup' && this.selectedRestaurantId()) {
      this.loadRestaurantSetup(this.selectedRestaurantId()!);
    }
  }

  onRestaurantSelect(event: Event) {
    const value = (event.target as HTMLSelectElement).value;
    this.selectRestaurant(Number(value));
  }

  selectRestaurant(id: number) {
    this.selectedRestaurantId.set(id);
    this.cancelTableEdit();
    this.cancelSlotEdit();
    if (this.activeTab() === 'setup') {
      this.loadRestaurantSetup(id);
    }
  }

  loadRestaurantSetup(restaurantId: number) {
    this.adminService.getTables(restaurantId).subscribe({
      next: (list) => this.tables.set(list),
      error: () => this.toast.show('Failed to load tables', 'error'),
    });

    this.adminService.getSlots(restaurantId).subscribe({
      next: (list) => this.slots.set(list),
      error: () => this.toast.show('Failed to load time slots', 'error'),
    });
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile.set(input.files[0]);
    }
  }

  saveRestaurant() {
    if (this.restaurantForm.invalid) {
      this.restaurantForm.markAllAsTouched();
      const invalidFields = Object.keys(this.restaurantForm.controls).filter(
        (key) => this.restaurantForm.controls[key].invalid
      );
      console.warn('Add restaurant form validation failed for fields:', invalidFields);
      this.toast.show('Please fill in all required fields.', 'error');
      return;
    }

    const raw = this.restaurantForm.getRawValue();
    const body = {
      name: raw.name.trim(),
      description: raw.description?.trim() || undefined,
      address: raw.address.trim(),
      city: raw.city.trim(),
      phone: raw.phone.trim(),
      cuisineType: raw.cuisineType.trim(),
      openingTime: this.formatTime(raw.openingTime),
      closingTime: this.formatTime(raw.closingTime),
    };

    this.savingRestaurant.set(true);

    this.adminService.createRestaurant(body).subscribe({
      next: (created) => {
        const file = this.selectedFile();
        if (file) {
          this.adminService.uploadRestaurantImage(created.id, file).subscribe({
            next: (updated) => {
              this.savingRestaurant.set(false);
              this.toast.show(`Restaurant "${updated.name}" created with photo`, 'success');
              this.resetFormAndNavigate(updated.id);
            },
            error: () => {
              this.savingRestaurant.set(false);
              this.toast.show(`Restaurant "${created.name}" created, but photo upload failed`, 'info');
              this.resetFormAndNavigate(created.id);
            }
          });
        } else {
          this.savingRestaurant.set(false);
          this.toast.show(`Restaurant "${created.name}" created`, 'success');
          this.resetFormAndNavigate(created.id);
        }
      },
      error: () => {
        this.savingRestaurant.set(false);
        this.toast.show(
          'Could not create restaurant. Log in with an ADMIN account (role ADMIN in database).',
          'error',
        );
      },
    });
  }

  private resetFormAndNavigate(id: number) {
    this.restaurantForm.reset({
      name: '',
      description: '',
      address: '',
      city: '',
      phone: '',
      cuisineType: '',
      openingTime: '09:00',
      closingTime: '22:00',
    });
    this.selectedFile.set(null);
    if (this.fileInput && this.fileInput.nativeElement) {
      this.fileInput.nativeElement.value = '';
    }
    this.loadOverview(true);
    this.selectRestaurant(id);
    this.setTab('setup');
  }

  private formatTime(value: string): string {
    if (!value) {
      return '09:00';
    }
    return value.length >= 5 ? value.substring(0, 5) : value;
  }

  pendingCount(): number {
    return this.reservations().filter((r) => r.status === 'PENDING').length;
  }

  selectedRestaurant(): AdminRestaurantInterface | undefined {
    return this.restaurants().find((r) => r.id === this.selectedRestaurantId());
  }

  startTableEdit(table: TableInterface) {
    this.editingTableId.set(table.id);
    this.tableForm.patchValue({
      tableNumber: table.tableNumber,
      capacity: table.capacity,
      location: table.location,
      isAvailable: table.isAvailable,
    });
  }

  cancelTableEdit() {
    this.editingTableId.set(null);
    this.tableForm.reset({
      tableNumber: 1,
      capacity: 4,
      location: 'INDOOR',
      isAvailable: true,
    });
  }

  saveTable() {
    const restaurantId = this.selectedRestaurantId();
    if (!restaurantId) {
      this.toast.show('Please select a restaurant first.', 'error');
      return;
    }
    if (this.tableForm.invalid) {
      this.tableForm.markAllAsTouched();
      this.toast.show('Please fill in all table details.', 'error');
      return;
    }

    const value = this.tableForm.getRawValue();
    const editingId = this.editingTableId();

    if (editingId) {
      this.adminService.updateTable(restaurantId, editingId, value).subscribe({
        next: () => {
          this.toast.show('Table updated', 'success');
          this.cancelTableEdit();
          this.loadRestaurantSetup(restaurantId);
        },
        error: () => this.toast.show('Failed to update table', 'error'),
      });
    } else {
      this.adminService
        .createTable(restaurantId, {
          tableNumber: value.tableNumber,
          capacity: value.capacity,
          location: value.location,
        })
        .subscribe({
          next: () => {
            this.toast.show('Table added', 'success');
            this.cancelTableEdit();
            this.loadRestaurantSetup(restaurantId);
          },
          error: () => this.toast.show('Failed to add table', 'error'),
        });
    }
  }

  deleteTable(table: TableInterface) {
    const restaurantId = this.selectedRestaurantId();
    if (!restaurantId) return;

    this.triggerConfirm(
      'Delete Table',
      `Are you sure you want to delete Table #${table.tableNumber}? This action cannot be undone.`,
      'Delete Table',
      true,
      () => {
        this.adminService.deleteTable(restaurantId, table.id).subscribe({
          next: () => {
            this.toast.show('Table deleted', 'success');
            this.loadRestaurantSetup(restaurantId);
          },
          error: () => this.toast.show('Failed to delete table', 'error'),
        });
      },
    );
  }

  startSlotEdit(slot: TimeSlotInterface) {
    this.editingSlotId.set(slot.id);
    this.slotForm.patchValue({
      slotTime: slot.slotTime?.substring(0, 5) ?? '18:00',
      dayOfWeek: slot.dayOfWeek ?? '',
      isActive: slot.isActive,
    });
  }

  cancelSlotEdit() {
    this.editingSlotId.set(null);
    this.slotForm.reset({ slotTime: '18:00', dayOfWeek: '', isActive: true });
  }

  saveSlot() {
    const restaurantId = this.selectedRestaurantId();
    if (!restaurantId) {
      this.toast.show('Please select a restaurant first.', 'error');
      return;
    }
    if (this.slotForm.invalid) {
      this.slotForm.markAllAsTouched();
      this.toast.show('Please fill in slot details.', 'error');
      return;
    }

    const raw = this.slotForm.getRawValue();
    const body = {
      slotTime: this.formatTime(raw.slotTime),
      dayOfWeek: raw.dayOfWeek || undefined,
      isActive: raw.isActive,
    };
    const editingId = this.editingSlotId();

    if (editingId) {
      this.adminService.updateSlot(restaurantId, editingId, body).subscribe({
        next: () => {
          this.toast.show('Time slot updated', 'success');
          this.cancelSlotEdit();
          this.loadRestaurantSetup(restaurantId);
        },
        error: () => this.toast.show('Failed to update time slot', 'error'),
      });
    } else {
      this.adminService.createSlot(restaurantId, body).subscribe({
        next: () => {
          this.toast.show('Time slot added', 'success');
          this.cancelSlotEdit();
          this.loadRestaurantSetup(restaurantId);
        },
        error: () => this.toast.show('Failed to add time slot', 'error'),
      });
    }
  }

  deleteSlot(slot: TimeSlotInterface) {
    const restaurantId = this.selectedRestaurantId();
    if (!restaurantId) return;

    this.triggerConfirm(
      'Delete Time Slot',
      `Are you sure you want to delete the ${slot.slotTime} time slot? This action cannot be undone.`,
      'Delete Slot',
      true,
      () => {
        this.adminService.deleteSlot(restaurantId, slot.id).subscribe({
          next: () => {
            this.toast.show('Time slot deleted', 'success');
            this.loadRestaurantSetup(restaurantId);
          },
          error: () => this.toast.show('Failed to delete time slot', 'error'),
        });
      },
    );
  }

  confirmReservation(reservation: ReservationsInterface) {
    this.triggerConfirm(
      'Confirm Reservation',
      `Confirm reservation #${reservation.id} for ${reservation.guestCount} guest(s) on ${reservation.reservationDate}?`,
      'Confirm Booking',
      false,
      () => {
        this.adminService.confirmReservation(reservation.id).subscribe({
          next: (updated) => {
            this.toast.show('Reservation confirmed', 'success');
            this.reservations.update((list) =>
              list.map((r) => (r.id === updated.id ? updated : r)),
            );
          },
          error: () => this.toast.show('Failed to confirm reservation', 'error'),
        });
      },
    );
  }

  cancelReservation(reservation: ReservationsInterface) {
    this.triggerConfirm(
      'Cancel Reservation',
      `Are you sure you want to cancel reservation #${reservation.id}? This action cannot be undone.`,
      'Cancel Booking',
      true,
      () => {
        this.adminService.cancelReservation(reservation.id).subscribe({
          next: (updated) => {
            this.toast.show('Reservation cancelled', 'success');
            this.reservations.update((list) =>
              list.map((r) => (r.id === updated.id ? updated : r)),
            );
          },
          error: () => this.toast.show('Failed to cancel reservation', 'error'),
        });
      },
    );
  }

  deleteRestaurant(restaurant: AdminRestaurantInterface) {
    this.triggerConfirm(
      'Delete Restaurant',
      `Are you sure you want to permanently delete "${restaurant.name}"? All associated tables, time slots, and reservations will be affected.`,
      'Delete Restaurant',
      true,
      () => {
        this.adminService.deleteRestaurant(restaurant.id).subscribe({
          next: () => {
            this.toast.show(`"${restaurant.name}" deleted`, 'success');
            this.restaurants.update((list) => list.filter((r) => r.id !== restaurant.id));
            if (this.selectedRestaurantId() === restaurant.id) {
              this.selectedRestaurantId.set(null);
            }
          },
          error: () => this.toast.show('Failed to delete restaurant', 'error'),
        });
      },
    );
  }

  startRestaurantEdit(restaurant: AdminRestaurantInterface) {
    this.editingRestaurant.set(restaurant);
    this.selectedEditFile.set(null);
    if (this.editFileInput && this.editFileInput.nativeElement) {
      this.editFileInput.nativeElement.value = '';
    }
    this.editRestaurantForm.patchValue({
      name: restaurant.name,
      description: restaurant.description,
      address: restaurant.address,
      city: restaurant.city,
      phone: restaurant.phone,
      cuisineType: restaurant.cuisineType,
      openingTime: restaurant.openingTime?.substring(0, 5) ?? '09:00',
      closingTime: restaurant.closingTime?.substring(0, 5) ?? '22:00',
    });
  }

  cancelRestaurantEdit() {
    this.editingRestaurant.set(null);
    this.selectedEditFile.set(null);
    this.editRestaurantForm.reset();
  }

  onEditFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedEditFile.set(input.files[0]);
    }
  }

  saveRestaurantEdit() {
    const current = this.editingRestaurant();
    if (!current) return;

    if (this.editRestaurantForm.invalid) {
      this.editRestaurantForm.markAllAsTouched();
      const invalidFields = Object.keys(this.editRestaurantForm.controls).filter(
        (key) => this.editRestaurantForm.controls[key].invalid
      );
      console.warn('Edit restaurant form validation failed for fields:', invalidFields);
      this.toast.show('Please fill in all required fields.', 'error');
      return;
    }

    const raw = this.editRestaurantForm.getRawValue();
    const body = {
      name: raw.name.trim(),
      description: raw.description?.trim() || undefined,
      address: raw.address.trim(),
      city: raw.city.trim(),
      phone: raw.phone.trim(),
      cuisineType: raw.cuisineType.trim(),
      openingTime: this.formatTime(raw.openingTime),
      closingTime: this.formatTime(raw.closingTime),
    };

    this.savingRestaurantEdit.set(true);

    this.adminService.updateRestaurant(current.id, body).subscribe({
      next: (updatedRes) => {
        const file = this.selectedEditFile();
        if (file) {
          this.adminService.uploadRestaurantImage(current.id, file).subscribe({
            next: (updatedWithImage) => {
              this.savingRestaurantEdit.set(false);
              this.toast.show(`Restaurant "${updatedWithImage.name}" updated with photo`, 'success');
              this.editingRestaurant.set(null);
              this.loadOverview(true);
            },
            error: () => {
              this.savingRestaurantEdit.set(false);
              this.toast.show(`Restaurant "${updatedRes.name}" info updated, but photo upload failed`, 'info');
              this.editingRestaurant.set(null);
              this.loadOverview(true);
            }
          });
        } else {
          this.savingRestaurantEdit.set(false);
          this.toast.show(`Restaurant "${updatedRes.name}" updated`, 'success');
          this.editingRestaurant.set(null);
          this.loadOverview(true);
        }
      },
      error: () => {
        this.savingRestaurantEdit.set(false);
        this.toast.show('Failed to update restaurant details', 'error');
      }
    });
  }

  getRestaurantImageUrl(url: string | null | undefined): string {
    if (!url) {
      return 'images/hero-1.jpg';
    }
    if (url.startsWith('http')) {
      return url;
    }
    return `${environment.imageBase}/${url}`;
  }

  toggleRestaurantActive() {
    const id = this.selectedRestaurantId();
    if (!id) {
      return;
    }

    this.adminService.toggleRestaurantStatus(id).subscribe({
      next: (updated) => {
        this.restaurants.update((list) => list.map((r) => (r.id === updated.id ? updated : r)));
        this.toast.show(
          updated.isActive ? 'Restaurant activated' : 'Restaurant deactivated',
          'success',
        );
      },
      error: () =>
        this.toast.show('Failed to update status (ADMIN account required)', 'error'),
    });
  }

  statusClass(status: string): string {
    switch (status) {
      case 'CONFIRMED':
        return 'bg-green-100 text-green-700';
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-700';
      case 'CANCELLED':
        return 'bg-red-100 text-red-600';
      default:
        return 'bg-gray-100 text-gray-600';
    }
  }
}
