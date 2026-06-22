export type ExpenseCategory = "SUPPLIES" | "RENT" | "UTILITIES" | "SALARY" | "MAINTENANCE" | "OTHER";

// Display order + Macedonian labels for the expense categories (single source of truth).
export const EXPENSE_CATEGORIES: ExpenseCategory[] = ["SUPPLIES", "RENT", "UTILITIES", "SALARY", "MAINTENANCE", "OTHER"];
export const EXPENSE_CATEGORY_LABEL: Record<ExpenseCategory, string> = {
    SUPPLIES: "Материјали",
    RENT: "Кирија",
    UTILITIES: "Комуналии",
    SALARY: "Плата",
    MAINTENANCE: "Одржување",
    OTHER: "Останато",
};

export const MONTHS_MK = [
    "Јануари", "Февруари", "Март", "Април", "Мај", "Јуни",
    "Јули", "Август", "Септември", "Октомври", "Ноември", "Декември",
];

// GET /expenses -> ExpenseResponse (one expense row).
export interface Expense {
    id: number;
    staffId: number;
    staffName: string | null;
    category: ExpenseCategory;
    amount: number;
    description: string | null;
    date: string;
    createdAt: string;
}

// GET /expenses/summary -> ExpenseMonthlySummary (one calendar month).
export interface ExpenseMonthlySummary {
    year: number;
    month: number; // 1-12
    totalAmount: number;
    count: number;
    totalsByCategory: Partial<Record<ExpenseCategory, number>>;
}
