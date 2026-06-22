import ReactDom from "react-dom";
import { useState } from "react";
import axios from "axios";
import { X, Tag, User, Package, Euro, Ban } from "lucide-react";
import { API_BASE } from "../../shared/api/config.ts";
import { SaleDetailed } from "./types.ts";
import { useAuth } from "../../GlobalContext.tsx";
import ModalActions from "../../shared/components/ModalActions.tsx";
import ModalShowMessagePawn from "../pawns/ModalShowMessagePawn.tsx";
import { resolveActiveSessionId } from "../../shared/utils/activeSession.ts";

interface Props {
    sale: SaleDetailed;
    closeModal: (e?: React.MouseEvent) => void;
    refresh: () => void;
}

const STATUS_MK: Record<string, string> = { AVAILABLE: "Достапен", SOLD: "Продаден", CANCELED: "Откажан" };
const ITEM_TYPE_MK: Record<string, string> = {
    GOLD: "Злато", ELECTRONIC: "Електроника", WATCH: "Часовник", VEHICLE: "Возило", OTHER: "Останато",
};
const ORIGIN_MK: Record<string, string> = { PAWN: "Залог", PURCHASE: "Откуп", OTHER: "Останато" };
const ITEM_STATUS_MK: Record<string, string> = { IN_PAWN: "Во залог", REDEEMED: "Откупен", IN_SALE: "Во продажба", SOLD: "Продаден" };
const ATTR_MK: Record<string, string> = {
    weightGrams: "Тежина (гр)", grams: "Тежина (гр)", carats: "Каратажа", karat: "Каратажа",
    pieceType: "Тип парче", pricePerGram: "Цена по грам", brand: "Бренд", make: "Марка", model: "Модел",
    year: "Година", material: "Материјал", category: "Категорија", serial: "Сериски број",
    vehicleType: "Тип возило", registrationNumber: "Рег. број", plate: "Таблички", mileage: "Километража",
    numberOfKeys: "Број клучеви", serviceHistoryAvailable: "Сервисна историја", lastServiceDate: "Последен сервис",
    registrationExpiryDate: "Рег. важи до", originalBoxIncluded: "Оригинална кутија",
    originalPapersIncluded: "Оригинални документи", warrantyCardIncluded: "Гарантна картичка",
    warrantyExpirationDate: "Гаранција до", functional: "Функционален", serviceRequired: "Потребен сервис",
};

const money = (n: number | null | undefined) => (n == null ? "—" : `${Number(n).toLocaleString("de-DE")} ден`);
const date = (s: string | null | undefined) => (s ? String(s).substring(0, 10) : "—");
const fmtAttr = (v: unknown) => (v == null || v === "" ? "—" : typeof v === "boolean" ? (v ? "Да" : "Не") : String(v));

function Field({ label, value }: { label: string; value: React.ReactNode }) {
    return (
        <div className="flex flex-col gap-0.5">
            <span className="text-[#666] text-xs">{label}</span>
            <span className="text-sm font-medium break-words">{value ?? "—"}</span>
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

const divider = <hr className="border-black/15" />;

export default function ModalReadMoreSale({ sale, closeModal, refresh }: Props) {
    const { can } = useAuth();
    const c = sale.customer ?? ({} as SaleDetailed["customer"]);
    const item = sale.item ?? ({} as SaleDetailed["item"]);
    const attrs = item.attributes ?? {};
    const canWrite = can("SALE_WRITE");
    const canCancel = can("SALE_CANCEL");
    const canAct = sale.status === "AVAILABLE" && (canWrite || canCancel);

    const [modalSell, setModalSell] = useState(false);
    const [modalCancel, setModalCancel] = useState(false);
    const [modalSuccess, setModalSuccess] = useState(false);
    const [successMsg, setSuccessMsg] = useState("");
    const [loading, setLoading] = useState(false);

    const getOpenSessionId = (): Promise<number | null> => resolveActiveSessionId();

    const sellItem = async (id: number, _category: string, salePrice: number, _description: string) => {
        setLoading(true);
        try {
            const sessionId = await getOpenSessionId();
            if (!sessionId) { setSuccessMsg("Нема отворена каса"); setModalSell(false); setModalSuccess(true); return; }
            await axios.post(`${API_BASE}/sales/${id}/sell`, { salePrice, cashRegisterSessionId: sessionId });
            setSuccessMsg("Успешно продаден предмет");
            setModalSell(false);
            setModalSuccess(true);
        } catch (error) {
            console.error("Error selling item:", error);
        } finally { setLoading(false); }
    };

    const cancelSale = async (id: number, _category: string) => {
        setLoading(true);
        try {
            await axios.post(`${API_BASE}/sales/${id}/cancel`, {});
            setSuccessMsg("Продажбата е откажана");
            setModalCancel(false);
            setModalSuccess(true);
        } catch (error) {
            console.error("Error canceling sale:", error);
        } finally { setLoading(false); }
    };

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" onClick={closeModal} />
            <div className="flex flex-col gap-6 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-8 rounded-lg w-[min(900px,92%)] max-h-[90vh] overflow-y-auto scrollbar-hidden">
                <div className="flex justify-between items-center">
                    <div className="flex items-center gap-3">
                        <h1 className="text-2xl font-semibold flex items-center gap-2"><Tag size={26} /> Продажба #{sale.id}</h1>
                        <span className={`px-2 py-0.5 rounded text-xs font-semibold ${sale.status === 'AVAILABLE' ? "bg-green/15 text-green" : sale.status === "CANCELED" ? "bg-red-500/15 text-red-500" : "bg-gray-500/15 text-gray-500"}`}>{STATUS_MK[sale.status] ?? sale.status}</span>
                    </div>
                    <button className="close-x-btn" onClick={closeModal}><X size={28} /></button>
                </div>

                <Section icon={<Tag size={20} />} title="Продажба">
                    <Field label="Откупна цена" value={money(sale.purchasePrice)} />
                    <Field label="Продажна цена" value={money(sale.salePrice)} />
                    <Field label="Профит" value={sale.profit == null ? "—" : <span className={sale.profit < 0 ? "text-red-500" : "text-green"}>{money(sale.profit)}</span>} />
                    <Field label="Статус" value={STATUS_MK[sale.status] ?? sale.status} />
                    <Field label="Продаден на" value={date(sale.soldAt)} />
                    <Field label="Креиран на" value={date(sale.createdAt)} />
                    <Field label="Изменет на" value={date(sale.updatedAt)} />
                </Section>

                {divider}

                <Section icon={<User size={20} />} title="Клиент">
                    <Field label="Име и презиме" value={c.fullName} />
                    <Field label="ЕМБГ" value={c.nationalId} />
                    <Field label="Телефон" value={c.phonePrimary} />
                    <Field label="Телефон 2" value={c.phoneSecondary || "—"} />
                    <Field label="Адреса" value={c.address} />
                    <Field label="Град" value={c.city} />
                </Section>

                {divider}

                <Section icon={<Package size={20} />} title="Предмет">
                    <Field label="Тип" value={ITEM_TYPE_MK[item.type] ?? item.type} />
                    <Field label="Потекло" value={ORIGIN_MK[item.origin] ?? item.origin} />
                    <Field label="Статус" value={ITEM_STATUS_MK[item.status] ?? item.status} />
                    <Field label="Опис" value={item.description || "—"} />
                    {Object.entries(attrs).map(([key, value]) => (
                        <Field key={key} label={ATTR_MK[key] ?? key} value={fmtAttr(value)} />
                    ))}
                </Section>

                {canAct && (
                    <div className="flex flex-wrap gap-3 mt-2">
                        {canWrite && (
                            <button onClick={() => setModalSell(true)}
                                className="flex items-center gap-2 px-4 py-2 rounded text-sm font-medium shadow-sm shadow-green/20 border border-green/60 text-green hover:bg-green hover:text-white transition-all">
                                <Euro size={16} /> Продади
                            </button>
                        )}
                        {canCancel && (
                            <button onClick={() => setModalCancel(true)}
                                className="flex items-center gap-2 px-4 py-2 rounded text-sm font-medium shadow-sm shadow-red-500/20 border border-red-500/60 text-red-500 hover:bg-red-500 hover:text-white transition-all">
                                <Ban size={16} /> Откажи продажба
                            </button>
                        )}
                    </div>
                )}
            </div>

            {modalSell && (
                <ModalActions
                    pawnAction={null}
                    action={sellItem}
                    id={sale.id}
                    category="sale"
                    successMsg="Успешно продаден предмет!"
                    closeModal={() => setModalSell(false)}
                    priceBought={sale.purchasePrice}
                    suggestedPrice={sale.purchasePrice}
                    title="По која цена е продаден предметот?"
                    loading={loading}
                />
            )}

            {modalCancel && (
                <ModalActions
                    pawnAction="cancel"
                    action={cancelSale}
                    id={sale.id}
                    category="sale"
                    successMsg="Продажбата е откажана!"
                    closeModal={() => setModalCancel(false)}
                    priceBought={sale.purchasePrice}
                    title="Откажи продажба?"
                    loading={loading}
                />
            )}

            {modalSuccess && (
                <ModalShowMessagePawn
                    closeModal={() => { setModalSuccess(false); refresh(); closeModal(); }}
                    successMsg={successMsg}
                    infoMsg=""
                    clientName={c.fullName}
                />
            )}
        </>,
        document.getElementById("portal")!
    );
}
