export type SaleStatus = 'AVAILABLE' | 'SOLD' | 'CANCELED';
export type ItemType = 'GOLD' | 'ELECTRONIC' | 'WATCH' | 'VEHICLE' | 'OTHER';

// Sales list row.
export interface SaleRow {
  Id: number;
  Customer: string;
  Status: SaleStatus;
  Category: 'Electronics' | 'Gold' | 'Watch' | 'Vehicle' | 'Other';
  About: string;
  'Item Cost': number;          // purchasePrice
  'Sale Price': number | null;  // salePrice (null until sold)
  Profit: number | null;
  'Date Bought': string;        // createdAt
  'Sold At': string | null;     // soldAt
}

// --- Sale detail (GET /sales/{id} -> SaleDetailedResponse) ---

export interface SaleCustomerDetail {
  id: number;
  fullName: string;
  nationalId: string;
  phonePrimary: string;
  phoneSecondary: string | null;
  address: string;
  city: string;
  createdAt: string;
  updatedAt: string;
}

export interface SaleItemDetail {
  id: number;
  type: ItemType;
  origin: string;
  status: string;
  description: string | null;
  attributes: Record<string, unknown> | null;
  createdAt: string;
  updatedAt: string;
}

export interface SaleDetailed {
  id: number;
  customer: SaleCustomerDetail;
  item: SaleItemDetail;
  createdByStaffId: number;
  status: SaleStatus;
  purchasePrice: number;
  salePrice: number | null;
  profit: number | null;
  soldAt: string | null;
  createdAt: string;
  updatedAt: string;
}
