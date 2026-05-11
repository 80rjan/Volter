import React, { useState } from "react";
import ReactDom from "react-dom";
import axios from "axios";
import {
    UserRound, Euro, RotateCcw, X, CircleDollarSign, Printer,
    UserPen, Check, ArrowLeft, ArrowDownToLine, Laptop, Watch, Car, Coins
} from 'lucide-react';
import html2canvas from "html2canvas";
import jsPDF from "jspdf";
import DogovorZaZaem from "../documents/DogovorZaZaem.tsx";
import DogovorZaRacenZalog from "../documents/DogovorZaRacenZalog.tsx";
import Loading from "./Loading.tsx";
import AneksDogovorZaZaem from "../documents/AneksDogovorZaZaem.tsx";
import { PawnInfo } from "../types.ts";

interface Props {
    category: string;
    pawnInfo: PawnInfo;
    closeModal: (e: React.MouseEvent) => void;
    closePawn: () => void;
    continuePawn: () => void;
    movePawnToSale: () => void;
    oldPawn: any;
    refreshCashReg: () => void;
}

export default function ModalReadMorePawn({ category, pawnInfo, closeModal, closePawn, continuePawn, movePawnToSale, oldPawn, refreshCashReg }: Props) {
    const [modalPrintDocument, setModalPrintDocument] = React.useState(false);
    const [clientAddress, setClientAddress] = React.useState("");
    const [idCard, setIdCard] = React.useState("");
    const [pawnDescription, setPawnDescription] = React.useState("");
    const client = pawnInfo.client;
    const [pawn, setPawn] = useState({ ...pawnInfo.pawn, "Days Left": pawnInfo["Days Left"] });
    const [isDownloading, setIsDownloading] = React.useState(false);
    const hiddenDocRefLoan = React.useRef<HTMLDivElement>(null);
    const hiddenDocRefPawn = React.useRef<HTMLDivElement>(null);
    const hiddenDocRefAnnexLoan = React.useRef<HTMLDivElement>(null);
    const [isEditing, setIsEditing] = React.useState(false);
    const editPawn = React.useRef({
        pricePawned: pawn.price_pawned,
        provision: pawn.provision,
        description: pawn.description,
        goldGramsDiff: 0,
        totalDays: pawn.total_days,
    });
    const [isLoading, setIsLoading] = React.useState(false);
    const [whichDocToPrint, setWhichDocToPrint] = React.useState("insert");

    const getCat: Record<string, string> = {
        Electronics: "Електроника", Watch: "Часовници", Vehicle: "Возила", Gold: "Злато", Other: "Останато",
    };
    const getCatIcon: Record<string, React.ReactNode> = {
        Electronics: <Laptop size={32} />, Watch: <Watch size={32} />, Vehicle: <Car size={32} />,
        Gold: <Coins size={32} />, Other: <CircleDollarSign size={32} />,
    };

    const handlePrintDoc = async (ref: React.RefObject<HTMLDivElement | null>, isLoan: boolean, isAnnex: boolean) => {
        setIsDownloading(true);
        const element = ref.current;
        if (!element) return;
        const pdf = new jsPDF({ orientation: "portrait", unit: "mm", format: "a4" });
        try {
            const canvas = await html2canvas(element, { scale: 2, useCORS: true });
            const imgData = canvas.toDataURL("image/png");
            const imgWidth = 210;
            const pageHeight = 297;
            const imgHeight = (canvas.height * imgWidth) / canvas.width;
            let heightLeft = imgHeight;
            let position = 0;
            pdf.addImage(imgData, "PNG", 0, position, imgWidth, imgHeight);
            while (heightLeft > pageHeight) {
                position -= pageHeight;
                pdf.addPage();
                pdf.addImage(imgData, "PNG", 0, position, imgWidth, imgHeight);
                heightLeft -= pageHeight;
            }
            pdf.save(`${isAnnex ? "Aneks_za_dogovor_za_zaem" : isLoan ? "Dogovor_za_zaem" : "Dogovor_za_racen_zalog"}.pdf`);
        } catch (error) { console.error(error); }
        finally { setIsDownloading(false); }
    };

    const handleUpdatePawn = () => {
        setIsLoading(true);
        const tableName: Record<string, string> = {
            Electronics: "electronics_pawn", Watch: "watch_pawn", Vehicle: "vehicle_pawn", Gold: "gold_pawn", Other: "other_pawn",
        };
        axios.put(`http://localhost:3000/updatePawn`, {
            tableName: tableName[category], id: pawn.id,
            pricePawned: editPawn.current.pricePawned,
            provision: editPawn.current.provision,
            description: editPawn.current.description,
            goldGramsDiff: editPawn.current.goldGramsDiff,
            totalDays: editPawn.current.totalDays,
        })
            .then(res => {
                const data = res.data.pawn;
                const newDateToStr = new Date(new Date(pawn.date_to).getTime() + (data.total_days - pawn.total_days) * 86400000).toISOString();
                const newDaysLeft = pawn["Days Left"] + (data.total_days - pawn.total_days);
                oldPawn["About"] = data.description;
                oldPawn["Item Cost"] = data.price_pawned;
                oldPawn["Provision"] = data.provision;
                oldPawn["Total Days"] = data.total_days;
                oldPawn["Days Left"] = newDaysLeft;
                oldPawn["Valid Until"] = newDateToStr;
                editPawn.current = { ...editPawn.current, totalDays: data.total_days, goldGramsDiff: 0 };
                setPawn({ ...data, date_to: newDateToStr, "Days Left": newDaysLeft });
            })
            .catch(err => console.error("Error updating pawn " + err))
            .finally(() => { setIsEditing(false); setIsLoading(false); refreshCashReg(); });
    };

    const detailInput = "w-full text-base p-1 border-2 border-green rounded";
    const detailSelect = "w-full text-base p-1 border-2 border-green rounded";

    const pawnDetailRow = (label: string, value: React.ReactNode) => (
        <span className="flex flex-col gap-0 font-medium">
            <p className="font-normal text-[#666] -ml-1">{label}</p>
            {value}
        </span>
    );

    const renderDetails = () => {
        const common = [
            pawnDetailRow("Шифра на залог:", <p>{Number(pawn.id).toLocaleString("de-DE")}</p>),
            pawnDetailRow("Опис:", isEditing
                ? <input type="text" defaultValue={pawn.description} onChange={e => editPawn.current.description = e.target.value} className={detailInput} />
                : <p>{pawn.description}</p>),
            pawnDetailRow("Вредност на залогот:", isEditing
                ? <input type="number" defaultValue={pawn.price_pawned} onChange={e => editPawn.current.pricePawned = Number(e.target.value)} className={detailInput} />
                : <p>{Number(pawn.price_pawned).toLocaleString("de-DE")}</p>),
            pawnDetailRow("Рата за продолжување:", isEditing
                ? <input type="number" step="0.001" defaultValue={pawn.provision} onChange={e => editPawn.current.provision = Number(e.target.value)} className={detailInput} />
                : <p>{Number(pawn.provision).toLocaleString("de-DE")}</p>),
            pawnDetailRow("Цена за подигање:", <p>{Number(pawn.price_to_redeem).toLocaleString("de-DE")}</p>),
            pawnDetailRow("Провизија:", <p>{(Math.round((pawn.provision / pawn.price_pawned * 100) * 100) / 100).toLocaleString("de-DE")}%</p>),
            pawnDetailRow("Дневна провизија:", <p>{(Math.round((pawn.provision / pawn.price_pawned * 100) / pawn.total_days * 100) / 100).toLocaleString("de-DE")}%</p>),
            pawnDetailRow("Денови валидно:", isEditing
                ? <select defaultValue={pawn.total_days} onChange={e => editPawn.current.totalDays = Number(e.target.value)} className={detailSelect}><option value={15}>15</option><option value={30}>30</option></select>
                : <p>{pawn.total_days}</p>),
            pawnDetailRow("Валидно од:", <p>{pawn.date_from.substring(0, 10)}</p>),
            pawnDetailRow("Валидно до:", <p>{pawn.date_to.substring(0, 10)}</p>),
            pawnDetailRow("Преостанато:", <p>{pawn["Days Left"]}</p>),
        ];

        const catSpecific: React.ReactNode[] = [];
        if (category === 'Electronics' || category === 'Watch') {
            catSpecific.push(pawnDetailRow("Бренд:", <p>{pawn.brand}</p>));
            catSpecific.push(pawnDetailRow("Година:", <p>{pawn.year}</p>));
        } else if (category === 'Vehicle') {
            catSpecific.push(pawnDetailRow("Бренд:", <p>{pawn.brand}</p>));
            catSpecific.push(pawnDetailRow("Модел:", <p>{pawn.model}</p>));
            catSpecific.push(pawnDetailRow("Година:", <p>{pawn.year}</p>));
        } else if (category === 'Gold') {
            catSpecific.push(pawnDetailRow("Тип:", <p>{pawn.type}</p>));
            catSpecific.push(pawnDetailRow("Тежина во грам:", isEditing
                ? <input type="number" step="0.001" defaultValue={pawn.weight} onChange={e => editPawn.current.goldGramsDiff = Number((Number(e.target.value) - (pawn.weight ?? 0)).toFixed(3))} className={detailInput} />
                : <p>{Number(pawn.weight).toLocaleString("de-DE")}</p>));
            catSpecific.push(pawnDetailRow("Каратажа:", <p>{pawn.carats}</p>));
            catSpecific.push(pawnDetailRow("Цена по грам:", <p>{Number(Math.round(pawn.price_pawned / (pawn.weight ?? 1))).toLocaleString("de-DE")}</p>));
        }

        return [...catSpecific, ...common];
    };

    const actionBtnClass = "flex items-center gap-4 rounded px-8 py-2 text-xl text-white w-max transition-all duration-400 hover:scale-105 shadow-[0_2px_8px_rgba(0,0,0,0.3)] disabled:cursor-not-allowed disabled:opacity-40";

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" />
            <div className="flex flex-col items-center fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-4 pb-8 px-8 rounded-lg min-w-fit max-w-[90%]">
                <X size={32} className="ml-auto close-x-btn" onClick={closeModal as any} />
                {!modalPrintDocument ? (
                    <>
                        <div className="flex w-full justify-around items-stretch" style={{ gap: isEditing ? '4rem' : '0' }}>
                            {/* Client */}
                            <div className="flex flex-col gap-4">
                                <div className="flex items-center gap-2 text-2xl font-semibold">
                                    <UserRound size={32} />
                                    {client.name} <span className="text-base font-normal mt-1">({client.date_joined.split("T")[0]})</span>
                                </div>
                                <div className="flex flex-col gap-1">
                                    {[['Шифра на клиент:', String(client.id)], ['Ембг:', client.embg], ['Телефон 1:', client.telephone]].map(([lbl, val]) => (
                                        <span key={lbl} className="flex flex-col gap-0 font-medium">
                                            <p className="font-normal text-[#666] -ml-1">{lbl}</p>
                                            <p>{val}</p>
                                        </span>
                                    ))}
                                    {client.telephone_2 && client.telephone_2.trim() !== "" && (
                                        <span className="flex flex-col gap-0 font-medium">
                                            <p className="font-normal text-[#666] -ml-1">Телефон 2:</p>
                                            <p>{client.telephone_2}</p>
                                        </span>
                                    )}
                                    <span className="flex flex-col gap-0 font-medium">
                                        <p className="font-normal text-[#666] -ml-1">Град:</p>
                                        <p>{client.city}</p>
                                    </span>
                                </div>
                            </div>
                            <div className="w-px bg-black/20 rounded-full" />
                            {/* Pawn */}
                            <div className="flex flex-col gap-4">
                                <div className="flex items-center gap-2 text-2xl font-semibold">
                                    {getCatIcon[category]}{getCat[category]}
                                </div>
                                <div className="grid gap-1 gap-x-8 [grid-auto-flow:column] [grid-template-rows:repeat(4,max-content)] w-max">
                                    {renderDetails()}
                                </div>
                            </div>
                        </div>
                        <div className="flex gap-8 mt-8">
                            {isEditing ? (
                                <div className="flex gap-4 items-center">
                                    <button disabled={isLoading} className={`${actionBtnClass} bg-[#444]`} onClick={() => { editPawn.current = { pricePawned: pawn.price_pawned, provision: pawn.provision, description: pawn.description, goldGramsDiff: 0, totalDays: pawn.total_days }; setIsEditing(false); }}>
                                        <ArrowLeft size={32} /> Врати се назад
                                    </button>
                                    <button disabled={isLoading} className={`${actionBtnClass} bg-green`} onClick={handleUpdatePawn}>
                                        <Check size={32} /> Потврди промени
                                    </button>
                                    {isLoading && <Loading width={40} height={40} />}
                                </div>
                            ) : (
                                <>
                                    <button className={`${actionBtnClass} bg-[#444]`} onClick={() => { closePawn(); closeModal({} as any); }}><X size={32} /> Затвори Залог</button>
                                    <button className={`${actionBtnClass} bg-cta`} onClick={() => { continuePawn(); closeModal({} as any); }}><RotateCcw size={32} /> Продолжи Залог</button>
                                    <button className={`${actionBtnClass} bg-green`} onClick={() => { movePawnToSale(); closeModal({} as any); }}><Euro size={32} /> Премести Залог во Продажба</button>
                                    <UserPen size={40} color="#444" className="cursor-pointer transition-all duration-400 hover:scale-110" onClick={() => setIsEditing(true)} />
                                    <Printer size={40} color="#444" className="cursor-pointer transition-all duration-400 hover:scale-110" onClick={() => setModalPrintDocument(true)} />
                                </>
                            )}
                        </div>
                    </>
                ) : (
                    <>
                        <div style={{ height: "0px", overflow: "hidden" }}>
                            <DogovorZaZaem ref={hiddenDocRefLoan} fullName={client.name} city={client.city} address={clientAddress} embg={client.embg} idCard={idCard} telephone={client.telephone} moneyGiven={pawn.price_pawned} pawnDays={pawn.total_days} dateFrom={pawn.date_from.substring(0, 10)} dateTo={pawn.date_to.substring(0, 10)} />
                        </div>
                        <div style={{ height: "0px", overflow: "hidden" }}>
                            <DogovorZaRacenZalog ref={hiddenDocRefPawn} fullName={client.name} city={client.city} address={clientAddress} embg={client.embg} idCard={idCard} telephone={client.telephone} moneyGiven={pawn.price_pawned} pawnDays={pawn.total_days} dateFrom={pawn.date_from.substring(0, 10)} dateTo={pawn.date_to.substring(0, 10)} pawnInfo={pawnDescription} />
                        </div>
                        <div style={{ height: "0px", overflow: "hidden" }}>
                            <AneksDogovorZaZaem ref={hiddenDocRefAnnexLoan} fullName={client.name} city={client.city} address={clientAddress} embg={client.embg} idCard={idCard} telephone={client.telephone} moneyGiven={pawn.price_pawned} pawnDays={pawn.total_days} dateFrom={pawn.date_from.substring(0, 10)} dateTo={pawn.date_to.substring(0, 10)} />
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
                                <button onClick={() => setModalPrintDocument(false)} className={`${actionBtnClass} bg-[#444]`}><ArrowLeft size={32} /> Врати се назад</button>
                                <button type="button" className={`${actionBtnClass} bg-green`} onClick={() => {
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
