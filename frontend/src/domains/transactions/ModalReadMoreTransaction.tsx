import ReactDom from "react-dom";
import { X, FileText, User, Package, Tag, HandCoins, Wallet, ArrowDownLeft, ArrowUpRight } from "lucide-react";
import { TransactionDetailed } from "./types.ts";

interface Props {
    tx: TransactionDetailed;
    closeModal: (e?: React.MouseEvent) => void;
}

const TYPE_MK: Record<string, string> = {
    PAWN: "Залог", SALE: "Продажба", EXPENSE: "Расход", CASH_REGISTER: "Каса",
};
const DIRECTION_MK: Record<string, string> = { IN: "Влез", OUT: "Излез" };
const ITEM_TYPE_MK: Record<string, string> = {
    GOLD: "Злато", ELECTRONIC: "Електроника", WATCH: "Часовник", VEHICLE: "Возило", OTHER: "Останато",
};
const ORIGIN_MK: Record<string, string> = { PAWN: "Залог", PURCHASE: "Откуп", OTHER: "Останато" };
const ITEM_STATUS_MK: Record<string, string> = {
    IN_PAWN: "Во залог", REDEEMED: "Откупен", IN_SALE: "Во продажба", SOLD: "Продаден",
};
const PAWN_STATUS_MK: Record<string, string> = {
    ACTIVE: "Активен", REDEEMED: "Откупен", FORFEITED: "Пренесен во продажба",
};
const SALE_STATUS_MK: Record<string, string> = { AVAILABLE: "Достапен", SOLD: "Продаден", CANCELED: "Откажан" };
const EXPENSE_CAT_MK: Record<string, string> = {
    SUPPLIES: "Материјали", RENT: "Кирија", UTILITIES: "Режии", SALARY: "Плата", MAINTENANCE: "Одржување", OTHER: "Останато",
};
const SESSION_STATUS_MK: Record<string, string> = { OPEN: "Отворена", CLOSED: "Затворена" };
const STAFF_STATUS_MK: Record<string, string> = {
    ACTIVE: "Активен", INACTIVE: "Неактивен", SUSPENDED: "Суспендиран",
};
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
const datetime = (s: string | null | undefined) => (s ? String(s).substring(0, 19).replace("T", " ") : "—");
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

export default function ModalReadMoreTransaction({ tx, closeModal }: Props) {
    const isIn = tx.direction === "IN";
    const { pawn, sale, expense, cashRegisterSession: session, staff } = tx;
    const customer = pawn?.customer ?? sale?.customer ?? null;
    const item = pawn?.item ?? sale?.item ?? null;
    const attrs = item?.attributes ?? {};

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" onClick={closeModal} />
            <div className="flex flex-col gap-6 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-8 rounded-lg w-[min(900px,92%)] max-h-[90vh] overflow-y-auto scrollbar-hidden">
                <div className="flex justify-between items-center">
                    <div className="flex items-center gap-3">
                        <h1 className="text-2xl font-semibold">Трансакција #{tx.id}</h1>
                        <span className="px-2 py-0.5 rounded text-xs font-semibold bg-black/10 text-[#333]">
                            {TYPE_MK[tx.type] ?? tx.type}
                        </span>
                        <span className={`flex items-center gap-1 px-2 py-0.5 rounded text-xs font-semibold ${isIn ? "bg-green/15 text-green" : "bg-red-500/15 text-red-500"}`}>
                            {isIn ? <ArrowDownLeft size={14} /> : <ArrowUpRight size={14} />}
                            {DIRECTION_MK[tx.direction] ?? tx.direction}
                        </span>
                    </div>
                    <button className="close-x-btn" onClick={closeModal}><X size={28} /></button>
                </div>

                <Section icon={<FileText size={20} />} title="Трансакција">
                    <Field label="Тип" value={TYPE_MK[tx.type] ?? tx.type} />
                    <Field label="Износ" value={<span className={isIn ? "text-green" : "text-red-500"}>{money(tx.amount)}</span>} />
                    <Field label="Насока" value={DIRECTION_MK[tx.direction] ?? tx.direction} />
                    <Field label="Каса сесија" value={tx.cashRegisterSessionId == null ? "—" : `#${tx.cashRegisterSessionId}`} />
                    <Field label="Опис" value={tx.description || "—"} />
                    <Field label="Датум" value={datetime(tx.createdAt)} />
                </Section>

                {staff && (
                    <>
                        {divider}
                        <Section icon={<User size={20} />} title="Вработен">
                            <Field label="Име и презиме" value={staff.fullName} />
                            <Field label="Корисничко име" value={staff.username} />
                            <Field label="Статус" value={STAFF_STATUS_MK[staff.status] ?? staff.status} />
                            <Field label="ID" value={`#${staff.id}`} />
                        </Section>
                    </>
                )}

                {pawn && (
                    <>
                        {divider}
                        <Section icon={<HandCoins size={20} />} title="Залог">
                            <Field label="Залог" value={`#${pawn.id}`} />
                            <Field label="Вредност" value={money(pawn.principalAmount)} />
                            <Field label="Провизија" value={money(pawn.interestAmount)} />
                            <Field label="Времетраење" value={`${pawn.termDays} дена`} />
                            <Field label="Заложено на" value={date(pawn.issueDate)} />
                            <Field label="Важи до" value={date(pawn.dueDate)} />
                            <Field label="Статус" value={PAWN_STATUS_MK[pawn.status] ?? pawn.status} />
                            <Field label="Задоцнет" value={pawn.daysOverdue > 0 ? `${pawn.daysOverdue} дена` : "—"} />
                        </Section>
                    </>
                )}

                {sale && (
                    <>
                        {divider}
                        <Section icon={<Tag size={20} />} title="Продажба">
                            <Field label="Продажба" value={`#${sale.id}`} />
                            <Field label="Откупна цена" value={money(sale.purchasePrice)} />
                            <Field label="Продажна цена" value={money(sale.salePrice)} />
                            <Field label="Профит" value={sale.profit == null ? "—" : <span className={sale.profit < 0 ? "text-red-500" : "text-green"}>{money(sale.profit)}</span>} />
                            <Field label="Статус" value={SALE_STATUS_MK[sale.status] ?? sale.status} />
                            <Field label="Продаден на" value={date(sale.soldAt)} />
                        </Section>
                    </>
                )}

                {expense && (
                    <>
                        {divider}
                        <Section icon={<Wallet size={20} />} title="Расход">
                            <Field label="Расход" value={`#${expense.id}`} />
                            <Field label="Категорија" value={EXPENSE_CAT_MK[expense.category] ?? expense.category} />
                            <Field label="Износ" value={money(expense.amount)} />
                            <Field label="Датум" value={date(expense.date)} />
                            <Field label="Опис" value={expense.description || "—"} />
                            <Field label="Внесено од" value={expense.staffName || "—"} />
                        </Section>
                    </>
                )}

                {session && (
                    <>
                        {divider}
                        <Section icon={<Wallet size={20} />} title="Каса сесија">
                            <Field label="Сесија" value={`#${session.id}`} />
                            <Field label="Каса" value={session.cashRegisterCode} />
                            <Field label="Статус" value={SESSION_STATUS_MK[session.status] ?? session.status} />
                            <Field label="Отворена на" value={datetime(session.openedAt)} />
                            <Field label="Затворена на" value={datetime(session.closedAt)} />
                            <Field label="Почетно салдо" value={money(session.openingBalance)} />
                            <Field label="Тековно салдо" value={money(session.currentBalance)} />
                            <Field label="Завршно салдо" value={money(session.closingBalance)} />
                            <Field label="Очекувана провизија" value={money(session.expectedInterest)} />
                        </Section>
                    </>
                )}

                {customer && (
                    <>
                        {divider}
                        <Section icon={<User size={20} />} title="Клиент">
                            <Field label="Име и презиме" value={customer.fullName} />
                            <Field label="ЕМБГ" value={customer.nationalId} />
                            <Field label="Телефон" value={customer.phonePrimary} />
                            <Field label="Телефон 2" value={customer.phoneSecondary || "—"} />
                            <Field label="Адреса" value={customer.address} />
                            <Field label="Град" value={customer.city} />
                        </Section>
                    </>
                )}

                {item && (
                    <>
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
                    </>
                )}
            </div>
        </>,
        document.getElementById("portal")!
    );
}
