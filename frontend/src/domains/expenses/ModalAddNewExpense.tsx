import ReactDom from "react-dom";
import { X, Wallet, CheckCheck, TriangleAlert } from "lucide-react";
import { useState, useEffect } from "react";
import axios from "axios";
import Loading from "../../shared/components/Loading.tsx";
import { API_BASE } from "../../shared/api/config.ts";
import { resolveActiveSessionId } from "../../shared/utils/activeSession.ts";
import { EXPENSE_CATEGORIES, EXPENSE_CATEGORY_LABEL } from "./types.ts";

interface Props {
    closeModal: () => void;
    refresh: () => void;
}

const inputClass = "bg-white border-none rounded text-base p-2 w-full shadow-[0_0_4px_rgba(0,0,0,0.2)]";

export default function ModalAddNewExpense({ closeModal, refresh }: Props) {
    const [form, setForm] = useState({
        category: "RENT",
        amount: "",
        description: "",
        date: new Date().toISOString().split("T")[0],
    });
    const [openSessionId, setOpenSessionId] = useState<number | null>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    useEffect(() => {
        // An expense moves money out of the active register's open session.
        resolveActiveSessionId()
            .then(setOpenSessionId)
            .catch(() => setOpenSessionId(null));
    }, []);

    const h = (name: string, value: string) => setForm(prev => ({ ...prev, [name]: value }));

    const submit = (e: React.FormEvent) => {
        e.preventDefault();
        if (!openSessionId) { setError("Нема отворена каса — отворете каса пред да внесете расход."); return; }
        const amount = Number(form.amount);
        if (!form.amount.trim() || isNaN(amount) || amount <= 0) { setError("Внеси валидна сума."); return; }
        setLoading(true);
        setError("");
        axios.post(`${API_BASE}/expenses`, {
            category: form.category,
            amount,
            description: form.description.trim() || null,
            date: form.date,
            cashRegisterSessionId: openSessionId,
        })
            .then(() => { refresh(); closeModal(); })
            .catch(() => { setError("Грешка при внесување на расходот."); setLoading(false); });
    };

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" onClick={loading ? undefined : closeModal} />
            <div className="flex flex-col gap-6 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-8 rounded-lg w-[min(620px,92%)] max-h-[92vh] overflow-y-auto scrollbar-hidden">
                <div className="flex justify-between">
                    <div className="flex items-center gap-2">
                        <Wallet size={28} />
                        <h1 className="text-2xl font-semibold">Внеси нов расход</h1>
                    </div>
                    <button className="close-x-btn disabled:opacity-40 disabled:cursor-not-allowed" onClick={closeModal} disabled={loading}><X size={32} /></button>
                </div>

                {!openSessionId && (
                    <div className="flex items-center gap-2 bg-red-500/10 text-red-600 rounded p-3 text-sm">
                        <TriangleAlert size={18} /> Нема отворена каса. Отворете каса за да можете да внесете расход.
                    </div>
                )}

                <form onSubmit={submit} className="flex flex-col gap-6">
                    <div className="grid grid-cols-2 gap-x-8 gap-y-4">
                        <div className="flex flex-col gap-1">
                            <p className="text-[#666] text-sm">Тип на расход</p>
                            <select className={inputClass} value={form.category} onChange={e => h("category", e.target.value)} required>
                                {EXPENSE_CATEGORIES.map(c => <option key={c} value={c}>{EXPENSE_CATEGORY_LABEL[c]}</option>)}
                            </select>
                        </div>
                        <div className="flex flex-col gap-1">
                            <p className="text-[#666] text-sm">Износ</p>
                            <input className={inputClass} type="number" value={form.amount} onChange={e => h("amount", e.target.value)} required />
                        </div>
                        <div className="flex flex-col gap-1">
                            <p className="text-[#666] text-sm">Датум</p>
                            <input className={inputClass} type="date" value={form.date} onChange={e => h("date", e.target.value)} required />
                        </div>
                        <div className="flex flex-col gap-1">
                            <p className="text-[#666] text-sm">Опис</p>
                            <input className={inputClass} value={form.description} onChange={e => h("description", e.target.value)} />
                        </div>
                    </div>

                    {error && <p className="text-red-500 text-sm">{error}</p>}

                    <div className="flex gap-4 items-center justify-center mt-2 min-h-[44px]">
                        {loading ? <Loading width={30} height={30} /> : (
                            <button type="submit" disabled={!openSessionId}
                                    className="group relative overflow-hidden flex justify-center items-center gap-2 px-16 py-2.5 rounded bg-green text-white text-base font-semibold shadow-[4px_2px_6px_rgba(0,0,0,0.2)] transition-all duration-300 hover:scale-105 active:scale-[0.98] disabled:cursor-not-allowed disabled:opacity-40 disabled:hover:scale-100">
                                <span className="pointer-events-none absolute inset-0 -translate-x-full bg-gradient-to-r from-transparent via-white/25 to-transparent transition-transform duration-700 ease-out group-hover:translate-x-[180%]" />
                                <CheckCheck size={22} /> Потврди
                            </button>
                        )}
                    </div>
                </form>
            </div>
        </>,
        document.getElementById("portal")!
    );
}
