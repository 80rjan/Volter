import ReactDom from "react-dom";
import { X, CircleHelp, CheckCheck, CircleCheckBig } from 'lucide-react';
import { useEffect, useState } from "react";
import axios from "axios";
import Loading from './Loading.tsx';

interface Props {
    closeModal: () => void;
    isInsert: boolean;
    refresh: () => void;
}

export default function ModalAdjustCashRegister({ closeModal, isInsert, refresh }: Props) {
    const [showSuccMsg, setShowSuccMsg] = useState(false);
    const [showAdjust, setShowAdjust] = useState(true);
    const [successMsg, setSuccessMsg] = useState("");
    const [infoMsg, setInfoMsg] = useState("");
    const [amount, setAmount] = useState<string>("");
    const [description, setDescription] = useState("/");
    const [error, setError] = useState("");
    const [showError, setShowError] = useState(true);
    const [loading, setLoading] = useState(false);

    const insert = (amount: string, description: string) => {
        setLoading(true);
        axios.put(`http://localhost:3000/cashRegister/insert`, { amount, description })
            .then(() => {
                setSuccessMsg("Успешен внес на пари");
                setInfoMsg(`Додадени се ${Number(amount).toLocaleString("de-DE")} во каса!`);
                setShowSuccMsg(true);
                refresh();
            })
            .catch(error => console.error('Error inserting money:', error))
            .finally(() => setLoading(false));
    };

    const remove = (amount: string, description: string) => {
        setLoading(true);
        axios.put(`http://localhost:3000/cashRegister/remove`, { amount, description })
            .then(() => {
                setSuccessMsg("Успешен излез на пари");
                setInfoMsg(`Земени се ${Number(amount).toLocaleString("de-DE")} од каса!`);
                setShowSuccMsg(true);
                refresh();
            })
            .catch(error => console.error('Error removing money:', error))
            .finally(() => setLoading(false));
    };

    useEffect(() => {
        if (!showAdjust)
            isInsert ? insert(amount, description) : remove(amount, description);
    }, [showAdjust]);

    const inputClass = "border-none rounded-sm text-base p-2 shadow-[0_0_4px_rgba(0,0,0,0.2)] h-fit";
    const btnClass = "flex justify-center items-center gap-2 px-32 py-2 rounded bg-green text-white text-2xl shadow-[0_0_8px_rgba(0,0,0,0.2)] transition-all duration-400 hover:scale-105";

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" />
            <div className="flex flex-col items-center gap-3 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-4 pb-8 px-8 rounded-lg min-w-fit max-w-[90%]">
                <X size={32} className="ml-auto close-x-btn" onClick={closeModal} />
                {showAdjust && (
                    <>
                        <CircleHelp size={120} className="text-green mx-auto" />
                        <h1 className="text-center">Внеси сума за {isInsert ? 'влез во' : 'излез од'} каса</h1>
                        <form
                            className="flex flex-col items-center gap-4 mt-4"
                            onSubmit={e => {
                                e.preventDefault();
                                if (amount.length > 0) {
                                    isNaN(Number(amount))
                                        ? setError('Внеси валиден број!')
                                        : setShowAdjust(false);
                                } else {
                                    setShowError(true);
                                    setError('Внеси валидна сума!');
                                }
                            }}
                        >
                            <span className="flex gap-8">
                                <input className={inputClass} onChange={e => setAmount(e.target.value)} placeholder="Внеси сума" required />
                                <input className={inputClass} onChange={e => setDescription(e.target.value)} placeholder="Внеси причина" required />
                            </span>
                            <button type="submit" className={btnClass}>
                                <CheckCheck size={28} /> Потврди
                            </button>
                        </form>
                        {showError && <span className="text-red-500 italic text-xl">{error}</span>}
                    </>
                )}
                {loading ? <Loading /> : showSuccMsg && (
                    <>
                        <CircleCheckBig size={120} className="text-green mx-auto" />
                        <h1>{successMsg}</h1>
                        <p className="text-xl font-normal">{infoMsg}</p>
                    </>
                )}
            </div>
        </>,
        document.getElementById("portal")!
    );
}
