import React, { useEffect, useRef, useState } from "react";
import axios from "axios";
import PawnRow from "./Pawn.tsx";
import Nav from "../../shared/components/Nav.tsx";
import { Plus, ChevronUp, ChevronDown, Minus } from "lucide-react";
import ModalAddNewPawn from "./ModalAddNewPawn.tsx";
import CashRegister from "../../shared/components/CashRegister.tsx";
import Loading from "../../shared/components/Loading.tsx";
import { PawnRow as PawnRowType } from "./types.ts";

const filterSelectOptions = [
    { value: "Gold", label: "Залог злато" },
    { value: "Electronics", label: "Залог електроника" },
    { value: "Watch", label: "Залог часовници" },
    { value: "Vehicle", label: "Залог возила" },
    { value: "Other", label: "Залог останато" },
];

const cols = "grid-cols-[3rem_1.5fr_1fr_2fr_repeat(4,1fr)_1.5fr_0.5fr]";

function SortIcon({ dir }: { dir: number }) {
    return dir === 0 ? <Minus size={14} /> : dir === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />;
}

export default function Pawns() {
    const [allPawns, setAllPawns] = useState<PawnRowType[]>([]);
    const [summary, setSummary] = useState<any>({});
    const [orderBy, setOrderBy] = useState("Valid Until");
    const orderDirectionArr = useRef([0, 0, 0, 0, 0, 0, 1]);
    const [orderDirection, setOrderDirection] = useState("ASC");
    const [searchByName, setSearchByName] = useState("");
    const [searchByEmbg, setSearchByEmbg] = useState("");
    const [searchByTel, setSearchByTel] = useState("");
    const [searchByCategory, setSearchByCategory] = useState("");
    const [modalAddNewPawn, setModalAddNewPawn] = useState(false);
    const [refresh, setRefresh] = useState(false);
    const offset = useRef(0);
    const limit = 60;
    const [isLastPage, setIsLastPage] = useState(false);
    const scrollablePawnsRef = useRef<HTMLDivElement>(null);
    const [loading, setLoading] = useState(false);
    const isFetchingRef = useRef(false);
    const [isFetching, setIsFetching] = useState(false);
    const [refreshCashReg, setRefreshCashReg] = useState(false);
    const fetchedPawnIds = useRef(new Set<string>());

    const fetchPawns = (limit: number, off: number, order: string, direction: string, name: string, embg: string, tel: string, cat: string, isLoading: boolean) => {
        if (isFetching) return;
        setIsFetching(true);
        setLoading(isLoading);
        isFetchingRef.current = true;
        axios.get(`http://localhost:3000?limit=${limit}&offset=${off}&orderBy=${order}&orderDirection=${direction}&searchByName=${name}&searchByEmbg=${embg}&searchByTel=${tel}&searchByCategory=${cat}`)
            .then(res => {
                setSummary(res.data.summary);
                const pawns: PawnRowType[] = res.data.pawns;
                const newUnique = pawns.filter(p => !fetchedPawnIds.current.has(`${p.Category}_${p.Id}`));
                newUnique.forEach(p => fetchedPawnIds.current.add(`${p.Category}_${p.Id}`));
                setAllPawns(prev => [...prev, ...newUnique]);
                setIsLastPage(pawns.length < limit);
            })
            .catch(error => console.error("Error fetching pawns:", error))
            .finally(() => { setLoading(false); isFetchingRef.current = false; setIsFetching(false); });
    };

    useEffect(() => {
        const el = scrollablePawnsRef.current!;
        const handleScroll = () => {
            if (el.scrollHeight - el.scrollTop - el.clientHeight <= el.scrollHeight * 0.3 && !isLastPage && !isFetchingRef.current) {
                offset.current += limit;
                fetchPawns(limit, offset.current, orderBy, orderDirection, searchByName, searchByEmbg, searchByTel, searchByCategory, false);
            }
        };
        el.addEventListener("scroll", handleScroll);
        return () => el.removeEventListener("scroll", handleScroll);
    }, [isLastPage, refresh, orderBy, orderDirection, searchByName, searchByEmbg, searchByTel, searchByCategory]);

    useEffect(() => {
        fetchedPawnIds.current.clear();
        setAllPawns([]);
        offset.current = 0;
        fetchPawns(limit, 0, orderBy, orderDirection, searchByName, searchByEmbg, searchByTel, searchByCategory, true);
    }, [refresh, orderBy, orderDirection, searchByName, searchByEmbg, searchByTel, searchByCategory]);

    const handleOrder = (by: string, index: number) => {
        const newDir = new Array(orderDirectionArr.current.length).fill(0);
        newDir[index] = orderDirectionArr.current[index] === 0 ? 1 : orderDirectionArr.current[index] === 1 ? -1 : 1;
        orderDirectionArr.current = newDir;
        setOrderDirection(newDir.includes(-1) ? "DESC" : "ASC");
        setOrderBy(by);
    };

    const hasSearch = searchByTel.length > 0 || searchByEmbg.length > 0 || searchByName.length > 0 || searchByCategory.length > 0;

    const headerItems: [string, string, number][] = [
        ["Ид", "Client Id", 0], ["Име", "Name", 1], ["Категорија", "Category", 2],
        ["Опис", "About", 3], ["Вредност", "Item Cost", 4], ["Провизија", "Provision", 5], ["Рок", "Days Left", 6],
    ];

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
                    {modalAddNewPawn && <ModalAddNewPawn closeModal={() => setModalAddNewPawn(false)} refresh={() => setRefresh(prev => !prev)} />}
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
                                refresh={() => setRefresh(prev => !prev)}
                                isOdd={index % 2 !== 0}
                                refreshCashReg={() => setRefreshCashReg(prev => !prev)}
                            />
                        ))}
                    </div>

                    {hasSearch && (
                        <div className="flex justify-between px-4 py-2 mt-auto shadow-[0_-2px_6px_rgba(0,0,0,0.2)] text-xs font-medium">
                            <div className="flex gap-2 items-center">
                                <span className="text-[#444] font-normal whitespace-nowrap">Бр. залози:</span>
                                {loading ? <Loading width={20} height={20} /> : Number(summary["Num Pawns"]).toLocaleString("de-DE")}
                            </div>
                            <div className="flex gap-2 items-center">
                                <span className="text-[#444] font-normal whitespace-nowrap">Исплатени средства:</span>
                                {loading ? <Loading width={20} height={20} /> : Number(summary["Money Pawns"]).toLocaleString("de-DE")}
                            </div>
                            <div className="flex gap-2 items-center">
                                <span className="text-[#444] font-normal whitespace-nowrap">Очекуван приход:</span>
                                {loading ? <Loading width={20} height={20} /> : Number(summary["Provision"]).toLocaleString("de-DE")}
                                <span className="text-[#444]">/</span>
                                {loading ? <Loading width={20} height={20} /> : <span>{((summary["Provision"] / summary["Money Pawns"]) * 100 || 0).toFixed(2)}%</span>}
                            </div>
                        </div>
                    )}
                </div>

                <CashRegister refreshDependency={refresh} refreshDependencyAdjustPawn={refreshCashReg} />
            </div>
        </div>
    );
}
