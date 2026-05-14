import { useState } from "react";
import { Ellipsis } from "lucide-react";
import ModalReadMoreExpense from "./ModalReadMoreExpense.tsx";
import { ExpenseRow } from "./types.ts";

interface Props {
    expense: ExpenseRow;
    index: number;
    expenseTypeLabels: Record<string, string>;
}

export default function Expense({ expense, index, expenseTypeLabels }: Props) {
    const [modalReadMore, setModalReadMore] = useState(false);

    return (
        <div
            style={{ background: index % 2 === 1 ? "#f0f0f0" : "#ffffff" }}
            className="grid place-items-center text-center grid-cols-[1fr_1fr_1fr_3fr_.5fr] gap-1 px-2 py-2 border-b border-black/20"
        >
            <p className="text-sm">{expense.date}</p>
            <p className="text-sm font-medium">{expenseTypeLabels[expense.expenseType] ?? expense.expenseType}</p>
            <p className="text-sm font-medium">{Number(expense.amount).toLocaleString("de-DE")}</p>
            <p className="text-sm">{expense.description}</p>
            <Ellipsis size={28} color="#888" className="cursor-pointer transition-all duration-300 hover:scale-110" onClick={() => setModalReadMore(true)} />

            {modalReadMore && (
                <ModalReadMoreExpense
                    expense={expense}
                    expenseTypeLabels={expenseTypeLabels}
                    closeModal={() => setModalReadMore(false)}
                />
            )}
        </div>
    );
}
