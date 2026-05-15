import React, { useState } from "react";
import PawnRow from "./Pawn.tsx";
import Nav from "../../shared/components/Nav.tsx";
import { Plus, ChevronUp, ChevronDown, Minus } from "lucide-react";
import ModalAddNewPawn from "./ModalAddNewPawn.tsx";
import CashRegister from "../../shared/components/CashRegister.tsx";
import Loading from "../../shared/components/Loading.tsx";
import { usePawns, SORT_FIELD } from "./usePawns.ts";

const filterSelectOptions = [
    { value: "Gold", label: "Залог злато" },
    { value: "Electronics", label: "Залог електроника" },
    { value: "Watch", label: "Залог часовници" },
    { value: "Vehicle", label: "Залог возила" },
    { value: "Other", label: "Залог останато" },
];

const cols = "grid-cols-[3rem_1.5fr_1fr_2fr_repeat(4,1fr)_1.5fr_0.5fr]";

const headerItems: [string, string, number][] = [
    ["Ид", "Client Id", 0], ["Име", "Name", 1], ["Категорија", "Category", 2],
    ["Опис", "About", 3], ["Вредност", "Item Cost", 4], ["Провизија", "Provision", 5], ["Рок", "Days Left", 6],
];

function SortIcon({ dir }: { dir: number }) {
    return dir === 0 ? <Minus size={14} /> : dir === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />;
}

export default function Pawns() {
    const [modalAddNewPawn, setModalAddNewPawn] = useState(false);

    const {
        allPawns, loading,
        searchByCategory, setSearchByCategory,
        setSearchByName, setSearchByEmbg, setSearchByTel,
        scrollablePawnsRef, orderDirectionArr, handleOrder,
        onRefresh, onRefreshCashReg,
        refreshDependency, refreshCashRegDependency,
    } = usePawns();

    const inputClass = "border-none rounded text-sm font-medium w-1/5 p-2 shadow-[0_0_8px_rgba(0,0,0,0.2)]";

    return (
        <div className="h-screen grid grid-cols-[max(15%,240px)_auto]">
            <Nav />
            <div className="flex flex-col px-8 pt-2 gap-3 flex-1 overflow-hidden">
                <div className="flex justify-between w-full">
                    <select
                        className={inputClass}
                        style={{ color: searchByCategory === "" ? "#888" : "#000" }}
                        onChange={e => setSearchByCategory(e.target.value)}
                    >
                        <option style={{ color: "#888" }} value="">Пребарубај по</option>
                        {filterSelectOptions.map(o => <option style={{ color: "#111" }} key={o.value} value={o.value}>{o.label}</option>)}
                    </select>
                    <input className={inputClass} type="search" placeholder="Пребарувај по име" onChange={e => setSearchByName(e.target.value)} />
                    <input className={inputClass} type="search" placeholder="Пребарувај по ембг" onChange={e => setSearchByEmbg(e.target.value)} />
                    <input className={inputClass} type="search" placeholder="Пребарувај по телефон" onChange={e => setSearchByTel(e.target.value)} />
                    <button
                        className="flex items-center gap-2 bg-green h-fit text-white rounded px-5 py-2 text-sm shadow-[4px_2px_6px_rgba(0,0,0,0.2)] transition-all duration-300 hover:scale-105 group"
                        onClick={() => setModalAddNewPawn(true)}
                    >
                        <Plus size={16} color="white" strokeWidth={3} className="transition-transform duration-500 group-hover:rotate-90" />
                        Внеси Нов Залог
                    </button>
                    {modalAddNewPawn && <ModalAddNewPawn closeModal={() => setModalAddNewPawn(false)} refresh={onRefresh} />}
                </div>

                <div className="flex flex-col bg-white rounded-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] overflow-hidden flex-1 min-h-0">
                    <div className={`grid place-items-center ${cols} px-2 py-2 border-b-2 border-black/20 text-[#eee] bg-[#666]`}>
                        {headerItems.map(([label, key, idx]) => (
                            <div key={key} className="text-xs font-medium flex items-center cursor-pointer" onClick={() => handleOrder(key, idx)}>
                                {label} <SortIcon dir={orderDirectionArr.current[idx]} />
                            </div>
                        ))}
                        <div className="text-xs font-medium cursor-default">Валидно до</div>
                        <div className="text-xs font-medium cursor-default">Акции</div>
                        <div className="text-xs font-medium cursor-default">Повеќе</div>
                    </div>

                    <div ref={scrollablePawnsRef} className="overflow-y-auto overflow-x-hidden flex-1 scrollbar-thin">
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

                <CashRegister refreshDependency={refreshDependency} refreshDependencyAdjustPawn={refreshCashRegDependency} />
            </div>
        </div>
    );
}
