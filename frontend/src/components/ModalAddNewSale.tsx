import ReactDom from "react-dom";
import { X, BookmarkPlus, CheckCheck } from 'lucide-react';
import { useState } from "react";
import axios from "axios";
import Loading from "./Loading.tsx";

interface Props {
    closeModal: () => void;
    refresh: () => void;
}

export default function ModalAddNewSale({ closeModal, refresh }: Props) {
    const [formData, setFormData] = useState({ price_bought: 0, description: '' });
    const [loading, setLoading] = useState(false);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        axios.post('http://localhost:3000/sales/insertSale', formData)
            .then(() => { refresh(); closeModal(); })
            .catch(err => console.error('Error adding sale:', err))
            .finally(() => setLoading(false));
    };

    const inputClass = "border-none rounded-sm text-base p-2 shadow-[0_0_4px_rgba(0,0,0,0.2)] h-fit";
    const labelClass = "text-[#666] text-sm -ml-1";

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" />
            <div className="flex flex-col gap-8 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-8 rounded-lg min-w-fit max-w-[90%]">
                <div className="flex justify-between">
                    <div className="flex items-center gap-2">
                        <BookmarkPlus size={32} />
                        <h1>Внеси Нова Продажба</h1>
                    </div>
                    <button onClick={closeModal} className="[&_svg]:transition-all [&_svg]:duration-400 [&_svg:hover]:rotate-90">
                        <X size={32} />
                    </button>
                </div>
                <form className="flex flex-col items-center gap-8" onSubmit={handleSubmit}>
                    <div className="flex gap-8">
                        <div className="flex flex-col gap-1">
                            <p className={labelClass}>Вредност на предметот</p>
                            <input name="price_bought" type="number" className={inputClass} onChange={handleInputChange} required />
                        </div>
                        <div className="flex flex-col gap-1">
                            <p className={labelClass}>Опис</p>
                            <input name="description" className={inputClass} onChange={handleInputChange} required />
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
            </div>
        </>,
        document.getElementById("portal")!
    );
}
