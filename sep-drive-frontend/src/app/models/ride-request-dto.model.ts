import { CarClass, RideStatus } from './enums.model';

export interface RideRequestDto {
  id: number;
  customerId: number;
  customerUsername: string;
  startLatitude: number;
  startLongitude: number;
  startAddress?: string;
  destinationLatitude: number;
  destinationLongitude: number;
  destinationAddress?: string;
  requestedCarClass: CarClass;
  status: RideStatus;
  createdAt: string; // Kommt als ISO String vom Backend (Instant)
}
