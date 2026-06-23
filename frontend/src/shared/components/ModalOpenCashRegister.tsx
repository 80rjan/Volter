import ReactDom from "react-dom";
import { X, CheckCheck, Landmark, TriangleAlert } from "lucide-react";
import { useEffect, useState } from "react";
import axios from "axios";
import Loading from "./Loading.tsx";
import { API_BASE } from "../api/config.ts";

interface CashRegisterOption {
    id: number;
    code: string;
}

interface Props {
    registerId?: number;
    closeModal: () => void;
    refresh: () => void;
}

export default function ModalOpenCashRegister({ registerId, closeModal, refresh }: Props) {
    const [registers, setRegisters] = useState<CashRegisterOption[]>([]);
    const [cashRegisterId, setCashRegisterId] = useState<number | "">(registerId ?? "");
    const [openingBalance, setOpeningBalance] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    useEffect(() => {
        axios.get(`${API_BASE}/cash-registers`)
            .then(res => {
                setRegisters(res.data);
                if (registerId != null) setCashRegisterId(registerId);
                else if (res.data.length > 0) setCashRegisterId(res.data[0].id);
            })
            .catch(err => console.error("Error fetching registers:", err));
    }, []);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (cashRegisterId === "") return;
        setLoading(true);
        setError("");
        axios.post(`${API_BASE}/cash-registers/${cashRegisterId}/open-session`, {
            openingBalance: Number(openingBalance),
        })
            .then(() => { refresh(); closeModal(); })
            .catch(err => {
                const msg: string = err?.response?.data?.message ?? "";
                // The backend rejects opening a second session on the same register.
                if (/already has an OPEN session/i.test(msg)) {
                    setError("Оваа каса веќе има отворена сесија. За да отворите нова, прво мора таа да се затвори.");
                } else {
                    setError(msg || "Грешка при отворање каса. Обидете се повторно.");
                }
            })
            .finally(() => setLoading(false));
    };

    const inputClass = "border-none rounded text-base p-2 shadow-[0_0_4px_rgba(0,0,0,0.2)] h-fit bg-white";

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" />
            <div className="flex flex-col gap-6 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-8 rounded-lg min-w-[340px]">
                <div className="flex justify-between items-center">
                    <div className="flex items-center gap-2">
                        <Landmark size={28} />
                        <h1 className="text-xl font-semibold">Отвори Каса</h1>
                    </div>
                    <button className="close-x-btn disabled:opacity-40 disabled:cursor-not-allowed" onClick={closeModal} disabled={loading}><X size={28} /></button>
                </div>
                <form onSubmit={handleSubmit} className="flex flex-col gap-4">
                    <div className="flex flex-col gap-1">
                        <p className="text-[#666]">Каса</p>
                        <select
                            className="bg-white border-none shadow-[0_0_4px_rgba(0,0,0,0.2)] rounded p-2 text-base"
                            value={cashRegisterId}
                            onChange={e => setCashRegisterId(Number(e.target.value))}
                            required
                        >
                            {registers.map(r => (
                                <option key={r.id} value={r.id}>{r.code}</option>
                            ))}
                        </select>
                    </div>
                    <div className="flex flex-col gap-1">
                        <p className="text-[#666]">Почетна состојба (ден)</p>
                        <input
                            className={inputClass}
                            type="number"
                            value={openingBalance}
                            onChange={e => setOpeningBalance(e.target.value)}
                            required
                        />
                    </div>
                    {error && (
                        <div className="flex items-center gap-2 bg-red-500/10 text-red-600 rounded p-3 text-sm">
                            <TriangleAlert size={18} className="shrink-0" /> {error}
                        </div>
                    )}
                    {loading ? (
                        <div className="flex justify-center items-center mt-2 min-h-[40px]"><Loading width={28} height={28} /></div>
                    ) : (
                        <button
                            type="submit"
                            className="flex justify-center items-center gap-2 px-8 py-2 rounded bg-green text-white text-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] transition-all duration-300 hover:scale-105 disabled:opacity-40 disabled:cursor-not-allowed mt-2"
                        >
                            <CheckCheck size={22} /> Потврди
                        </button>
                    )}
                </form>
            </div>
        </>,
        document.getElementById("portal")!
    );
}
