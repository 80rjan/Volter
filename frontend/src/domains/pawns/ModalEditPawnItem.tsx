import ReactDom from "react-dom";
import { useState } from "react";
import axios from "axios";
import { X, CheckCheck, Package } from "lucide-react";
import Loading from "../../shared/components/Loading.tsx";
import { API_BASE } from "../../shared/api/config.ts";
import { PawnDetailed } from "./types.ts";

interface Props {
    item: PawnDetailed["item"];
    closeModal: () => void;
    onSaved: () => void;
}

// Macedonian labels for the known attribute keys; unknown keys fall back to the raw key.
const ATTR_MK: Record<string, string> = {
    weightGrams: "Тежина (гр)", grams: "Тежина (гр)",
    carats: "Каратажа", karat: "Каратажа",
    pieceType: "Тип парче", pricePerGram: "Цена по грам",
    brand: "Бренд", make: "Марка", model: "Модел", year: "Година",
    material: "Материјал", category: "Категорија", serial: "Сериски број",
    vehicleType: "Тип возило", registrationNumber: "Рег. број", plate: "Таблички",
    mileage: "Километража", numberOfKeys: "Број клучеви",
    serviceHistoryAvailable: "Сервисна историја", lastServiceDate: "Последен сервис",
    registrationExpiryDate: "Рег. важи до",
    originalBoxIncluded: "Оригинална кутија", originalPapersIncluded: "Оригинални документи",
    warrantyCardIncluded: "Гарантна картичка", warrantyExpirationDate: "Гаранција до",
    functional: "Функционален", serviceRequired: "Потребен сервис",
};

const inputCls = "bg-white border-none rounded text-sm p-2 w-full shadow-[0_0_4px_rgba(0,0,0,0.2)]";

// Edits item description + its free-form `attributes` JSON. Each existing key is
// rendered as a labeled, type-aware field (the value's original type is preserved
// on save), so the staff never edits raw JSON.
export default function ModalEditPawnItem({ item, closeModal, onSaved }: Props) {
    const original = item.attributes ?? {};
    const keys = Object.keys(original);
    const [description, setDescription] = useState(item.description ?? "");
    const [attrs, setAttrs] = useState<Record<string, unknown>>({ ...original });
    const [busy, setBusy] = useState(false);
    const [error, setError] = useState("");

    const setAttr = (key: string, raw: string, orig: unknown) => {
        let val: unknown = raw;
        if (typeof orig === "number") val = raw === "" ? null : Number(raw);
        else if (typeof orig === "boolean") val = raw === "true";
        setAttrs(p => ({ ...p, [key]: val }));
    };

    const save = async () => {
        setBusy(true); setError("");
        try {
            await axios.patch(`${API_BASE}/items/${item.id}`, { description: description || null, attributes: attrs });
            onSaved();
        } catch (err: any) {
            setError(err?.response?.data?.message || "Измената не успеа.");
            setBusy(false);
        }
    };

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1100]" onClick={busy ? undefined : closeModal} />
            <div className="flex flex-col gap-5 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1100] p-8 rounded-lg w-[min(560px,92%)] max-h-[90vh] overflow-y-auto scrollbar-hidden">
                <div className="flex justify-between items-center">
                    <h1 className="flex items-center gap-2 text-xl font-semibold"><Package size={20} /> Измени предмет</h1>
                    <button className="close-x-btn disabled:opacity-40 disabled:cursor-not-allowed" onClick={closeModal} disabled={busy}><X size={26} /></button>
                </div>
                <div className="flex flex-col gap-4">
                    <div className="flex flex-col gap-1">
                        <span className="text-[#666] text-xs">Опис</span>
                        <textarea className={inputCls} rows={2} value={description} onChange={e => setDescription(e.target.value)} />
                    </div>
                    <div className="grid grid-cols-2 gap-x-6 gap-y-3">
                        {keys.map(key => {
                            const orig = (original as any)[key];
                            const cur = (attrs as any)[key];
                            return (
                                <div key={key} className="flex flex-col gap-1">
                                    <span className="text-[#666] text-xs">{ATTR_MK[key] ?? key}</span>
                                    {typeof orig === "boolean" ? (
                                        <select className={inputCls} value={String(cur)} onChange={e => setAttr(key, e.target.value, orig)}>
                                            <option value="true">Да</option>
                                            <option value="false">Не</option>
                                        </select>
                                    ) : (
                                        <input className={inputCls} type={typeof orig === "number" ? "number" : "text"}
                                            value={cur == null ? "" : String(cur)} onChange={e => setAttr(key, e.target.value, orig)} />
                                    )}
                                </div>
                            );
                        })}
                    </div>
                    {keys.length === 0 && <p className="text-xs text-[#888]">Овој предмет нема дополнителни атрибути.</p>}
                    {error && <p className="text-red-500 text-sm">{error}</p>}
                </div>
                <div className="flex justify-end items-center gap-3 min-h-[40px]">
                    <button onClick={closeModal} disabled={busy} className="px-5 py-2 rounded bg-black/10 text-sm hover:bg-black/15 transition-colors disabled:opacity-40 disabled:cursor-not-allowed">Откажи</button>
                    {busy ? <Loading width={26} height={26} /> : (
                        <button onClick={save}
                            className="flex items-center justify-center gap-2 px-6 py-2 rounded text-white font-medium bg-green shadow-[4px_2px_6px_rgba(0,0,0,0.2)] transition-all hover:scale-105 disabled:opacity-40">
                            <CheckCheck size={20} /> Зачувај
                        </button>
                    )}
                </div>
            </div>
        </>,
        document.getElementById("portal")!
    );
}
