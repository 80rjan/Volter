import Nav from "../../shared/components/Nav.tsx";
import CashRegister from "../../shared/components/CashRegister.tsx";
import { DollarSign, Handshake, Landmark, Plus, Tag } from "lucide-react";
import { useState } from "react";
import axios from "axios";
import Loading from "../../shared/components/Loading.tsx";

const getCat: Record<string, string> = {
    Electronics: "Електроника", Watch: "Часовници", Vehicle: "Возила",
    Gold: "Злато", Other: "Останато", Sale: "Продажба",
    Insert: "Внес Каса", Remove: "Излез Каса", Expense: "Расходи",
};

export default function PeriodReport() {
    const [dateFrom, setDateFrom] = useState(new Date().toISOString().split("T")[0]);
    const [dateTo, setDateTo] = useState(new Date().toISOString().split("T")[0]);
    const [showDate, setShowDate] = useState<string | null>(null);
    const [report, setReport] = useState<any>(null);
    const [loading, setLoading] = useState(false);

    const getReport = (from: string, to: string) => {
        setShowDate(`${from} / ${to}`);
        setLoading(true);
        axios.get(`http://localhost:3000/periodReport?dateFrom=${from}&dateTo=${to}`)
            .then(res => setReport(res.data))
            .catch(error => console.error("Error fetching period report:", error))
            .finally(() => setLoading(false));
    };

    const dateInputClass = "px-4 py-2 h-fit border-2 border-green/40 rounded shadow-[4px_2px_6px_rgba(0,0,0,0.2)] text-base focus:border-green outline-none";

    return (
        <div className="h-screen grid grid-cols-[max(15%,240px)_auto]">
            <Nav />
            <div className="flex flex-col px-8 pt-8 gap-4 flex-1 overflow-hidden">
                <div className="flex justify-between w-full">
                    <h1 className="text-3xl font-semibold">
                        Периодичен Извештај <span className="ml-4 font-normal text-base">{showDate}</span>
                    </h1>
                    <div className="flex flex-row gap-4 items-center">
                        <input className={dateInputClass} type="date" value={dateFrom} onChange={e => setDateFrom(e.target.value)} />
                        <input className={dateInputClass} type="date" value={dateTo} onChange={e => setDateTo(e.target.value)} />
                        <button
                            className="flex items-center gap-2 bg-green h-fit text-white rounded px-6 py-3 text-base shadow-[4px_2px_6px_rgba(0,0,0,0.2)] transition-all duration-300 hover:scale-105 group"
                            onClick={() => getReport(dateFrom, dateTo)}
                        >
                            <Plus size={22} color="white" strokeWidth={3} className="transition-transform duration-500 group-hover:rotate-90" />
                            Генерирај Извештај
                        </button>
                    </div>
                </div>

                {loading ? <Loading /> : report === null ? (
                    <div className="flex-1" />
                ) : (
                    <div className="flex flex-col bg-white rounded-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] overflow-hidden flex-1 min-h-0 h-full">
                        {/* Main stats */}
                        <div className="flex px-8 pt-8 justify-evenly items-center gap-8">
                            {[
                                { icon: <DollarSign color="var(--grey)" size={40} />, val: report.total.profit, label: "Бруто профит" },
                                { icon: <DollarSign color="var(--green)" size={40} />, val: Number(report?.total?.profit || 0) - Number(report?.cashFlow?.find((c: any) => c.category === "Expense")?.moneyGiven || 0), label: "Нето профит" },
                                { icon: <Landmark color="var(--grey)" size={40} />, val: report.total.moneyGiven, label: "Исплатени средства" },
                                { icon: <Handshake color="var(--grey)" size={40} />, val: report.numPawns.numTransactions, label: "Нови залози" },
                                { icon: <Tag color="var(--grey)" size={40} />, val: report.numSales.numTransactions, label: "Нови продажби" },
                            ].map(({ icon, val, label }) => (
                                <div key={label} className="flex items-center leading-none gap-4">
                                    {icon}
                                    <div className="flex flex-col gap-2">
                                        <h1 className="text-3xl font-semibold">{Number(val).toLocaleString("de-DE")}</h1>
                                        <p className="font-normal">{label}</p>
                                    </div>
                                </div>
                            ))}
                        </div>

                        {/* Detailed */}
                        <div className="grid grid-cols-[1fr_2.5fr] overflow-hidden h-full">
                            {/* Category breakdown */}
                            <div className="grid grid-cols-2 content-evenly px-8 py-8 gap-x-4">
                                {report.categories.map((cat: any) => (
                                    <div key={cat.category} className="flex flex-col gap-1">
                                        <h2 className="text-xl font-semibold text-left">{getCat[cat.category]}</h2>
                                        <div className="flex flex-col items-start gap-1">
                                            {[["Нови:", cat.numNew], ["Исплата:", cat.moneyGiven], ["Профит:", cat.profit]].map(([lbl, v]) => (
                                                <div key={String(lbl)} className="flex gap-2 text-base font-semibold">
                                                    <span className="font-normal text-[var(--grey)]">{lbl}</span>
                                                    {Number(v).toLocaleString("de-DE")}
                                                </div>
                                            ))}
                                        </div>
                                    </div>
                                ))}
                            </div>

                            {/* Cashflow + transactions */}
                            <div className="flex flex-col px-8 py-8 gap-4 overflow-hidden">
                                <div className="flex w-full justify-between">
                                    <div className="flex gap-2 font-semibold">
                                        <span className="font-normal text-[var(--grey)]">Бр. трансакции:</span>
                                        {Number(report.transactions.length).toLocaleString("de-DE")}
                                    </div>
                                    {report.cashFlow.map((item: any) => (
                                        <div key={item.category} className="flex gap-2 font-semibold">
                                            <span className="font-normal text-[var(--grey)]">{getCat[item.category]}:</span>
                                            {Number(item.category === "Insert" ? item.moneyGot : item.moneyGiven).toLocaleString("de-DE")}
                                        </div>
                                    ))}
                                </div>

                                <div className="flex flex-col bg-white rounded-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] overflow-hidden">
                                    <div className="grid place-items-center grid-cols-[1.5fr_1.5fr_3fr_repeat(4,1.5fr)] gap-4 px-2 py-3 border-b-2 border-black/20 text-[#eee] bg-[#666]">
                                        {["Име", "Категорија", "Дескрипција", "Дадено", "Земено", "Профит", "Отстапување"].map(h => (
                                            <div key={h} className="text-xs font-medium">{h}</div>
                                        ))}
                                    </div>
                                    <div className="overflow-y-auto flex flex-col scrollbar-thin">
                                        {report.transactions.map((tx: any, index: number) => (
                                            <div
                                                key={index}
                                                style={{ background: tx.Description?.startsWith("Промена!") ? "#ffe6e6" : index % 2 === 1 ? "#f0f0f0" : "#ffffff" }}
                                                className="grid place-items-center grid-cols-[1.5fr_1.5fr_3fr_repeat(4,1.5fr)] gap-4 font-normal"
                                            >
                                                <span className="text-sm">{tx.Name}</span>
                                                <span className="text-sm">{getCat[tx.Category]}</span>
                                                <span className="text-sm">{tx.Description}</span>
                                                {+tx.Given + +tx.Got + +tx.Profit + +tx.Diff === 0 ? (
                                                    <span className="text-sm col-span-4">{tx.Description}</span>
                                                ) : (
                                                    <>
                                                        <span className={`text-sm italic ${Number(tx.Given) !== 0 ? "font-semibold" : ""}`}>{Number(tx.Given).toLocaleString("de-DE")}</span>
                                                        <span className={`text-sm italic ${Number(tx.Got) !== 0 ? "font-semibold" : ""}`}>{Number(tx.Got).toLocaleString("de-DE")}</span>
                                                        <span className={`text-sm italic ${Number(tx.Profit) !== 0 ? "font-semibold" : ""}`}>{Number(tx.Profit).toLocaleString("de-DE")}</span>
                                                        <span className={`text-sm italic ${Number(tx.Diff) === 0 ? "" : Number(tx.Diff) < 0 ? "text-red-600 font-black text-base" : "text-green-700 font-black"}`}>{tx.Diff}</span>
                                                    </>
                                                )}
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                )}

                <CashRegister refreshDependency={true} />
            </div>
        </div>
    );
}
