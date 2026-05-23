export interface ReservationsInterface {
  id: number;
  restaurantName: string;
  tableNumber: number;
  reservationDate: string;
  slotTime: string;
  guestCount: number;
  status: 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';
  specialRequest: string;
  createdAt: string;
}