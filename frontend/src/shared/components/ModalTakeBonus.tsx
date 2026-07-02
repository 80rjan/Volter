import ReactDom from "react-dom";
import { useEffect, useState } from "react";
import axios from "axios";
import { X, CheckCheck, HandCoins } from "lucide-react";
import Loading from "./Loading.tsx";
import { API_BASE } from "../api/config.ts";

interface Props {
    sessionId: number;
    closeModal: () => void;
    refresh: () => void;
}

const inputCls = "bg-white border-none rounded text-base p-2 w-full shadow-[0_0_4px_rgba(0,0,0,0.2)]";
const money = (n: number) => Number(n).toLocaleString("de-DE");

// Withdraw part (or all) of the caller's available profit-share bonus as cash out
// of the open session. The available total is fetched from the server.
export default function ModalTakeBonus({ sessionId, closeModal, refresh }: Props) {
    const [available, setAvailable] = useState<number | null>(null);
    const [amount, setAmount] = useState("");
    const [loading, setLoading] = useState(true);
    const [busy, setBusy] = useState(false);
    const [error, setError] = useState("");

    useEffect(() => {
        axios.get(`${API_BASE}/staff-bonus/available`)
            .then(res => setAvailable(res.data.available ?? 0))
            .catch(() => setError("Неуспешно вчитување на бонусот."))
            .finally(() => setLoading(false));
    }, []);

    const save = async () => {
        const amt = Number(amount);
        if (!amount.trim() || amt <= 0) { setError("Внесете износ поголем од 0."); return; }
        setBusy(true); setError("");
        try {
            await axios.post(`${API_BASE}/staff-bonus`, { amount: amt, cashRegisterSessionId: sessionId });
            refresh();
            closeModal();
        } catch (err: any) {
            setError(err?.response?.data?.message || "Земањето бонус не успеа.");
            setBusy(false);
        }
    };

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1100]" onClick={busy ? undefined : closeModal} />
            <div className="flex flex-col gap-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1100] p-8 rounded-lg w-[min(460px,92%)]">
                <div className="flex justify-between items-center">
                    <h1 className="flex items-center gap-2 text-xl font-semibold"><HandCoins size={20} /> Земи бонус</h1>
                    <button className="close-x-btn disabled:opacity-40 disabled:cursor-not-allowed" onClick={closeModal} disabled={busy}><X size={26} /></button>
                </div>

                {loading ? (
                    <div className="flex justify-center py-6"><Loading /></div>
                ) : (
                    <div className="flex flex-col gap-4">
                        <div className="flex flex-col gap-1">
                            <span className="text-[#666] text-xs">Износ *</span>
                            <input className={inputCls} type="number" value={amount} onChange={e => setAmount(e.target.value)} autoFocus />
                        </div>
                        <div className="bg-white rounded p-3 shadow-[0_0_4px_rgba(0,0,0,0.1)] flex items-center justify-between">
                            <span className="text-[#666] text-sm">Достапен бонус до сега</span>
                            <b className="text-green">{available == null ? "—" : `${money(available)} ден`}</b>
                        </div>
                        {error && <p className="text-red-500 text-sm">{error}</p>}
                    </div>
                )}

                <div className="flex justify-end items-center gap-3 min-h-[40px]">
                    <button onClick={closeModal} disabled={busy} className="px-5 py-2 rounded bg-black/10 text-sm hover:bg-black/15 transition-colors disabled:opacity-40 disabled:cursor-not-allowed">Откажи</button>
                    {busy ? <Loading width={26} height={26} /> : (
                        <button onClick={save} disabled={loading}
                            className="flex items-center justify-center gap-2 px-6 py-2 rounded text-white font-medium bg-green shadow-[4px_2px_6px_rgba(0,0,0,0.2)] transition-all hover:scale-105 disabled:opacity-40 disabled:cursor-not-allowed">
                            <CheckCheck size={20} /> Земи
                        </button>
                    )}
                </div>
            </div>
        </>,
        document.getElementById("portal")!
    );
}
