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

export interface PawnDetail {
  id: number;
  description: string;
  price_pawned: number;
  provision: number;
  price_to_redeem: number;
  total_days: number;
  date_from: string;
  date_to: string;
  'Days Left': number;
  // electronics / watch / vehicle
  brand?: string;
  model?: string;
  year?: number;
  // gold
  weight?: number;
  carats?: number;
  type?: string;
}

export interface ClientRecord {
  id: number;
  name: string;
  embg: string;
  telephone: string;
  telephone_2: string;
  city: string;
  date_joined: string;
}

export interface PawnInfo {
  pawn: PawnDetail;
  client: ClientRecord;
  'Days Left': number;
}

export interface ClientRow {
  Id: number;
  Name: string;
  Embg: string;
  'Telephone 1': string;
  'Telephone 2': string;
  City: string;
  'Date Joined': string;
  'Total Pawns': number | string;
  'Active Pawns': number | string;
  'Money Pawns': number | string;
  'Money Provision': number | string;
}

export interface SaleRow {
  Id: number;
  About: string;
  'Item Cost': number | string;
  'Date Bought': string;
  Name?: string;
}

export interface ExpenseRow {
  Id: number;
  Year: number;
  Month: number;
  Rent: number | string;
  Salaries: number | string;
  Bills: number | string;
  Other: number | string;
}

export interface TransactionRow {
  Id: number;
  'Client Id': number;
  Name: string;
  Embg: string;
  Category: string;
  Description: string;
  Given: number | string;
  Got: number | string;
  Profit: number | string;
  Diff: number | string;
  Date: string;
}

export interface MonthlyReportRow {
  Id: number;
  Year: number;
  Month: number;
  [key: string]: number | string;
}

export interface CashRegisterData {
  money_pawns: number;
  num_pawns: number;
  total_provision: number;
  gold_grams: number;
  money_sale_items: number;
  num_sale_items: number;
  register_money: number;
  last_updated: string;
  profit?: number;
}
