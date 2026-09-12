// The three pawn actions, named to match the backend endpoints.
export type PawnAction = 'extend' | 'redeem' | 'forfeit';

// Adequate Macedonian labels for the action buttons — single source of truth.
export const PAWN_ACTION_LABEL: Record<PawnAction, string> = {
    extend: 'Продолжи',
    redeem: 'Затвори',
    forfeit: 'Пренеси во продажба',
};

// Pawn list row (used by usePawns, the Pawn row, and ModalActions).
export interface PawnRow {
  Id: number;
  'Client Id': number;
  Name: string;
  Category: 'Electronics' | 'Gold' | 'Watch' | 'Vehicle' | 'Other';
  Status: 'ACTIVE' | 'REDEEMED' | 'FORFEITED';
  About: string;
  'Item Cost': number | string;
  Provision: number | string;
  'Days Left': number;
  'Valid Until': string;
  'Total Days': number | string;
}

// --- Pawn detail (GET /pawns/{id} -> PawnContractDetailedResponse) ---

export type ItemType = 'GOLD' | 'ELECTRONIC' | 'WATCH' | 'VEHICLE' | 'OTHER';

export interface CustomerDetail {
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

export interface ItemDetail {
  id: number;
  type: ItemType;
  origin: string;
  status: string;
  description: string | null;
  // Type-specific data is a free-form key/value map.
  attributes: Record<string, unknown> | null;
  createdAt: string;
  updatedAt: string;
}

export interface PawnExtension {
  id: number;
  previousDueDate: string;
  newDueDate: string;
  interestPaid: number;
  fee: number;
  createdAt: string;
}

// A staff-written note on a pawn. ACTIVE until someone marks it RESOLVED;
// resolved notes stay in the list, just de-emphasised.
export type PawnNoteStatus = 'ACTIVE' | 'RESOLVED';

export interface PawnNote {
  id: number;
  description: string;
  status: PawnNoteStatus;
  createdByStaffId: number;
  createdAt: string;
  resolvedAt: string | null;
}

export interface PawnDetailed {
  id: number;
  customer: CustomerDetail;
  item: ItemDetail;
  createdByStaffId: number;
  createdByStaffName: string | null;
  principalAmount: number;
  interestAmount: number;
  termDays: number;
  issueDate: string;
  dueDate: string;
  originalDueDate: string;
  status: string;
  daysOverdue: number;
  redeemedAt: string | null;
  forfeitedAt: string | null;
  extensions: PawnExtension[];
  notes: PawnNote[];
  createdAt: string;
  updatedAt: string;
}
