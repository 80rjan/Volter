import ReactDom from "react-dom";
import { X, ClipboardPlus, CheckCheck } from 'lucide-react';
import { useState } from "react";
import axios from "axios";
import Loading from "../../shared/components/Loading.tsx";

interface Props {
    closeModal: () => void;
    refresh: () => void;
}

export default function ModalAddNewExpense({ closeModal, refresh }: Props) {
    const [formData, setFormData] = useState({
        year: 0, month: 0, rent: 0, salaries: 0, bills: 0, other: 0, description: '',
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        if (formData.year < 2000 || formData.month < 1 || formData.month > 12) {
            setError("Внеси валидна година и месец");
            setLoading(false);
            return;
        }
        axios.post('http://localhost:3000/expenses/insert', formData)
            .then(res => {
                if (res.data.message !== 'Успешно внесен расход') setError(res.data.message);
                else { refresh(); closeModal(); }
            })
            .catch(err => console.error('Error adding expense:', err))
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
                        <ClipboardPlus size={32} />
                        <h1>Внеси Нов Расход</h1>
                    </div>
                    <button onClick={closeModal} className="[&_svg]:transition-all [&_svg]:duration-400 [&_svg:hover]:rotate-90">
                        <X size={32} />
                    </button>
                </div>
                <form className="flex flex-col items-center gap-8" onSubmit={handleSubmit}>
                    <div className="grid grid-cols-2 gap-4 gap-x-16">
                        {[
                            { name: 'year', label: 'Година', type: 'number', required: true },
                            { name: 'month', label: 'Месец', type: 'number', required: true },
                            { name: 'rent', label: 'Кирија', type: 'number', required: false },
                            { name: 'salaries', label: 'Плати', type: 'number', required: false },
                            { name: 'bills', label: 'Сметки', type: 'number', required: false },
                            { name: 'other', label: 'Друго', type: 'number', required: false },
                        ].map(f => (
                            <div key={f.name} className="flex flex-col gap-1">
                                <p className={labelClass}>{f.label}</p>
                                <input name={f.name} type={f.type} className={inputClass} onChange={handleInputChange} required={f.required} />
                            </div>
                        ))}
                        <div className="col-span-2 flex flex-col gap-1">
                            <p className={labelClass}>Опис</p>
                            <input name="description" className={inputClass} onChange={handleInputChange} />
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
                    <p className="text-red-500 italic text-xl">{error}</p>
                </form>
            </div>
        </>,
        document.getElementById("portal")!
    );
}
