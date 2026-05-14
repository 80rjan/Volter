import React, { useEffect, useRef, useState } from "react";
import axios from "axios";
import PawnRow from "./Pawn.tsx";
import Nav from "../../shared/components/Nav.tsx";
import { Plus, ChevronUp, ChevronDown, Minus } from "lucide-react";
import ModalAddNewPawn from "./ModalAddNewPawn.tsx";
import CashRegister from "../../shared/components/CashRegister.tsx";
import Loading from "../../shared/components/Loading.tsx";
import { PawnRow as PawnRowType } from "./types.ts";
import { API_BASE } from "../../shared/api/config.ts";

const ITEM_TYPE_TO_CATEGORY: Record<string, PawnRowType['Category']> = {
    GOLD: 'Gold', ELECTRONIC: 'Electronics', VEHICLE: 'Vehicle', WATCH: 'Watch', OTHER: 'Other',
};

const CATEGORY_TO_ITEM_TYPE: Record<string, string> = {
    Electronics: 'ELECTRONIC', Gold: 'GOLD', Watch: 'WATCH', Vehicle: 'VEHICLE', Other: 'OTHER',
};

const SORT_FIELD: Record<string, string> = {
    'Valid Until': 'maturityDate',
    Name: 'customerName',
    'Client Id': 'customerId',
    Category: 'item.itemType',
    About: 'item.description',
    'Item Cost': 'amount',
    Provision: 'interest',
    'Days Left': 'maturityDate',
};

function mapPawnResponse(r: any): PawnRowType {
    const today = new Date();
    const daysLeft = Math.floor((new Date(r.maturityDate).getTime() - today.getTime()) / 86400000);
    return {
        Id: r.id,
        'Client Id': r.customerId,
        Name: r.customerName,
        Category: ITEM_TYPE_TO_CATEGORY[r.item?.itemType] ?? 'Other',
        About: r.item?.description ?? '',
        'Item Cost': r.amount,
        Provision: r.interest,
        'Days Left': daysLeft,
        'Valid Until': r.maturityDate,
        'Total Days': r.defaultDurationDays,
    };
}

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
    const [orderBy, setOrderBy] = useState("Valid Until");
    const orderDirectionArr = useRef([0, 0, 0, 0, 0, 0, 1]);
    const [orderDirection, setOrderDirection] = useState("ASC");
    const [searchByName, setSearchByName] = useState("");
    const [searchByEmbg, setSearchByEmbg] = useState("");
    const [searchByTel, setSearchByTel] = useState("");
    const [searchByCategory, setSearchByCategory] = useState("");
    const [modalAddNewPawn, setModalAddNewPawn] = useState(false);
    const [refresh, setRefresh] = useState(false);
    const page = useRef(0);
    const size = 60;
    const [isLastPage, setIsLastPage] = useState(false);
    const scrollablePawnsRef = useRef<HTMLDivElement>(null);
    const [loading, setLoading] = useState(false);
    const isFetchingRef = useRef(false);
    const [isFetching, setIsFetching] = useState(false);
    const [refreshCashReg, setRefreshCashReg] = useState(false);
    const fetchedPawnIds = useRef(new Set<string>());

    const fetchPawns = (pg: number, order: string, direction: string, name: string, embg: string, tel: string, cat: string, isLoading: boolean) => {
        if (isFetching) return;
        setIsFetching(true);
        setLoading(isLoading);
        isFetchingRef.current = true;

        const params = new URLSearchParams({
            page: String(pg),
            size: String(size),
            sort: `${SORT_FIELD[order] ?? 'maturityDate'},${direction}`,
            active: 'true',
        });
        if (name) params.set('customerName', name);
        if (embg) params.set('customerEmbg', embg);
        if (tel) params.set('customerPhoneNumber', tel);
        if (cat) params.set('itemType', CATEGORY_TO_ITEM_TYPE[cat] ?? cat);

        axios.get(`${API_BASE}/pawns?${params}`)
            .then(res => {
                const pawns: PawnRowType[] = (res.data.content ?? []).map(mapPawnResponse);
                const newUnique = pawns.filter(p => !fetchedPawnIds.current.has(`${p.Category}_${p.Id}`));
                newUnique.forEach(p => fetchedPawnIds.current.add(`${p.Category}_${p.Id}`));
                setAllPawns(prev => [...prev, ...newUnique]);
                setIsLastPage(res.data.last ?? true);
            })
            .catch(error => console.error("Error fetching pawns:", error))
            .finally(() => { setLoading(false); isFetchingRef.current = false; setIsFetching(false); });
    };

    useEffect(() => {
        const el = scrollablePawnsRef.current!;
        const handleScroll = () => {
            if (el.scrollHeight - el.scrollTop - el.clientHeight <= el.scrollHeight * 0.3 && !isLastPage && !isFetchingRef.current) {
                page.current += 1;
                fetchPawns(page.current, orderBy, orderDirection, searchByName, searchByEmbg, searchByTel, searchByCategory, false);
            }
        };
        el.addEventListener("scroll", handleScroll);
        return () => el.removeEventListener("scroll", handleScroll);
    }, [isLastPage, refresh, orderBy, orderDirection, searchByName, searchByEmbg, searchByTel, searchByCategory]);

    useEffect(() => {
        fetchedPawnIds.current.clear();
        setAllPawns([]);
        page.current = 0;
        fetchPawns(0, orderBy, orderDirection, searchByName, searchByEmbg, searchByTel, searchByCategory, true);
    }, [refresh, orderBy, orderDirection, searchByName, searchByEmbg, searchByTel, searchByCategory]);

    const handleOrder = (by: string, index: number) => {
        const newDir = new Array(orderDirectionArr.current.length).fill(0);
        newDir[index] = orderDirectionArr.current[index] === 0 ? 1 : orderDirectionArr.current[index] === 1 ? -1 : 1;
        orderDirectionArr.current = newDir;
        setOrderDirection(newDir.includes(-1) ? "DESC" : "ASC");
        setOrderBy(by);
    };

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
                </div>

                <CashRegister refreshDependency={refresh} refreshDependencyAdjustPawn={refreshCashReg} />
            </div>
        </div>
    );
}
