import ReactDom from "react-dom";
import { X, CheckCheck, Plus, Minus } from "lucide-react";
import { useState } from "react";
import axios from "axios";
import Loading from "./Loading.tsx";
import { API_BASE } from "../api/config.ts";

interface Props {
    sessionId: number;
    isInsert: boolean;        // true = deposit (cash in), false = withdrawal (cash out)
    closeModal: () => void;
    refresh: () => void;
}

export default function ModalAdjustCashRegister({ sessionId, isInsert, closeModal, refresh }: Props) {
    const [amount, setAmount] = useState("");
    const [description, setDescription] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const submit = () => {
        const value = Number(amount);
        if (!amount.trim() || isNaN(value) || value <= 0) { setError("Внеси валидна сума!"); return; }
        setLoading(true);
        setError("");
        axios.post(`${API_BASE}/cash-register-sessions/${sessionId}/record-transaction`, {
            action: isInsert ? "DEPOSIT" : "WITHDRAWAL",
            direction: isInsert ? "IN" : "OUT",
            amount: value,
            description: description.trim() || null,
        })
            .then(() => { refresh(); closeModal(); })
            .catch(() => { setError("Дејството не успеа, обидете се повторно."); setLoading(false); });
    };

    const inputCls = "bg-white border-none rounded text-base p-2 w-full shadow-[0_0_4px_rgba(0,0,0,0.2)]";
    const accent = isInsert ? "bg-green/15 text-green" : "bg-red-500/15 text-red-500";

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" onClick={closeModal} />
            <div className="flex flex-col gap-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-7 rounded-lg w-[min(480px,92%)]">
                <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                        <span className={`flex h-10 w-10 items-center justify-center rounded-full ${accent}`}>
                            {isInsert ? <Plus size={20} /> : <Minus size={20} />}
                        </span>
                        <h1 className="text-xl font-semibold">{isInsert ? "Влез на пари во каса" : "Излез на пари од каса"}</h1>
                    </div>
                    <button className="close-x-btn" onClick={closeModal}><X size={24} /></button>
                </div>

                <div className="grid grid-cols-2 gap-4">
                    <div className="flex flex-col gap-1">
                        <span className="text-[#666] text-xs">Сума</span>
                        <input className={inputCls} type="number" value={amount} placeholder="Внеси сума"
                               onChange={e => setAmount(e.target.value)} autoFocus />
                    </div>
                    <div className="flex flex-col gap-1">
                        <span className="text-[#666] text-xs">Причина</span>
                        <input className={inputCls} value={description} placeholder="Внеси причина"
                               onChange={e => setDescription(e.target.value)} />
                    </div>
                </div>

                {error && <span className="text-red-500 text-sm">{error}</span>}

                <div className="flex gap-3 items-center justify-end">
                    <button type="button" onClick={closeModal} disabled={loading}
                            className="px-5 py-2 rounded bg-black/10 text-sm hover:bg-black/15 transition-colors">
                        Откажи
                    </button>
                    <button type="button" onClick={submit} disabled={loading}
                            className={`group relative overflow-hidden flex items-center justify-center gap-2 px-6 py-2 rounded text-white font-medium shadow-[4px_2px_6px_rgba(0,0,0,0.2)] transition-all duration-300 hover:scale-105 active:scale-[0.98] disabled:cursor-not-allowed disabled:opacity-40 ${isInsert ? "bg-green" : "bg-red-500"}`}>
                        {loading ? <Loading width={24} height={24} /> : (<>
                            <span className="pointer-events-none absolute inset-0 -translate-x-full bg-gradient-to-r from-transparent via-white/25 to-transparent transition-transform duration-700 ease-out group-hover:translate-x-[180%]" />
                            <CheckCheck size={20} /> Потврди
                        </>)}
                    </button>
                </div>
            </div>
        </>,
        document.getElementById("portal")!
    );
}
