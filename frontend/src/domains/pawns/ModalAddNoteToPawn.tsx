import ReactDom from "react-dom";
import { useState } from "react";
import axios from "axios";
import { X, CheckCheck, StickyNote } from "lucide-react";
import Loading from "../../shared/components/Loading.tsx";
import { API_BASE } from "../../shared/api/config.ts";
import { PawnNote } from "./types.ts";

interface Props {
    pawnId: number;
    closeModal: () => void;
    /** Receives the created note so the caller can add it to a list without refetching. */
    onSaved: (note: PawnNote) => void;
}

const inputCls = "bg-white border-none rounded text-base p-2 w-full shadow-[0_0_4px_rgba(0,0,0,0.2)]";

// Add a note to a pawn. The description is the only thing staff enter — the
// author, timestamp and ACTIVE status are set by the server.
export default function ModalAddNoteToPawn({ pawnId, closeModal, onSaved }: Props) {
    const [description, setDescription] = useState("");
    const [busy, setBusy] = useState(false);
    const [error, setError] = useState("");

    const save = async () => {
        if (!description.trim()) { setError("Внесете опис на белешката."); return; }
        setBusy(true); setError("");
        try {
            const res = await axios.post(`${API_BASE}/pawns/${pawnId}/notes`, { description: description.trim() });
            onSaved(res.data);
        } catch (err: any) {
            setError(err?.response?.data?.message || "Белешката не е зачувана.");
            setBusy(false);
        }
    };

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1100]" onClick={busy ? undefined : closeModal} />
            <div className="flex flex-col gap-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1100] p-8 rounded-lg w-[min(520px,92%)]">
                <div className="flex justify-between items-center">
                    <h1 className="flex items-center gap-2 text-xl font-semibold">
                        <StickyNote size={20} /> Нова белешка за залог #{pawnId}
                    </h1>
                    <button className="close-x-btn disabled:opacity-40 disabled:cursor-not-allowed"
                            onClick={closeModal} disabled={busy}><X size={26} /></button>
                </div>

                <div className="flex flex-col gap-1">
                    <span className="text-[#666] text-xs">Опис *</span>
                    <textarea
                        className={`${inputCls} resize-none`} rows={4} autoFocus
                        value={description} placeholder="Внеси опис на белешката"
                        onChange={e => setDescription(e.target.value)}
                    />
                </div>

                {error && <span className="text-red-500 text-sm">{error}</span>}

                <div className="flex gap-3 items-center justify-end min-h-[40px]">
                    <button type="button" onClick={closeModal} disabled={busy}
                            className="px-5 py-2 rounded bg-black/10 text-sm hover:bg-black/15 transition-colors disabled:opacity-40 disabled:cursor-not-allowed">
                        Откажи
                    </button>
                    {busy ? <Loading width={28} height={28} /> : (
                        <button type="button" onClick={save}
                                className="group relative overflow-hidden flex items-center justify-center gap-2 px-6 py-2 rounded text-white font-medium bg-green shadow-[4px_2px_6px_rgba(0,0,0,0.2)] transition-all duration-300 hover:scale-105 active:scale-[0.98]">
                            <span className="pointer-events-none absolute inset-0 -translate-x-full bg-gradient-to-r from-transparent via-white/25 to-transparent transition-transform duration-700 ease-out group-hover:translate-x-[180%]" />
                            <CheckCheck size={20} /> Зачувај
                        </button>
                    )}
                </div>
            </div>
        </>,
        document.getElementById("portal")!
    );
}
