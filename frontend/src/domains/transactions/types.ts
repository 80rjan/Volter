import { PawnDetailed } from "../pawns/types.ts";
import { SaleDetailed } from "../sales/types.ts";

// The activity list's categories: the five ledger types, plus non-monetary
// events that have no transaction behind them.
export type TransactionType =
    | "PAWN" | "SALE" | "EXPENSE" | "CASH_REGISTER" | "STAFF_BONUS"
    | "PAWN_FORFEITED";
export type TransactionDirection = "IN" | "OUT";

// Which source a row came from. A PAWN_EVENT row moved no money, so its amount,
// direction and session are null, and its detail lives behind a different URL.
export type ActivityKind = "TRANSACTION" | "PAWN_EVENT";

// Activity list row (GET /transactions -> TransactionResponse).
export interface TransactionRow {
    // Unique across the whole list ("T12" / "E3"); `id` is only unique per kind.
    entryId: string;
    kind: ActivityKind;
    id: number;
    staffId: number;
    cashRegisterSessionId: number | null;
    type: TransactionType;
    amount: number | null;
    direction: TransactionDirection | null;
    description: string;
    clientName: string | null;
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
    // Null on a non-monetary event.
    amount: number | null;
    direction: TransactionDirection | null;
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
