export interface TransactionRow {
    id: number;
    transactionCategory: string;
    amount: number;
    direction: string;
    marginAmount: number;
    marginType: string;
    createdAt: string;
    description: string;
}
