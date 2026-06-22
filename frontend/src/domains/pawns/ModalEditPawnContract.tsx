import ReactDom from "react-dom";
import { useState } from "react";
import axios from "axios";
import { X, CheckCheck, FileText } from "lucide-react";
import Loading from "../../shared/components/Loading.tsx";
import { API_BASE } from "../../shared/api/config.ts";
import { PawnDetailed } from "./types.ts";
import { resolveActiveSessionId } from "../../shared/utils/activeSession.ts";

interface Props {
    pawn: PawnDetailed;
    closeModal: () => void;
    onSaved: () => void;
}

const inputCls = "bg-white border-none rounded text-base p-2 w-full shadow-[0_0_4px_rgba(0,0,0,0.2)]";

// Edit an active contract's terms. Changing the value (principal) moves cash on
// the open session; changing the duration shifts the due date.
export default function ModalEditPawnContract({ pawn, closeModal, onSaved }: Props) {
    const [principal, setPrincipal] = useState(String(pawn.principalAmount));
    const [interest, setInterest] = useState(String(pawn.interestAmount));
    // Duration is restricted to the same options as pawn creation: 15 or 30 days.
    const [termDays, setTermDays] = useState(pawn.termDays === 15 ? "15" : "30");
    const [busy, setBusy] = useState(false);
    const [error, setError] = useState("");

    const delta = Number(principal) - pawn.principalAmount;
    const principalChanged = Number.isFinite(delta) && delta !== 0;

    const save = async () => {
        if (!principal.trim() || !interest.trim() || !termDays.trim()) { setError("Пополни ги сите полиња."); return; }
        if (Number(termDays) < 1) { setError("Времетраењето мора да биде барем 1 ден."); return; }
        setBusy(true); setError("");
        try {
            let sessionId: number | null = null;
            if (principalChanged) {
                sessionId = await resolveActiveSessionId();
                if (!sessionId) { setError("Нема отворена каса за промена на вредноста."); setBusy(false); return; }
            }
            await axios.patch(`${API_BASE}/pawns/${pawn.id}/contract`, {
                principalAmount: Number(principal),
                interestAmount: Number(interest),
                termDays: Number(termDays),
                cashRegisterSessionId: sessionId,
            });
            onSaved();
        } catch (err: any) {
            setError(err?.response?.data?.message || "Измената не успеа.");
            setBusy(false);
        }
    };

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1100]" onClick={closeModal} />
            <div className="flex flex-col gap-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1100] p-8 rounded-lg w-[min(520px,92%)]">
                <div className="flex justify-between items-center">
                    <h1 className="flex items-center gap-2 text-xl font-semibold"><FileText size={20} /> Измени залог #{pawn.id}</h1>
                    <button className="close-x-btn" onClick={closeModal}><X size={26} /></button>
                </div>
                <div className="flex flex-col gap-4">
                    <div className="flex flex-col gap-1">
                        <span className="text-[#666] text-xs">Вредност на залог *</span>
                        <input className={inputCls} type="number" value={principal} onChange={e => setPrincipal(e.target.value)} />
                    </div>
                    <div className="flex flex-col gap-1">
                        <span className="text-[#666] text-xs">Провизија *</span>
                        <input className={inputCls} type="number" value={interest} onChange={e => setInterest(e.target.value)} />
                    </div>
                    <div className="flex flex-col gap-1">
                        <span className="text-[#666] text-xs">Времетраење *</span>
                        <select className={inputCls} value={termDays} onChange={e => setTermDays(e.target.value)}>
                            <option value="15">15 дена</option>
                            <option value="30">30 дена</option>
                        </select>
                    </div>
                    {principalChanged && (
                        <p className={`text-xs ${delta > 0 ? "text-red-500" : "text-green"}`}>
                            {delta > 0
                                ? `Ќе се издадат ${Math.abs(delta).toLocaleString("de-DE")} ден од каса (доплата на клиент).`
                                : `Ќе се вратат ${Math.abs(delta).toLocaleString("de-DE")} ден во каса (поврат од клиент).`}
                        </p>
                    )}
                    {error && <p className="text-red-500 text-sm">{error}</p>}
                </div>
                <div className="flex justify-end gap-3">
                    <button onClick={closeModal} disabled={busy} className="px-5 py-2 rounded bg-black/10 text-sm hover:bg-black/15 transition-colors">Откажи</button>
                    <button onClick={save} disabled={busy}
                        className="flex items-center justify-center gap-2 px-6 py-2 rounded text-white font-medium bg-green shadow-[4px_2px_6px_rgba(0,0,0,0.2)] transition-all hover:scale-105 disabled:opacity-40">
                        {busy ? <Loading width={20} height={20} /> : <><CheckCheck size={20} /> Зачувај</>}
                    </button>
                </div>
            </div>
        </>,
        document.getElementById("portal")!
    );
}
