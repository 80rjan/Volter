import ReactDom from "react-dom";
import { useState } from "react";
import axios from "axios";
import { X, UserRound, Phone, Pencil, CheckCheck } from "lucide-react";
import Loading from "../../shared/components/Loading.tsx";
import { API_BASE } from "../../shared/api/config.ts";
import { Customer } from "./types.ts";
import { useAuth } from "../../GlobalContext.tsx";

interface Props {
    client: Customer;
    closeModal: (e?: React.MouseEvent) => void;
    onUpdated: (updated: Customer) => void;
}

const date = (s: string | null | undefined) => (s ? String(s).substring(0, 10) : "—");
const inputCls = "bg-white border-none rounded text-base p-2 shadow-[0_0_4px_rgba(0,0,0,0.2)] w-full";

function Field({ label, value }: { label: string; value: React.ReactNode }) {
    return (
        <div className="flex flex-col gap-0.5">
            <span className="text-[#666] text-xs">{label}</span>
            <span className="text-sm font-medium break-words">{value || "—"}</span>
        </div>
    );
}

function Section({ icon, title, children }: { icon: React.ReactNode; title: string; children: React.ReactNode }) {
    return (
        <div className="flex flex-col gap-3">
            <span className="flex items-center gap-2 font-medium">{icon}{title}</span>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-x-8 gap-y-3">{children}</div>
        </div>
    );
}

function EditField({ label, value, onChange, type = "text" }:
    { label: string; value: string; onChange: (v: string) => void; type?: string }) {
    return (
        <div className="flex flex-col gap-1">
            <span className="text-[#666] text-xs">{label}</span>
            <input className={inputCls} type={type} value={value} onChange={e => onChange(e.target.value)} />
        </div>
    );
}

const divider = <hr className="border-black/15" />;

export default function ModalReadMoreClient({ client, closeModal, onUpdated }: Props) {
    const { can } = useAuth();
    const canWrite = can("CUSTOMER_WRITE");

    const [editing, setEditing] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [form, setForm] = useState({
        fullName: client.fullName,
        phonePrimary: client.phonePrimary,
        phoneSecondary: client.phoneSecondary ?? "",
        address: client.address,
        city: client.city,
    });

    const set = (k: keyof typeof form) => (v: string) => setForm(prev => ({ ...prev, [k]: v }));

    const save = () => {
        if (!form.fullName.trim() || !form.phonePrimary.trim() || !form.address.trim() || !form.city.trim()) {
            setError("Пополни ги задолжителните полиња.");
            return;
        }
        setLoading(true);
        setError("");
        axios.patch(`${API_BASE}/customers/${client.id}`, form)
            .then(res => { onUpdated(res.data); setEditing(false); })
            .catch(() => setError("Зачувувањето не успеа, обидете се повторно."))
            .finally(() => setLoading(false));
    };

    const cancelEdit = () => {
        setForm({
            fullName: client.fullName,
            phonePrimary: client.phonePrimary,
            phoneSecondary: client.phoneSecondary ?? "",
            address: client.address,
            city: client.city,
        });
        setError("");
        setEditing(false);
    };

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" onClick={closeModal} />
            <div className="flex flex-col gap-6 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-8 rounded-lg w-[min(820px,92%)] max-h-[90vh] overflow-y-auto scrollbar-hidden">
                <div className="flex justify-between items-center">
                    <div className="flex items-center gap-3">
                        <span className="flex h-10 w-10 items-center justify-center rounded-full bg-green/15 text-green">
                            <UserRound size={20} />
                        </span>
                        <h1 className="text-2xl font-semibold">{client.fullName}</h1>
                    </div>
                    <div className="flex items-center gap-3">
                        {canWrite && !editing && (
                            <button onClick={() => setEditing(true)}
                                className="flex items-center gap-2 px-4 py-2 rounded text-sm font-medium border border-green/60 text-green hover:bg-green hover:text-white transition-all">
                                <Pencil size={16} /> Измени
                            </button>
                        )}
                        <button className="close-x-btn" onClick={closeModal}><X size={28} /></button>
                    </div>
                </div>

                {!editing ? (
                    <>
                        <Section icon={<UserRound size={20} />} title="Лични податоци">
                            <Field label="Име и презиме" value={client.fullName} />
                            <Field label="ЕМБГ" value={client.nationalId} />
                            <Field label="Клиент од" value={date(client.createdAt)} />
                            <Field label="Изменет на" value={date(client.updatedAt)} />
                        </Section>

                        {divider}

                        <Section icon={<Phone size={20} />} title="Контакт">
                            <Field label="Телефон" value={client.phonePrimary} />
                            <Field label="Телефон 2" value={client.phoneSecondary} />
                            <Field label="Адреса" value={client.address} />
                            <Field label="Град" value={client.city} />
                        </Section>
                    </>
                ) : (
                    <div className="flex flex-col gap-4">
                        <div className="grid grid-cols-2 md:grid-cols-3 gap-x-8 gap-y-4">
                            <EditField label="Име и презиме" value={form.fullName} onChange={set("fullName")} />
                            {/* ЕМБГ е непроменлив идентификатор. */}
                            <div className="flex flex-col gap-1">
                                <span className="text-[#666] text-xs">ЕМБГ (непроменливо)</span>
                                <input className={`${inputCls} opacity-60 cursor-not-allowed`} value={client.nationalId} disabled />
                            </div>
                            <EditField label="Град" value={form.city} onChange={set("city")} />
                            <EditField label="Телефон" value={form.phonePrimary} onChange={set("phonePrimary")} />
                            <EditField label="Телефон 2" value={form.phoneSecondary} onChange={set("phoneSecondary")} />
                            <EditField label="Адреса" value={form.address} onChange={set("address")} />
                        </div>

                        {error && <span className="text-red-500 text-sm">{error}</span>}

                        <div className="flex gap-3 items-center justify-end min-h-[40px]">
                            <button type="button" onClick={cancelEdit} disabled={loading}
                                className="px-5 py-2 rounded bg-black/10 text-sm hover:bg-black/15 transition-colors disabled:opacity-40 disabled:cursor-not-allowed">
                                Откажи
                            </button>
                            {loading ? <Loading width={28} height={28} /> : (
                                <button type="button" onClick={save}
                                    className="group relative overflow-hidden flex items-center justify-center gap-2 px-6 py-2 rounded text-white font-medium bg-green shadow-[4px_2px_6px_rgba(0,0,0,0.2)] transition-all duration-300 hover:scale-105 active:scale-[0.98] disabled:cursor-not-allowed disabled:opacity-40">
                                    <span className="pointer-events-none absolute inset-0 -translate-x-full bg-gradient-to-r from-transparent via-white/25 to-transparent transition-transform duration-700 ease-out group-hover:translate-x-[180%]" />
                                    <CheckCheck size={20} /> Зачувај
                                </button>
                            )}
                        </div>
                    </div>
                )}
            </div>
        </>,
        document.getElementById("portal")!
    );
}
