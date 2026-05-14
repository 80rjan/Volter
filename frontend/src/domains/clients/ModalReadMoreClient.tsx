import React, { useEffect } from "react";
import ReactDom from "react-dom";
import { UserRound, X, DollarSign, Sigma, History } from "lucide-react";
import axios from "axios";
import Loading from "../../shared/components/Loading.tsx";
import { ClientRow } from "./types.ts";
import { API_BASE } from "../../shared/api/config.ts";

interface Props {
    client: ClientRow;
    updateTelephones: (tel1: string, tel2: string) => void;
    closeModal: () => void;
}

export default function ModalReadMoreClient({ client, updateTelephones, closeModal }: Props) {
    const [pawns, setPawns] = React.useState<any[]>([]);
    const [transactions, setTransactions] = React.useState<any[]>([]);
    const [loading, setLoading] = React.useState(false);
    const [updateTelephone, setUpdateTelephone] = React.useState(false);
    const [telephones, setTelephones] = React.useState({
        telephone1: client["Telephone 1"],
        telephone2: client["Telephone 2"],
    });
    const [loadingTelephoneUpdate, setLoadingTelephoneUpdate] = React.useState(false);

    const getCat: Record<string, string> = {
        Electronics: "Електроника", Watch: "Часовници", Vehicle: "Возила",
        Gold: "Злато", Other: "Останато", Sale: "Продажба",
        Insert: "Внес Каса", Remove: "Излез Каса", Expense: "Расходи",
    };

    const updateTelephoneNumbers = () => {
        setLoadingTelephoneUpdate(true);
        // TODO: No customer update endpoint in Spring Boot backend yet
        axios.post(`${API_BASE}/customers/${client.Id}/telephones`, {
            phoneNumber: telephones.telephone1,
            reservePhoneNumber: telephones.telephone2,
        })
            .then(() => {
                updateTelephones(telephones.telephone1, telephones.telephone2);
                setUpdateTelephone(false);
            })
            .catch(err => console.error("Error updating telephones:", err))
            .finally(() => setLoadingTelephoneUpdate(false));
    };

    useEffect(() => {
        setLoading(true);
        // TODO: No customer details endpoint in Spring Boot backend yet
        axios.get(`${API_BASE}/customers/${client.Id}/details`)
            .then(res => { setPawns(res.data.pawns ?? []); setTransactions(res.data.transactions ?? []); })
            .catch(() => { setPawns([]); setTransactions([]); })
            .finally(() => setLoading(false));
    }, []);

    const pawnHeaderCols = "grid-cols-[1.5fr_3fr_repeat(3,1fr)_1.5fr]";
    const txHeaderCols = "grid-cols-[1.5fr_3fr_repeat(4,1fr)_1.5fr]";

    const tableHeader = (cols: string, headers: string[]) => (
        <div className={`grid place-items-center text-center ${cols} px-2 py-2 border-b-2 border-black/20 text-[#eee] bg-[#666]`}>
            {headers.map(h => <div key={h} className="text-xs font-medium flex items-center">{h}</div>)}
        </div>
    );

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" />
            <div className="flex flex-col items-center fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-4 rounded-lg min-w-[95%] overflow-hidden max-h-[90%]">
                <X size={32} className="ml-auto close-x-btn shrink-0" onClick={closeModal} />

                {/* Client info */}
                <div className="flex flex-col gap-1 text-3xl font-semibold w-full">
                    <div className="flex items-center gap-2 whitespace-nowrap">
                        <UserRound size={32} />
                        {client.Name}
                        <div className="text-xl font-normal mt-1">({client["Date Joined"].split("T")[0]})</div>
                    </div>
                    <div className="flex items-center gap-4 whitespace-nowrap h-full">
                        {[['Град:', client.City], ['Ембг:', client.Embg]].map(([lbl, val]) => (
                            <span key={lbl} className="flex items-center gap-2 text-base font-normal">
                                <p>{lbl}</p><p>{val}</p>
                            </span>
                        ))}
                        <span className="flex items-center gap-2 text-base font-normal">
                            <p>Телефон:</p>
                            {loadingTelephoneUpdate ? <Loading width={32} height={32} /> : updateTelephone ? (
                                <div className="flex items-center gap-2">
                                    <input type="text" defaultValue={client["Telephone 1"]} onChange={e => setTelephones({ ...telephones, telephone1: e.target.value })} className="w-full text-base p-1 border-2 border-green rounded" />
                                    <input type="text" defaultValue={client["Telephone 2"]} onChange={e => setTelephones({ ...telephones, telephone2: e.target.value })} className="w-full text-base p-1 border-2 border-green rounded" />
                                    <button onClick={updateTelephoneNumbers} className="ml-1 h-full px-4 py-1 text-sm bg-green text-white rounded shadow-[0_2px_8px_rgba(0,0,0,0.3)] hover:scale-105 transition-all duration-300">Зачувај</button>
                                    <X className="close-x-btn" onClick={() => setUpdateTelephone(false)} />
                                </div>
                            ) : (
                                <>
                                    <p>{client["Telephone 1"]}{client["Telephone 2"].trim() !== "" ? ` / ${client["Telephone 2"]}` : ""}</p>
                                    <button onClick={() => setUpdateTelephone(true)} className="ml-1 px-4 py-1 text-sm bg-green text-white rounded shadow-[0_2px_8px_rgba(0,0,0,0.3)] hover:scale-105 transition-all duration-300">Измени/Додади телефон</button>
                                </>
                            )}
                        </span>
                    </div>
                </div>

                {/* Statistics */}
                <div className="flex justify-evenly items-center gap-8 my-8">
                    {[
                        { icon: <Sigma color="var(--grey)" size={40} />, val: client["Total Pawns"], label: 'Вкупно залози' },
                        { icon: <History color="var(--grey)" size={40} />, val: client["Active Pawns"], label: 'Активни залози' },
                        { icon: <DollarSign color="var(--grey)" size={40} />, val: client["Money Pawns"], label: 'Вредност на залози' },
                        { icon: <DollarSign color="var(--green)" size={40} />, val: client["Money Provision"], label: 'Очекуван приход' },
                    ].map(({ icon, val, label }) => (
                        <div key={label} className="flex items-center leading-none gap-4 whitespace-nowrap">
                            <div className="shrink-0">{icon}</div>
                            <div className="flex flex-col gap-1">
                                <h1 className="text-3xl">{Number(val).toLocaleString("de-DE")}</h1>
                                <p className="font-normal text-base">{label}</p>
                            </div>
                        </div>
                    ))}
                </div>

                {/* Tables */}
                <div className="grid grid-cols-2 gap-8 overflow-hidden">
                    <div className="flex flex-col overflow-hidden">
                        <h2 className="font-semibold text-xl mb-1">Залози</h2>
                        <div className="flex flex-col bg-white rounded-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] overflow-hidden">
                            {tableHeader(pawnHeaderCols, ['Категорија', 'Опис', 'Вредност', 'Провизија', 'Рок', 'Валидно до'])}
                            <div className="overflow-y-auto flex flex-col scrollbar-thin">
                                {loading ? Array.from({ length: 10 }).map((_, i) => (
                                    <div key={i} style={{ background: i % 2 === 1 ? '#f0f0f0' : '#fff' }} className="px-2 py-1">
                                        <div className="skeleton-shimmer" />
                                    </div>
                                )) : pawns.map((pawn, i) => (
                                    <div key={i} style={{ background: i % 2 === 1 ? '#f0f0f0' : '#fff' }} className={`grid place-items-center text-center ${pawnHeaderCols}`}>
                                        <span className="text-sm">{getCat[pawn.category]}</span>
                                        <span className="text-sm">{pawn.description}</span>
                                        <span className="text-sm">{Number(pawn.price_pawned).toLocaleString("de-DE")}</span>
                                        <span className="text-sm">{Number(pawn.provision).toLocaleString("de-DE")}</span>
                                        <span className={`text-sm font-normal ${pawn.days_left < 0 ? 'text-red-500' : 'text-green'}`}>{pawn.days_left}</span>
                                        <span className="text-sm whitespace-nowrap">{pawn.date_to.split("T")[0]}</span>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>
                    <div className="flex flex-col overflow-hidden">
                        <h2 className="font-semibold text-xl mb-1">Трансакции</h2>
                        <div className="flex flex-col bg-white rounded-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] overflow-hidden">
                            {tableHeader(txHeaderCols, ['Категорија', 'Опис', 'Дадено', 'Земено', 'Профит', 'Отстапување', 'Датум'])}
                            <div className="overflow-y-auto flex flex-col scrollbar-thin">
                                {loading ? Array.from({ length: 10 }).map((_, i) => (
                                    <div key={i} style={{ background: i % 2 === 1 ? '#f0f0f0' : '#fff' }} className="px-2 py-1">
                                        <div className="skeleton-shimmer" />
                                    </div>
                                )) : transactions.map((tx, i) => (
                                    <div key={i} style={{ background: i % 2 === 1 ? '#f0f0f0' : '#fff' }} className={`grid place-items-center text-center ${txHeaderCols}`}>
                                        <span className="text-sm">{getCat[tx.category]}</span>
                                        <span className="text-sm">{tx.description}</span>
                                        <span className="text-sm italic">{Number(tx.money_given).toLocaleString("de-DE")}</span>
                                        <span className="text-sm italic">{Number(tx.money_got).toLocaleString("de-DE")}</span>
                                        <span className="text-sm italic">{Number(tx.profit).toLocaleString("de-DE")}</span>
                                        <span className={`text-sm italic font-normal ${Number(tx.money_diff) === 0 ? '' : Number(tx.money_diff) < 0 ? 'text-red-500' : 'text-green'}`}>
                                            {Number(tx.money_diff).toLocaleString("de-DE")}
                                        </span>
                                        <span className="text-sm whitespace-nowrap">{tx.date.split("T")[0]}</span>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </>,
        document.getElementById("portal")!
    );
}
