import ReactDom from "react-dom";
import { X, UserPlus, CheckCheck } from "lucide-react";
import { useState } from "react";
import axios from "axios";
import Loading from "../../shared/components/Loading.tsx";
import { API_BASE } from "../../shared/api/config.ts";
import { StaffRow } from "./types.ts";

interface Props {
    managers: StaffRow[];
    closeModal: () => void;
    refresh: () => void;
}

const inputClass = "bg-white border-none rounded text-base p-2 w-full shadow-[0_0_4px_rgba(0,0,0,0.2)]";

const fields: [string, string, string, boolean][] = [
    // label, name, type, required
    ["Име и презиме", "fullName", "text", true],
    ["Корисничко име", "username", "text", true],
    ["Лозинка (мин. 8)", "password", "password", true],
    ["ЕМБГ", "nationalId", "text", true],
    ["Телефон", "phonePrimary", "text", true],
    ["Телефон 2", "phoneSecondary", "text", false],
    ["Основна плата", "baseSalary", "number", true],
    ["Бонус (%)", "bonusPercent", "number", true],
];

export default function ModalAddNewStaff({ managers, closeModal, refresh }: Props) {
    const [form, setForm] = useState<Record<string, string>>({
        fullName: "", username: "", password: "", nationalId: "",
        phonePrimary: "", phoneSecondary: "", baseSalary: "", bonusPercent: "0",
    });
    const [managerId, setManagerId] = useState<string>("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const h = (name: string, value: string) => setForm(prev => ({ ...prev, [name]: value }));

    const submit = (e: React.FormEvent) => {
        e.preventDefault();
        if (form.password.length < 8) { setError("Лозинката мора да има барем 8 карактери."); return; }
        setLoading(true);
        setError("");
        axios.post(`${API_BASE}/admin/staff`, {
            fullName: form.fullName,
            username: form.username,
            password: form.password,
            nationalId: form.nationalId,
            phonePrimary: form.phonePrimary,
            phoneSecondary: form.phoneSecondary || null,
            baseSalary: Number(form.baseSalary),
            bonusPercent: Number(form.bonusPercent),
            managerId: managerId ? Number(managerId) : null,
        })
            .then(() => { refresh(); closeModal(); })
            .catch(err => { setError(err?.response?.data?.message || "Грешка при внесување на вработениот."); setLoading(false); });
    };

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" onClick={loading ? undefined : closeModal} />
            <div className="flex flex-col gap-6 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-8 rounded-lg w-[min(760px,92%)] max-h-[92vh] overflow-y-auto scrollbar-hidden">
                <div className="flex justify-between">
                    <div className="flex items-center gap-2">
                        <UserPlus size={28} />
                        <h1 className="text-2xl font-semibold">Внеси нов вработен</h1>
                    </div>
                    <button className="close-x-btn disabled:opacity-40 disabled:cursor-not-allowed" onClick={closeModal} disabled={loading}><X size={32} /></button>
                </div>

                <form onSubmit={submit} className="flex flex-col gap-6">
                    <div className="grid grid-cols-2 md:grid-cols-3 gap-x-8 gap-y-4">
                        {fields.map(([label, name, type, required]) => (
                            <div key={name} className="flex flex-col gap-1">
                                <p className="text-[#666] text-sm">{label}{required ? " *" : ""}</p>
                                <input className={inputClass} type={type} value={form[name]} required={required}
                                       onChange={e => h(name, e.target.value)} />
                            </div>
                        ))}
                        <div className="flex flex-col gap-1">
                            <p className="text-[#666] text-sm">Менаџер</p>
                            <select className={inputClass} value={managerId} onChange={e => setManagerId(e.target.value)}>
                                <option value="">— Без менаџер —</option>
                                {managers.map(m => <option key={m.id} value={m.id}>{m.fullName}</option>)}
                            </select>
                        </div>
                    </div>

                    {error && <p className="text-red-500 text-sm">{error}</p>}

                    <div className="flex gap-4 items-center justify-center mt-2 min-h-[44px]">
                        {loading ? <Loading width={30} height={30} /> : (
                            <button type="submit"
                                    className="group relative overflow-hidden flex justify-center items-center gap-2 px-16 py-2.5 rounded bg-green text-white text-base font-semibold shadow-[4px_2px_6px_rgba(0,0,0,0.2)] transition-all duration-300 hover:scale-105 active:scale-[0.98] disabled:cursor-not-allowed disabled:opacity-40 disabled:hover:scale-100">
                                <span className="pointer-events-none absolute inset-0 -translate-x-full bg-gradient-to-r from-transparent via-white/25 to-transparent transition-transform duration-700 ease-out group-hover:translate-x-[180%]" />
                                <CheckCheck size={22} /> Потврди
                            </button>
                        )}
                    </div>
                </form>
            </div>
        </>,
        document.getElementById("portal")!
    );
}
