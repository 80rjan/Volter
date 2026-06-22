import { CashSession } from "../../shared/types.ts";

export type { CashSession };

export type DiscrepancyType = "SHORTAGE" | "OVERAGE";
export type DiscrepancyPhase = "OPENING" | "CLOSING";
export type DiscrepancyStatus = "OPEN" | "RESOLVED";

// GET /cash-register-session-discrepancies -> DiscrepancyResponse
export interface Discrepancy {
    id: number;
    sessionId: number;
    staffId: number;
    resolvedByStaffId: number | null;
    expectedAmount: number;
    countedAmount: number;
    difference: number;
    type: DiscrepancyType;
    phase: DiscrepancyPhase;
    status: DiscrepancyStatus;
    resolutionNote: string | null;
    createdAt: string;
    resolvedAt: string | null;
}

// The operator block of GET /cash-register-sessions/{id}/detailed (StaffResponse).
export interface CashSessionOperator {
    id: number;
    fullName: string;
    username: string;
    status: string;
    managerId: number | null;
    createdAt: string;
    deletedAt: string | null;
}

// GET /cash-register-sessions/{id}/detailed -> CashRegisterSessionDetailedResponse
export interface CashSessionDetailed {
    session: CashSession;
    staff: CashSessionOperator | null;
}
