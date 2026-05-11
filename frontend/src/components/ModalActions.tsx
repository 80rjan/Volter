import ReactDom from "react-dom";
import { X, CircleHelp, CheckCheck } from 'lucide-react';
import { useEffect, useState } from "react";
import Loading from "./Loading.tsx";

interface Props {
    pawnAction: 'close' | 'continue' | null;
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

export default function ModalActions({
    pawnAction, id, category, successMsg, action,
    priceBought, provision, dailyProvision, suggestedPrice,
    daysLeft, closeModal, title, loading,
}: Props) {
    const penaltyPrice = (daysLeft ?? 0) < 0
        ? (pawnAction === "continue" ? Math.abs(daysLeft!) * (dailyProvision ?? 0) : (provision ?? 0))
        : 0;

    const initialPrice = Math.round(category === "sale"
        ? (suggestedPrice ?? 0)
        : (suggestedPrice ?? 0) + penaltyPrice);

    const [price, setPrice] = useState(initialPrice);
    const [carryOverDays, setCarryOverDays] = useState(
        Math.round((initialPrice - ((suggestedPrice ?? 0) + penaltyPrice)) / (dailyProvision ?? 1))
    );
    const [description, setDescription] = useState("");
    const [error, setError] = useState("");
    const [showError] = useState(true);

    const useActionFunc = () => {
        try {
            if (category === "sale")
                action(id, price, description);
            else
                pawnAction === "continue"
                    ? action(id, category, price, description, carryOverDays)
                    : action(id, category, price, description);
        } catch (err) {
            console.error('Error in action:', err);
        }
    };

    const inputClass = "border-none rounded-sm text-xl p-2 shadow-[0_0_4px_rgba(0,0,0,0.2)] h-fit";

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" />
            <div className="flex flex-col items-center gap-3 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-4 pb-8 px-8 rounded-lg min-w-fit max-w-[90%]">
                <X size={32} className="ml-auto close-x-btn" onClick={closeModal} />
                <CircleHelp size={120} className="text-green mx-auto" />
                <h1 className="text-center">{title}</h1>
                <form
                    className="flex flex-col items-center gap-4 mt-4"
                    onSubmit={e => {
                        e.preventDefault();
                        if (String(price).length > 0 || price > 0) {
                            if (isNaN(price)) setError('Внеси валиден број!');
                            else useActionFunc();
                        } else {
                            setError('Внеси сума!');
                        }
                    }}
                >
                    <div className="flex items-center gap-8">
                        <div className="flex flex-col gap-4">
                            <p className="text-xl font-normal">Исплатени пари: <span className="font-semibold">{priceBought.toLocaleString("de-DE")}</span></p>
                            {category !== "sale" && <p className="text-xl font-normal">Провизија: <span className="font-semibold">{provision?.toLocaleString("de-DE")}</span></p>}
                            {category !== "sale" && <p className="text-xl font-normal">Казна: <span className="font-semibold">{Math.round(penaltyPrice).toLocaleString("de-DE")}</span></p>}
                            {successMsg === "Успешно продолжен залог!" && (
                                <p className="text-xl font-normal">Префрлени денови: <span className="font-semibold">{carryOverDays.toLocaleString("de-DE")}</span></p>
                            )}
                        </div>
                        <div className="flex flex-col gap-4">
                            <input
                                className={inputClass}
                                onChange={e => {
                                    setPrice(Number(e.target.value));
                                    setCarryOverDays(Math.round((Number(e.target.value) - ((suggestedPrice ?? 0) + penaltyPrice)) / (dailyProvision ?? 1)));
                                }}
                                type="number"
                                placeholder="Внеси сума"
                                value={price}
                                required
                            />
                            <input
                                className={inputClass}
                                onChange={e => setDescription(e.target.value)}
                                placeholder="Внеси опис"
                                value={description}
                            />
                        </div>
                    </div>
                    <div className="flex gap-4 items-center">
                        <button
                            type="submit"
                            disabled={loading}
                            className="flex justify-center items-center gap-2 px-32 py-2 rounded bg-green text-white text-2xl shadow-[0_0_8px_rgba(0,0,0,0.2)] transition-all duration-400 hover:scale-105 disabled:cursor-not-allowed disabled:opacity-40"
                        >
                            <CheckCheck size={28} /> Потврди
                        </button>
                        {loading && <Loading width={40} height={40} />}
                    </div>
                </form>
                {showError && <span className="text-red-500 italic text-xl">{error}</span>}
            </div>
        </>,
        document.getElementById("portal")!
    );
}
