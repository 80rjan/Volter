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

export interface ClientRecord {
  id: number;
  name: string;
  embg: string;
  telephone: string;
  telephone_2: string;
  city: string;
  date_joined: string;
}
