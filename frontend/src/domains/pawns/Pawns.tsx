import React, { useState } from "react";
import PawnRow from "./Pawn.tsx";
import {Plus, ChevronUp, ChevronDown, Minus, Handshake, Banknote, Percent, Gem, Coins} from "lucide-react";
import ModalAddNewPawn from "./ModalAddNewPawn.tsx";
import CashRegister from "../../shared/components/CashRegister.tsx";
import MonthlyProfitBar from "../../shared/components/MonthlyProfitBar.tsx";
import Loading from "../../shared/components/Loading.tsx";
import { usePawns, SORT_FIELD } from "./usePawns.ts";
import { useAuth } from "../../GlobalContext.tsx";
import { useTeam } from "../../shared/utils/useTeam.ts";

const filterSelectOptions = [
    { value: "Gold", label: "Залог злато" },
    { value: "Electronics", label: "Залог електроника" },
    { value: "Watch", label: "Залог часовници" },
    { value: "Vehicle", label: "Залог возила" },
    { value: "Other", label: "Залог останато" },
];

const statusOptions = [
    { value: "ACTIVE", label: "Активни" },
    { value: "REDEEMED", label: "Откупени" },
    { value: "FORFEITED", label: "Во продажба" },
    { value: "", label: "Сите статуси" },
];

const cols = "grid-cols-[3rem_1.5fr_1fr_2fr_repeat(4,1fr)_1.5fr_0.5fr]";

const headerItems: [string, string][] = [
    ["Код", "Client Id"], ["Име", "Name"], ["Категорија", "Category"],
    ["Опис", "About"], ["Вредност", "Item Cost"], ["Провизија", "Provision"], ["Рок", "Days Left"],
];

export default function Pawns() {
    const { can } = useAuth();
    const [modalAddNewPawn, setModalAddNewPawn] = useState(false);

    const team = useTeam();
    const {
        allPawns, summary, loading,
        searchByCategory, setSearchByCategory,
        searchByStatus, setSearchByStatus,
        searchByStaff, setSearchByStaff,
        setSearchByName, setSearchByEmbg, setSearchByTel,
        scrollablePawnsRef, sorts, handleOrder,
        onRefresh, onRefreshCashReg,
        refreshDependency, refreshCashRegDependency,
    } = usePawns();

    const inputClass = "bg-white border-none rounded text-xs font-medium px-2 py-2 shadow-sm grow basis-[calc(50%_-_0.25rem)] xl:basis-0 max-lg:landscape:basis-0 min-w-0";

    return (
        <div className="h-screen flex lg:pl-16 pt-12 lg:pt-0">
            <div className="flex flex-col px-3 md:px-8 pt-2 max-lg:landscape:pt-1 gap-3 max-lg:landscape:gap-1 flex-1 overflow-hidden">
                <div className="flex flex-wrap xl:flex-nowrap max-lg:landscape:flex-nowrap justify-between w-full gap-2 xl:gap-6 max-lg:landscape:gap-1">
                    <select
                        className={inputClass}
                        value={searchByStatus}
                        onChange={e => setSearchByStatus(e.target.value)}
                    >
                        {statusOptions.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
                    </select>
                    <select
                        className={inputClass}
                        style={{ color: searchByCategory === "" ? "#888" : "#000" }}
                        onChange={e => setSearchByCategory(e.target.value)}
                    >
                        <option style={{ color: "#888" }} value="">Пребарубај по тип</option>
                        {filterSelectOptions.map(o => <option style={{ color: "#111" }} key={o.value} value={o.value}>{o.label}</option>)}
                    </select>
                    <input className={inputClass} type="search" placeholder="Пребарувај по име" onChange={e => setSearchByName(e.target.value)} />
                    <input className={inputClass} type="search" placeholder="Пребарувај по ембг" onChange={e => setSearchByEmbg(e.target.value)} />
                    <input className={inputClass} type="search" placeholder="Пребарувај по телефон" onChange={e => setSearchByTel(e.target.value)} />
                    <select
                        className={inputClass}
                        style={{ color: searchByStaff === "" ? "#888" : "#000" }}
                        value={searchByStaff}
                        onChange={e => setSearchByStaff(e.target.value)}
                    >
                        <option style={{ color: "#888" }} value="">Сите вработени</option>
                        {team.map(s => <option style={{ color: "#111" }} key={s.id} value={s.id}>{s.fullName}</option>)}
                    </select>
                    {can("PAWN_WRITE") && (
                        <button
                            onClick={() => setModalAddNewPawn(true)}
                            className="group relative overflow-hidden flex shrink-0 items-center justify-center gap-2 bg-green xl:h-full xl:w-auto max-lg:landscape:h-full max-lg:landscape:w-auto text-white rounded px-5 py-2 whitespace-nowrap text-xs font-semibold shadow-[4px_2px_6px_rgba(0,0,0,0.2)] transition-all duration-300 hover:scale-105"
                        >
                            {/* light sweep on hover */}
                            <span className="pointer-events-none absolute inset-0 -translate-x-full bg-gradient-to-r from-transparent via-white/25 to-transparent transition-transform duration-700 ease-out group-hover:translate-x-[180%]" />
                            <Plus size={16} color="white" strokeWidth={3} className="transition-transform duration-500 group-hover:rotate-90" />
                            Внеси Нов Залог
                        </button>
                    )}
                    {modalAddNewPawn && <ModalAddNewPawn closeModal={() => setModalAddNewPawn(false)} refresh={onRefresh} />}
                </div>

                <div className="flex flex-col bg-white rounded-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] overflow-x-auto flex-1 min-h-0">
                    <div className={`grid place-items-center ${cols} min-w-[880px] md:min-w-0 px-2 py-2 border-b-2 border-black/20 text-[#eee] bg-[#666]`}>
                        {headerItems.map(([label, key]) => {
                            const i = sorts.findIndex(s => s.key === key);
                            const active = i !== -1;
                            return (
                                <div key={key} title="Кликни за подредување · Shift+клик за повеќекратно подредување"
                                     className="text-xs font-medium flex items-center gap-1 cursor-pointer select-none"
                                     onClick={e => handleOrder(key, e.shiftKey)}>
                                    {label}
                                    {active
                                        ? (sorts[i].dir === "DESC" ? <ChevronDown size={14} /> : <ChevronUp size={14} />)
                                        : <Minus size={14} />}
                                    {active && sorts.length > 1 && <span className="text-[10px] leading-none text-white/70">{i + 1}</span>}
                                </div>
                            );
                        })}
                        <div className="text-xs font-medium cursor-default">Валидно до</div>
                        <div className="text-xs font-medium cursor-default">Акции</div>
                        <div className="text-xs font-medium cursor-default">Повеќе</div>
                    </div>

                    <div ref={scrollablePawnsRef} className="overflow-y-auto overflow-x-hidden flex-1 scrollbar-thin min-w-[880px] md:min-w-0">
                        {loading ? <Loading /> : allPawns.map((pawn, index) => (
                            <PawnRow
                                key={index}
                                pawn={pawn}
                                refresh={onRefresh}
                                isOdd={index % 2 !== 0}
                                refreshCashReg={onRefreshCashReg}
                            />
                        ))}
                    </div>
                </div>

                {/* Totals for the currently filtered (active) pawns. */}
                <div className="flex flex-wrap w-full justify-between items-center gap-x-6 gap-y-1 bg-[#f4f4f4] border border-black/10 rounded-lg px-3 md:px-5 py-1.5 max-lg:landscape:py-0.5 text-xs text-[#666]">
                    <span className="flex items-center gap-1.5"><Handshake size={15} className="text-green" /> Залози: <b className="text-[#333]">{summary.count}</b></span>
                    <span className="flex items-center gap-1.5"><Banknote size={15} className="text-green" /> Дадени пари: <b className="text-[#333]">{summary.totalPrincipal.toLocaleString("de-DE")} ден</b></span>
                    <span className="flex items-center gap-1.5"><Percent size={15} className="text-green" /> Камата за наплата: <b className="text-[#333]">{summary.totalInterest.toLocaleString("de-DE")} ден</b></span>
                    <span className="flex items-center gap-1.5"><Coins size={15} className="text-green" /> Злато: <b className="text-[#333]">{summary.totalGoldGrams.toLocaleString("de-DE", { maximumFractionDigits: 2 })} гр</b></span>
                </div>

                {/* Profit since the first of the month: pawn provision + sale profit. */}
                <MonthlyProfitBar refreshDependency={refreshDependency} />

                <CashRegister refreshDependency={refreshDependency} refreshDependencyAdjustPawn={refreshCashRegDependency} />
            </div>
        </div>
    );
}
