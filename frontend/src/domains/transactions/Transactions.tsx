import React, { useEffect, useRef, useState } from "react";
import axios from "axios";
import Nav from "../../shared/components/Nav.tsx";
import { ChevronUp, ChevronDown, Minus } from "lucide-react";
import CashRegister from "../../shared/components/CashRegister.tsx";
import Loading from "../../shared/components/Loading.tsx";
import { TransactionRow } from "./types.ts";

const getCat: Record<string, string> = {
    Electronics: "Електроника", Watch: "Часовници", Vehicle: "Возила",
    Gold: "Злато", Other: "Останато", Sale: "Продажба",
    Insert: "Внес Каса", Remove: "Излез Каса", Expense: "Расход",
};

const getDesc: Record<string, string> = {
    "Added new pawn": "Додаден нов залог",
    "Continued pawn": "Продолжен залог",
    "Closed pawn": "Затворен залог",
    "Added new sale": "Додадена нова продажба",
    "Closed sale": "Затворена продажба",
    "Transferred pawn to sale": "Префрлен залог во продажба",
};

const filterSelectOptions = [
    { value: "Gold", label: "Залог злато" },
    { value: "Electronics", label: "Залог електроника" },
    { value: "Watch", label: "Залог часовници" },
    { value: "Vehicle", label: "Залог возила" },
    { value: "Other", label: "Залог останато" },
    { value: "Sale", label: "Продажби" },
    { value: "Change", label: "Промени во залози" },
    { value: "Insert", label: "Внес каса" },
    { value: "Remove", label: "Излез каса" },
    { value: "Expense", label: "Расходи" },
];

const cols = "grid-cols-[2rem_1.5fr_1.5fr_1fr_2fr_repeat(4,1fr)_2fr]";

function SortIcon({ dir }: { dir: number }) {
    return dir === 0 ? <Minus size={14} /> : dir === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />;
}

export default function Transactions() {
    const [dateFrom, setDateFrom] = useState("");
    const [dateTo, setDateTo] = useState("");
    const [allTransactions, setAllTransactions] = useState<TransactionRow[]>([]);
    const [orderBy, setOrderBy] = useState("Date");
    const orderDirectionArr = useRef([0, 0, 0, 0, 0, 0, 0, 0, -1]);
    const [orderDirection, setOrderDirection] = useState("DESC");
    const [searchByName, setSearchByName] = useState("");
    const [searchByEmbg, setSearchByEmbg] = useState("");
    const [searchByCategory, setSearchByCategory] = useState("");
    const [refresh] = useState(false);
    const offset = useRef(0);
    const limit = 60;
    const [isLastPage, setIsLastPage] = useState(false);
    const scrollableRef = useRef<HTMLDivElement>(null);
    const [loading, setLoading] = useState(false);
    const isFetchingRef = useRef(false);
    const [isFetching, setIsFetching] = useState(false);
    const [refreshCashReg, setRefreshCashReg] = useState(false);
    const fetchedIds = useRef(new Set<number>());

    const fetchTransactions = (limit: number, off: number, order: string, direction: string, name: string, embg: string, from: string, to: string, cat: string, isLoading: boolean) => {
        if (isFetching) return;
        setIsFetching(true);
        setLoading(isLoading);
        isFetchingRef.current = true;
        axios.get(`http://localhost:3000/transactions?limit=${limit}&offset=${off}&orderBy=${order}&orderDirection=${direction}&searchByName=${name}&searchByEmbg=${embg}&dateFrom=${from}&dateTo=${to}&searchByCategory=${cat}`)
            .then(res => {
                const newUnique = res.data.filter((t: TransactionRow) => !fetchedIds.current.has(t.Id));
                newUnique.forEach((t: TransactionRow) => fetchedIds.current.add(t.Id));
                setAllTransactions(prev => [...prev, ...newUnique]);
                setIsLastPage(res.data.length < limit);
            })
            .catch(error => console.error("Error fetching transactions:", error))
            .finally(() => { setLoading(false); isFetchingRef.current = false; setIsFetching(false); });
    };

    useEffect(() => {
        const el = scrollableRef.current!;
        const handleScroll = () => {
            if (el.scrollHeight - el.scrollTop - el.clientHeight <= el.scrollHeight * 0.3 && !isLastPage && !isFetchingRef.current) {
                offset.current += limit;
                fetchTransactions(limit, offset.current, orderBy, orderDirection, searchByName, searchByEmbg, dateFrom, dateTo, searchByCategory, false);
            }
        };
        el.addEventListener("scroll", handleScroll);
        return () => el.removeEventListener("scroll", handleScroll);
    }, [isLastPage, orderBy, orderDirection, searchByName, searchByEmbg, dateFrom, dateTo, searchByCategory]);

    useEffect(() => {
        fetchedIds.current.clear();
        setAllTransactions([]);
        offset.current = 0;
        fetchTransactions(limit, 0, orderBy, orderDirection, searchByName, searchByEmbg, dateFrom, dateTo, searchByCategory, true);
    }, [refresh, orderBy, orderDirection, searchByName, searchByEmbg, dateFrom, dateTo, searchByCategory]);

    const handleOrder = (by: string, index: number) => {
        const newDir = new Array(orderDirectionArr.current.length).fill(0);
        newDir[index] = orderDirectionArr.current[index] === 0 ? 1 : orderDirectionArr.current[index] === 1 ? -1 : 1;
        orderDirectionArr.current = newDir;
        setOrderDirection(newDir.includes(-1) ? "DESC" : "ASC");
        setOrderBy(by);
    };

    const inputClass = "border-none rounded text-sm w-1/5 p-2 shadow-[0_0_8px_rgba(0,0,0,0.2)]";
    const dateInputClass = "p-2 h-fit border-none rounded shadow-[4px_2px_6px_rgba(0,0,0,0.2)] text-sm";

    const headers: [string, string | null, number][] = [
        ["Ид", "Client Id", 0], ["Име", "Name", 1], ["Ембг", null, -1],
        ["Категорија", "Category", 2], ["Опис", "Description", 3], ["Дадено", "Given", 4],
        ["Земено", "Got", 5], ["Профит", "Profit", 6], ["Отстапување", "Diff", 7], ["Датум", "Date", 8],
    ];

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
                    <div className="flex gap-1 w-fit">
                        <input className={dateInputClass} type="date" value={dateFrom} onChange={e => setDateFrom(e.target.value)} style={{ opacity: dateFrom ? 1 : 0.6 }} />
                        <input className={dateInputClass} type="date" value={dateTo} onChange={e => setDateTo(e.target.value)} style={{ opacity: dateTo ? 1 : 0.6 }} />
                    </div>
                </div>

                <div className="flex flex-col bg-white rounded-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] overflow-hidden flex-1 min-h-0">
                    <div className={`grid place-items-center ${cols} gap-4 px-2 py-2 border-b-2 border-black/20 text-[#eee] bg-[#666]`}>
                        {headers.map(([label, key, idx]) => (
                            <div
                                key={label}
                                className={`text-xs font-medium flex items-center ${key ? "cursor-pointer" : "cursor-default"}`}
                                onClick={key ? () => handleOrder(key, idx) : undefined}
                            >
                                {label} {key && <SortIcon dir={orderDirectionArr.current[idx]} />}
                            </div>
                        ))}
                    </div>

                    <div ref={scrollableRef} className="overflow-y-auto overflow-x-hidden flex-1 scrollbar-thin">
                        {loading ? <Loading /> : allTransactions.map((tx, index) => (
                            <div
                                key={index}
                                style={{ background: index % 2 === 1 ? "#f0f0f0" : "#ffffff" }}
                                className={`grid place-items-center text-center ${cols} gap-2 px-1 py-1 border-b border-black/20`}
                            >
                                <p className="text-xs">{tx["Client Id"]}</p>
                                <p className="text-xs font-bold">{tx.Name}</p>
                                <p className="text-xs">{tx.Embg}</p>
                                <p className="text-xs">{getCat[tx.Category] || tx.Category}</p>
                                <p className="text-xs">{getDesc[tx.Description] || tx.Description}</p>
                                <p className="text-xs font-bold italic">{Number(tx.Given).toLocaleString("de-DE")}</p>
                                <p className="text-xs font-bold italic">{Number(tx.Got).toLocaleString("de-DE")}</p>
                                <p className="text-xs font-bold italic">{Number(tx.Profit).toLocaleString("de-DE")}</p>
                                <p className={`text-xs font-bold italic ${Number(tx.Diff) === 0 ? "" : Number(tx.Diff) < 0 ? "text-red-500" : "text-green-700"}`}>
                                    {Number(tx.Diff).toLocaleString("de-DE")}
                                </p>
                                <p className="text-xs">{tx.Date.split(".")[0].split("T").join(" ")}</p>
                            </div>
                        ))}
                    </div>
                </div>

                <CashRegister refreshDependency={refresh} refreshTransactions={() => setRefreshCashReg(prev => !prev)} />
            </div>
        </div>
    );
}
