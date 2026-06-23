import ReactDom from "react-dom";
import { useState } from "react";
import axios from "axios";
import { X, User, Package, FileText, History, RotateCcw, HandCoins, ShoppingCart, Pencil } from "lucide-react";
import { API_BASE } from "../../shared/api/config.ts";
import { PawnDetailed, PAWN_ACTION_LABEL } from "./types.ts";
import { useAuth } from "../../GlobalContext.tsx";
import ModalActions from "../../shared/components/ModalActions.tsx";
import ModalShowMessagePawn from "./ModalShowMessagePawn.tsx";
import ModalEditPawnContract from "./ModalEditPawnContract.tsx";
import ModalEditPawnItem from "./ModalEditPawnItem.tsx";
import { resolveActiveSessionId } from "../../shared/utils/activeSession.ts";
import { downloadExtensionDocById, downloadRedemptionDoc } from "../../shared/utils/pawnDocuments.tsx";

interface Props {
    pawn: PawnDetailed;
    closeModal: (e?: React.MouseEvent) => void;
    refresh: () => void;
}

const STATUS_MK: Record<string, string> = {
    ACTIVE: "Активен", REDEEMED: "Откупен", FORFEITED: "Пренесен во продажба",
};
const ITEM_TYPE_MK: Record<string, string> = {
    GOLD: "Злато", ELECTRONIC: "Електроника", WATCH: "Часовник", VEHICLE: "Возило", OTHER: "Останато",
};
const ORIGIN_MK: Record<string, string> = {
    PAWN: "Залог", PURCHASE: "Откуп", OTHER: "Останато",
};
const ITEM_STATUS_MK: Record<string, string> = {
    IN_PAWN: "Во залог", REDEEMED: "Откупен", IN_SALE: "Во продажба", SOLD: "Продаден",
};

// Macedonian labels for the known item attribute keys (the canonical set plus
// the demo-seed variants). Unknown keys fall back to the raw key.
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

const money = (n: number | null | undefined) =>
    n == null ? "—" : `${Number(n).toLocaleString("de-DE")} ден`;
const date = (s: string | null | undefined) => (s ? String(s).substring(0, 10) : "—");

function fmtAttr(value: unknown): string {
    if (value == null || value === "") return "—";
    if (typeof value === "boolean") return value ? "Да" : "Не";
    return String(value);
}

function Field({ label, value }: { label: string; value: React.ReactNode }) {
    return (
        <div className="flex flex-col gap-0.5">
            <span className="text-[#666] text-xs">{label}</span>
            <span className="text-sm font-medium break-words">{value ?? "—"}</span>
        </div>
    );
}

function Section({ icon, title, action, children }: { icon: React.ReactNode; title: string; action?: React.ReactNode; children: React.ReactNode }) {
    return (
        <div className="flex flex-col gap-3">
            <div className="flex items-center gap-3">
                <span className="flex items-center gap-2 font-medium">{icon}{title}</span>
                {action}
            </div>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-x-8 gap-y-3">{children}</div>
        </div>
    );
}

// Small, seamless inline "edit" affordance next to a section title.
function EditLink({ onClick }: { onClick: () => void }) {
    return (
        <button onClick={onClick} className="flex items-center gap-1 text-xs text-[#888] hover:text-green hover:font-semibold transition-colors">
            <Pencil size={14} /> Измени
        </button>
    );
}

const divider = <hr className="border-black/15" />;
const actionBtn = "flex items-center gap-2 px-4 py-2 rounded text-sm font-medium transition-all";

export default function ModalReadMorePawn({ pawn, closeModal, refresh }: Props) {
    const { can } = useAuth();
    const c = pawn.customer ?? ({} as PawnDetailed["customer"]);
    const item = pawn.item ?? ({} as PawnDetailed["item"]);
    const attrs = item.attributes ?? {};
    const overdue = pawn.daysOverdue > 0;
    const canWrite = can("PAWN_WRITE");
    const canForfeit = can("PAWN_FORFEIT");
    const canItemUpdate = can("ITEM_UPDATE");
    // Active pawn + at least one action the user is allowed to perform.
    const canAct = pawn.status === "ACTIVE" && (canWrite || canForfeit);
    const canEditContract = pawn.status === "ACTIVE" && can("PAWN_UPDATE");

    const [modalExtend, setModalExtend] = useState(false);
    const [modalRedeem, setModalRedeem] = useState(false);
    const [modalForfeit, setModalForfeit] = useState(false);
    const [modalEditContract, setModalEditContract] = useState(false);
    const [modalEditItem, setModalEditItem] = useState(false);
    const [modalSuccess, setModalSuccess] = useState(false);
    const [successMsg, setSuccessMsg] = useState("");
    const [infoMsg, setInfoMsg] = useState("");
    const [loading, setLoading] = useState(false);

    // ModalActions inputs map to these the same way the pawn row does.
    const dailyProvision = Math.abs(Math.round(pawn.interestAmount) / pawn.termDays);
    const daysLeft = -pawn.daysOverdue;

    // Extend/redeem post against the active register's open session (selected in the cash bar).
    const getOpenSessionId = (): Promise<number | null> => resolveActiveSessionId();

    const extendPawn = async (id: number, _category: string, provision: number, _description: string, _carryOverDays: number) => {
        setLoading(true);
        try {
            const sessionId = await getOpenSessionId();
            if (!sessionId) { setSuccessMsg("Нема отворена каса"); setInfoMsg(""); setModalExtend(false); setModalSuccess(true); return; }
            await axios.post(`${API_BASE}/pawns/${id}/extend`, { interestPaid: provision, fee: 0, cashRegisterSessionId: sessionId });
            // Download the annex (Анекс на договор за заем) for the staff member to print.
            downloadExtensionDocById(id).catch(err => console.error("Error generating annex:", err));
            setSuccessMsg("Успешно продолжен залог");
            setInfoMsg(`Додадени се ${provision.toLocaleString("de-DE")} во каса!`);
            setModalExtend(false);
            setModalSuccess(true);
        } catch (error) {
            console.error("Error extending pawn:", error);
        } finally { setLoading(false); }
    };

    // The staff-entered amount is the final redemption price recorded in the transaction.
    const redeemPawn = async (id: number, _category: string, priceClosed: number, _description: string) => {
        setLoading(true);
        try {
            const sessionId = await getOpenSessionId();
            if (!sessionId) { setSuccessMsg("Нема отворена каса"); setInfoMsg(""); setModalRedeem(false); setModalSuccess(true); return; }
            await axios.post(`${API_BASE}/pawns/${id}/redeem`, { paidAmount: priceClosed, cashRegisterSessionId: sessionId });
            // Download the return-of-item receipt for the staff member to print.
            downloadRedemptionDoc(c.fullName).catch(err => console.error("Error generating receipt:", err));
            setSuccessMsg("Успешно затворен залог");
            setInfoMsg("Залогот е затворен!");
            setModalRedeem(false);
            setModalSuccess(true);
        } catch (error) {
            console.error("Error redeeming pawn:", error);
        } finally { setLoading(false); }
    };

    const forfeitPawn = async (id: number, _category: string) => {
        setLoading(true);
        try {
            await axios.post(`${API_BASE}/pawns/${id}/forfeit`, {});
            setSuccessMsg("Успешно пренесен залог во продажба");
            setInfoMsg("");
            setModalForfeit(false);
            setModalSuccess(true);
        } catch (error) {
            console.error("Error forfeiting pawn:", error);
        } finally { setLoading(false); }
    };

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" onClick={closeModal} />
            <div className="flex flex-col gap-6 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-8 rounded-lg w-[min(900px,92%)] max-h-[90vh] overflow-y-auto scrollbar-hidden">
                <div className="flex justify-between items-center">
                    <div className="flex items-center gap-3">
                        <h1 className="text-2xl font-semibold">Залог #{pawn.id}</h1>
                        <span className={`px-2 py-0.5 rounded text-xs font-semibold ${pawn.status === 'ACTIVE' ? "bg-green/15 text-green" : "bg-gray-500/15 text-gray-500"}`}>
                            {STATUS_MK[pawn.status] ?? pawn.status}
                        </span>
                        {overdue && (
                            <span className="px-2 py-0.5 rounded text-xs font-semibold bg-red-500/15 text-red-500">
                                Истечен {pawn.daysOverdue} дена
                            </span>
                        )}
                    </div>
                    <button className="close-x-btn" onClick={closeModal}><X size={28} /></button>
                </div>

                <Section icon={<FileText size={20} />} title="Податоци за залогот"
                    action={canEditContract ? <EditLink onClick={() => setModalEditContract(true)} /> : undefined}>
                    <Field label="Вредност на залог" value={money(pawn.principalAmount)} />
                    <Field label="Провизија" value={money(pawn.interestAmount)} />
                    <Field label="Времетраење" value={`${pawn.termDays} дена`} />
                    <Field label="Заложено на" value={date(pawn.issueDate)} />
                    <Field label="Важи до" value={<span className={overdue ? "text-red-500" : ""}>{date(pawn.dueDate)}</span>} />
                    <Field label="Првично важи до" value={date(pawn.originalDueDate)} />
                    <Field label="Откупен на" value={date(pawn.redeemedAt)} />
                    <Field label="Пренесен на" value={date(pawn.forfeitedAt)} />
                    <Field label="Креиран од" value={pawn.createdByStaffName ?? `#${pawn.createdByStaffId}`} />
                    <Field label="Креиран на" value={date(pawn.createdAt)} />
                    <Field label="Изменет на" value={date(pawn.updatedAt)} />
                </Section>

                {divider}

                <Section icon={<User size={20} />} title="Податоци за клиентот">
                    <Field label="Име и презиме" value={c.fullName} />
                    <Field label="ЕМБГ" value={c.nationalId} />
                    <Field label="Телефон" value={c.phonePrimary} />
                    <Field label="Телефон 2" value={c.phoneSecondary || "—"} />
                    <Field label="Адреса" value={c.address} />
                    <Field label="Град" value={c.city} />
                </Section>

                {divider}

                <Section icon={<Package size={20} />} title="Податоци за предметот"
                    action={canItemUpdate ? <EditLink onClick={() => setModalEditItem(true)} /> : undefined}>
                    <Field label="Тип" value={ITEM_TYPE_MK[item.type] ?? item.type} />
                    <Field label="Потекло" value={ORIGIN_MK[item.origin] ?? item.origin} />
                    <Field label="Статус" value={ITEM_STATUS_MK[item.status] ?? item.status} />
                    <Field label="Опис" value={item.description || "—"} />
                    {Object.entries(attrs).map(([key, value]) => (
                        <Field key={key} label={ATTR_MK[key] ?? key} value={fmtAttr(value)} />
                    ))}
                </Section>

                {pawn.extensions && pawn.extensions.length > 0 && (
                    <>
                        {divider}
                        <div className="flex flex-col gap-3">
                            <span className="flex items-center gap-2 font-medium"><History size={20} /> Продолжувања ({pawn.extensions.length})</span>
                            {pawn.extensions.map(ext => (
                                <div key={ext.id} className="grid grid-cols-2 md:grid-cols-4 gap-x-8 gap-y-2 border-t border-black/10 pt-2 first:border-t-0 first:pt-0">
                                    <Field label="Од" value={date(ext.previousDueDate)} />
                                    <Field label="До" value={date(ext.newDueDate)} />
                                    <Field label="Платена провизија" value={money(ext.interestPaid)} />
                                    <Field label="Такса" value={money(ext.fee)} />
                                </div>
                            ))}
                        </div>
                    </>
                )}

                {canAct && (
                    <div className="flex flex-wrap gap-3 mt-2">
                        {canWrite && (
                            <button onClick={() => setModalExtend(true)}
                                className={`${actionBtn} shadow-sm shadow-green/20 border border-green/60 text-green hover:bg-green hover:text-white`}>
                                <RotateCcw size={16} /> {PAWN_ACTION_LABEL.extend}
                            </button>
                        )}
                        {canWrite && (
                            <button onClick={() => setModalRedeem(true)}
                                className={`${actionBtn} shadow-sm shadow-red-500/20 border border-red-500/60 text-red-500 hover:bg-red-500 hover:text-white`}>
                                <HandCoins size={16} /> {PAWN_ACTION_LABEL.redeem}
                            </button>
                        )}
                        {canForfeit && (
                            <button onClick={() => setModalForfeit(true)}
                                className={`${actionBtn} shadow-sm shadow-amber-500/20 border border-amber-500/60 text-amber-500 hover:bg-amber-500 hover:text-white`}>
                                <ShoppingCart size={16} /> {PAWN_ACTION_LABEL.forfeit}
                            </button>
                        )}
                    </div>
                )}
            </div>

            {modalExtend && (
                <ModalActions
                    pawnAction="extend"
                    action={extendPawn}
                    id={pawn.id}
                    category={item.type ?? ""}
                    successMsg="Успешно продолжен залог!"
                    closeModal={() => setModalExtend(false)}
                    priceBought={pawn.principalAmount}
                    provision={pawn.interestAmount}
                    dailyProvision={dailyProvision}
                    suggestedPrice={pawn.interestAmount}
                    daysLeft={daysLeft}
                    title="Со кој износ е продолжен залогот?"
                    loading={loading}
                />
            )}

            {modalRedeem && (
                <ModalActions
                    pawnAction="redeem"
                    action={redeemPawn}
                    id={pawn.id}
                    category={item.type ?? ""}
                    successMsg="Успешно затворен залог!"
                    closeModal={() => setModalRedeem(false)}
                    priceBought={pawn.principalAmount}
                    provision={pawn.interestAmount}
                    dailyProvision={dailyProvision}
                    suggestedPrice={pawn.principalAmount + pawn.interestAmount}
                    daysLeft={daysLeft}
                    title="Со кој износ е затворен залогот?"
                    loading={loading}
                />
            )}

            {modalForfeit && (
                <ModalActions
                    pawnAction="forfeit"
                    action={forfeitPawn}
                    id={pawn.id}
                    category={item.type ?? ""}
                    successMsg="Успешно пренесен залог во продажба!"
                    closeModal={() => setModalForfeit(false)}
                    priceBought={pawn.principalAmount}
                    title="Пренеси во продажба?"
                    loading={loading}
                />
            )}

            {modalEditContract && (
                <ModalEditPawnContract
                    pawn={pawn}
                    closeModal={() => setModalEditContract(false)}
                    onSaved={() => { setModalEditContract(false); refresh(); closeModal(); }}
                />
            )}

            {modalEditItem && (
                <ModalEditPawnItem
                    item={item}
                    closeModal={() => setModalEditItem(false)}
                    onSaved={() => { setModalEditItem(false); refresh(); closeModal(); }}
                />
            )}

            {modalSuccess && (
                <ModalShowMessagePawn
                    closeModal={() => { setModalSuccess(false); refresh(); closeModal(); }}
                    successMsg={successMsg}
                    infoMsg={infoMsg}
                    clientName={c.fullName}
                />
            )}
        </>,
        document.getElementById("portal")!
    );
}
