export interface RestauratsInterface {
  id: number;
  name: string;
  description: string;
  address: string;
  city: string;
  phone: string;
  cuisineType: string;
  imageUrl: string | null;
  openingTime: string;
  closingTime: string;
  isActive: boolean;
}