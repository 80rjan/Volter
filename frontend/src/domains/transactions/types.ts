import { PawnDetailed } from "../pawns/types.ts";
import { SaleDetailed } from "../sales/types.ts";

export type TransactionType = "PAWN" | "SALE" | "EXPENSE" | "CASH_REGISTER";
export type TransactionDirection = "IN" | "OUT";

// Transaction list row (GET /transactions -> TransactionResponse).
export interface TransactionRow {
    id: number;
    staffId: number;
    cashRegisterSessionId: number;
    type: TransactionType;
    amount: number;
    direction: TransactionDirection;
    description: string;
    createdAt: string;
}

// --- Transaction detail (GET /transactions/{id} -> TransactionDetailedResponse) ---

export interface TransactionStaff {
    id: number;
    fullName: string;
    username: string;
    status: string;
    managerId: number | null;
    createdAt: string;
    deletedAt: string | null;
}

export interface TransactionExpense {
    id: number;
    staffId: number;
    staffName: string | null;
    category: string;
    amount: number;
    description: string;
    date: string;
    createdAt: string;
}

export interface TransactionCashSession {
    id: number;
    cashRegisterId: number;
    cashRegisterCode: string;
    staffId: number;
    openedAt: string;
    closedAt: string | null;
    openingBalance: number;
    currentBalance: number;
    closingBalance: number | null;
    expectedInterest: number | null;
    status: string;
}

export interface TransactionDetailed {
    id: number;
    type: TransactionType;
    amount: number;
    direction: TransactionDirection;
    description: string | null;
    createdAt: string;
    cashRegisterSessionId: number | null;
    staff: TransactionStaff | null;
    // Exactly one of these matches `type`; the rest are null.
    pawn: PawnDetailed | null;
    sale: SaleDetailed | null;
    expense: TransactionExpense | null;
    cashRegisterSession: TransactionCashSession | null;
}
