import { useEffect, useMemo, useState } from "react";
import axios from "axios";
import { ChevronDown, ChevronUp, Minus, Ellipsis, Lock } from "lucide-react";
import CashRegister from "../../shared/components/CashRegister.tsx";
import Loading from "../../shared/components/Loading.tsx";
import ModalReadMoreMonthReport from "./ModalReadMoreMonthReport.tsx";
import { MonthlyReportRow, MONTHS_MK } from "./types.ts";
import { API_BASE } from "../../shared/api/config.ts";
import { useAuth } from "../../GlobalContext.tsx";

const money = (n: number | null | undefined) => (n == null ? "—" : Number(n).toLocaleString("de-DE"));
const period = (r: MonthlyReportRow) => {
    const m = parseInt(r.dateFrom.substring(5, 7), 10);
    return `${MONTHS_MK[m - 1] ?? m} ${r.dateFrom.substring(0, 4)}`;
};

// Client-side sort keys per column (reports are few; fetched whole).
const SORT_KEY: Record<string, (r: MonthlyReportRow) => number | string> = {
    Period: r => r.dateFrom,
    Revenue: r => r.totalRevenue,
    ToClients: r => r.moneyGivenToClients,
    Expenses: r => r.totalExpenses,
    NetProfit: r => r.netProfit,
};

function SortIcon({ dir }: { dir: number }) {
    return dir === 0 ? <Minus size={14} /> : dir === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />;
}

const cols = "grid-cols-[1.5fr_1.2fr_1.4fr_1.2fr_1.2fr_0.5fr]";

export default function MonthlyReport() {
    const { can } = useAuth();
    const allowed = can("REPORT_READ");

    const [reports, setReports] = useState<MonthlyReportRow[]>([]);
    const [loading, setLoading] = useState(false);
    const [filterFrom, setFilterFrom] = useState("");
    const [filterTo, setFilterTo] = useState("");
    const [orderBy, setOrderBy] = useState("Period");
    const [orderDir, setOrderDir] = useState(-1); // -1 desc, 1 asc
    const [selected, setSelected] = useState<MonthlyReportRow | null>(null);

    useEffect(() => {
        if (!allowed) return;
        setLoading(true);
        const params = new URLSearchParams({ size: "1000", type: "MONTHLY_SUMMARY", sort: "dateFrom,DESC" });
        if (filterFrom) params.set("dateFrom", filterFrom);
        if (filterTo) params.set("dateTo", filterTo);
        axios.get(`${API_BASE}/reports?${params}`)
            .then(res => setReports(res.data.content ?? []))
            .catch(error => console.error("Error fetching monthly reports:", error))
            .finally(() => setLoading(false));
    }, [allowed, filterFrom, filterTo]);

    const sorted = useMemo(() => {
        const key = SORT_KEY[orderBy] ?? SORT_KEY.Period;
        return [...reports].sort((a, b) => {
            const va = key(a), vb = key(b);
            const cmp = va < vb ? -1 : va > vb ? 1 : 0;
            return cmp * orderDir;
        });
    }, [reports, orderBy, orderDir]);

    const handleOrder = (by: string) => {
        if (orderBy === by) setOrderDir(d => -d);
        else { setOrderBy(by); setOrderDir(-1); }
    };
    const dirOf = (by: string) => (orderBy === by ? orderDir : 0);

    const inputClass = "bg-white border-none rounded text-sm px-2 py-2 shadow-[0_0_8px_rgba(0,0,0,0.2)]";
    const headers: [string, string | null][] = [
        ["Период", "Period"], ["Приход", "Revenue"], ["Дадено на клиенти", "ToClients"],
        ["Расходи", "Expenses"], ["Нето профит", "NetProfit"], ["", null],
    ];

    return (
        <div className="h-screen flex lg:pl-16 pt-12 lg:pt-0">
            <div className="flex flex-col px-3 md:px-8 py-2 gap-3 flex-1 overflow-hidden">
                {!allowed ? (
                    <div className="flex flex-1 flex-col items-center justify-center gap-3 text-[#666]">
                        <Lock size={40} />
                        <p className="text-sm">Немате дозвола за преглед на извештаи.</p>
                    </div>
                ) : (
                    <>
                        <div className="flex items-center w-full gap-3 flex-wrap">
                            <h1 className="text-xl font-semibold mr-auto">Месечен извештај</h1>
                            <div className="flex items-center gap-2">
                                <span className="text-xs text-[#666]">Од</span>
                                <input type="date" className={inputClass} value={filterFrom} onChange={e => setFilterFrom(e.target.value)} />
                            </div>
                            <div className="flex items-center gap-2">
                                <span className="text-xs text-[#666]">До</span>
                                <input type="date" className={inputClass} value={filterTo} onChange={e => setFilterTo(e.target.value)} />
                            </div>
                            {(filterFrom || filterTo) && (
                                <button onClick={() => { setFilterFrom(""); setFilterTo(""); }}
                                        className="text-xs text-[#666] underline hover:text-black transition-colors">Исчисти филтри</button>
                            )}
                        </div>

                        <div className="flex flex-col bg-white rounded-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] overflow-x-auto flex-1 min-h-0">
                            <div className={`grid place-items-center ${cols} min-w-[880px] md:min-w-0 gap-2 px-2 py-2 border-b-2 border-black/20 text-[#eee] bg-[#666] text-xs font-medium`}>
                                {headers.map(([label, key], i) => (
                                    <div key={i}
                                         className={`flex items-center ${key ? "cursor-pointer" : "cursor-default"}`}
                                         onClick={key ? () => handleOrder(key) : undefined}>
                                        {label} {key && <SortIcon dir={dirOf(key)} />}
                                    </div>
                                ))}
                            </div>

                            <div className="overflow-y-auto overflow-x-hidden flex-1 scrollbar-thin min-w-[880px] md:min-w-0 svg-hover">
                                {loading ? <Loading /> : sorted.length === 0 ? (
                                    <p className="text-center text-sm text-[#888] py-6">Нема извештаи за прикажување.</p>
                                ) : sorted.map((r, index) => (
                                    <div
                                        key={r.id}
                                        style={{ background: index % 2 === 1 ? "#f0f0f0" : "#ffffff" }}
                                        className={`grid place-items-center text-center ${cols} gap-2 px-2 py-1 border-b border-black/20`}
                                    >
                                        <p className="text-xs font-semibold">{period(r)}</p>
                                        <p className="text-xs text-green font-semibold">{money(r.totalRevenue)}</p>
                                        <p className="text-xs">{money(r.moneyGivenToClients)}</p>
                                        <p className="text-xs text-red-500">{money(r.totalExpenses)}</p>
                                        <p className={`text-xs font-bold ${r.netProfit < 0 ? "text-red-500" : "text-green"}`}>{money(r.netProfit)}</p>
                                        <Ellipsis size={18} color="#888" className="cursor-pointer" onClick={() => setSelected(r)} />
                                    </div>
                                ))}
                            </div>
                        </div>

                    </>
                )}
            </div>

            {selected && <ModalReadMoreMonthReport report={selected} closeModal={() => setSelected(null)} />}
        </div>
    );
}
