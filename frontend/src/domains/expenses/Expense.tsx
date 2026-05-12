import { Ellipsis } from "lucide-react";
import { useEffect, useState } from "react";
import axios from "axios";
import ModalReadMoreExpense from "./ModalReadMoreExpense.tsx";
import Loading from "../../shared/components/Loading.tsx";
import { ExpenseRow } from "./types.ts";

interface Props {
    expense: ExpenseRow;
    index: number;
}

export default function Expense({ expense, index }: Props) {
    const [modalReadMore, setModalReadMore] = useState(false);
    const [expenseInfo, setExpenseInfo] = useState<any>(null);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (expenseInfo != null) setModalReadMore(true);
    }, [expenseInfo]);

    const fetchExpense = () => {
        setLoading(true);
        axios.get(`http://localhost:3000/expenses/getExpense?month=${expense.Month}&year=${expense.Year}`)
            .then(res => setExpenseInfo(res.data))
            .catch(error => console.error("Error fetching expense:", error))
            .finally(() => setLoading(false));
    };

    return (
        <div
            style={{ background: index % 2 === 1 ? "#f0f0f0" : "#ffffff" }}
            className="grid place-items-center text-center grid-cols-[repeat(7,1fr)] gap-1 px-2 py-2 border-b border-black/20"
        >
            <p className="text-sm font-medium">{expense.Year}</p>
            <p className="text-sm font-medium">{expense.Month}</p>
            <p className="text-sm font-medium">{Number(expense.Rent).toLocaleString("de-DE")}</p>
            <p className="text-sm font-medium">{Number(expense.Salaries).toLocaleString("de-DE")}</p>
            <p className="text-sm font-medium">{Number(expense.Bills).toLocaleString("de-DE")}</p>
            <p className="text-sm font-medium">{Number(expense.Other).toLocaleString("de-DE")}</p>
            {loading ? <Loading width={30} height={30} /> : (
                <Ellipsis size={28} color="#888" className="cursor-pointer transition-all duration-300 hover:scale-110" onClick={fetchExpense} />
            )}

            {modalReadMore && (
                <ModalReadMoreExpense
                    expenseInfo={expenseInfo}
                    closeModal={() => setModalReadMore(false)}
                />
            )}
        </div>
    );
}
