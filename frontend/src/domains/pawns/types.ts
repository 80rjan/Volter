export interface PawnRow {
  Id: number;
  'Client Id': number;
  Name: string;
  Category: 'Electronics' | 'Gold' | 'Watch' | 'Vehicle' | 'Other';
  About: string;
  'Item Cost': number | string;
  Provision: number | string;
  'Days Left': number;
  'Valid Until': string;
  'Total Days': number | string;
}

export interface PawnCustomer {
  name: string;
  phoneNumber: string;
  reservePhoneNumber: string | null;
  embg: string;
  address: string;
  city: string;
  createdAt: string;
  updatedAt: string;
  riskLevel: string;
  totalPawnCount: number;
  totalSaleCount: number;
  lateRenewalCount: number;
  avgDaysLate: number;
  onTimeRenewalCount: number;
  forfeitCount: number;
  redeemCount: number;
}

interface BaseItemDetailed {
  itemType: string;
  description: string;
  itemStatus: string;
  createdAt: string;
  updatedAt: string;
}

export interface GoldItemDetailed extends BaseItemDetailed {
  itemType: 'GOLD';
  weightGrams: number;
  carats: string;
  pieceType: string;
  pricePerGram: number;
}

export interface ElectronicItemDetailed extends BaseItemDetailed {
  itemType: 'ELECTRONIC';
  brand: string;
  category: string;
  year: number;
}

export interface WatchItemDetailed extends BaseItemDetailed {
  itemType: 'WATCH';
  brand: string;
  model: string;
  material: string;
  year: number;
  originalBoxIncluded: boolean;
  originalPapersIncluded: boolean;
  warrantyCardIncluded: boolean;
  warrantyExpirationDate: string | null;
  functional: boolean;
  serviceRequired: boolean;
}

export interface VehicleItemDetailed extends BaseItemDetailed {
  itemType: 'VEHICLE';
  brand: string;
  model: string;
  year: number;
  registrationNumber: string;
  vehicleType: string;
  mileage: number;
  serviceHistoryAvailable: boolean;
  lastServiceDate: string | null;
  registrationExpiryDate: string;
  numberOfKeys: number;
}

export interface OtherItemDetailed extends BaseItemDetailed {
  itemType: 'OTHER';
  category: string;
}

export type AnyItemDetailed =
  | GoldItemDetailed
  | ElectronicItemDetailed
  | WatchItemDetailed
  | VehicleItemDetailed
  | OtherItemDetailed;

export interface PawnDetailed {
  id: number;
  amount: number;
  interest: number;
  issueDate: string;
  maturityDate: string;
  defaultDurationDays: number;
  status: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
  customer: PawnCustomer;
  item: AnyItemDetailed;
  daysLeft: number;
}
