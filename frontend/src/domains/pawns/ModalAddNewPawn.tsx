import ReactDom from "react-dom";
import { X, CopyPlus, CheckCheck, User, Database } from "lucide-react";
import { useEffect, useRef, useState } from "react";
import axios from "axios";
import { Autocomplete, TextField } from "@mui/material";
import Loading from "../../shared/components/Loading.tsx";

interface Props {
    closeModal: () => void;
    refresh: () => void;
}

interface FormData {
    name: string;
    embg: string;
    telephone: string;
    telephone_2: string;
    city: string;
    brand: string;
    model: string;
    year: string;
    price_pawned: string;
    provision: number;
    provisionPercent: number;
    total_days: string;
    description: string;
    weight: string;
    carat: string;
    type: string;
    date: string;
    category: string;
    [key: string]: any;
}

interface ClientOption {
    id: number;
    name: string;
    embg: string;
    telephone: string;
    telephone_2: string;
    city: string;
}

const inputClass = "border-none rounded text-base p-2 shadow-[0_0_4px_rgba(0,0,0,0.2)] h-fit";
const selectClass = "border-none! shadow-[0_0_4px_rgba(0,0,0,0.2)] rounded p-1";

function ProvisionFields({ formData, handleInputChange }: { formData: FormData; handleInputChange: (name: string, value: any) => void }) {
    return (
        <div className="flex flex-col gap-1">
            <p className="-ml-1 text-[#666]">Провизија</p>
            <div className="flex gap-2 items-end w-full">
                <span className="flex items-center gap-1 text-[#666] m-0">
                    <input
                        className={inputClass + " w-1/2"}
                        name="provision"
                        value={formData.provision || ""}
                        onChange={e => {
                            handleInputChange(e.target.name, e.target.value);
                            handleInputChange("provisionPercent", Math.round((Number(e.target.value) / Number(formData.price_pawned)) * 100 * 100) / 100);
                        }}
                        type="number"
                        required
                    />
                    ден
                </span>
                <span className="flex items-center gap-1 text-[#666] m-0">
                    <input
                        className={inputClass + " w-1/2"}
                        name="provisionPercent"
                        value={formData.provisionPercent || ""}
                        onChange={e => {
                            handleInputChange(e.target.name, e.target.value);
                            handleInputChange("provision", Math.round((Number(e.target.value) / 100) * Number(formData.price_pawned)));
                        }}
                        type="number"
                        step="0.001"
                        required
                    />
                    %
                </span>
            </div>
        </div>
    );
}

function DaysAndDesc({ handleInputChange, date }: { handleInputChange: (name: string, value: any) => void; date: string }) {
    return (
        <>
            <div className="flex flex-col gap-1">
                <p className="-ml-1 text-[#666]">Валидност во денови</p>
                <select className={selectClass} name="total_days" onChange={e => handleInputChange(e.target.name, e.target.value)} required>
                    <option value=""></option>
                    <option value="15">15</option>
                    <option value="30">30</option>
                </select>
            </div>
            <div className="flex flex-col gap-1">
                <p className="-ml-1 text-[#666]">Опис</p>
                <input className={inputClass} name="description" onChange={e => handleInputChange(e.target.name, e.target.value)} required />
            </div>
            <div className="flex flex-col gap-1">
                <p className="-ml-1 text-[#666]">Заложено на</p>
                <input className={inputClass} type="date" value={date} name="date" onChange={e => handleInputChange(e.target.name, e.target.value)} required />
            </div>
        </>
    );
}

function PriceField({ formData, handleInputChange }: { formData: FormData; handleInputChange: (name: string, value: any) => void }) {
    return (
        <div className="flex flex-col gap-1">
            <p className="-ml-1 text-[#666]">Вредност на залогот</p>
            <input
                className={inputClass}
                name="price_pawned"
                onChange={e => {
                    handleInputChange(e.target.name, e.target.value);
                    handleInputChange("provisionPercent", Math.round((formData.provision / Number(e.target.value)) * 100 * 100) / 100);
                }}
                type="number"
                required
            />
        </div>
    );
}

function ElectronicsInputs({ handleInputChange, formData, date }: { handleInputChange: (n: string, v: any) => void; formData: FormData; date: string }) {
    return (
        <>
            <div className="flex flex-col gap-1"><p className="-ml-1 text-[#666]">Бренд</p><input className={inputClass} name="brand" onChange={e => handleInputChange(e.target.name, e.target.value)} required /></div>
            <div className="flex flex-col gap-1"><p className="-ml-1 text-[#666]">Година</p><input className={inputClass} name="year" onChange={e => handleInputChange(e.target.name, e.target.value)} type="number" required /></div>
            <PriceField formData={formData} handleInputChange={handleInputChange} />
            <ProvisionFields formData={formData} handleInputChange={handleInputChange} />
            <DaysAndDesc handleInputChange={handleInputChange} date={date} />
        </>
    );
}

function GoldInputs({ handleInputChange, formData, date }: { handleInputChange: (n: string, v: any) => void; formData: FormData; date: string }) {
    return (
        <>
            <div className="flex flex-col gap-1"><p className="-ml-1 text-[#666]">Тежина (во грамови)</p><input className={inputClass} name="weight" onChange={e => handleInputChange(e.target.name, e.target.value)} type="number" step="0.001" required /></div>
            <div className="flex flex-col gap-1"><p className="-ml-1 text-[#666]">Каратажа (број)</p><input className={inputClass} name="carats" onChange={e => handleInputChange(e.target.name, e.target.value)} type="number" required /></div>
            <div className="flex flex-col gap-1"><p className="-ml-1 text-[#666]">Тип на злато</p><input className={inputClass} name="type" onChange={e => handleInputChange(e.target.name, e.target.value)} required /></div>
            <PriceField formData={formData} handleInputChange={handleInputChange} />
            <ProvisionFields formData={formData} handleInputChange={handleInputChange} />
            <DaysAndDesc handleInputChange={handleInputChange} date={date} />
        </>
    );
}

function VehicleInputs({ handleInputChange, formData, date }: { handleInputChange: (n: string, v: any) => void; formData: FormData; date: string }) {
    return (
        <>
            <div className="flex flex-col gap-1"><p className="-ml-1 text-[#666]">Бренд</p><input className={inputClass} name="brand" onChange={e => handleInputChange(e.target.name, e.target.value)} required /></div>
            <div className="flex flex-col gap-1"><p className="-ml-1 text-[#666]">Модел</p><input className={inputClass} name="model" onChange={e => handleInputChange(e.target.name, e.target.value)} required /></div>
            <div className="flex flex-col gap-1"><p className="-ml-1 text-[#666]">Година</p><input className={inputClass} name="year" onChange={e => handleInputChange(e.target.name, e.target.value)} type="number" required /></div>
            <PriceField formData={formData} handleInputChange={handleInputChange} />
            <ProvisionFields formData={formData} handleInputChange={handleInputChange} />
            <DaysAndDesc handleInputChange={handleInputChange} date={date} />
        </>
    );
}

function WatchInputs({ handleInputChange, formData, date }: { handleInputChange: (n: string, v: any) => void; formData: FormData; date: string }) {
    return (
        <>
            <div className="flex flex-col gap-1"><p className="-ml-1 text-[#666]">Бренд</p><input className={inputClass} name="brand" onChange={e => handleInputChange(e.target.name, e.target.value)} required /></div>
            <div className="flex flex-col gap-1"><p className="-ml-1 text-[#666]">Година</p><input className={inputClass} name="year" onChange={e => handleInputChange(e.target.name, e.target.value)} type="number" required /></div>
            <PriceField formData={formData} handleInputChange={handleInputChange} />
            <ProvisionFields formData={formData} handleInputChange={handleInputChange} />
            <DaysAndDesc handleInputChange={handleInputChange} date={date} />
        </>
    );
}

function OtherInputs({ handleInputChange, formData, date }: { handleInputChange: (n: string, v: any) => void; formData: FormData; date: string }) {
    return (
        <>
            <PriceField formData={formData} handleInputChange={handleInputChange} />
            <ProvisionFields formData={formData} handleInputChange={handleInputChange} />
            <DaysAndDesc handleInputChange={handleInputChange} date={date} />
        </>
    );
}

export default function ModalAddNewPawn({ closeModal, refresh }: Props) {
    const [clients, setClients] = useState<ClientOption[]>([]);
    const [formData, setFormData] = useState<FormData>({
        name: "", embg: "", telephone: "", telephone_2: "", city: "",
        brand: "", model: "", year: "", price_pawned: "",
        provision: 0, provisionPercent: 0, total_days: "", description: "",
        weight: "", carat: "", type: "",
        date: new Date().toISOString().split("T")[0],
        category: "electronics_pawn",
    });
    const offset = useRef(0);
    const limit = 5;
    const [isLastPage, setIsLastPage] = useState(false);
    const scrollableClientsRef = useRef(null);
    const [autocompleteValue, setAutocompleteValue] = useState("");
    const [loading, setLoading] = useState(false);

    const handleInputChange = (name: string, value: any) => {
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleObjectSelect = (_event: any, value: ClientOption | null) => {
        if (value) {
            setFormData(prev => ({
                ...prev,
                name: value.name, embg: value.embg,
                telephone: value.telephone, telephone_2: value.telephone_2,
                city: value.city,
            }));
        }
    };

    const fetchClients = (limit: number, offset: number, search: string) => {
        axios.get(`http://localhost:3000/clientsAutocomplete?limit=${limit}&offset=${offset}&search=${search}`)
            .then(res => {
                if (res.data.length > 0) {
                    offset === 0 ? setClients(res.data) : setClients(prev => [...prev, ...res.data]);
                    setIsLastPage(res.data.length < limit);
                }
            })
            .catch(error => console.error("Error fetching clients:", error));
    };

    const handleScroll = (event: React.UIEvent<HTMLUListElement>) => {
        const el = event.target as HTMLElement;
        if (el.scrollHeight - el.scrollTop - el.clientHeight <= el.scrollHeight * 0.2 && !isLastPage) {
            offset.current += limit;
            fetchClients(limit, offset.current, autocompleteValue);
        }
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        axios.post(`http://localhost:3000/insertPawn`, formData)
            .then(() => { closeModal(); refresh(); })
            .catch(error => console.error("Error adding pawn:", error))
            .finally(() => setLoading(false));
    };

    useEffect(() => {
        fetchClients(limit, offset.current, autocompleteValue);
    }, []);

    const renderCategoryInputs = () => {
        const props = { handleInputChange, formData, date: formData.date };
        switch (formData.category) {
            case "electronics_pawn": return <ElectronicsInputs {...props} />;
            case "gold_pawn": return <GoldInputs {...props} />;
            case "vehicle_pawn": return <VehicleInputs {...props} />;
            case "watch_pawn": return <WatchInputs {...props} />;
            case "other_pawn": return <OtherInputs {...props} />;
        }
    };

    const clientFields: [string, string, boolean][] = [
        ["Име", "name", true],
        ["Ембг", "embg", true],
        ["Телефон 1", "telephone", true],
        ["Телефон 2", "telephone_2", false],
        ["Град", "city", true],
    ];

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" />
            <div className="flex flex-col gap-8 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-8 rounded-lg min-w-fit max-w-[90%]">
                <div className="flex justify-between">
                    <div className="flex items-center gap-2">
                        <CopyPlus size={32} />
                        <h1 className="text-2xl font-semibold">Внеси Нов Залог</h1>
                    </div>
                    <button className="close-x-btn" onClick={closeModal}><X size={32} /></button>
                </div>

                <form onSubmit={handleSubmit} className="flex flex-col items-center gap-8">
                    <div className="flex gap-8 items-start">
                        {/* Client inputs */}
                        <div className="flex flex-col gap-2">
                            <span className="flex items-center gap-2 font-medium mb-2 w-max">
                                <User size={20} /> Внеси Податоци за Клиентот
                            </span>
                            <Autocomplete
                                ref={scrollableClientsRef}
                                options={clients}
                                getOptionLabel={option => option.name}
                                onChange={handleObjectSelect}
                                onInputChange={(_e, value) => {
                                    offset.current = 0;
                                    setAutocompleteValue(value);
                                    fetchClients(limit, 0, value);
                                }}
                                ListboxProps={{ onScroll: handleScroll as any }}
                                renderInput={params => <TextField {...params} label="Постоечки клиенти" />}
                                isOptionEqualToValue={(option, value) => option.id === value.id}
                                renderOption={(props, option) => (
                                    <li {...props} key={option.id} style={{ display: "flex", flexDirection: "column", gap: ".1rem", alignItems: "flex-start" }}>
                                        <strong>{option.name}</strong>
                                        <span>{option.embg}</span>
                                        <span>{option.telephone}</span>
                                        {option.telephone_2 && <span>{option.telephone_2}</span>}
                                    </li>
                                )}
                                filterOptions={(options, state) => options.filter(o =>
                                    o.name.toLowerCase().includes(state.inputValue.toLowerCase()) ||
                                    (o.embg && o.embg.includes(state.inputValue)) ||
                                    (o.telephone && o.telephone.includes(state.inputValue)) ||
                                    (o.telephone_2 && o.telephone_2.includes(state.inputValue))
                                )}
                                sx={{ background: "white", borderRadius: ".3rem", fontSize: "1rem", boxShadow: "0 0 4px rgba(0,0,0,0.2)" }}
                            />
                            {clientFields.map(([label, name, required]) => (
                                <div key={name} className="flex items-center gap-4 justify-between">
                                    <p className="text-[#666] whitespace-nowrap">{label}</p>
                                    <input
                                        className={inputClass}
                                        name={name}
                                        value={formData[name]}
                                        onChange={e => handleInputChange(e.target.name, e.target.value)}
                                        required={required}
                                    />
                                </div>
                            ))}
                        </div>

                        {/* Pawn inputs */}
                        <div className="grid grid-cols-2 gap-2 gap-x-8">
                            <span className="col-span-2 flex items-center gap-2 font-medium">
                                <Database size={20} /> Внеси Податоци за Предметот
                            </span>
                            <select
                                className="col-span-2 h-full px-2 py-1 rounded border-2 border-black/60 cursor-pointer text-base"
                                name="category"
                                value={formData.category}
                                onChange={e => handleInputChange(e.target.name, e.target.value)}
                            >
                                <option value="electronics_pawn">Електроника</option>
                                <option value="gold_pawn">Злато</option>
                                <option value="vehicle_pawn">Возила</option>
                                <option value="watch_pawn">Часовници</option>
                                <option value="other_pawn">Останато</option>
                            </select>
                            {renderCategoryInputs()}
                        </div>
                    </div>

                    <div className="flex gap-4 items-center">
                        <button
                            type="submit"
                            disabled={loading}
                            className="flex justify-center items-center gap-2 px-32 py-3 rounded bg-green text-white text-2xl shadow-[0_0_8px_rgba(0,0,0,0.2)] transition-all duration-300 hover:scale-105 disabled:cursor-not-allowed disabled:opacity-40"
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
