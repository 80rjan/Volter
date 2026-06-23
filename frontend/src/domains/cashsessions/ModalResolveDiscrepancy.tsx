import ReactDom from "react-dom";
import { useState } from "react";
import axios from "axios";
import { X, CheckCheck, Wrench } from "lucide-react";
import Loading from "../../shared/components/Loading.tsx";
import { API_BASE } from "../../shared/api/config.ts";
import { Discrepancy } from "./types.ts";

interface Props {
    discrepancy: Discrepancy;
    onResolved: (d: Discrepancy) => void;
    closeModal: () => void;
}

const money = (n: number | null | undefined) => (n == null ? "—" : `${Number(n).toLocaleString("de-DE")} ден`);
const inputCls = "bg-white border-none rounded text-base p-2 w-full shadow-[0_0_4px_rgba(0,0,0,0.2)]";

export default function ModalResolveDiscrepancy({ discrepancy, onResolved, closeModal }: Props) {
    const [note, setNote] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const over = discrepancy.difference > 0;

    const submit = () => {
        if (!note.trim()) { setError("Внеси белешка за решавање."); return; }
        setLoading(true);
        setError("");
        axios.post(`${API_BASE}/cash-register-session-discrepancies/${discrepancy.id}/resolve`, { resolutionNote: note.trim() })
            .then(res => onResolved(res.data))
            .catch(err => {
                setError(err?.response?.data?.message || "Решавањето не успеа, обидете се повторно.");
                setLoading(false);
            });
    };

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1100]" onClick={loading ? undefined : closeModal} />
            <div className="flex flex-col gap-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1100] p-7 rounded-lg w-[min(520px,92%)]">
                <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                        <span className="flex h-10 w-10 items-center justify-center rounded-full bg-green/15 text-green">
                            <Wrench size={20} />
                        </span>
                        <h1 className="text-xl font-semibold">Реши отстапување</h1>
                    </div>
                    <button className="close-x-btn disabled:opacity-40 disabled:cursor-not-allowed" onClick={closeModal} disabled={loading}><X size={24} /></button>
                </div>

                <div className="flex flex-wrap gap-x-6 gap-y-1 text-sm">
                    <span className="text-[#666]">Тип: <span className="font-semibold text-black">{over ? "Вишок" : "Кусок"}</span></span>
                    <span className="text-[#666]">Очекувано: <span className="font-semibold text-black">{money(discrepancy.expectedAmount)}</span></span>
                    <span className="text-[#666]">Изброено: <span className="font-semibold text-black">{money(discrepancy.countedAmount)}</span></span>
                    <span className="text-[#666]">Разлика: <span className={`font-semibold ${over ? "text-green" : "text-red-500"}`}>{over ? "+" : ""}{money(discrepancy.difference)}</span></span>
                </div>

                <div className="flex flex-col gap-1">
                    <span className="text-[#666] text-xs">Белешка за решавање</span>
                    <textarea className={`${inputCls} resize-none`} rows={3} value={note} placeholder="Опиши како е решено отстапувањето"
                              onChange={e => setNote(e.target.value)} autoFocus />
                </div>

                {error && <span className="text-red-500 text-sm">{error}</span>}

                <div className="flex gap-3 items-center justify-end min-h-[40px]">
                    <button type="button" onClick={closeModal} disabled={loading}
                            className="px-5 py-2 rounded bg-black/10 text-sm hover:bg-black/15 transition-colors disabled:opacity-40 disabled:cursor-not-allowed">
                        Откажи
                    </button>
                    {loading ? <Loading width={28} height={28} /> : (
                        <button type="button" onClick={submit}
                                className="group relative overflow-hidden flex items-center justify-center gap-2 px-6 py-2 rounded text-white font-medium bg-green shadow-[4px_2px_6px_rgba(0,0,0,0.2)] transition-all duration-300 hover:scale-105 active:scale-[0.98] disabled:cursor-not-allowed disabled:opacity-40">
                            <span className="pointer-events-none absolute inset-0 -translate-x-full bg-gradient-to-r from-transparent via-white/25 to-transparent transition-transform duration-700 ease-out group-hover:translate-x-[180%]" />
                            <CheckCheck size={20} /> Потврди
                        </button>
                    )}
                </div>
            </div>
        </>,
        document.getElementById("portal")!
    );
}
