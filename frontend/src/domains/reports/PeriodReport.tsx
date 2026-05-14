import Nav from "../../shared/components/Nav.tsx";
import CashRegister from "../../shared/components/CashRegister.tsx";
import { Plus } from "lucide-react";
import { useState } from "react";

// TODO: No period report endpoint in Spring Boot backend yet

export default function PeriodReport() {
    const [dateFrom, setDateFrom] = useState(new Date().toISOString().split("T")[0]);
    const [dateTo, setDateTo] = useState(new Date().toISOString().split("T")[0]);
    const [showDate, setShowDate] = useState<string | null>(null);

    const getReport = (from: string, to: string) => {
        setShowDate(`${from} / ${to}`);
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

                <div className="flex flex-1 items-center justify-center text-[#888] text-xl">
                    Периодичниот извештај не е достапен во оваа верзија.
                </div>

                <CashRegister refreshDependency={true} />
            </div>
        </div>
    );
}
