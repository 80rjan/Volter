export type ReportType = "MONTHLY_SUMMARY" | "STAFF_PERFORMANCE";

export const MONTHS_MK = [
    "Јануари", "Февруари", "Март", "Април", "Мај", "Јуни",
    "Јули", "Август", "Септември", "Октомври", "Ноември", "Декември",
];

// GET /reports -> ReportResponse (list row; metrics derived from payload server-side).
export interface MonthlyReportRow {
    id: number;
    shopId: number;
    ownerStaffId: number | null;
    subjectStaffId: number | null;
    type: ReportType;
    dateFrom: string;
    dateTo: string;
    generatedAt: string;
    totalRevenue: number;
    totalExpenses: number;
    netProfit: number;
    moneyGivenToClients: number;
}

// --- payload shapes (GET /reports/{id} -> ReportDetailedResponse.payload) ---

export interface CashFlow {
    totalTransactions: number;
    inflow: number;
    outflow: number;
    net: number;
}
export interface ExpenseBucket {
    count: number;
    amount: number;
}
export interface CashRegisterSummary {
    totalTransactions: number;
    inflow: number;
    outflow: number;
    net: number;
    withdrawals: number;
    deposits: number;
    adjustments: number;
}
export interface SessionSummary {
    sessionId: number;
    date: string;
    totalTransactions: number;
    inflow: number;
    outflow: number;
    net: number;
}

export interface ReportPayload {
    pawns?: Record<string, CashFlow>;
    sales?: Record<string, CashFlow>;
    expenses?: Record<string, ExpenseBucket>;
    cashRegister?: CashRegisterSummary;
    sessions?: SessionSummary[];
    // Pawn loan principal, as a position and as a flow. Optional: reports generated
    // before these existed only carry them once the backfill migration has run.
    pawnPrincipalAtPeriodStart?: number;
    pawnPrincipalGiven?: number;
}

export interface MonthlyReportDetailed {
    id: number;
    shopId: number;
    type: ReportType;
    dateFrom: string;
    dateTo: string;
    generatedAt: string;
    payload: ReportPayload | null;
}

// GET /reports/period -> PeriodReportResponse (computed on the fly, not persisted).
export interface PeriodReport {
    dateFrom: string;
    dateTo: string;
    totalRevenue: number;
    totalExpenses: number;
    netProfit: number;
    moneyGivenToClients: number;
    payload: ReportPayload | null;
}
