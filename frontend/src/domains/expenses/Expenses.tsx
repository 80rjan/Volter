import { useEffect, useRef, useState } from "react";
import axios from "axios";
import Nav from "../../shared/components/Nav.tsx";
import { Plus, ChevronUp, ChevronDown, Minus } from "lucide-react";
import CashRegister from "../../shared/components/CashRegister.tsx";
import Loading from "../../shared/components/Loading.tsx";
import ModalAddNewExpense from "./ModalAddNewExpense.tsx";
import ExpenseRow from "./Expense.tsx";
import { ExpenseRow as ExpenseRowType } from "./types.ts";

function SortIcon({ dir }: { dir: number }) {
    return dir === 0 ? <Minus size={14} /> : dir === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />;
}

export default function Expenses() {
    const [allExpenses, setAllExpenses] = useState<ExpenseRowType[]>([]);
    const [orderBy, setOrderBy] = useState("Year * 100 %2B Month");
    const orderDirectionArr = useRef([-1, 0, 0, 0, 0, 0]);
    const [orderDirection, setOrderDirection] = useState("DESC");
    const [searchByMonth, setSearchByMonth] = useState("");
    const [searchByYear, setSearchByYear] = useState("");
    const [modalAddNewExpense, setModalAddNewExpense] = useState(false);
    const [refresh, setRefresh] = useState(false);
    const offset = useRef(0);
    const limit = 40;
    const [isLastPage, setIsLastPage] = useState(false);
    const scrollableExpensesRef = useRef<HTMLDivElement>(null);
    const [loading, setLoading] = useState(false);
    const isFetchingRef = useRef(false);
    const [isFetching, setIsFetching] = useState(false);
    const fetchedExpenseIds = useRef(new Set<number>());

    const fetchExpenses = (limit: number, off: number, order: string, direction: string, month: string, year: string, isLoading: boolean) => {
        if (isFetching) return;
        setIsFetching(true);
        setLoading(isLoading);
        isFetchingRef.current = true;
        axios.get(`http://localhost:3000/expenses?limit=${limit}&offset=${off}&orderBy=${order}&orderDirection=${direction}&searchByMonth=${month}&searchByYear=${year}`)
            .then(res => {
                const newUnique = res.data.filter((e: ExpenseRowType) => !fetchedExpenseIds.current.has(e.Id));
                newUnique.forEach((e: ExpenseRowType) => fetchedExpenseIds.current.add(e.Id));
                setAllExpenses(prev => [...prev, ...newUnique]);
                setIsLastPage(res.data.length < limit);
            })
            .catch(error => console.error("Error fetching expenses:", error))
            .finally(() => { setLoading(false); isFetchingRef.current = false; setIsFetching(false); });
    };

    useEffect(() => {
        const el = scrollableExpensesRef.current!;
        const handleScroll = () => {
            if (el.scrollHeight - el.scrollTop - el.clientHeight <= el.scrollHeight * 0.3 && !isLastPage && !isFetchingRef.current) {
                offset.current += limit;
                fetchExpenses(limit, offset.current, orderBy, orderDirection, searchByMonth, searchByYear, false);
            }
        };
        el.addEventListener("scroll", handleScroll);
        return () => el.removeEventListener("scroll", handleScroll);
    }, [isLastPage, refresh, orderBy, orderDirection, searchByMonth, searchByYear]);

    useEffect(() => {
        fetchedExpenseIds.current.clear();
        setAllExpenses([]);
        offset.current = 0;
        fetchExpenses(limit, 0, orderBy, orderDirection, searchByMonth, searchByYear, true);
    }, [refresh, orderBy, orderDirection, searchByMonth, searchByYear]);

    const handleOrder = (by: string, index: number) => {
        const newDir = new Array(orderDirectionArr.current.length).fill(0);
        newDir[index] = orderDirectionArr.current[index] === 0 ? 1 : orderDirectionArr.current[index] === 1 ? -1 : 1;
        orderDirectionArr.current = newDir;
        setOrderDirection(newDir.includes(-1) ? "DESC" : "ASC");
        setOrderBy(by);
    };

    const headers: [string, string | null, number][] = [
        ["Година", "Year * 100 %2B Month", 0], ["Месец", "Month", 1], ["Кирија", "Rent", 2],
        ["Плати", "Salaries", 3], ["Сметки", "Bills", 4], ["Друго", "Other", 5], ["Повеќе", null, -1],
    ];

    const inputClass = "border-none rounded text-base w-1/4 p-2 shadow-[0_0_8px_rgba(0,0,0,0.2)]";

    return (
        <div className="h-screen grid grid-cols-[max(15%,240px)_auto]">
            <Nav />
            <div className="flex flex-col px-8 pt-8 gap-4 flex-1 overflow-hidden">
                <div className="flex justify-between w-full">
                    <h1 className="text-3xl font-semibold">Расходи</h1>
                    <div className="flex items-center gap-4">
                        <button
                            className="flex items-center gap-2 bg-green h-fit text-white rounded px-6 py-3 text-base shadow-[4px_2px_6px_rgba(0,0,0,0.2)] transition-all duration-300 hover:scale-105 group"
                            onClick={() => setModalAddNewExpense(true)}
                        >
                            <Plus size={22} color="white" strokeWidth={3} className="transition-transform duration-500 group-hover:rotate-90" />
                            Внеси Нов Расход
                        </button>
                    </div>
                    {modalAddNewExpense && <ModalAddNewExpense closeModal={() => setModalAddNewExpense(false)} refresh={() => setRefresh(prev => !prev)} />}
                </div>

                <div className="flex justify-evenly w-full">
                    <input className={inputClass} placeholder="Пребарувај по месец (број)" type="number" onKeyUp={e => setSearchByMonth((e.target as HTMLInputElement).value)} />
                    <input className={inputClass} placeholder="Пребарувај по година (број)" type="number" onKeyUp={e => setSearchByYear((e.target as HTMLInputElement).value)} />
                </div>

                <div className="flex flex-col bg-white rounded-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] overflow-hidden flex-1 min-h-0">
                    <div className="grid place-items-center grid-cols-[repeat(7,1fr)] px-2 py-4 border-b-2 border-black/20 text-[#eee] bg-[#666]">
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
                            <ExpenseRow key={index} expense={expense} index={index} />
                        ))}
                    </div>
                </div>

                <CashRegister refreshDependency={refresh} />
            </div>
        </div>
    );
}
