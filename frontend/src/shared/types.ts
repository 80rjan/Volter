export interface CashRegisterData {
  openedAt: string;
  updatedAt: string;
  openingBalance: number;
  currentBalance: number;
  expectedPawnInterest: number;
}

// Cash register session (GET /cash-register-sessions -> CashRegisterSessionResponse).
export interface CashSession {
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
  status: "OPEN" | "CLOSED";
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
