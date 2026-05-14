export interface CashRegisterData {
  openedAt: string;
  updatedAt: string;
  openingBalance: number;
  currentBalance: number;
  expectedPawnInterest: number;
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
