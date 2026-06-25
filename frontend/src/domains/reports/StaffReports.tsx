import { useEffect, useState } from "react";
import ReactDom from "react-dom";
import axios from "axios";
import { Lock, Ellipsis, X, BarChart3 } from "lucide-react";
import Loading from "../../shared/components/Loading.tsx";
import { API_BASE } from "../../shared/api/config.ts";
import { useAuth } from "../../GlobalContext.tsx";
import { useTeam, StaffOption } from "../../shared/utils/useTeam.ts";
import { StaffPerformance } from "../staff/types.ts";

const money = (n: number | null | undefined) => (n == null ? "—" : `${Number(n).toLocaleString("de-DE")} ден`);
const num = (n: number | null | undefined) => (n == null ? "0" : String(n));

interface Row {
    staff: StaffOption;
    perf: StaffPerformance;
}

const cols = "grid-cols-[1.8fr_1.1fr_1.1fr_0.9fr_0.9fr_0.8fr_0.5fr]";

function Field({ label, value }: { label: string; value: React.ReactNode }) {
    return (
        <div className="flex flex-col gap-0.5">
            <span className="text-[#666] text-xs">{label}</span>
            <span className="text-sm font-medium break-words">{value}</span>
        </div>
    );
}

function DetailModal({ row, closeModal }: { row: Row; closeModal: () => void }) {
    const p = row.perf;
    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" onClick={closeModal} />
            <div className="flex flex-col gap-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-8 rounded-lg w-[min(820px,92%)] max-h-[90vh] overflow-y-auto scrollbar-hidden">
                <div className="flex justify-between items-center">
                    <div className="flex items-center gap-3">
                        <span className="flex h-10 w-10 items-center justify-center rounded-full bg-green/15 text-green"><BarChart3 size={20} /></span>
                        <div className="flex flex-col">
                            <h1 className="text-2xl font-semibold">{row.staff.fullName}</h1>
                            <span className="text-xs text-[#666]">{p.dateFrom} — {p.dateTo}</span>
                        </div>
                    </div>
                    <button className="close-x-btn" onClick={closeModal}><X size={28} /></button>
                </div>
                <div className="grid grid-cols-2 md:grid-cols-4 gap-x-8 gap-y-3">
                    <Field label="Приход" value={<span className="text-green font-semibold">{money(p.revenue)}</span>} />
                    <Field label="Дадено на клиенти" value={money(p.moneyGiven)} />
                    <Field label="Профит (залози)" value={money(p.pawnProfit)} />
                    <Field label="Профит (продажби)" value={money(p.saleProfit)} />
                    <Field label="Вкупен профит" value={<span className="text-green font-semibold">{money(p.totalProfit)}</span>} />
                    <Field label="Отворени залози" value={num(p.pawnsOpened)} />
                    <Field label="Продолжени залози" value={num(p.pawnsExtended)} />
                    <Field label="Затворени залози" value={num(p.pawnsRedeemed)} />
                    <Field label="Просечен залог" value={money(p.avgLoanSize)} />
                    <Field label="Креирани продажби" value={num(p.salesCreated)} />
                    <Field label="Продадени" value={num(p.salesSold)} />
                    <Field label="Расходи внесени" value={num(p.expensesRecorded)} />
                    <Field label="Ризик знаменца" value={<span className={p.riskFlags > 0 ? "text-red-500 font-semibold" : ""}>{num(p.riskFlags)}</span>} />
                    <Field label="Потценети продажби" value={num(p.underpricedSales)} />
                    <Field label="Подплатени откупи" value={num(p.underpaidRedemptions)} />
                    <Field label="Каса отстапувања" value={`${p.discrepancyCount} (${money(p.discrepancyTotal)})`} />
                </div>
            </div>
        </>,
        document.getElementById("portal")!
    );
}

export default function StaffReports() {
    const { can } = useAuth();
    const allowed = can("REPORT_READ");
    const team = useTeam();

    const today = new Date().toISOString().split("T")[0];
    const monthStart = `${today.substring(0, 7)}-01`;
    const [from, setFrom] = useState(monthStart);
    const [to, setTo] = useState(today);

    const [rows, setRows] = useState<Row[]>([]);
    const [loading, setLoading] = useState(false);
    const [selected, setSelected] = useState<Row | null>(null);

    const load = () => {
        if (!allowed || team.length === 0 || !from || !to) return;
        setLoading(true);
        Promise.all(team.map(staff =>
            axios.get(`${API_BASE}/reports/staff/${staff.id}/period`, { params: { dateFrom: from, dateTo: to } })
                .then(res => ({ staff, perf: res.data as StaffPerformance }))
                .catch(() => null)
        ))
            .then(results => setRows(results.filter((r): r is Row => r !== null)))
            .finally(() => setLoading(false));
    };

    useEffect(() => { load(); /* eslint-disable-next-line react-hooks/exhaustive-deps */ }, [allowed, team]);

    const inputClass = "bg-white border-none rounded text-sm px-2 py-2 shadow-[0_0_8px_rgba(0,0,0,0.2)]";
    const headers = ["Вработен", "Приход", "Вкупен профит", "Залози", "Продажби", "Ризик", ""];

    return (
        <div className="h-screen flex pl-16">
            <div className="flex flex-col px-8 py-2 gap-3 flex-1 overflow-hidden">
                {!allowed ? (
                    <div className="flex flex-1 flex-col items-center justify-center gap-3 text-[#666]">
                        <Lock size={40} />
                        <p className="text-sm">Немате дозвола за преглед на извештаи за вработени.</p>
                    </div>
                ) : (
                    <>
                        <div className="flex items-center gap-3 flex-wrap">
                            <div className="flex items-center gap-2">
                                <span className="text-xs text-[#666]">Од</span>
                                <input type="date" style={{ color: from ? "#000" : "#888" }} className={inputClass} value={from} onChange={e => setFrom(e.target.value)} />
                            </div>
                            <div className="flex items-center gap-2">
                                <span className="text-xs text-[#666]">До</span>
                                <input type="date" style={{ color: to ? "#000" : "#888" }} className={inputClass} value={to} onChange={e => setTo(e.target.value)} />
                            </div>
                            <button onClick={load} disabled={loading}
                                    className="group relative overflow-hidden flex items-center gap-2 px-5 py-2 rounded bg-green text-white text-sm font-semibold shadow-[4px_2px_6px_rgba(0,0,0,0.2)] transition-all duration-300 hover:scale-105 disabled:opacity-40">
                                <span className="pointer-events-none absolute inset-0 -translate-x-full bg-gradient-to-r from-transparent via-white/25 to-transparent transition-transform duration-700 ease-out group-hover:translate-x-[180%]" />
                                Прикажи
                            </button>
                        </div>

                        <div className="flex flex-col bg-white rounded-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] overflow-hidden flex-1 min-h-0">
                            <div className={`grid place-items-center ${cols} gap-2 px-2 py-2 border-b-2 border-black/20 text-[#eee] bg-[#666] text-xs font-medium`}>
                                {headers.map((h, i) => <div key={i} className={i === 0 ? "justify-self-start pl-2" : ""}>{h}</div>)}
                            </div>
                            <div className="overflow-y-auto overflow-x-hidden flex-1 scrollbar-thin svg-hover">
                                {loading ? <Loading /> : rows.length === 0 ? (
                                    <p className="text-center text-sm text-[#888] py-6">Нема податоци за прикажување.</p>
                                ) : rows.map((r, index) => (
                                    <div
                                        key={r.staff.id}
                                        style={{ background: index % 2 === 1 ? "#f0f0f0" : "#ffffff" }}
                                        className={`grid place-items-center text-center ${cols} gap-2 px-2 py-2 border-b border-black/20`}
                                    >
                                        <p className="text-sm font-medium justify-self-start pl-2">{r.staff.fullName}</p>
                                        <p className="text-xs font-bold text-green">{money(r.perf.revenue)}</p>
                                        <p className="text-xs font-bold">{money(r.perf.totalProfit)}</p>
                                        <p className="text-xs">{num(r.perf.pawnsOpened)}</p>
                                        <p className="text-xs">{num(r.perf.salesSold)}</p>
                                        <p className={`text-xs font-bold ${r.perf.riskFlags > 0 ? "text-red-500" : "text-[#aaa]"}`}>{num(r.perf.riskFlags)}</p>
                                        <button onClick={() => setSelected(r)} className="text-[#666] hover:text-black transition-colors"><Ellipsis size={18} /></button>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </>
                )}
            </div>
            {selected && <DetailModal row={selected} closeModal={() => setSelected(null)} />}
        </div>
    );
}
