import ReactDom from "react-dom";
import { X, Tag, CheckCheck, User, Database, UserX, TriangleAlert, Coins } from "lucide-react";
import { useState, useEffect } from "react";
import axios from "axios";
import { Autocomplete, TextField } from "@mui/material";
import Loading from "../../shared/components/Loading.tsx";
import { API_BASE } from "../../shared/api/config.ts";
import { resolveActiveSessionId } from "../../shared/utils/activeSession.ts";
import {
    ItemFormData, ITEM_FORM_DEFAULTS, Handle, inputClass,
    ItemTypeSelect, ItemFields, DescField, buildAttributes, GoldPriceStrip,
} from "../../shared/components/itemForm.tsx";

interface Props {
    closeModal: () => void;
    refresh: () => void;
}

interface CustomerOption {
    id: number;
    fullName: string;
    nationalId: string;
    phonePrimary: string;
    phoneSecondary: string | null;
    address: string;
    city: string;
}

interface FormData extends ItemFormData {
    fullName: string; nationalId: string; phonePrimary: string; phoneSecondary: string; address: string; city: string;
    existingCustomerId: number | null;
    purchasePrice: string;
}

const customerFields: [string, string, boolean][] = [
    ["Име и презиме", "fullName", true],
    ["ЕМБГ", "nationalId", true],
    ["Телефон 1", "phonePrimary", true],
    ["Телефон 2", "phoneSecondary", false],
    ["Адреса", "address", true],
    ["Град", "city", true],
];

export default function ModalAddNewSale({ closeModal, refresh }: Props) {
    const [customers, setCustomers] = useState<CustomerOption[]>([]);
    const [selected, setSelected] = useState<CustomerOption | null>(null);
    const [mode, setMode] = useState<"existing" | "new">("existing");
    const [openSessionId, setOpenSessionId] = useState<number | null>(null);
    const [goldPrices, setGoldPrices] = useState<Record<string, number> | null>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [formData, setFormData] = useState<FormData>({
        ...ITEM_FORM_DEFAULTS,
        fullName: "", nationalId: "", phonePrimary: "", phoneSecondary: "", address: "", city: "",
        existingCustomerId: null,
        purchasePrice: "",
    });

    useEffect(() => {
        axios.get(`${API_BASE}/customers`, { params: { size: 1_000_000, sort: "fullName,ASC" } })
            .then(res => setCustomers(res.data.content ?? []))
            .catch(err => console.error("Error fetching customers:", err));
        // A sale's purchase moves money, so it needs the active register's open session.
        resolveActiveSessionId()
            .then(setOpenSessionId)
            .catch(err => console.error("Error fetching cash sessions:", err));
    }, []);

    useEffect(() => {
        if (formData.type !== "GOLD" || goldPrices) return;
        const cached = sessionStorage.getItem("goldPrice");
        if (cached) { try { setGoldPrices(JSON.parse(cached)); return; } catch { /* refetch below */ } }
        axios.get(`${API_BASE}/gold/price`)
            .then(res => { setGoldPrices(res.data); sessionStorage.setItem("goldPrice", JSON.stringify(res.data)); })
            .catch(() => { /* non-critical reference */ });
    }, [formData.type, goldPrices]);

    const h: Handle = (name, value) => setFormData(prev => ({ ...prev, [name]: value }));

    const pickCustomer = (_e: any, value: CustomerOption | null) => {
        setSelected(value);
        h("existingCustomerId", value ? value.id : null);
    };
    const clearSelected = () => { setSelected(null); h("existingCustomerId", null); };
    const switchMode = (m: "existing" | "new") => { setMode(m); if (m === "new") clearSelected(); };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!openSessionId) { setError("Нема отворена каса — отворете каса пред да внесете продажба."); return; }
        if (mode === "existing" && !formData.existingCustomerId) { setError("Изберете постоечки клиент."); return; }
        setLoading(true);
        setError("");
        try {
            let customerId = formData.existingCustomerId;
            if (mode === "new") {
                const created = await axios.post(`${API_BASE}/customers`, {
                    fullName: formData.fullName,
                    nationalId: formData.nationalId,
                    phonePrimary: formData.phonePrimary,
                    phoneSecondary: formData.phoneSecondary || null,
                    address: formData.address,
                    city: formData.city,
                });
                customerId = created.data.id;
            }

            await axios.post(`${API_BASE}/sales`, {
                customerId,
                item: {
                    type: formData.type,
                    origin: "PURCHASE",
                    initialStatus: "IN_SALE",
                    description: formData.description,
                    attributes: buildAttributes(formData),
                },
                purchasePrice: Number(formData.purchasePrice),
                cashRegisterSessionId: openSessionId,
            });
            closeModal();
            refresh();
        } catch (err) {
            console.error("Error adding sale:", err);
            setError("Грешка при внесување на продажбата.");
        } finally {
            setLoading(false);
        }
    };

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" />
            <div className="flex flex-col gap-6 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-8 rounded-lg w-[min(900px,92%)] max-h-[92vh] overflow-y-auto scrollbar-hidden">
                <div className="flex justify-between">
                    <div className="flex items-center gap-2">
                        <Tag size={32} />
                        <h1 className="text-2xl font-semibold">Внеси Нова Продажба</h1>
                    </div>
                    <button className="close-x-btn disabled:opacity-40 disabled:cursor-not-allowed" onClick={closeModal} disabled={loading}><X size={32} /></button>
                </div>

                {!openSessionId && (
                    <div className="flex items-center gap-2 bg-red-500/10 text-red-600 rounded p-3 text-sm">
                        <TriangleAlert size={18} /> Нема отворена каса. Отворете каса за да можете да внесете продажба.
                    </div>
                )}

                <form onSubmit={handleSubmit} className="flex flex-col gap-6">
                    {/* Section: client */}
                    <div className="flex flex-col gap-3">
                        <div className="flex gap-3 items-center flex-wrap">
                            <span className="flex items-center gap-2 font-medium"><User size={20} /> Податоци за клиентот</span>
                            <div className="flex gap-1 bg-white rounded p-1 shadow-[0_0_4px_rgba(0,0,0,0.2)] w-fit">
                                {(["existing", "new"] as const).map(m => (
                                    <button key={m} type="button" onClick={() => switchMode(m)}
                                            className={`px-4 whitespace-nowrap py-1.5 rounded text-sm font-medium transition-colors ${mode === m ? "bg-green text-white" : "text-[#666] hover:bg-black/5"}`}>
                                        {m === "existing" ? "Постоечки" : "Нов клиент"}
                                    </button>
                                ))}
                            </div>
                        </div>

                        {mode === "existing" ? (
                            selected ? (
                                <div className="flex flex-col gap-2 max-w-sm">
                                    <div className="bg-white rounded shadow-[0_0_4px_rgba(0,0,0,0.2)] p-3 flex flex-col gap-1 text-sm">
                                        <p className="font-semibold text-base">{selected.fullName}</p>
                                        <p className="text-[#666]">{selected.nationalId}</p>
                                        <p className="text-[#666]">{selected.phonePrimary}</p>
                                        {selected.phoneSecondary && <p className="text-[#666]">{selected.phoneSecondary}</p>}
                                        <p className="text-[#666]">{selected.address}, {selected.city}</p>
                                    </div>
                                    <button type="button" onClick={clearSelected}
                                            className="flex items-center gap-2 px-3 py-1.5 rounded bg-black/10 text-sm w-fit hover:bg-black/15 transition-colors">
                                        <UserX size={15} /> Промени клиент
                                    </button>
                                </div>
                            ) : (
                                <Autocomplete
                                    className="max-w-sm"
                                    options={customers}
                                    value={selected}
                                    getOptionLabel={o => o.fullName}
                                    onChange={pickCustomer}
                                    isOptionEqualToValue={(o, v) => o.id === v.id}
                                    renderInput={params => <TextField {...params} label="Пребарај клиент (име, ЕМБГ, телефон)" />}
                                    filterOptions={(opts, state) => opts.filter(o =>
                                        o.fullName.toLowerCase().includes(state.inputValue.toLowerCase()) ||
                                        (o.nationalId && o.nationalId.includes(state.inputValue)) ||
                                        (o.phonePrimary && o.phonePrimary.includes(state.inputValue)) ||
                                        (o.phoneSecondary && o.phoneSecondary.includes(state.inputValue))
                                    )}
                                    renderOption={(props, o) => (
                                        <li {...props} key={o.id} style={{ display: "flex", flexDirection: "column", gap: ".1rem", alignItems: "flex-start" }}>
                                            <strong>{o.fullName}</strong><span>{o.nationalId}</span><span>{o.phonePrimary}</span>
                                        </li>
                                    )}
                                    sx={{ background: "white", borderRadius: ".3rem", boxShadow: "0 0 4px rgba(0,0,0,0.2)" }}
                                />
                            )
                        ) : (
                            <div className="grid grid-cols-2 md:grid-cols-3 gap-x-8 gap-y-3 max-w-2xl">
                                {customerFields.map(([label, name, required]) => (
                                    <div key={name} className="flex flex-col gap-1">
                                        <p className="text-[#666] text-sm">{label}{required ? " *" : ""}</p>
                                        <input className={inputClass} name={name} value={formData[name]}
                                               onChange={e => h(e.target.name, e.target.value)} required={required} />
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>

                    <hr className="border-black/15" />

                    {/* Section: purchase */}
                    <div className="flex flex-col gap-3">
                        <span className="flex items-center gap-2 font-medium"><Coins size={20} /> Податоци за откупот</span>
                        <div className="flex flex-col gap-1 max-w-[12rem]">
                            <p className="-ml-1 text-[#666]">Откупна цена</p>
                            <input className={inputClass} name="purchasePrice" type="number" value={formData.purchasePrice}
                                   onChange={e => h(e.target.name, e.target.value)} required />
                        </div>
                    </div>

                    <hr className="border-black/15" />

                    {/* Section: item */}
                    <div className="flex flex-col gap-3">
                        <div className="flex gap-3 items-center flex-wrap">
                            <span className="flex items-center gap-2 font-medium"><Database size={20} /> Податоци за предметот</span>
                            <ItemTypeSelect value={formData.type} onChange={v => h("type", v)} />
                        </div>
                        <GoldPriceStrip prices={formData.type === "GOLD" ? goldPrices : null} />
                        <div className="grid grid-cols-2 md:grid-cols-4 gap-x-8 gap-y-3">
                            <ItemFields f={formData} h={h} />
                            <DescField h={h} />
                        </div>
                    </div>

                    {error && <p className="text-red-500 text-sm">{error}</p>}

                    <div className="flex gap-4 items-center justify-center mt-2 min-h-[44px]">
                        {loading ? <Loading width={30} height={30} /> : (
                            <button type="submit" disabled={!openSessionId}
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
