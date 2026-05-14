import { useEffect, useRef, useState } from "react";
import axios from "axios";
import Nav from "../../shared/components/Nav.tsx";
import { ChevronUp, ChevronDown, Minus } from "lucide-react";
import CashRegister from "../../shared/components/CashRegister.tsx";
import Loading from "../../shared/components/Loading.tsx";
import { TransactionRow } from "./types.ts";
import { API_BASE } from "../../shared/api/config.ts";

const CATEGORY_LABELS: Record<string, string> = {
    PAWN: 'Залог', SALE: 'Продажба', EXPENSE: 'Расход', CASH_REGISTER: 'Каса',
};

const DIRECTION_LABELS: Record<string, string> = {
    IN: 'Влез', OUT: 'Излез', NEUTRAL: 'Неутрално',
};

const SORT_FIELD: Record<string, string> = {
    Category: 'transactionCategory',
    Amount: 'amount',
    Date: 'createdAt',
    Margin: 'marginAmount',
};

const filterSelectOptions = [
    { value: 'PAWN', label: 'Залози' },
    { value: 'SALE', label: 'Продажби' },
    { value: 'EXPENSE', label: 'Расходи' },
    { value: 'CASH_REGISTER', label: 'Каса' },
];

function mapTransactionResponse(r: any): TransactionRow {
    return {
        id: r.id,
        transactionCategory: r.transactionCategory,
        amount: r.amount,
        direction: r.direction,
        marginAmount: r.marginAmount,
        marginType: r.marginType,
        createdAt: r.createdAt ?? '',
        description: r.description ?? '',
    };
}

const cols = "grid-cols-[1fr_2fr_1fr_1fr_1fr_2fr]";

function SortIcon({ dir }: { dir: number }) {
    return dir === 0 ? <Minus size={14} /> : dir === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />;
}

export default function Transactions() {
    const [allTransactions, setAllTransactions] = useState<TransactionRow[]>([]);
    const [orderBy, setOrderBy] = useState("Date");
    const orderDirectionArr = useRef([0, 0, 0, 0, -1, 0]);
    const [orderDirection, setOrderDirection] = useState("DESC");
    const [searchByCategory, setSearchByCategory] = useState("");
    const [refresh] = useState(false);
    const page = useRef(0);
    const size = 60;
    const [isLastPage, setIsLastPage] = useState(false);
    const scrollableRef = useRef<HTMLDivElement>(null);
    const [loading, setLoading] = useState(false);
    const isFetchingRef = useRef(false);
    const [isFetching, setIsFetching] = useState(false);
    const fetchedIds = useRef(new Set<number>());

    const fetchTransactions = (pg: number, order: string, direction: string, cat: string, isLoading: boolean) => {
        if (isFetching) return;
        setIsFetching(true);
        setLoading(isLoading);
        isFetchingRef.current = true;
        const params = new URLSearchParams({
            page: String(pg),
            size: String(size),
            sort: `${SORT_FIELD[order] ?? 'createdAt'},${direction}`,
        });
        if (cat) params.set('transactionCategory', cat);
        axios.get(`${API_BASE}/transactions?${params}`)
            .then(res => {
                const txns: TransactionRow[] = (res.data.content ?? []).map(mapTransactionResponse);
                const newUnique = txns.filter(t => !fetchedIds.current.has(t.id));
                newUnique.forEach(t => fetchedIds.current.add(t.id));
                setAllTransactions(prev => [...prev, ...newUnique]);
                setIsLastPage(res.data.last ?? true);
            })
            .catch(error => console.error("Error fetching transactions:", error))
            .finally(() => { setLoading(false); isFetchingRef.current = false; setIsFetching(false); });
    };

    useEffect(() => {
        const el = scrollableRef.current!;
        const handleScroll = () => {
            if (el.scrollHeight - el.scrollTop - el.clientHeight <= el.scrollHeight * 0.3 && !isLastPage && !isFetchingRef.current) {
                page.current += 1;
                fetchTransactions(page.current, orderBy, orderDirection, searchByCategory, false);
            }
        };
        el.addEventListener("scroll", handleScroll);
        return () => el.removeEventListener("scroll", handleScroll);
    }, [isLastPage, orderBy, orderDirection, searchByCategory]);

    useEffect(() => {
        fetchedIds.current.clear();
        setAllTransactions([]);
        page.current = 0;
        fetchTransactions(0, orderBy, orderDirection, searchByCategory, true);
    }, [refresh, orderBy, orderDirection, searchByCategory]);

    const handleOrder = (by: string, index: number) => {
        const newDir = new Array(orderDirectionArr.current.length).fill(0);
        newDir[index] = orderDirectionArr.current[index] === 0 ? 1 : orderDirectionArr.current[index] === 1 ? -1 : 1;
        orderDirectionArr.current = newDir;
        setOrderDirection(newDir.includes(-1) ? "DESC" : "ASC");
        setOrderBy(by);
    };

    const inputClass = "border-none rounded text-sm w-1/5 p-2 shadow-[0_0_8px_rgba(0,0,0,0.2)]";

    const headers: [string, string | null, number][] = [
        ["Категорија", "Category", 0], ["Опис", null, -1], ["Износ", "Amount", 2],
        ["Насока", null, -1], ["Маргина", "Margin", 4], ["Датум", "Date", 5],
    ];

    return (
        <div className="h-screen grid grid-cols-[max(15%,240px)_auto]">
            <Nav />
            <div className="flex flex-col px-8 pt-2 gap-3 flex-1 overflow-hidden">
                <div className="flex w-full">
                    <select
                        className={inputClass}
                        style={{ color: searchByCategory === "" ? "#888" : "#000" }}
                        onChange={e => setSearchByCategory(e.target.value)}
                    >
                        <option style={{ color: "#888" }} value="">Пребарубај по</option>
                        {filterSelectOptions.map(o => <option style={{ color: "#111" }} key={o.value} value={o.value}>{o.label}</option>)}
                    </select>
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
                                key={tx.id}
                                style={{ background: index % 2 === 1 ? "#f0f0f0" : "#ffffff" }}
                                className={`grid place-items-center text-center ${cols} gap-2 px-1 py-1 border-b border-black/20`}
                            >
                                <p className="text-xs">{CATEGORY_LABELS[tx.transactionCategory] ?? tx.transactionCategory}</p>
                                <p className="text-xs">{tx.description}</p>
                                <p className="text-xs font-bold italic">{Number(tx.amount).toLocaleString("de-DE")}</p>
                                <p className={`text-xs ${tx.direction === 'IN' ? 'text-green' : tx.direction === 'OUT' ? 'text-red-500' : ''}`}>
                                    {DIRECTION_LABELS[tx.direction] ?? tx.direction}
                                </p>
                                <p className={`text-xs font-bold italic ${tx.marginType === 'LOSS' ? 'text-red-500' : tx.marginType === 'PROFIT' ? 'text-green' : ''}`}>
                                    {Number(tx.marginAmount).toLocaleString("de-DE")}
                                </p>
                                <p className="text-xs">{tx.createdAt.split(".")[0].replace("T", " ")}</p>
                            </div>
                        ))}
                    </div>
                </div>

                <CashRegister refreshDependency={refresh} />
            </div>
        </div>
    );
}
