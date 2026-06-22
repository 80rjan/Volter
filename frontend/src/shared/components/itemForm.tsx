// Shared item-attribute form used by the "add pawn" and "add sale" modals so they
// stay visually and structurally identical. Owns the per-type fields, the type
// selector, the description field, the gold-price reference strip, and the
// attribute builder that turns the form state into the backend `attributes` map.
import { EUR_TO_MKD } from "../api/config.ts";

export type ItemType = "GOLD" | "ELECTRONIC" | "WATCH" | "VEHICLE" | "OTHER";
export type Handle = (name: string, value: any) => void;

export const inputClass = "bg-white border-none rounded text-base p-2 shadow-[0_0_4px_rgba(0,0,0,0.2)] h-fit";
export const selectClass = "bg-white border-none! shadow-[0_0_4px_rgba(0,0,0,0.2)] rounded p-1 h-full";

// Item-related form fields. Each modal's FormData extends this.
export interface ItemFormData {
    type: ItemType;
    description: string;
    weightGrams: string; carats: string; pieceType: string; pricePerGram: string;
    brand: string; model: string; year: string; material: string;
    electronicCategory: string; otherCategory: string;
    vehicleType: string; registrationNumber: string; mileage: string; numberOfKeys: string;
    serviceHistoryAvailable: boolean; registrationExpiryDate: string; lastServiceDate: string;
    originalBoxIncluded: boolean; originalPapersIncluded: boolean; warrantyCardIncluded: boolean;
    warrantyExpirationDate: string; functional: boolean; serviceRequired: boolean;
    [key: string]: any;
}

export const ITEM_FORM_DEFAULTS: ItemFormData = {
    type: "ELECTRONIC", description: "",
    weightGrams: "", carats: "", pieceType: "", pricePerGram: "",
    brand: "", model: "", year: "", material: "",
    electronicCategory: "", otherCategory: "",
    vehicleType: "", registrationNumber: "", mileage: "", numberOfKeys: "",
    serviceHistoryAvailable: false, registrationExpiryDate: "", lastServiceDate: "",
    originalBoxIncluded: false, originalPapersIncluded: false, warrantyCardIncluded: false,
    warrantyExpirationDate: "", functional: true, serviceRequired: false,
};

export const Text = ({ label, name, h, type = "text", step }: {
    label: string; name: string; h: Handle; type?: string; step?: string;
}) => (
    <div className="flex flex-col gap-1">
        <p className="-ml-1 text-[#666]">{label}</p>
        <input className={inputClass} name={name} type={type} step={step}
               onChange={e => h(e.target.name, e.target.value)}
               required={type !== "date" || name === "registrationExpiryDate"} />
    </div>
);

export const Check = ({ label, name, value, h }: { label: string; name: string; value: boolean; h: Handle }) => (
    <div className="flex flex-col gap-1">
        <p className="-ml-1 text-[#666]">{label}</p>
        <div className="h-full flex items-center">
            <input type="checkbox" checked={value} onChange={e => h(name, e.target.checked)} className="w-5 h-5 cursor-pointer" />
        </div>
    </div>
);

export function ItemTypeSelect({ value, onChange }: { value: ItemType; onChange: (v: ItemType) => void }) {
    return (
        <select className="px-2 py-1.5 rounded border-2 border-black/60 cursor-pointer text-base"
                name="type" value={value} onChange={e => onChange(e.target.value as ItemType)}>
            <option value="ELECTRONIC">Електроника</option>
            <option value="GOLD">Злато</option>
            <option value="VEHICLE">Возила</option>
            <option value="WATCH">Часовници</option>
            <option value="OTHER">Останато</option>
        </select>
    );
}

export function ItemFields({ f, h }: { f: ItemFormData; h: Handle }) {
    switch (f.type) {
        case "GOLD":
            return (
                <>
                    <Text label="Тежина (во грамови)" name="weightGrams" h={h} type="number" step="0.001" />
                    <div className="flex flex-col gap-1">
                        <p className="-ml-1 text-[#666]">Каратажа</p>
                        <select className={selectClass} name="carats" value={f.carats}
                                onChange={e => h(e.target.name, e.target.value)} required>
                            <option value=""></option>
                            {["8", "9", "10", "14", "18", "21", "22", "24"].map(k => <option key={k} value={k}>{k}k</option>)}
                        </select>
                    </div>
                    <Text label="Тип парче" name="pieceType" h={h} />
                    <Text label="Цена по грам" name="pricePerGram" h={h} type="number" step="0.01" />
                </>
            );
        case "ELECTRONIC":
            return (
                <>
                    <Text label="Бренд" name="brand" h={h} />
                    <Text label="Категорија" name="electronicCategory" h={h} />
                    <Text label="Година" name="year" h={h} type="number" />
                </>
            );
        case "WATCH":
            return (
                <>
                    <Text label="Бренд" name="brand" h={h} />
                    <Text label="Модел" name="model" h={h} />
                    <Text label="Материјал" name="material" h={h} />
                    <Text label="Година" name="year" h={h} type="number" />
                    <Check label="Оригинална кутија" name="originalBoxIncluded" value={f.originalBoxIncluded} h={h} />
                    <Check label="Оригинални документи" name="originalPapersIncluded" value={f.originalPapersIncluded} h={h} />
                    <Check label="Гарантна картичка" name="warrantyCardIncluded" value={f.warrantyCardIncluded} h={h} />
                    <Text label="Гаранција до" name="warrantyExpirationDate" h={h} type="date" />
                    <Check label="Функционален" name="functional" value={f.functional} h={h} />
                    <Check label="Потребен сервис" name="serviceRequired" value={f.serviceRequired} h={h} />
                </>
            );
        case "VEHICLE":
            return (
                <>
                    <Text label="Бренд" name="brand" h={h} />
                    <Text label="Модел" name="model" h={h} />
                    <Text label="Тип на возило" name="vehicleType" h={h} />
                    <Text label="Година" name="year" h={h} type="number" />
                    <Text label="Рег. број" name="registrationNumber" h={h} />
                    <Text label="Километража" name="mileage" h={h} type="number" />
                    <Text label="Број на клучеви" name="numberOfKeys" h={h} type="number" />
                    <Text label="Рег. важи до" name="registrationExpiryDate" h={h} type="date" />
                    <Text label="Последен сервис" name="lastServiceDate" h={h} type="date" />
                    <Check label="Сервисна историја" name="serviceHistoryAvailable" value={f.serviceHistoryAvailable} h={h} />
                </>
            );
        case "OTHER":
            return <Text label="Категорија" name="otherCategory" h={h} />;
    }
}

export function DescField({ h }: { h: Handle }) {
    return (
        <div className="flex flex-col gap-1">
            <p className="-ml-1 text-[#666]">Опис</p>
            <input className={inputClass} name="description" onChange={e => h(e.target.name, e.target.value)} required />
        </div>
    );
}

export function buildAttributes(f: ItemFormData): Record<string, unknown> {
    switch (f.type) {
        case "GOLD":
            return { weightGrams: Number(f.weightGrams), carats: f.carats, pieceType: f.pieceType, pricePerGram: Number(f.pricePerGram) };
        case "ELECTRONIC":
            return { brand: f.brand, category: f.electronicCategory, year: Number(f.year) };
        case "WATCH":
            return {
                brand: f.brand, model: f.model, material: f.material, year: Number(f.year),
                originalBoxIncluded: f.originalBoxIncluded, originalPapersIncluded: f.originalPapersIncluded,
                warrantyCardIncluded: f.warrantyCardIncluded, warrantyExpirationDate: f.warrantyExpirationDate || null,
                functional: f.functional, serviceRequired: f.serviceRequired,
            };
        case "VEHICLE":
            return {
                brand: f.brand, model: f.model, vehicleType: f.vehicleType, year: Number(f.year),
                registrationNumber: f.registrationNumber, mileage: Number(f.mileage), numberOfKeys: Number(f.numberOfKeys),
                serviceHistoryAvailable: f.serviceHistoryAvailable, lastServiceDate: f.lastServiceDate || null,
                registrationExpiryDate: f.registrationExpiryDate,
            };
        case "OTHER":
            return { category: f.otherCategory };
    }
}

// Live gold price reference strip (shown for GOLD items).
export function GoldPriceStrip({ prices }: { prices: Record<string, number> | null }) {
    if (!prices) return null;
    const karats: [string, string][] = [
        ["24к", "price_gram_24k"], ["22к", "price_gram_22k"], ["21к", "price_gram_21k"],
        ["18к", "price_gram_18k"], ["14к", "price_gram_14k"], ["10к", "price_gram_10k"],
    ];
    return (
        <div className="flex flex-wrap items-center gap-x-5 gap-y-1 border-l-2 border-orange-400 pl-3 text-sm">
            <span className="text-[#666] font-medium">Тековни цени на злато (€/грам):</span>
            {karats.filter(([, k]) => prices[k] != null).map(([label, k]) => (
                <span key={k} className="font-semibold text-orange-600">
                    {label}: {prices[k].toFixed(2)}€
                    <span className="font-normal text-[#888]"> / {(prices[k] * EUR_TO_MKD).toFixed(0)} ден</span>
                </span>
            ))}
        </div>
    );
}
