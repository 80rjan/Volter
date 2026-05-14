import ReactDom from "react-dom";
import { X, ClipboardPlus, CheckCheck } from 'lucide-react';
import { useState } from "react";
import axios from "axios";
import Loading from "../../shared/components/Loading.tsx";
import { API_BASE } from "../../shared/api/config.ts";

interface Props {
    closeModal: () => void;
    refresh: () => void;
}

const EXPENSE_TYPES = [
    { value: 'RENT', label: 'Кирија' },
    { value: 'SALARIES', label: 'Плати' },
    { value: 'BILLS', label: 'Сметки' },
    { value: 'UTILITIES', label: 'Комуналии' },
    { value: 'SUPPLIES', label: 'Материјали' },
    { value: 'MAINTENANCE', label: 'Одржување' },
    { value: 'MARKETING', label: 'Маркетинг' },
    { value: 'TRAVEL', label: 'Патувања' },
    { value: 'OTHER', label: 'Останато' },
];

export default function ModalAddNewExpense({ closeModal, refresh }: Props) {
    const [formData, setFormData] = useState({
        expenseType: 'RENT',
        amount: '',
        description: '',
        date: new Date().toISOString().split('T')[0],
    });
    const [loading, setLoading] = useState(false);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        axios.post(`${API_BASE}/expenses/create`, {
            expenseType: formData.expenseType,
            amount: Number(formData.amount),
            description: formData.description,
            date: formData.date,
            transactionDescription: formData.description,
        })
            .then(() => { refresh(); closeModal(); })
            .catch(err => console.error('Error adding expense:', err))
            .finally(() => setLoading(false));
    };

    const inputClass = "border-none rounded-sm text-base p-2 shadow-[0_0_4px_rgba(0,0,0,0.2)] h-fit";
    const selectClass = "border-none rounded-sm text-base p-2 shadow-[0_0_4px_rgba(0,0,0,0.2)] h-fit bg-white";
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
                <form className="flex flex-col items-center gap-6" onSubmit={handleSubmit}>
                    <div className="grid grid-cols-2 gap-4 gap-x-12">
                        <div className="flex flex-col gap-1">
                            <p className={labelClass}>Тип на расход</p>
                            <select name="expenseType" className={selectClass} value={formData.expenseType} onChange={handleChange} required>
                                {EXPENSE_TYPES.map(t => (
                                    <option key={t.value} value={t.value}>{t.label}</option>
                                ))}
                            </select>
                        </div>
                        <div className="flex flex-col gap-1">
                            <p className={labelClass}>Износ</p>
                            <input name="amount" type="number" className={inputClass} onChange={handleChange} required />
                        </div>
                        <div className="flex flex-col gap-1">
                            <p className={labelClass}>Датум</p>
                            <input name="date" type="date" className={inputClass} value={formData.date} onChange={handleChange} required />
                        </div>
                        <div className="flex flex-col gap-1">
                            <p className={labelClass}>Опис</p>
                            <input name="description" className={inputClass} onChange={handleChange} />
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
