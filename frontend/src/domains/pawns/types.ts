import { ClientRecord } from "../../shared/types";

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
  brand?: string;
  model?: string;
  year?: number;
  weight?: number;
  carats?: number;
  type?: string;
}

export interface PawnInfo {
  pawn: PawnDetail;
  client: ClientRecord;
  'Days Left': number;
}
