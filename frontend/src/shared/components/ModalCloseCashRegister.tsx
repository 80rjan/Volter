import ReactDom from "react-dom";
import { X, CheckCheck, LockKeyhole } from "lucide-react";
import { useState } from "react";
import axios from "axios";
import Loading from "./Loading.tsx";
import { API_BASE } from "../api/config.ts";

interface Props {
    registerId: number;
    expectedBalance?: number;
    closeModal: () => void;
    refresh: () => void;
}

export default function ModalCloseCashRegister({ registerId, expectedBalance, closeModal, refresh }: Props) {
    const [closingBalance, setClosingBalance] = useState("");
    const [loading, setLoading] = useState(false);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        axios.post(`${API_BASE}/cash-registers/${registerId}/close-session`, { countedClosingBalance: Number(closingBalance) })
            .then(() => { refresh(); closeModal(); })
            .catch(err => console.error("Error closing session:", err))
            .finally(() => setLoading(false));
    };

    const inputClass = "border-none rounded text-base p-2 shadow-[0_0_4px_rgba(0,0,0,0.2)] h-fit bg-white";

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" />
            <div className="flex flex-col gap-6 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-8 rounded-lg min-w-[320px]">
                <div className="flex justify-between items-center">
                    <div className="flex items-center gap-2">
                        <LockKeyhole size={28} />
                        <h1 className="text-xl font-semibold">Затвори Каса</h1>
                    </div>
                    <button className="close-x-btn disabled:opacity-40 disabled:cursor-not-allowed" onClick={closeModal} disabled={loading}><X size={28} /></button>
                </div>
                <form onSubmit={handleSubmit} className="flex flex-col gap-4">
                    <div className="flex flex-col gap-1">
                        <p className="text-[#666]">Изброена крајна состојба (ден)</p>
                        <input
                            className={inputClass}
                            type="number"
                            value={closingBalance}
                            onChange={e => setClosingBalance(e.target.value)}
                            required
                        />
                        {expectedBalance != null && (
                            <p className="text-xs text-[#666] mt-1">
                                Очекувано: {Number(expectedBalance).toLocaleString("de-DE")} ден — отстапување создава раздолжување.
                            </p>
                        )}
                    </div>
                    {loading ? (
                        <div className="flex justify-center items-center mt-2 min-h-[40px]"><Loading width={28} height={28} /></div>
                    ) : (
                        <button
                            type="submit"
                            className="flex justify-center items-center gap-2 px-8 py-2 rounded bg-red-500 text-white text-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] transition-all duration-300 hover:scale-105 disabled:opacity-40 disabled:cursor-not-allowed mt-2"
                        >
                            <CheckCheck size={22} /> Затвори
                        </button>
                    )}
                </form>
            </div>
        </>,
        document.getElementById("portal")!
    );
}
