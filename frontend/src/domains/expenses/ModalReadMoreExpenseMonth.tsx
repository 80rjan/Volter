import ReactDom from "react-dom";
import { useEffect, useRef, useState } from "react";
import axios from "axios";
import { X, Wallet } from "lucide-react";
import Loading from "../../shared/components/Loading.tsx";
import { API_BASE } from "../../shared/api/config.ts";
import { Expense, ExpenseMonthlySummary, EXPENSE_CATEGORIES, EXPENSE_CATEGORY_LABEL, MONTHS_MK } from "./types.ts";

interface Props {
    summary: ExpenseMonthlySummary;
    staffId?: string | null;
    closeModal: (e?: React.MouseEvent) => void;
}

const money = (n: number | null | undefined) => (n == null ? "0" : Number(n).toLocaleString("de-DE"));
const pad = (n: number) => String(n).padStart(2, "0");

const cols = "grid-cols-[1.2fr_1.3fr_1fr_2.5fr_1.4fr]";

export default function ModalReadMoreExpenseMonth({ summary, staffId, closeModal }: Props) {
    const { year, month } = summary;
    const dateFrom = `${year}-${pad(month)}-01`;
    const dateTo = `${year}-${pad(month)}-${pad(new Date(year, month, 0).getDate())}`;

    const [rows, setRows] = useState<Expense[]>([]);
    const [filterCategory, setFilterCategory] = useState("");
    const [loading, setLoading] = useState(false);
    const page = useRef(0);
    const size = 50;
    const [isLastPage, setIsLastPage] = useState(false);
    const isFetchingRef = useRef(false);
    const fetchedIds = useRef(new Set<number>());
    const scrollRef = useRef<HTMLDivElement>(null);

    const fetchRows = (pg: number, isLoading: boolean) => {
        if (isFetchingRef.current) return;
        isFetchingRef.current = true;
        setLoading(isLoading);
        const params = new URLSearchParams({ page: String(pg), size: String(size), sort: "date,DESC", dateFrom, dateTo });
        if (staffId) params.set("staffId", staffId);
        if (filterCategory) params.set("category", filterCategory);
        axios.get(`${API_BASE}/expenses?${params}`)
            .then(res => {
                const fresh: Expense[] = (res.data.content ?? []).filter((e: Expense) => !fetchedIds.current.has(e.id));
                fresh.forEach(e => fetchedIds.current.add(e.id));
                setRows(prev => [...prev, ...fresh]);
                setIsLastPage(res.data.page ? res.data.page.number >= res.data.page.totalPages - 1 : true);
            })
            .catch(error => console.error("Error fetching month expenses:", error))
            .finally(() => { setLoading(false); isFetchingRef.current = false; });
    };

    useEffect(() => {
        fetchedIds.current.clear();
        setRows([]);
        page.current = 0;
        fetchRows(0, true);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [filterCategory]);

    const onScroll = () => {
        const el = scrollRef.current;
        if (!el) return;
        if (el.scrollHeight - el.scrollTop - el.clientHeight <= el.scrollHeight * 0.3 && !isLastPage && !isFetchingRef.current) {
            page.current += 1;
            fetchRows(page.current, false);
        }
    };

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" onClick={closeModal} />
            <div className="flex flex-col gap-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-8 rounded-lg w-[min(900px,92%)] max-h-[90vh] overflow-hidden">
                <div className="flex justify-between items-center">
                    <div className="flex items-center gap-3">
                        <span className="flex h-10 w-10 items-center justify-center rounded-full bg-green/15 text-green">
                            <Wallet size={20} />
                        </span>
                        <h1 className="text-2xl font-semibold">Расходи — {MONTHS_MK[month - 1]} {year}</h1>
                    </div>
                    <button className="close-x-btn" onClick={closeModal}><X size={28} /></button>
                </div>

                {/* Per-category totals for the month (independent of the row filter below). */}
                <div className="flex flex-wrap gap-2">
                    {EXPENSE_CATEGORIES.map(c => (
                        <div key={c} className="flex flex-col bg-white rounded px-3 py-2 shadow-[0_0_4px_rgba(0,0,0,0.15)] min-w-[7rem]">
                            <span className="text-[#666] text-xs">{EXPENSE_CATEGORY_LABEL[c]}</span>
                            <span className="text-sm font-bold">{money(summary.totalsByCategory[c] ?? 0)} ден</span>
                        </div>
                    ))}
                    <div className="flex flex-col bg-green/10 rounded px-3 py-2 shadow-[0_0_4px_rgba(0,0,0,0.15)] min-w-[7rem]">
                        <span className="text-green text-xs font-medium">Вкупно</span>
                        <span className="text-sm font-bold text-green">{money(summary.totalAmount)} ден</span>
                    </div>
                </div>

                <hr className="border-black/15" />

                <div className="flex items-center justify-between gap-3">
                    <span className="font-medium">Расходи ({summary.count})</span>
                    <select
                        className="bg-white border-none rounded text-sm p-2 shadow-[0_0_4px_rgba(0,0,0,0.2)]"
                        style={{ color: filterCategory === "" ? "#888" : "#000" }}
                        value={filterCategory}
                        onChange={e => setFilterCategory(e.target.value)}
                    >
                        <option value="">Сите типови</option>
                        {EXPENSE_CATEGORIES.map(c => <option key={c} value={c}>{EXPENSE_CATEGORY_LABEL[c]}</option>)}
                    </select>
                </div>

                <div className="flex flex-col bg-white rounded-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] overflow-hidden flex-1 min-h-0">
                    <div className={`grid place-items-center ${cols} gap-2 px-2 py-2 border-b-2 border-black/20 text-[#eee] bg-[#666] text-xs font-medium`}>
                        <div>Датум</div><div>Тип</div><div>Износ</div><div>Опис</div><div>Внел</div>
                    </div>
                    <div ref={scrollRef} onScroll={onScroll} className="overflow-y-auto overflow-x-hidden flex-1 scrollbar-thin">
                        {loading ? <Loading /> : rows.length === 0 ? (
                            <p className="text-center text-sm text-[#888] py-6">Нема расходи за прикажување.</p>
                        ) : rows.map((e, index) => (
                            <div
                                key={e.id}
                                style={{ background: index % 2 === 1 ? "#f0f0f0" : "#ffffff" }}
                                className={`grid place-items-center text-center ${cols} gap-2 px-2 py-1 border-b border-black/20`}
                            >
                                <p className="text-xs">{String(e.date).substring(0, 10)}</p>
                                <p className="text-xs">{EXPENSE_CATEGORY_LABEL[e.category] ?? e.category}</p>
                                <p className="text-xs font-bold italic">{money(e.amount)}</p>
                                <p className="text-xs">{e.description || "—"}</p>
                                <p className="text-xs">{e.staffName || `#${e.staffId}`}</p>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </>,
        document.getElementById("portal")!
    );
}
