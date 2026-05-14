import Nav from "../../shared/components/Nav.tsx";
import CashRegister from "../../shared/components/CashRegister.tsx";
import { ChevronDown, ChevronUp, Ellipsis, Minus, Plus } from "lucide-react";
import { useEffect, useRef, useState } from "react";
import axios from "axios";
import Loading from "../../shared/components/Loading.tsx";
import ModalReadMoreMonthReport from "./ModalReadMoreMonthReport.tsx";
import { MonthlyReportRow } from "./types.ts";
import { API_BASE } from "../../shared/api/config.ts";

const SORT_FIELD: Record<string, string> = {
    Date: 'year,month',
    Turnover: 'turnover',
    CashOut: 'cashOut',
    Revenue: 'revenue',
    GrossProfit: 'grossProfit',
    Expenses: 'expenses',
    NetProfit: 'netProfit',
};

function mapReportResponse(r: any): MonthlyReportRow {
    return {
        id: r.id,
        year: r.year,
        month: r.month,
        turnover: r.turnover,
        cashOut: r.cashOut,
        revenue: r.revenue,
        grossProfit: r.grossProfit,
        expenses: r.expenses,
        netProfit: r.netProfit,
    };
}

function SortIcon({ dir }: { dir: number }) {
    return dir === 0 ? <Minus size={14} /> : dir === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />;
}

export default function MonthlyReport() {
    const [allReports, setAllReports] = useState<MonthlyReportRow[]>([]);
    const [orderBy, setOrderBy] = useState("Date");
    const orderDirectionArr = useRef([-1, 0, 0, 0, 0, 0, 0, 0]);
    const [orderDirection, setOrderDirection] = useState("DESC");
    const [filterMonth, setFilterMonth] = useState("");
    const [filterYear, setFilterYear] = useState("");
    const [refresh, setRefresh] = useState(false);
    const page = useRef(0);
    const size = 40;
    const [isLastPage, setIsLastPage] = useState(false);
    const scrollableRef = useRef<HTMLDivElement>(null);
    const [loading, setLoading] = useState(false);
    const isFetchingRef = useRef(false);
    const [isFetching, setIsFetching] = useState(false);
    const [modalReadMore, setModalReadMore] = useState(false);
    const [reportReadMore, setReportReadMore] = useState<MonthlyReportRow | null>(null);
    const [error, setError] = useState("");
    const [reportGenerateInfo, setReportGenerateInfo] = useState({ year: 0, month: 0 });
    const fetchedReportIds = useRef(new Set<number>());

    const fetchReports = (pg: number, order: string, direction: string, month: string, year: string, isLoading: boolean) => {
        if (isFetching) return;
        setIsFetching(true);
        setLoading(isLoading);
        isFetchingRef.current = true;
        const params = new URLSearchParams({
            page: String(pg),
            size: String(size),
            sort: `${SORT_FIELD[order] ?? 'year'},${direction}`,
        });
        if (month) params.set('month', month);
        if (year) params.set('year', year);
        axios.get(`${API_BASE}/reports/monthly?${params}`)
            .then(res => {
                const reports: MonthlyReportRow[] = (res.data.content ?? []).map(mapReportResponse);
                const newUnique = reports.filter(r => !fetchedReportIds.current.has(r.id));
                newUnique.forEach(r => fetchedReportIds.current.add(r.id));
                setAllReports(prev => [...prev, ...newUnique]);
                setIsLastPage(res.data.last ?? true);
            })
            .catch(error => console.error("Error fetching monthly reports:", error))
            .finally(() => { setLoading(false); isFetchingRef.current = false; setIsFetching(false); });
    };

    const generateReport = (year: number, month: number) => {
        if (year <= 0 || month <= 0 || month >= 13) { setError("Внесете валиден месец и година"); return; }
        setError("");
        setLoading(true);
        axios.post(`${API_BASE}/reports/monthly/generate`, null, { params: { month, year } })
            .then(() => setRefresh(prev => !prev))
            .catch(error => { console.error("Error generating report:", error); setError("Грешка при генерирање"); })
            .finally(() => setLoading(false));
    };

    useEffect(() => {
        const el = scrollableRef.current!;
        const handleScroll = () => {
            if (el.scrollHeight - el.scrollTop - el.clientHeight <= el.scrollHeight * 0.3 && !isLastPage && !isFetchingRef.current) {
                page.current += 1;
                fetchReports(page.current, orderBy, orderDirection, filterMonth, filterYear, false);
            }
        };
        el.addEventListener("scroll", handleScroll);
        return () => el.removeEventListener("scroll", handleScroll);
    }, [isLastPage, refresh, orderBy, orderDirection, filterMonth, filterYear]);

    useEffect(() => {
        fetchedReportIds.current.clear();
        setAllReports([]);
        page.current = 0;
        fetchReports(0, orderBy, orderDirection, filterMonth, filterYear, true);
    }, [refresh, orderBy, orderDirection, filterMonth, filterYear]);

    const handleOrder = (by: string, index: number) => {
        const newDir = new Array(orderDirectionArr.current.length).fill(0);
        newDir[index] = orderDirectionArr.current[index] === 0 ? 1 : orderDirectionArr.current[index] === 1 ? -1 : 1;
        orderDirectionArr.current = newDir;
        setOrderDirection(newDir.includes(-1) ? "DESC" : "ASC");
        setOrderBy(by);
    };

    const headers: [string, string | null, number][] = [
        ["Година", "Date", 0], ["Месец", null, -1],
        ["Промет", "Turnover", 2], ["Исплати", "CashOut", 3], ["Приход", "Revenue", 4],
        ["Бруто Профит", "GrossProfit", 5], ["Расходи", "Expenses", 6],
        ["Нето Профит", "NetProfit", 7], ["Повеќе", null, -1],
    ];

    const inputClass = "border-none rounded text-base w-1/4 p-2 shadow-[0_0_8px_rgba(0,0,0,0.2)]";

    return (
        <div className="h-screen grid grid-cols-[max(15%,240px)_auto]">
            <Nav />
            <div className="flex flex-col px-8 pt-8 gap-4 flex-1 overflow-hidden">
                <div className="flex justify-between w-full">
                    <h1 className="text-3xl font-semibold">Месечен Извештај</h1>
                    <div className="flex flex-row gap-4 items-center">
                        {error && <span className="text-red-500 italic text-base">{error}</span>}
                        <input
                            className="px-4 py-2 h-fit border-2 border-green/40 rounded shadow-[4px_2px_6px_rgba(0,0,0,0.2)] text-base max-w-32 focus:border-green outline-none"
                            placeholder="Месец" type="number"
                            onChange={e => setReportGenerateInfo(prev => ({ ...prev, month: Number(e.target.value) }))}
                        />
                        <input
                            className="px-4 py-2 h-fit border-2 border-green/40 rounded shadow-[4px_2px_6px_rgba(0,0,0,0.2)] text-base max-w-32 focus:border-green outline-none"
                            placeholder="Година" type="number"
                            onChange={e => setReportGenerateInfo(prev => ({ ...prev, year: Number(e.target.value) }))}
                        />
                        <button
                            className="flex items-center gap-2 bg-green h-fit text-white rounded px-6 py-3 text-base shadow-[4px_2px_6px_rgba(0,0,0,0.2)] transition-all duration-300 hover:scale-105 group"
                            onClick={() => generateReport(reportGenerateInfo.year, reportGenerateInfo.month)}
                        >
                            <Plus size={22} color="white" strokeWidth={3} className="transition-transform duration-500 group-hover:rotate-90" />
                            Генерирај Извештај
                        </button>
                    </div>
                </div>

                <div className="flex justify-evenly w-full">
                    <input className={inputClass} placeholder="Пребарувај по месец (број)" type="number" onKeyUp={e => setFilterMonth((e.target as HTMLInputElement).value)} />
                    <input className={inputClass} placeholder="Пребарувај по година (број)" type="number" onKeyUp={e => setFilterYear((e.target as HTMLInputElement).value)} />
                </div>

                <div className="flex flex-col bg-white rounded-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] overflow-hidden flex-1 min-h-0">
                    <div className="grid place-items-center grid-cols-[repeat(9,1fr)] px-2 py-4 border-b-2 border-black/20 text-[#eee] bg-[#666]">
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

                    <div ref={scrollableRef} className="overflow-y-auto overflow-x-hidden flex-1 scrollbar-thin">
                        {loading ? <Loading /> : allReports.map((report, index) => (
                            <div
                                key={report.id}
                                style={{ background: index % 2 === 1 ? "#f0f0f0" : "#ffffff" }}
                                className="grid place-items-center grid-cols-[repeat(9,1fr)] gap-2 px-4 py-3 border-b border-black/20 svg-hover"
                            >
                                <p className="text-sm font-medium">{report.year}</p>
                                <p className="text-sm font-medium">{report.month}</p>
                                <p className="text-sm">{Number(report.turnover).toLocaleString("de-DE")}</p>
                                <p className="text-sm">{Number(report.cashOut).toLocaleString("de-DE")}</p>
                                <p className="text-sm">{Number(report.revenue).toLocaleString("de-DE")}</p>
                                <p className="text-sm font-medium">{Number(report.grossProfit).toLocaleString("de-DE")}</p>
                                <p className="text-sm text-red-500">{Number(report.expenses).toLocaleString("de-DE")}</p>
                                <p className="text-sm font-semibold text-green">{Number(report.netProfit).toLocaleString("de-DE")}</p>
                                <Ellipsis size={28} color="#888" className="cursor-pointer" onClick={() => { setReportReadMore(report); setModalReadMore(true); }} />
                            </div>
                        ))}
                    </div>
                </div>

                {modalReadMore && reportReadMore && (
                    <ModalReadMoreMonthReport report={reportReadMore} closeModal={() => setModalReadMore(false)} />
                )}

                <CashRegister refreshDependency={true} />
            </div>
        </div>
    );
}
