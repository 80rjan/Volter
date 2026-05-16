import { useState } from "react";
import type { ReactNode } from "react";
import ReactDom from "react-dom";
import {
    UserRound, Euro, RotateCcw, X, CircleDollarSign, Printer,
    UserPen, Check, ArrowLeft, ArrowDownToLine, Laptop, Watch, Car, Coins
} from 'lucide-react';
import DogovorZaZaem from "./documents/DogovorZaZaem.tsx";
import DogovorZaRacenZalog from "./documents/DogovorZaRacenZalog.tsx";
import AneksDogovorZaZaem from "./documents/AneksDogovorZaZaem.tsx";
import Loading from "../../shared/components/Loading.tsx";
import {
    PawnDetailed, PawnRow,
    GoldItemDetailed, ElectronicItemDetailed, WatchItemDetailed, VehicleItemDetailed, OtherItemDetailed,
} from "./types.ts";
import { useModalReadMorePawn } from "./useModalReadMorePawn.ts";

const ITEM_TYPE_TO_CATEGORY: Record<string, string> = {
    GOLD: 'Gold', ELECTRONIC: 'Electronics', VEHICLE: 'Vehicle', WATCH: 'Watch', OTHER: 'Other',
};
const CAT_LABEL: Record<string, string> = {
    Gold: "Злато", Electronics: "Електроника", Watch: "Часовници", Vehicle: "Возила", Other: "Останато",
};
const CAT_ICON: Record<string, ReactNode> = {
    Electronics: <Laptop size={28} />, Watch: <Watch size={28} />, Vehicle: <Car size={28} />,
    Gold: <Coins size={28} />, Other: <CircleDollarSign size={28} />,
};
const RISK_LEVEL: Record<string, { label: string; cls: string }> = {
    LOW: { label: 'Низок', cls: 'text-green font-semibold' },
    MEDIUM: { label: 'Среден', cls: 'text-yellow-600 font-semibold' },
    HIGH: { label: 'Висок', cls: 'text-red-500 font-semibold' },
};
const PAWN_STATUS: Record<string, string> = {
    ACTIVE: 'Активен', REDEEMED: 'Подигнат', FORFEITED: 'Истечен', RENEWED: 'Продолжен',
};
const ITEM_STATUS: Record<string, string> = {
    IN_PAWN: "Во залог" , REDEEMED: 'Подигнат', FOR_SALE: 'За продажба', SOLD: 'Продаден',
};

interface Props {
    pawnDetailed: PawnDetailed;
    closeModal: (e: React.MouseEvent) => void;
    closePawn: () => void;
    continuePawn: () => void;
    movePawnToSale: () => void;
    oldPawnRow: PawnRow;
    refreshCashReg: () => void;
}

type Field = { label: string; value: ReactNode };

export default function ModalReadMorePawn({ pawnDetailed, closeModal, closePawn, continuePawn, movePawnToSale, oldPawnRow, refreshCashReg }: Props) {
    const [clientAddress, setClientAddress] = useState("");
    const [idCard, setIdCard] = useState("");
    const [pawnDescription, setPawnDescription] = useState("");

    const {
        pawn, isEditing, setIsEditing, cancelEdit,
        isLoading, isDownloading,
        modalPrintDocument, setModalPrintDocument,
        whichDocToPrint, setWhichDocToPrint,
        editRef,
        hiddenDocRefLoan, hiddenDocRefPawn, hiddenDocRefAnnexLoan,
        handleUpdatePawn, handlePrintDoc,
    } = useModalReadMorePawn(pawnDetailed, oldPawnRow, refreshCashReg);

    const category = ITEM_TYPE_TO_CATEGORY[pawn.item.itemType] ?? 'Other';
    const { customer, item } = pawn;
    const risk = RISK_LEVEL[customer.riskLevel] ?? { label: customer.riskLevel, cls: '' };

    const inp = "w-full text-base p-1 border-2 border-green rounded";
    const sel = "w-full text-base p-1 border-2 border-green rounded";
    const bool = (v: boolean) => <p>{v ? "Да" : "Не"}</p>;
    const dateStr = (v: string | null | undefined) => <p>{v ? String(v).substring(0, 10) : "—"}</p>;

    const pawnFields: Field[] = [
        { label: "Шифра:", value: <p>{pawn.id}</p> },
        { label: "Статус:", value: <p>{PAWN_STATUS[pawn.status] ?? pawn.status}</p> },
        { label: "Опис:", value: isEditing
            ? <input type="text" defaultValue={item.description} onChange={e => { editRef.current.description = e.target.value; }} className={inp} />
            : <p>{item.description}</p> },
        { label: "Вредност:", value: isEditing
            ? <input type="number" defaultValue={pawn.amount} onChange={e => { editRef.current.amount = Number(e.target.value); }} className={inp} />
            : <p>{Number(pawn.amount).toLocaleString("de-DE")}</p> },
        { label: "Провизија:", value: isEditing
            ? <input type="number" step="0.001" defaultValue={pawn.interest} onChange={e => { editRef.current.interest = Number(e.target.value); }} className={inp} />
            : <p>{Number(pawn.interest).toLocaleString("de-DE")} <span className="text-[#444] text-sm">/ {(Math.round(pawn.interest / pawn.amount * 10000) / 100).toLocaleString("de-DE")}%</span></p> },
        { label: "Цена за подигање:", value: <p>{Number(pawn.amount + pawn.interest).toLocaleString("de-DE")}</p> },
        { label: "Дневна провизија %:", value: <p>{(Math.round(pawn.interest / pawn.amount * 10000 / pawn.defaultDurationDays) / 100).toLocaleString("de-DE")}%</p> },
        { label: "Денови валидно:", value: isEditing
            ? <select defaultValue={pawn.defaultDurationDays} onChange={e => { editRef.current.defaultDurationDays = Number(e.target.value); }} className={sel}><option value={15}>15</option><option value={30}>30</option></select>
            : <p>{pawn.defaultDurationDays}</p> },
        { label: "Валидно од:", value: dateStr(pawn.issueDate) },
        { label: "Валидно до:", value: dateStr(pawn.maturityDate) },
        { label: "Преостанато:", value: <p>{pawn.daysLeft} ден.</p> },
        { label: "Внесено на:", value: dateStr(pawn.createdAt) },
        { label: "Ажурирано на:", value: dateStr(pawn.updatedAt) },
    ];

    const itemFields = (): Field[] => {
        const common: Field[] = [
            { label: "Статус на предмет:", value: <p>{ITEM_STATUS[item.itemStatus] ?? item.itemStatus}</p> },
            { label: "Внесено:", value: dateStr(item.createdAt) },
            { label: "Ажурирано:", value: dateStr(item.updatedAt) },
        ];

        if (category === 'Gold') {
            const g = item as GoldItemDetailed;
            return [
                { label: "Тип на предмет:", value: <p>{g.pieceType}</p> },
                { label: "Каратажа:", value: <p>{g.carats.split('_')[1]}</p> },
                { label: "Тежина (г):", value: isEditing
                    ? <input type="number" step="0.001" defaultValue={g.weightGrams} onChange={e => { editRef.current.goldGramsDiff = Number((Number(e.target.value) - Number(g.weightGrams)).toFixed(3)); }} className={inp} />
                    : <p>{Number(g.weightGrams).toLocaleString("de-DE")}</p> },
                { label: "Цена по грам:", value: <p>{Number(g.pricePerGram).toLocaleString("de-DE")}</p> },
                ...common,
            ];
        }
        if (category === 'Electronics') {
            const e = item as ElectronicItemDetailed;
            return [
                { label: "Бренд:", value: <p>{e.brand}</p> },
                { label: "Категорија:", value: <p>{e.category}</p> },
                { label: "Година:", value: <p>{e.year}</p> },
                ...common,
            ];
        }
        if (category === 'Watch') {
            const w = item as WatchItemDetailed;
            return [
                { label: "Бренд:", value: <p>{w.brand}</p> },
                { label: "Модел:", value: <p>{w.model}</p> },
                { label: "Материјал:", value: <p>{w.material}</p> },
                { label: "Година:", value: <p>{w.year}</p> },
                { label: "Оригинална кутија:", value: bool(w.originalBoxIncluded) },
                { label: "Оригинална документ.:", value: bool(w.originalPapersIncluded) },
                { label: "Гарантна карта:", value: bool(w.warrantyCardIncluded) },
                { label: "Истек на гаранција:", value: dateStr(w.warrantyExpirationDate) },
                { label: "Функционален:", value: bool(w.functional) },
                { label: "Потребен сервис:", value: bool(w.serviceRequired) },
                ...common,
            ];
        }
        if (category === 'Vehicle') {
            const v = item as VehicleItemDetailed;
            return [
                { label: "Бренд:", value: <p>{v.brand}</p> },
                { label: "Модел:", value: <p>{v.model}</p> },
                { label: "Година:", value: <p>{v.year}</p> },
                { label: "Рег. таблица:", value: <p>{v.registrationNumber}</p> },
                { label: "Тип на возило:", value: <p>{v.vehicleType}</p> },
                { label: "Километража:", value: <p>{Number(v.mileage).toLocaleString("de-DE")} км</p> },
                { label: "Историја сервис:", value: bool(v.serviceHistoryAvailable) },
                { label: "Последен сервис:", value: dateStr(v.lastServiceDate) },
                { label: "Истек регистрација:", value: dateStr(v.registrationExpiryDate) },
                { label: "Број на клучеви:", value: <p>{v.numberOfKeys}</p> },
                ...common,
            ];
        }
        const o = item as OtherItemDetailed;
        return [
            { label: "Категорија:", value: <p>{o.category}</p> },
            ...common,
        ];
    };

    const actionBtn = "flex items-center gap-4 rounded px-8 py-2 text-xl text-white w-max transition-all duration-400 hover:scale-105 shadow-[0_2px_8px_rgba(0,0,0,0.3)] disabled:cursor-not-allowed disabled:opacity-40";

    const FieldList = ({ fields }: { fields: Field[] }) => (
        <div className="grid grid-cols-3 gap-x-8 gap-y-1">
            {fields.map(({ label, value }) => (
                <span key={label} className="flex flex-col font-medium">
                    <p className="font-normal text-[#666] text-xs">{label}</p>
                    {value}
                </span>
            ))}
        </div>
    );

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" />
            <div className="flex flex-col items-center fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-4 pb-8 px-8 rounded-lg min-w-fit w-[90%] max-h-[90vh] overflow-y-auto scrollbar-thin">
                <X size={32} className="ml-auto close-x-btn" onClick={closeModal as any} />
                {!modalPrintDocument ? (
                    <>
                        <div className="flex w-full justify-around items-start gap-10">
                            {/* Customer */}
                            <div className="flex flex-col gap-3 min-w-56">
                                <div className="flex items-start gap-2 text-2xl font-semibold">
                                    <UserRound size={28} className="mt-1 shrink-0" />
                                    <div>
                                        <p>{customer.name}</p>
                                        <p className="text-xs font-normal text-[#666]">Клиент од {String(pawn.createdAt).substring(0, 10)}</p>
                                    </div>
                                </div>
                                <div className="flex flex-col gap-1">
                                    {([
                                        ["ЕМБГ:", customer.embg],
                                        ["Телефон 1:", customer.phoneNumber],
                                        ...(customer.reservePhoneNumber ? [["Телефон 2:", customer.reservePhoneNumber]] : []),
                                        ["Адреса:", customer.address],
                                        ["Град:", customer.city],
                                    ] as [string, string][]).map(([lbl, val]) => (
                                        <span key={lbl} className="flex flex-col font-medium">
                                            <p className="font-normal text-[#666] text-xs">{lbl}</p>
                                            <p>{val}</p>
                                        </span>
                                    ))}
                                    <span className="flex flex-col font-medium">
                                        <p className="font-normal text-[#666] text-xs">Ниво на ризик:</p>
                                        <p className={risk.cls}>{risk.label}</p>
                                    </span>
                                </div>
                                <div className="border-t border-black/20 pt-2 flex flex-col gap-1">
                                    <p className="text-xs font-semibold text-[#444] mb-1">Статистики</p>
                                    {([
                                        ["Вкупно залози:", customer.totalPawnCount],
                                        ["Вкупно продажби:", customer.totalSaleCount],
                                        ["Навремени продолж.:", customer.onTimeRenewalCount],
                                        ["Доцни продолж.:", customer.lateRenewalCount],
                                        ["Просечно задоцн. (ден.):", Number(customer.avgDaysLate ?? 0).toFixed(1)],
                                        ["Подигања:", customer.redeemCount],
                                        ["Запленувања:", customer.forfeitCount],
                                    ] as [string, string | number][]).map(([lbl, val]) => (
                                        <div key={lbl} className="flex justify-between gap-4">
                                            <p className="text-xs text-[#666]">{lbl}</p>
                                            <p className="text-xs font-semibold">{val}</p>
                                        </div>
                                    ))}
                                </div>
                            </div>

                            <div className="w-px bg-black/20 rounded-full self-stretch" />

                            {/* Pawn + Item */}
                            <div className="flex flex-col gap-4 flex-1">
                                <div className="flex items-center gap-2 text-2xl font-semibold">
                                    {CAT_ICON[category]} {CAT_LABEL[category]}
                                </div>
                                <div className="flex flex-col gap-2 ">
                                    <p className="text-sm underline font-bold text-[#444]">Залог</p>
                                    <FieldList fields={pawnFields} />
                                </div>
                                <div className="flex flex-col gap-2 border-t border-black/20 pt-2">
                                    <p className="text-sm underline font-bold text-[#444]">Предмет</p>
                                    <FieldList fields={itemFields()} />
                                </div>
                            </div>
                        </div>

                        <div className="flex gap-8 mt-8">
                            {isEditing ? (
                                <div className="flex gap-4 items-center">
                                    <button disabled={isLoading} className={`${actionBtn} bg-[#444]`} onClick={cancelEdit}>
                                        <ArrowLeft size={32} /> Врати се назад
                                    </button>
                                    <button disabled={isLoading} className={`${actionBtn} bg-green`} onClick={handleUpdatePawn}>
                                        <Check size={32} /> Потврди промени
                                    </button>
                                    {isLoading && <Loading width={40} height={40} />}
                                </div>
                            ) : (
                                <>
                                    <button className={`${actionBtn} bg-[#444]`} onClick={() => { closePawn(); closeModal({} as any); }}><X size={32} /> Затвори Залог</button>
                                    <button className={`${actionBtn} bg-cta`} onClick={() => { continuePawn(); closeModal({} as any); }}><RotateCcw size={32} /> Продолжи Залог</button>
                                    <button className={`${actionBtn} bg-green`} onClick={() => { movePawnToSale(); closeModal({} as any); }}><Euro size={32} /> Премести Залог во Продажба</button>
                                    <UserPen size={40} color="#444" className="cursor-pointer transition-all duration-400 hover:scale-110" onClick={() => setIsEditing(true)} />
                                    <Printer size={40} color="#444" className="cursor-pointer transition-all duration-400 hover:scale-110" onClick={() => setModalPrintDocument(true)} />
                                </>
                            )}
                        </div>
                    </>
                ) : (
                    <>
                        <div style={{ height: "0px", overflow: "hidden" }}>
                            <DogovorZaZaem ref={hiddenDocRefLoan} fullName={customer.name} city={customer.city} address={clientAddress} embg={customer.embg} idCard={idCard} telephone={customer.phoneNumber} moneyGiven={pawn.amount} pawnDays={pawn.defaultDurationDays} dateFrom={String(pawn.issueDate).substring(0, 10)} dateTo={String(pawn.maturityDate).substring(0, 10)} />
                        </div>
                        <div style={{ height: "0px", overflow: "hidden" }}>
                            <DogovorZaRacenZalog ref={hiddenDocRefPawn} fullName={customer.name} city={customer.city} address={clientAddress} embg={customer.embg} idCard={idCard} telephone={customer.phoneNumber} moneyGiven={pawn.amount} pawnDays={pawn.defaultDurationDays} dateFrom={String(pawn.issueDate).substring(0, 10)} dateTo={String(pawn.maturityDate).substring(0, 10)} pawnInfo={pawnDescription} />
                        </div>
                        <div style={{ height: "0px", overflow: "hidden" }}>
                            <AneksDogovorZaZaem ref={hiddenDocRefAnnexLoan} fullName={customer.name} city={customer.city} address={clientAddress} embg={customer.embg} idCard={idCard} telephone={customer.phoneNumber} moneyGiven={pawn.amount} pawnDays={pawn.defaultDurationDays} dateFrom={String(pawn.issueDate).substring(0, 10)} dateTo={String(pawn.maturityDate).substring(0, 10)} />
                        </div>
                        <form className="flex flex-col gap-1 mt-4">
                            <span className="flex flex-row gap-4 items-center text-xl">
                                Печати документи за
                                <select value={whichDocToPrint} onChange={e => setWhichDocToPrint(e.target.value)} className="p-1 text-base border-2 border-green rounded bg-white">
                                    <option value="insert">Внес на залог</option>
                                    <option value="continue">Продолжување на залог</option>
                                </select>
                            </span>
                            {whichDocToPrint === "insert" && (
                                <span className="flex flex-col gap-1 text-xl">
                                    Внеси опис на залогот:
                                    <input onChange={e => setPawnDescription(e.target.value)} className="text-base p-1 border-2 border-green rounded" />
                                </span>
                            )}
                            <span className="flex flex-col gap-1 text-xl">
                                Внеси адреса и број на адреса:
                                <input onChange={e => setClientAddress(e.target.value)} className="text-base p-1 border-2 border-green rounded" />
                            </span>
                            <span className="flex flex-col gap-1 text-xl">
                                Внеси број на лична карта:
                                <input onChange={e => setIdCard(e.target.value)} className="text-base p-1 border-2 border-green rounded" />
                            </span>
                            <div className="flex gap-8 mt-4">
                                <button type="button" onClick={() => setModalPrintDocument(false)} className={`${actionBtn} bg-[#444]`}><ArrowLeft size={32} /> Врати се назад</button>
                                <button type="button" className={`${actionBtn} bg-green`} onClick={() => {
                                    (async () => {
                                        try {
                                            if (whichDocToPrint === "insert") {
                                                await handlePrintDoc(hiddenDocRefLoan, true, false);
                                                await handlePrintDoc(hiddenDocRefPawn, false, false);
                                            } else {
                                                await handlePrintDoc(hiddenDocRefAnnexLoan, false, true);
                                            }
                                        } catch (err) { console.error("Error downloading pdf:", err); }
                                    })();
                                }}>
                                    <ArrowDownToLine size={32} /> {isDownloading ? "Се Симнува..." : "Симни"}
                                </button>
                            </div>
                        </form>
                    </>
                )}
            </div>
        </>,
        document.getElementById("portal")!
    );
}
