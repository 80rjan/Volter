import ReactDom from "react-dom";
import { X, RotateCcw, HandCoins, ShoppingCart, Coins, CheckCheck, Ban, TriangleAlert } from 'lucide-react';
import { useState } from "react";
import Loading from "./Loading.tsx";
import CashSessionSelect from "./CashSessionSelect.tsx";

interface Props {
    pawnAction: 'redeem' | 'extend' | 'forfeit' | 'cancel' | null;
    id: number;
    category: string;
    successMsg: string;
    action: (...args: any[]) => void;
    priceBought: number;
    provision?: number;
    dailyProvision?: number;
    suggestedPrice?: number;
    daysLeft?: number;
    closeModal: () => void;
    title: string;
    loading: boolean;
    refresh?: () => void;
}

// Contextual icon + accent per action.
const META: Record<string, { Icon: any; ring: string }> = {
    redeem: { Icon: HandCoins, ring: "bg-red-500/15 text-red-500" },
    extend: { Icon: RotateCcw, ring: "bg-green/15 text-green" },
    forfeit: { Icon: ShoppingCart, ring: "bg-amber-100 text-amber-600" },
    cancel: { Icon: Ban, ring: "bg-red-100 text-red-600" },
    sale: { Icon: Coins, ring: "bg-green/15 text-green" },
};

const inputClass = "bg-white border-none rounded text-base p-2 w-full shadow-[0_0_4px_rgba(0,0,0,0.2)]";

export default function ModalActions({
    pawnAction, id, category, action,
    priceBought, provision, dailyProvision, suggestedPrice,
    daysLeft, closeModal, title, loading,
}: Props) {
    const isConfirmOnly = pawnAction === "forfeit" || pawnAction === "cancel";
    // Redeem/extend/sell move cash, so they need an open register session;
    // forfeit/cancel are confirm-only and don't.
    const requiresSession = !isConfirmOnly;

    // The session the action records into — chosen via the selector below, which
    // also syncs the active register so the parent's session resolution matches.
    const [openSessionId, setOpenSessionId] = useState<number | null>(null);
    const [sessionChecked, setSessionChecked] = useState(!requiresSession);
    const noSession = requiresSession && sessionChecked && openSessionId == null;

    const penaltyPrice = (daysLeft ?? 0) < 0
        ? (pawnAction === "extend" ? Math.abs(daysLeft!) * (dailyProvision ?? 0) : (provision ?? 0))
        : 0;

    const initialPrice = Math.round(category === "sale"
        ? (suggestedPrice ?? 0)
        : (suggestedPrice ?? 0) + penaltyPrice);

    const [price, setPrice] = useState(initialPrice);
    const [carryOverDays, setCarryOverDays] = useState(0);
    const [description, setDescription] = useState("");
    const [error, setError] = useState("");

    const submit = () => {
        if (isConfirmOnly) { action(id, category); return; }
        if (!(price > 0) || String(price).length === 0) { setError("Внеси сума!"); return; }
        if (isNaN(price)) { setError("Внеси валиден број!"); return; }
        if (pawnAction === "extend") action(id, category, price, description, carryOverDays);
        else action(id, category, price, description);
    };

    const meta = META[pawnAction ?? "sale"] ?? META.sale;
    const Icon = meta.Icon;

    const info = (label: string, value: React.ReactNode) => (
        <span className="text-[#666]">{label}: <span className="font-semibold text-black">{value}</span></span>
    );

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" onClick={loading ? undefined : closeModal} />
            <div className="flex flex-col gap-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-7 rounded-lg w-[min(540px,92%)]">
                <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                        <span className={`flex h-10 w-10 items-center justify-center rounded-full ${meta.ring}`}>
                            <Icon size={20} />
                        </span>
                        <h1 className="text-xl font-semibold">{title}</h1>
                    </div>
                    <button className="close-x-btn disabled:opacity-40 disabled:cursor-not-allowed" onClick={closeModal} disabled={loading}><X size={24} /></button>
                </div>

                {noSession && (
                    <div className="flex items-center gap-2 bg-red-500/10 text-red-600 rounded p-3 text-sm">
                        <TriangleAlert size={18} /> Нема отворена каса. Отворете каса за да го извршите дејството.
                    </div>
                )}

                {isConfirmOnly ? (
                    <p className="text-[#666]">
                        {pawnAction === "cancel"
                            ? "Продажбата ќе биде откажана. Ова дејство не може да се врати."
                            : "Залогот ќе биде пренесен во продажба. Ова дејство не може да се врати."}
                    </p>
                ) : (
                    <div className="flex flex-col gap-4">
                        <div className="flex flex-wrap gap-x-6 gap-y-1 text-sm">
                            {info("Исплатени пари", priceBought.toLocaleString("de-DE"))}
                            {category !== "sale" && info("Провизија", provision?.toLocaleString("de-DE"))}
                            {category !== "sale" && info("Казна", Math.round(penaltyPrice).toLocaleString("de-DE"))}
                            {pawnAction === "extend" && info("Префрлени денови", carryOverDays.toLocaleString("de-DE"))}
                        </div>
                        <CashSessionSelect value={openSessionId} onChange={setOpenSessionId}
                                           onLoaded={() => setSessionChecked(true)} />
                        <div className="grid grid-cols-2 gap-4">
                            <div className="flex flex-col gap-1">
                                <span className="text-[#666] text-xs">Сума</span>
                                <input
                                    className={inputClass} type="number" value={price} placeholder="Внеси сума" required
                                    onChange={e => {
                                        setPrice(Number(e.target.value));
                                        setCarryOverDays(Math.floor((Number(e.target.value) - ((suggestedPrice ?? 0) + penaltyPrice)) / (dailyProvision ?? 1)));
                                    }}
                                />
                            </div>
                            <div className="flex flex-col gap-1">
                                <span className="text-[#666] text-xs">Опис</span>
                                <input className={inputClass} value={description} placeholder="Внеси опис"
                                       onChange={e => setDescription(e.target.value)} />
                            </div>
                        </div>
                    </div>
                )}

                {error && <span className="text-red-500 text-sm">{error}</span>}

                <div className="flex gap-3 items-center justify-end min-h-[40px]">
                    <button type="button" onClick={closeModal} disabled={loading}
                            className="px-5 py-2 rounded bg-black/10 text-sm hover:bg-black/15 transition-colors disabled:opacity-40 disabled:cursor-not-allowed">
                        Откажи
                    </button>
                    {loading ? <Loading width={28} height={28} /> : (
                        <button type="button" onClick={submit} disabled={noSession}
                                className={`group relative overflow-hidden flex items-center justify-center gap-2 px-6 py-2 rounded text-white font-medium shadow-[4px_2px_6px_rgba(0,0,0,0.2)] transition-all duration-300 hover:scale-105 active:scale-[0.98] disabled:cursor-not-allowed disabled:opacity-40 ${pawnAction === "cancel" || pawnAction == "redeem" ? "bg-red-500" : pawnAction === "forfeit" ? "bg-amber-500" : "bg-green"}`}>
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
