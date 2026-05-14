import ReactDom from "react-dom";
import { X, BookmarkPlus, CheckCheck } from 'lucide-react';
import { useState } from "react";
import axios from "axios";
import Loading from "../../shared/components/Loading.tsx";
import { API_BASE } from "../../shared/api/config.ts";

interface Props {
    closeModal: () => void;
    refresh: () => void;
}

export default function ModalAddNewSale({ closeModal, refresh }: Props) {
    const [formData, setFormData] = useState({
        purchasePrice: '', description: '',
        name: '', embg: '', telephone: '', telephone_2: '', city: '',
    });
    const [loading, setLoading] = useState(false);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        axios.post(`${API_BASE}/sales`, {
            purchasePrice: Number(formData.purchasePrice),
            transactionDescription: formData.description,
            item: {
                referenceStrategy: "NEW",
                itemType: "OTHER",
                description: formData.description,
                category: "OTHER",
            },
            customer: {
                referenceStrategy: "NEW",
                name: formData.name,
                embg: formData.embg,
                phoneNumber: formData.telephone,
                reservePhoneNumber: formData.telephone_2 || null,
                city: formData.city,
            },
        })
            .then(() => { refresh(); closeModal(); })
            .catch(err => console.error('Error adding sale:', err))
            .finally(() => setLoading(false));
    };

    const inputClass = "border-none rounded-sm text-base p-2 shadow-[0_0_4px_rgba(0,0,0,0.2)] h-fit";
    const labelClass = "text-[#666] text-sm -ml-1";

    const field = (label: string, name: string, type = "text", required = true) => (
        <div key={name} className="flex flex-col gap-1">
            <p className={labelClass}>{label}</p>
            <input name={name} type={type} className={inputClass} onChange={handleChange} required={required} />
        </div>
    );

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
                <form className="flex flex-col items-center gap-6" onSubmit={handleSubmit}>
                    <div className="flex gap-8 items-start">
                        <div className="flex flex-col gap-2">
                            <p className="font-medium mb-1">Податоци за предметот</p>
                            {field("Вредност на предметот", "purchasePrice", "number")}
                            {field("Опис", "description")}
                        </div>
                        <div className="flex flex-col gap-2">
                            <p className="font-medium mb-1">Податоци за клиентот</p>
                            {field("Ime", "name")}
                            {field("Ембг", "embg")}
                            {field("Телефон 1", "telephone")}
                            {field("Телефон 2", "telephone_2", "text", false)}
                            {field("Град", "city")}
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
