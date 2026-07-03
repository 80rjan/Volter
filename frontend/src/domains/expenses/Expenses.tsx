import {useEffect, useMemo, useState} from "react";
import axios from "axios";
import {Plus, Ellipsis, Lock, ChevronUp, ChevronDown, Minus} from "lucide-react";
import CashRegister from "../../shared/components/CashRegister.tsx";
import Loading from "../../shared/components/Loading.tsx";
import ModalAddNewExpense from "./ModalAddNewExpense.tsx";
import ModalReadMoreExpenseMonth from "./ModalReadMoreExpenseMonth.tsx";
import {
    ExpenseMonthlySummary,
    ExpenseCategory,
    EXPENSE_CATEGORIES,
    EXPENSE_CATEGORY_LABEL,
    MONTHS_MK
} from "./types.ts";
import {API_BASE} from "../../shared/api/config.ts";
import {useAuth} from "../../GlobalContext.tsx";
import {useTeam} from "../../shared/utils/useTeam.ts";

const money = (n: number | null | undefined) => (!n ? "—" : Number(n).toLocaleString("de-DE"));
const cols = "grid-cols-[1.3fr_repeat(6,1fr)_1.1fr_0.5fr]";

// Client-side sort key per column ("Month"/"Total"/a category).
const sortValue = (s: ExpenseMonthlySummary, by: string): number =>
    by === "Month" ? s.year * 12 + s.month
        : by === "Total" ? s.totalAmount
            : (s.totalsByCategory[by as ExpenseCategory] ?? 0);

function SortIcon({dir}: { dir: number }) {
    return dir === 0 ? <Minus size={14}/> : dir === -1 ? <ChevronDown size={14}/> : <ChevronUp size={14}/>;
}

export default function Expenses() {
    const {can} = useAuth();
    const allowed = can("EXPENSE_READ");
    const canWrite = can("EXPENSE_WRITE");

    const team = useTeam();
    const [summaries, setSummaries] = useState<ExpenseMonthlySummary[]>([]);
    const [loading, setLoading] = useState(false);
    const [filterStaff, setFilterStaff] = useState("");
    const [filterCategory, setFilterCategory] = useState("");
    const [filterFrom, setFilterFrom] = useState("");
    const [filterTo, setFilterTo] = useState("");
    const [refresh, setRefresh] = useState(false);

    const [modalAdd, setModalAdd] = useState(false);
    const [selected, setSelected] = useState<ExpenseMonthlySummary | null>(null);
    const [orderBy, setOrderBy] = useState("Month");
    const [orderDir, setOrderDir] = useState(-1); // -1 desc, 1 asc

    useEffect(() => {
        if (!allowed) return;
        setLoading(true);
        const params = new URLSearchParams();
        if (filterStaff) params.set("staffId", filterStaff);
        if (filterCategory) params.set("category", filterCategory);
        if (filterFrom) params.set("dateFrom", filterFrom);
        if (filterTo) params.set("dateTo", filterTo);
        axios.get(`${API_BASE}/expenses/summary?${params}`)
            .then(res => setSummaries(res.data ?? []))
            .catch(error => console.error("Error fetching expense summary:", error))
            .finally(() => setLoading(false));
    }, [allowed, filterStaff, filterCategory, filterFrom, filterTo, refresh]);

    const sorted = useMemo(
        () => [...summaries].sort((a, b) => (sortValue(a, orderBy) - sortValue(b, orderBy)) * orderDir),
        [summaries, orderBy, orderDir]);

    const handleOrder = (by: string) => {
        if (orderBy === by) setOrderDir(d => -d);
        else {
            setOrderBy(by);
            setOrderDir(-1);
        }
    };
    const dirOf = (by: string) => (orderBy === by ? orderDir : 0);

    const inputClass = "bg-white border-none rounded text-xs font-medium px-2 py-2 shadow-sm flex-1 min-w-[140px] md:min-w-0";

    return (
        <div className="h-screen flex md:pl-16 pt-12 md:pt-0">
            <div className="flex flex-col px-3 md:px-8 pt-2 gap-3 flex-1 overflow-hidden">
                {!allowed ? (
                    <div className="flex flex-1 flex-col items-center justify-center gap-3 text-[#666]">
                        <Lock size={40}/>
                        <p className="text-sm">Немате дозвола за преглед на расходи.</p>
                    </div>
                ) : (
                    <>
                        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-[2fr_2fr_1.5fr_1.5fr_1fr_2fr] gap-2 md:gap-3">
                            <select
                                className={inputClass}
                                style={{color: filterStaff === "" ? "#888" : "#000"}}
                                value={filterStaff}
                                onChange={e => setFilterStaff(e.target.value)}
                            >
                                <option value="">Сите вработени</option>
                                {team.map(s => <option key={s.id} value={s.id}>{s.fullName}</option>)}
                            </select>
                            <select
                                className={inputClass}
                                style={{color: filterCategory === "" ? "#888" : "#000"}}
                                value={filterCategory}
                                onChange={e => setFilterCategory(e.target.value)}
                            >
                                <option value="">Сите типови</option>
                                {EXPENSE_CATEGORIES.map(c => <option key={c}
                                                                     value={c}>{EXPENSE_CATEGORY_LABEL[c]}</option>)}
                            </select>
                            <div className="flex items-center gap-2">
                                <span className="text-xs text-[#666]">Од</span>
                                <input type="date" style={{color: filterFrom ? "#000" : "#888"}} className={inputClass}
                                       value={filterFrom} onChange={e => setFilterFrom(e.target.value)}/>
                            </div>
                            <div className="flex items-center gap-2">
                                <span className="text-xs text-[#666]">До</span>
                                <input type="date" style={{color: filterTo ? "#000" : "#888"}} className={inputClass}
                                       value={filterTo} onChange={e => setFilterTo(e.target.value)}/>
                            </div>
                            {(filterStaff || filterCategory || filterFrom || filterTo) && (
                                <button onClick={() => {
                                    setFilterStaff("");
                                    setFilterCategory("");
                                    setFilterFrom("");
                                    setFilterTo("");
                                }}
                                        className="text-xs text-[#666] underline hover:text-black transition-colors">
                                    Исчисти филтри
                                </button>
                            )}

                            {canWrite && (
                                <div className="flex w-full justify-center md:col-start-6">
                                    <button
                                        className="group relative overflow-hidden flex items-center gap-2 bg-green h-fit text-white rounded px-5 py-2 text-sm shadow-[4px_2px_6px_rgba(0,0,0,0.2)] transition-all duration-300 hover:scale-105 shrink-0"
                                        onClick={() => setModalAdd(true)}
                                    >
                                    <span
                                        className="pointer-events-none absolute inset-0 -translate-x-full bg-gradient-to-r from-transparent via-white/25 to-transparent transition-transform duration-700 ease-out group-hover:translate-x-[180%]"/>
                                        <Plus size={18} color="white" strokeWidth={3}
                                              className="transition-transform duration-500 group-hover:rotate-90"/>
                                        Внеси нов расход
                                    </button>
                                </div>
                            )}
                        </div>

                        <div
                            className="flex flex-col bg-white rounded-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] overflow-x-auto flex-1 min-h-0">
                            <div
                                className={`grid place-items-center ${cols} min-w-[880px] md:min-w-0 gap-2 px-2 py-2 border-b-2 border-black/20 text-[#eee] bg-[#666] text-xs font-medium`}>
                                <div className="flex items-center cursor-pointer"
                                     onClick={() => handleOrder("Month")}>Месец <SortIcon dir={dirOf("Month")}/></div>
                                {EXPENSE_CATEGORIES.map(c => (
                                    <div key={c} className="flex items-center cursor-pointer"
                                         onClick={() => handleOrder(c)}>{EXPENSE_CATEGORY_LABEL[c]} <SortIcon
                                        dir={dirOf(c)}/></div>
                                ))}
                                <div className="flex items-center cursor-pointer"
                                     onClick={() => handleOrder("Total")}>Вкупно <SortIcon dir={dirOf("Total")}/></div>
                                <div></div>
                            </div>

                            <div className="overflow-y-auto overflow-x-hidden flex-1 scrollbar-thin min-w-[880px] md:min-w-0 svg-hover">
                                {loading ? <Loading/> : sorted.length === 0 ? (
                                    <p className="text-center text-sm text-[#888] py-6">Нема расходи за прикажување.</p>
                                ) : sorted.map((s, index) => (
                                    <div
                                        key={`${s.year}-${s.month}`}
                                        style={{background: index % 2 === 1 ? "#f0f0f0" : "#ffffff"}}
                                        className={`grid place-items-center text-center ${cols} gap-2 px-2 py-1 border-b border-black/20`}
                                    >
                                        <p className="text-xs font-semibold">{MONTHS_MK[s.month - 1]} {s.year}</p>
                                        {EXPENSE_CATEGORIES.map(c => (
                                            <p key={c} className="text-xs">{money(s.totalsByCategory[c])}</p>
                                        ))}
                                        <p className="text-xs font-bold italic">{money(s.totalAmount)}</p>
                                        <Ellipsis size={18} color="#888" className="cursor-pointer"
                                                  onClick={() => setSelected(s)}/>
                                    </div>
                                ))}
                            </div>
                        </div>

                        <CashRegister refreshDependency={refresh}/>
                    </>
                )}
            </div>

            {modalAdd &&
                <ModalAddNewExpense closeModal={() => setModalAdd(false)} refresh={() => setRefresh(p => !p)}/>}
            {selected && <ModalReadMoreExpenseMonth summary={selected} staffId={filterStaff || null}
                                                    closeModal={() => setSelected(null)}/>}
        </div>
    );
}
