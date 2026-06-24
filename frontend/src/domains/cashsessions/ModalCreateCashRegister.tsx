import ReactDom from "react-dom";
import { useState } from "react";
import axios from "axios";
import { X, CheckCheck, Landmark } from "lucide-react";
import Loading from "../../shared/components/Loading.tsx";
import { API_BASE } from "../../shared/api/config.ts";

interface Props {
    closeModal: () => void;
    onCreated: () => void;
}

const inputCls = "bg-white border-none rounded text-base p-2 w-full shadow-[0_0_4px_rgba(0,0,0,0.2)]";

export default function ModalCreateCashRegister({ closeModal, onCreated }: Props) {
    const [code, setCode] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const submit = () => {
        if (!code.trim()) { setError("Внеси ознака за касата."); return; }
        setLoading(true);
        setError("");
        axios.post(`${API_BASE}/cash-registers`, { code: code.trim() })
            .then(() => { onCreated(); closeModal(); })
            .catch(err => {
                const msg: string = err?.response?.data?.message ?? "";
                if (/already exists|duplicate|unique/i.test(msg)) {
                    setError("Веќе постои каса со оваа ознака.");
                } else {
                    setError(msg || "Грешка при креирање на каса.");
                }
                setLoading(false);
            });
    };

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" onClick={loading ? undefined : closeModal} />
            <div className="flex flex-col gap-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-7 rounded-lg w-[min(440px,92%)]">
                <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                        <span className="flex h-10 w-10 items-center justify-center rounded-full bg-green/15 text-green">
                            <Landmark size={20} />
                        </span>
                        <h1 className="text-xl font-semibold">Нова каса</h1>
                    </div>
                    <button className="close-x-btn disabled:opacity-40 disabled:cursor-not-allowed" onClick={closeModal} disabled={loading}><X size={24} /></button>
                </div>

                <div className="flex flex-col gap-1">
                    <span className="text-[#666] text-xs">Ознака на каса</span>
                    <input className={inputCls} value={code} placeholder="пр. CR-01" autoFocus
                           onChange={e => setCode(e.target.value)} onKeyDown={e => e.key === "Enter" && submit()} />
                </div>

                {error && <span className="text-red-500 text-sm">{error}</span>}

                <div className="flex gap-3 items-center justify-end min-h-[40px]">
                    <button type="button" onClick={closeModal} disabled={loading}
                            className="px-5 py-2 rounded bg-black/10 text-sm hover:bg-black/15 transition-colors disabled:opacity-40 disabled:cursor-not-allowed">
                        Откажи
                    </button>
                    {loading ? <Loading width={28} height={28} /> : (
                        <button type="button" onClick={submit}
                                className="flex items-center justify-center gap-2 px-6 py-2 rounded text-white font-medium bg-green shadow-[4px_2px_6px_rgba(0,0,0,0.2)] transition-all hover:scale-105 disabled:opacity-40">
                            <CheckCheck size={20} /> Креирај
                        </button>
                    )}
                </div>
            </div>
        </>,
        document.getElementById("portal")!
    );
}
