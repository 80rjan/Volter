import ReactDom from "react-dom";
import { useEffect, useState } from "react";
import axios from "axios";
import { X, CalendarFold } from "lucide-react";
import Loading from "../../shared/components/Loading.tsx";
import { API_BASE } from "../../shared/api/config.ts";
import { MonthlyReportRow, ReportPayload, MONTHS_MK } from "./types.ts";
import { ReportStats, ReportBreakdown } from "./ReportBreakdown.tsx";

interface Props {
    report: MonthlyReportRow;
    closeModal: (e?: React.MouseEvent) => void;
}

const period = (r: MonthlyReportRow) => `${MONTHS_MK[parseInt(r.dateFrom.substring(5, 7), 10) - 1]} ${r.dateFrom.substring(0, 4)}`;

export default function ModalReadMoreMonthReport({ report, closeModal }: Props) {
    const [payload, setPayload] = useState<ReportPayload | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        setLoading(true);
        axios.get(`${API_BASE}/reports/${report.id}`)
            .then(res => setPayload(res.data.payload ?? null))
            .catch(error => console.error("Error fetching report detail:", error))
            .finally(() => setLoading(false));
    }, [report.id]);

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" onClick={closeModal} />
            <div className="flex flex-col gap-6 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-8 rounded-lg w-[min(960px,94%)] max-h-[90vh] overflow-y-auto scrollbar-hidden">
                <div className="flex justify-between items-center">
                    <div className="flex items-center gap-3">
                        <span className="flex h-10 w-10 items-center justify-center rounded-full bg-green/15 text-green">
                            <CalendarFold size={20} />
                        </span>
                        <h1 className="text-2xl font-semibold">Извештај — {period(report)}</h1>
                    </div>
                    <button className="close-x-btn" onClick={closeModal}><X size={28} /></button>
                </div>

                <ReportStats
                    totalRevenue={report.totalRevenue}
                    totalExpenses={report.totalExpenses}
                    netProfit={report.netProfit}
                    moneyGivenToClients={report.moneyGivenToClients}
                />

                {loading ? (
                    <div className="flex justify-center py-8"><Loading /></div>
                ) : !payload ? (
                    <p className="text-center text-sm text-[#888] py-6">Нема детални податоци.</p>
                ) : (
                    <>
                        <hr className="border-black/15" />
                        <ReportBreakdown payload={payload} />
                    </>
                )}
            </div>
        </>,
        document.getElementById("portal")!
    );
}
