export interface AdminRestaurantInterface {
  id: number;
  name: string;
  description: string;
  address: string;
  city: string;
  phone: string;
  cuisineType: string;
  imageUrl: string;
  openingTime: string;
  closingTime: string;
  isActive: boolean;
  adminName: string;
  adminEmail: string;
  totalTables: number;
  totalReservations: number;
  activeReservations: number;
}
