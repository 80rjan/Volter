import Nav from "../components/Nav.tsx";
import CashRegister from "./CashRegister.tsx";
import { ChevronDown, ChevronUp, Ellipsis, Minus, Plus } from "lucide-react";
import { useEffect, useRef, useState } from "react";
import axios from "axios";
import Loading from "../components/Loading.tsx";
import ModalReadMoreMonthReport from "../components/ModalReadMoreMonthReport.tsx";
import { MonthlyReportRow } from "../types.ts";

function SortIcon({ dir }: { dir: number }) {
    return dir === 0 ? <Minus size={14} /> : dir === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />;
}

export default function MonthlyReport() {
    const [allReports, setAllReports] = useState<MonthlyReportRow[]>([]);
    const [orderBy, setOrderBy] = useState("Year * 100 %2B Month");
    const orderDirectionArr = useRef([-1, 0, 0, 0, 0, 0, 0, 0]);
    const [orderDirection, setOrderDirection] = useState("DESC");
    const [searchByMonth, setSearchByMonth] = useState("");
    const [searchByYear, setSearchByYear] = useState("");
    const [refresh, setRefresh] = useState(false);
    const offset = useRef(0);
    const limit = 40;
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

    const fetchReports = (limit: number, off: number, order: string, direction: string, month: string, year: string, isLoading: boolean) => {
        if (isFetching) return;
        setIsFetching(true);
        setLoading(isLoading);
        isFetchingRef.current = true;
        axios.get(`http://localhost:3000/monthlyReport?limit=${limit}&offset=${off}&orderBy=${order}&orderDirection=${direction}&searchByMonth=${month}&searchByYear=${year}`)
            .then(res => {
                const newUnique = res.data.filter((r: any) => !fetchedReportIds.current.has(r.Id));
                newUnique.forEach((r: any) => fetchedReportIds.current.add(r.Id));
                setAllReports(prev => [...prev, ...newUnique]);
                setIsLastPage(res.data.length < limit);
            })
            .catch(error => console.error("Error fetching monthly reports:", error))
            .finally(() => { setLoading(false); isFetchingRef.current = false; setIsFetching(false); });
    };

    const generateReport = (year: number, month: number) => {
        if (year <= 0 || month <= 0 || month >= 13) { setError("Внесете валиден месец и година"); return; }
        setLoading(true);
        axios.post(`http://localhost:3000/monthlyReport/generate`, { year, month })
            .then(res => { if (!res.data.passed) { setError(res.data.message); throw new Error(res.data.message); } })
            .catch(error => console.error(`Error generating report:`, error))
            .finally(() => { setLoading(false); setRefresh(prev => !prev); });
    };

    useEffect(() => {
        const el = scrollableRef.current!;
        const handleScroll = () => {
            if (el.scrollHeight - el.scrollTop - el.clientHeight <= el.scrollHeight * 0.3 && !isLastPage && !isFetchingRef.current) {
                offset.current += limit;
                fetchReports(limit, offset.current, orderBy, orderDirection, searchByMonth, searchByYear, false);
            }
        };
        el.addEventListener("scroll", handleScroll);
        return () => el.removeEventListener("scroll", handleScroll);
    }, [isLastPage, refresh, orderBy, orderDirection, searchByMonth, searchByYear]);

    useEffect(() => {
        fetchedReportIds.current.clear();
        setAllReports([]);
        offset.current = 0;
        fetchReports(limit, 0, orderBy, orderDirection, searchByMonth, searchByYear, true);
    }, [refresh, orderBy, orderDirection, searchByMonth, searchByYear]);

    const handleOrder = (by: string, index: number) => {
        const newDir = new Array(orderDirectionArr.current.length).fill(0);
        newDir[index] = orderDirectionArr.current[index] === 0 ? 1 : orderDirectionArr.current[index] === 1 ? -1 : 1;
        orderDirectionArr.current = newDir;
        setOrderDirection(newDir.includes(-1) ? "DESC" : "ASC");
        setOrderBy(by);
    };

    const headers: [string, string | null, number][] = [
        ["Година", "Year * 100 %2B Month", 0], ["Месец", "Month", 1],
        ["Исплатени Средства", "Money+Given", 2], ["Нето Профит", "Net+Profit", 3],
        ["Вкупно Залози", "Total+Pawns", 4], ["Профит Залози", "Profit+Pawns", 5],
        ["Вкупно Продажби", "Total+Sales", 6], ["Профит Продажби", "Profit+Sales", 7],
        ["Повеќе", null, -1],
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
                    <input className={inputClass} placeholder="Пребарувај по месец (број)" type="number" onKeyUp={e => setSearchByMonth((e.target as HTMLInputElement).value)} />
                    <input className={inputClass} placeholder="Пребарувај по година (број)" type="number" onKeyUp={e => setSearchByYear((e.target as HTMLInputElement).value)} />
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
                                key={index}
                                style={{ background: index % 2 === 1 ? "#f0f0f0" : "#ffffff" }}
                                className="grid place-items-center grid-cols-[repeat(9,1fr)] gap-2 px-4 py-3 border-b border-black/20 svg-hover"
                            >
                                <p className="text-sm font-medium">{report.Year}</p>
                                <p className="text-sm font-medium">{report.Month}</p>
                                <p className="text-sm font-medium">{Number(report["Money Given"]).toLocaleString("de-DE")}</p>
                                <p className="text-sm font-medium">{Number(report["Net Profit"]).toLocaleString("de-DE")}</p>
                                <p className="text-sm font-medium">{Number(report["Total Pawns"]).toLocaleString("de-DE")}</p>
                                <p className="text-sm font-medium">{Number(report["Profit Pawns"]).toLocaleString("de-DE")}</p>
                                <p className="text-sm font-medium">{Number(report["Total Sales"]).toLocaleString("de-DE")}</p>
                                <p className="text-sm font-medium">{Number(report["Profit Sales"]).toLocaleString("de-DE")}</p>
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
