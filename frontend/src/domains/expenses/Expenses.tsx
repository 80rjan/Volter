import { useEffect, useRef, useState } from "react";
import axios from "axios";
import Nav from "../../shared/components/Nav.tsx";
import { Plus, ChevronUp, ChevronDown, Minus } from "lucide-react";
import CashRegister from "../../shared/components/CashRegister.tsx";
import Loading from "../../shared/components/Loading.tsx";
import ModalAddNewExpense from "./ModalAddNewExpense.tsx";
import ExpenseRow from "./Expense.tsx";
import { ExpenseRow as ExpenseRowType } from "./types.ts";
import { API_BASE } from "../../shared/api/config.ts";

const SORT_FIELD: Record<string, string> = {
    Date: 'date',
    Type: 'expenseType',
    Amount: 'amount',
    Description: 'description',
};

const EXPENSE_TYPE_LABELS: Record<string, string> = {
    RENT: 'Кирија', SALARIES: 'Плати', BILLS: 'Сметки', UTILITIES: 'Комуналии',
    SUPPLIES: 'Материјали', MAINTENANCE: 'Одржување', MARKETING: 'Маркетинг',
    TRAVEL: 'Патувања', OTHER: 'Останато',
};

function mapExpenseResponse(r: any): ExpenseRowType {
    return {
        id: r.id,
        expenseType: r.expenseType,
        amount: r.amount,
        description: r.description ?? '',
        date: r.date ?? '',
    };
}

function SortIcon({ dir }: { dir: number }) {
    return dir === 0 ? <Minus size={14} /> : dir === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />;
}

export default function Expenses() {
    const [allExpenses, setAllExpenses] = useState<ExpenseRowType[]>([]);
    const [orderBy, setOrderBy] = useState("Date");
    const orderDirectionArr = useRef([-1, 0, 0, 0]);
    const [orderDirection, setOrderDirection] = useState("DESC");
    const [modalAddNewExpense, setModalAddNewExpense] = useState(false);
    const [refresh, setRefresh] = useState(false);
    const page = useRef(0);
    const size = 40;
    const [isLastPage, setIsLastPage] = useState(false);
    const scrollableExpensesRef = useRef<HTMLDivElement>(null);
    const [loading, setLoading] = useState(false);
    const isFetchingRef = useRef(false);
    const [isFetching, setIsFetching] = useState(false);
    const fetchedExpenseIds = useRef(new Set<number>());

    const fetchExpenses = (pg: number, order: string, direction: string, isLoading: boolean) => {
        if (isFetching) return;
        setIsFetching(true);
        setLoading(isLoading);
        isFetchingRef.current = true;
        const params = new URLSearchParams({
            page: String(pg),
            size: String(size),
            sort: `${SORT_FIELD[order] ?? 'date'},${direction}`,
        });
        axios.get(`${API_BASE}/expenses?${params}`)
            .then(res => {
                const expenses: ExpenseRowType[] = (res.data.content ?? []).map(mapExpenseResponse);
                const newUnique = expenses.filter(e => !fetchedExpenseIds.current.has(e.id));
                newUnique.forEach(e => fetchedExpenseIds.current.add(e.id));
                setAllExpenses(prev => [...prev, ...newUnique]);
                setIsLastPage(res.data.last ?? true);
            })
            .catch(error => console.error("Error fetching expenses:", error))
            .finally(() => { setLoading(false); isFetchingRef.current = false; setIsFetching(false); });
    };

    useEffect(() => {
        const el = scrollableExpensesRef.current!;
        const handleScroll = () => {
            if (el.scrollHeight - el.scrollTop - el.clientHeight <= el.scrollHeight * 0.3 && !isLastPage && !isFetchingRef.current) {
                page.current += 1;
                fetchExpenses(page.current, orderBy, orderDirection, false);
            }
        };
        el.addEventListener("scroll", handleScroll);
        return () => el.removeEventListener("scroll", handleScroll);
    }, [isLastPage, refresh, orderBy, orderDirection]);

    useEffect(() => {
        fetchedExpenseIds.current.clear();
        setAllExpenses([]);
        page.current = 0;
        fetchExpenses(0, orderBy, orderDirection, true);
    }, [refresh, orderBy, orderDirection]);

    const handleOrder = (by: string, index: number) => {
        const newDir = new Array(orderDirectionArr.current.length).fill(0);
        newDir[index] = orderDirectionArr.current[index] === 0 ? 1 : orderDirectionArr.current[index] === 1 ? -1 : 1;
        orderDirectionArr.current = newDir;
        setOrderDirection(newDir.includes(-1) ? "DESC" : "ASC");
        setOrderBy(by);
    };

    const headers: [string, string | null, number][] = [
        ["Датум", "Date", 0], ["Тип", "Type", 1], ["Износ", "Amount", 2],
        ["Опис", "Description", 3], ["Повеќе", null, -1],
    ];

    return (
        <div className="h-screen grid grid-cols-[max(15%,240px)_auto]">
            <Nav />
            <div className="flex flex-col px-8 pt-8 gap-4 flex-1 overflow-hidden">
                <div className="flex justify-between w-full">
                    <h1 className="text-3xl font-semibold">Расходи</h1>
                    <button
                        className="flex items-center gap-2 bg-green h-fit text-white rounded px-6 py-3 text-base shadow-[4px_2px_6px_rgba(0,0,0,0.2)] transition-all duration-300 hover:scale-105 group"
                        onClick={() => setModalAddNewExpense(true)}
                    >
                        <Plus size={22} color="white" strokeWidth={3} className="transition-transform duration-500 group-hover:rotate-90" />
                        Внеси Нов Расход
                    </button>
                    {modalAddNewExpense && <ModalAddNewExpense closeModal={() => setModalAddNewExpense(false)} refresh={() => setRefresh(prev => !prev)} />}
                </div>

                <div className="flex flex-col bg-white rounded-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] overflow-hidden flex-1 min-h-0">
                    <div className="grid place-items-center grid-cols-[1fr_1fr_1fr_3fr_.5fr] px-2 py-4 border-b-2 border-black/20 text-[#eee] bg-[#666]">
                        {headers.map(([label, key, idx]) => (
                            <div
                                key={label}
                                className={`text-sm font-semibold flex items-center ${key ? "cursor-pointer" : "cursor-default"}`}
                                onClick={key ? () => handleOrder(key, idx) : undefined}
                            >
                                {label} {key && <SortIcon dir={orderDirectionArr.current[idx]} />}
                            </div>
                        ))}
                    </div>

                    <div ref={scrollableExpensesRef} className="overflow-y-auto overflow-x-hidden flex-1 scrollbar-thin">
                        {loading ? <Loading /> : allExpenses.map((expense, index) => (
                            <ExpenseRow key={expense.id} expense={expense} index={index} expenseTypeLabels={EXPENSE_TYPE_LABELS} />
                        ))}
                    </div>
                </div>

                <CashRegister refreshDependency={refresh} />
            </div>
        </div>
    );
}
