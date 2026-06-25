import { useState } from "react";
import axios from "axios";
import { Search, Lock } from "lucide-react";
import CashRegister from "../../shared/components/CashRegister.tsx";
import Loading from "../../shared/components/Loading.tsx";
import { ReportStats, ReportBreakdown } from "./ReportBreakdown.tsx";
import { PeriodReport as PeriodReportData } from "./types.ts";
import { API_BASE } from "../../shared/api/config.ts";
import { useAuth } from "../../GlobalContext.tsx";

export default function PeriodReport() {
    const { can } = useAuth();
    const allowed = can("REPORT_READ");

    const [dateFrom, setDateFrom] = useState("");
    const [dateTo, setDateTo] = useState("");
    const [report, setReport] = useState<PeriodReportData | null>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [shownRange, setShownRange] = useState("");

    const run = () => {
        if (!dateFrom || !dateTo) { setError("Изберете период."); return; }
        if (dateFrom > dateTo) { setError("Почетниот датум е по крајниот."); return; }
        setLoading(true);
        setError("");
        axios.get(`${API_BASE}/reports/period`, { params: { dateFrom, dateTo } })
            .then(res => { setReport(res.data); setShownRange(`${dateFrom} — ${dateTo}`); })
            .catch(error => { console.error("Error fetching period report:", error); setError("Грешка при генерирање на извештајот."); })
            .finally(() => setLoading(false));
    };

    const inputClass = "bg-white border-none rounded text-sm px-2 py-2 shadow-[0_0_8px_rgba(0,0,0,0.2)]";

    return (
        <div className="h-screen flex pl-16">
            <div className="flex flex-col px-8 py-2 gap-3 flex-1 overflow-hidden">
                {!allowed ? (
                    <div className="flex flex-1 flex-col items-center justify-center gap-3 text-[#666]">
                        <Lock size={40} />
                        <p className="text-sm">Немате дозвола за преглед на извештаи.</p>
                    </div>
                ) : (
                    <>
                        <div className="flex items-center w-full gap-3 flex-wrap">
                            <h1 className="text-xl font-semibold mr-auto">Периодичен извештај</h1>
                            <div className="flex items-center gap-2">
                                <span className="text-xs text-[#666]">Од</span>
                                <input type="date" style={{ color: dateFrom ? "#000" : "#888" }} className={inputClass} value={dateFrom} onChange={e => setDateFrom(e.target.value)} />
                            </div>
                            <div className="flex items-center gap-2">
                                <span className="text-xs text-[#666]">До</span>
                                <input type="date" style={{ color: dateTo ? "#000" : "#888" }} className={inputClass} value={dateTo} onChange={e => setDateTo(e.target.value)} />
                            </div>
                            <button
                                onClick={run}
                                disabled={loading}
                                className="group relative overflow-hidden flex items-center gap-2 bg-green h-fit text-white rounded px-5 py-2 text-sm shadow-[4px_2px_6px_rgba(0,0,0,0.2)] transition-all duration-300 hover:scale-105 disabled:opacity-40 shrink-0"
                            >
                                <span className="pointer-events-none absolute inset-0 -translate-x-full bg-gradient-to-r from-transparent via-white/25 to-transparent transition-transform duration-700 ease-out group-hover:translate-x-[180%]" />
                                <Search size={16} /> Прикажи
                            </button>
                        </div>
                        {error && <p className="text-red-500 text-sm">{error}</p>}

                        <div className="flex flex-col gap-5 bg-[#eee] flex-1 min-h-0 overflow-y-auto scrollbar-thin pr-1 pb-2">
                            {loading ? (
                                <div className="flex flex-1 items-center justify-center"><Loading /></div>
                            ) : !report ? (
                                <div className="flex flex-1 items-center justify-center text-[#888] text-sm">
                                    Изберете период и притиснете „Прикажи“.
                                </div>
                            ) : (
                                <>
                                    <p className="text-sm text-[#666]">Период: <span className="font-semibold text-black">{shownRange}</span></p>
                                    <ReportStats
                                        totalRevenue={report.totalRevenue}
                                        totalExpenses={report.totalExpenses}
                                        netProfit={report.netProfit}
                                        moneyGivenToClients={report.moneyGivenToClients}
                                    />
                                    {report.payload && (
                                        <>
                                            <hr className="border-black/15" />
                                            <ReportBreakdown payload={report.payload} />
                                        </>
                                    )}
                                </>
                            )}
                        </div>

                    </>
                )}
            </div>
        </div>
    );
}
