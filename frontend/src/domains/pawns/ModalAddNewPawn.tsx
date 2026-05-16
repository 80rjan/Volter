import ReactDom from "react-dom";
import { X, CopyPlus, CheckCheck, User, Database, UserX } from "lucide-react";
import { useRef, useState, useEffect } from "react";
import axios from "axios";
import { Autocomplete, TextField } from "@mui/material";
import Loading from "../../shared/components/Loading.tsx";
import { API_BASE } from "../../shared/api/config.ts";

interface Props {
    closeModal: () => void;
    refresh: () => void;
}

interface FormData {
    name: string;
    embg: string;
    telephone: string;
    telephone_2: string;
    address: string;
    city: string;
    existingCustomerId: number | null;
    brand: string;
    model: string;
    year: string;
    price_pawned: string;
    provision: number;
    provisionPercent: number;
    total_days: string;
    description: string;
    weight: string;
    carats: string;
    type: string;
    pricePerGram: string;
    date: string;
    category: string;
    electronicCategory: string;
    vehicleType: string;
    registrationNumber: string;
    mileage: string;
    numberOfKeys: string;
    serviceHistoryAvailable: boolean;
    registrationExpiryDate: string;
    material: string;
    originalBoxIncluded: boolean;
    originalPapersIncluded: boolean;
    warrantyCardIncluded: boolean;
    functional: boolean;
    serviceRequired: boolean;
    otherCategory: string;
    [key: string]: any;
}

interface ClientOption {
    id: number;
    name: string;
    embg: string;
    phoneNumber: string;
    reservePhoneNumber: string | null;
    address: string;
    city: string;
}

const inputClass = "bg-white border-none rounded text-base p-2 shadow-[0_0_4px_rgba(0,0,0,0.2)] h-fit";
const selectClass = "bg-white border-none! shadow-[0_0_4px_rgba(0,0,0,0.2)] rounded p-1";
const checkRow = (label: string, name: string, value: boolean, onChange: (n: string, v: any) => void) => (
    <div key={name} className="flex flex-col gap-1">
        <p className="-ml-1 text-[#666]">{label}</p>
        <div className="flex items-center h-full">
            <input type="checkbox" checked={value} onChange={e => onChange(name, e.target.checked)} className="w-5 h-5 cursor-pointer" />
        </div>
    </div>
);

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
            <div className="flex flex-col gap-1"><p className="-ml-1 text-[#666]">Категорија</p><input className={inputClass} name="electronicCategory" onChange={e => handleInputChange(e.target.name, e.target.value)} required /></div>
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
            <div className="flex flex-col gap-1">
                <p className="-ml-1 text-[#666]">Каратажа</p>
                <select className={selectClass} name="carats" onChange={e => handleInputChange(e.target.name, e.target.value)} required>
                    <option value=""></option>
                    <option value="CARAT_14">14k</option>
                    <option value="CARAT_18">18k</option>
                    <option value="CARAT_21">21k</option>
                    <option value="CARAT_22">22k</option>
                    <option value="CARAT_24">24k</option>
                </select>
            </div>
            <div className="flex flex-col gap-1"><p className="-ml-1 text-[#666]">Тип на злато</p><input className={inputClass} name="type" onChange={e => handleInputChange(e.target.name, e.target.value)} required /></div>
            <div className="flex flex-col gap-1"><p className="-ml-1 text-[#666]">Цена по грам</p><input className={inputClass} name="pricePerGram" onChange={e => handleInputChange(e.target.name, e.target.value)} type="number" step="0.01" required /></div>
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
            <div className="flex flex-col gap-1"><p className="-ml-1 text-[#666]">Тип на возило</p><input className={inputClass} name="vehicleType" onChange={e => handleInputChange(e.target.name, e.target.value)} required /></div>
            <div className="flex flex-col gap-1"><p className="-ml-1 text-[#666]">Рег. број</p><input className={inputClass} name="registrationNumber" onChange={e => handleInputChange(e.target.name, e.target.value)} required /></div>
            <div className="flex flex-col gap-1"><p className="-ml-1 text-[#666]">Километража</p><input className={inputClass} name="mileage" onChange={e => handleInputChange(e.target.name, e.target.value)} type="number" required /></div>
            <div className="flex flex-col gap-1"><p className="-ml-1 text-[#666]">Број на клучеви</p><input className={inputClass} name="numberOfKeys" onChange={e => handleInputChange(e.target.name, e.target.value)} type="number" required /></div>
            <div className="flex flex-col gap-1"><p className="-ml-1 text-[#666]">Рег. важи до</p><input className={inputClass} type="date" name="registrationExpiryDate" onChange={e => handleInputChange(e.target.name, e.target.value)} required /></div>
            {checkRow("Сервисна историја", "serviceHistoryAvailable", formData.serviceHistoryAvailable, handleInputChange)}
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
            <div className="flex flex-col gap-1"><p className="-ml-1 text-[#666]">Модел</p><input className={inputClass} name="model" onChange={e => handleInputChange(e.target.name, e.target.value)} required /></div>
            <div className="flex flex-col gap-1"><p className="-ml-1 text-[#666]">Материјал</p><input className={inputClass} name="material" onChange={e => handleInputChange(e.target.name, e.target.value)} required /></div>
            <div className="flex flex-col gap-1"><p className="-ml-1 text-[#666]">Година</p><input className={inputClass} name="year" onChange={e => handleInputChange(e.target.name, e.target.value)} type="number" required /></div>
            {checkRow("Оригинална кутија", "originalBoxIncluded", formData.originalBoxIncluded, handleInputChange)}
            {checkRow("Оригинални документи", "originalPapersIncluded", formData.originalPapersIncluded, handleInputChange)}
            {checkRow("Гарантна картичка", "warrantyCardIncluded", formData.warrantyCardIncluded, handleInputChange)}
            {checkRow("Функционален", "functional", formData.functional, handleInputChange)}
            {checkRow("Потребен сервис", "serviceRequired", formData.serviceRequired, handleInputChange)}
            <PriceField formData={formData} handleInputChange={handleInputChange} />
            <ProvisionFields formData={formData} handleInputChange={handleInputChange} />
            <DaysAndDesc handleInputChange={handleInputChange} date={date} />
        </>
    );
}

function OtherInputs({ handleInputChange, formData, date }: { handleInputChange: (n: string, v: any) => void; formData: FormData; date: string }) {
    return (
        <>
            <div className="flex flex-col gap-1"><p className="-ml-1 text-[#666]">Категорија</p><input className={inputClass} name="otherCategory" onChange={e => handleInputChange(e.target.name, e.target.value)} required /></div>
            <PriceField formData={formData} handleInputChange={handleInputChange} />
            <ProvisionFields formData={formData} handleInputChange={handleInputChange} />
            <DaysAndDesc handleInputChange={handleInputChange} date={date} />
        </>
    );
}

export default function ModalAddNewPawn({ closeModal, refresh }: Props) {
    const [clients, setClients] = useState<ClientOption[]>([]);
    const [selectedClient, setSelectedClient] = useState<ClientOption | null>(null);
    const [formData, setFormData] = useState<FormData>({
        name: "", embg: "", telephone: "", telephone_2: "", address: "", city: "",
        existingCustomerId: null,
        brand: "", model: "", year: "", price_pawned: "",
        provision: 0, provisionPercent: 0, total_days: "", description: "",
        weight: "", carats: "", type: "", pricePerGram: "",
        date: new Date().toISOString().split("T")[0],
        category: "ELECTRONIC",
        electronicCategory: "",
        vehicleType: "", registrationNumber: "", mileage: "", numberOfKeys: "",
        serviceHistoryAvailable: false, registrationExpiryDate: "",
        material: "", originalBoxIncluded: false, originalPapersIncluded: false,
        warrantyCardIncluded: false, functional: true, serviceRequired: false,
        otherCategory: "",
    });
    const scrollableClientsRef = useRef(null);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        axios.get(`${API_BASE}/customers`, { params: { size: 1_000_000, sort: "name,ASC" } })
            .then(res => setClients(res.data.content))
            .catch(err => console.error("Error fetching clients:", err));
    }, []);

    const handleInputChange = (name: string, value: any) => {
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleObjectSelect = (_event: any, value: ClientOption | null) => {
        if (value) {
            setSelectedClient(value);
            setFormData(prev => ({
                ...prev,
                existingCustomerId: value.id,
                name: value.name,
                embg: value.embg,
                telephone: value.phoneNumber,
                telephone_2: value.reservePhoneNumber ?? "",
                address: value.address,
                city: value.city,
            }));
        }
    };

    const deselectClient = () => {
        setSelectedClient(null);
        setFormData(prev => ({
            ...prev,
            existingCustomerId: null,
            name: "", embg: "", telephone: "", telephone_2: "", address: "", city: "",
        }));
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);

        const issueDate = formData.date;
        const durationDays = Number(formData.total_days);
        const maturityDate = (() => {
            const d = new Date(issueDate);
            d.setDate(d.getDate() + durationDays);
            return d.toISOString().split("T")[0];
        })();

        const customer = formData.existingCustomerId
            ? { referenceStrategy: "EXISTING", customerId: formData.existingCustomerId }
            : {
                referenceStrategy: "NEW",
                name: formData.name,
                embg: formData.embg,
                phoneNumber: formData.telephone,
                reservePhoneNumber: formData.telephone_2 || null,
                address: formData.address,
                city: formData.city,
            };

        let itemData: any = {
            itemType: formData.category,
            itemOriginType: "PAWN",
            itemStatus: "IN_PAWN",
            description: formData.description,
        };

        switch (formData.category) {
            case "ELECTRONIC":
                itemData = { ...itemData, brand: formData.brand, category: formData.electronicCategory, year: Number(formData.year) };
                break;
            case "GOLD":
                itemData = {
                    ...itemData,
                    weightGrams: Number(formData.weight),
                    carats: formData.carats,
                    pieceType: formData.type,
                    pricePerGram: Number(formData.pricePerGram),
                };
                break;
            case "VEHICLE":
                itemData = {
                    ...itemData,
                    brand: formData.brand,
                    model: formData.model,
                    year: Number(formData.year),
                    vehicleType: formData.vehicleType,
                    registrationNumber: formData.registrationNumber,
                    mileage: Number(formData.mileage),
                    numberOfKeys: Number(formData.numberOfKeys),
                    serviceHistoryAvailable: formData.serviceHistoryAvailable,
                    registrationExpiryDate: formData.registrationExpiryDate,
                };
                break;
            case "WATCH":
                itemData = {
                    ...itemData,
                    brand: formData.brand,
                    model: formData.model,
                    year: Number(formData.year),
                    material: formData.material,
                    originalBoxIncluded: formData.originalBoxIncluded,
                    originalPapersIncluded: formData.originalPapersIncluded,
                    warrantyCardIncluded: formData.warrantyCardIncluded,
                    functional: formData.functional,
                    serviceRequired: formData.serviceRequired,
                };
                break;
            case "OTHER":
                itemData = { ...itemData, category: formData.otherCategory };
                break;
        }

        axios.post(`${API_BASE}/pawns`, {
            amount: Number(formData.price_pawned),
            interest: formData.provision,
            durationDays,
            maturityDate,
            issueDate,
            customer,
            item: { referenceStrategy: "NEW", data: itemData },
        })
            .then(() => { closeModal(); refresh(); })
            .catch(error => console.error("Error adding pawn:", error))
            .finally(() => setLoading(false));
    };

    const renderCategoryInputs = () => {
        const props = { handleInputChange, formData, date: formData.date };
        switch (formData.category) {
            case "ELECTRONIC": return <ElectronicsInputs {...props} />;
            case "GOLD": return <GoldInputs {...props} />;
            case "VEHICLE": return <VehicleInputs {...props} />;
            case "WATCH": return <WatchInputs {...props} />;
            case "OTHER": return <OtherInputs {...props} />;
        }
    };

    const clientFields: [string, string, boolean][] = [
        ["Име", "name", true],
        ["Ембг", "embg", true],
        ["Телефон 1", "telephone", true],
        ["Телефон 2", "telephone_2", false],
        ["Адреса", "address", true],
        ["Град", "city", true],
    ];

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" />
            <div className="flex flex-col gap-8 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-8 rounded-lg min-w-fit w-[90%]">
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
                            {selectedClient ? (
                                <div className="flex flex-col gap-2">
                                    <div className="bg-white rounded shadow-[0_0_4px_rgba(0,0,0,0.2)] p-3 flex flex-col gap-1 text-sm">
                                        <p className="font-semibold text-base">{selectedClient.name}</p>
                                        <p className="text-[#666]">{selectedClient.embg}</p>
                                        <p className="text-[#666]">{selectedClient.phoneNumber}</p>
                                        {selectedClient.reservePhoneNumber && <p className="text-[#666]">{selectedClient.reservePhoneNumber}</p>}
                                        <p className="text-[#666]">{selectedClient.address}, {selectedClient.city}</p>
                                    </div>
                                    <button
                                        type="button"
                                        onClick={deselectClient}
                                        className="flex items-center gap-2 px-3 py-1.5 rounded bg-red-500 text-white text-sm hover:bg-red-600 transition-colors"
                                    >
                                        <UserX size={15} /> Нов клиент
                                    </button>
                                </div>
                            ) : (
                                <>
                                    <Autocomplete
                                        ref={scrollableClientsRef}
                                        options={clients}
                                        value={selectedClient}
                                        getOptionLabel={option => option.name}
                                        onChange={handleObjectSelect}
                                        renderInput={params => <TextField {...params} label="Постоечки клиенти" />}
                                        isOptionEqualToValue={(option, value) => option.id === value.id}
                                        filterOptions={(options, state) => options.filter(o =>
                                            o.name.toLowerCase().includes(state.inputValue.toLowerCase()) ||
                                            (o.embg && o.embg.includes(state.inputValue)) ||
                                            (o.phoneNumber && o.phoneNumber.includes(state.inputValue)) ||
                                            (o.reservePhoneNumber && o.reservePhoneNumber.includes(state.inputValue))
                                        )}
                                        renderOption={(props, option) => (
                                            <li {...props} key={option.id} style={{ display: "flex", flexDirection: "column", gap: ".1rem", alignItems: "flex-start" }}>
                                                <strong>{option.name}</strong>
                                                <span>{option.embg}</span>
                                                <span>{option.phoneNumber}</span>
                                                {option.reservePhoneNumber && <span>{option.reservePhoneNumber}</span>}
                                            </li>
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
                                </>
                            )}
                        </div>

                        {/* Pawn inputs */}
                        <div className="grid grid-cols-3 gap-2 gap-x-8">
                            <span className=" flex items-center gap-2 font-medium">
                                <Database size={20} /> Внеси Податоци за Предметот
                            </span>
                            <select
                                className="col-span-2 h-full px-2 py-1 rounded border-2 border-black/60 cursor-pointer text-base"
                                name="category"
                                value={formData.category}
                                onChange={e => handleInputChange(e.target.name, e.target.value)}
                            >
                                <option value="ELECTRONIC">Електроника</option>
                                <option value="GOLD">Злато</option>
                                <option value="VEHICLE">Возила</option>
                                <option value="WATCH">Часовници</option>
                                <option value="OTHER">Останато</option>
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
