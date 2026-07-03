import { useEffect, useRef, useState } from "react";
import axios from "axios";
import { ChevronUp, ChevronDown, Minus, Ellipsis, Lock } from "lucide-react";
import CashRegister from "../../shared/components/CashRegister.tsx";
import Loading from "../../shared/components/Loading.tsx";
import ModalReadMoreTransaction from "./ModalReadMoreTransaction.tsx";
import { TransactionRow, TransactionDetailed } from "./types.ts";
import { API_BASE } from "../../shared/api/config.ts";
import { useAuth } from "../../GlobalContext.tsx";
import { useTeam } from "../../shared/utils/useTeam.ts";

const TYPE_LABELS: Record<string, string> = {
    PAWN: "Залог", SALE: "Продажба", EXPENSE: "Расход", CASH_REGISTER: "Каса", STAFF_BONUS: "Бонус",
};
const DIRECTION_LABELS: Record<string, string> = { IN: "Влез", OUT: "Излез" };

const SORT_FIELD: Record<string, string> = {
    Category: "type",
    Amount: "amount.amount",
    Direction: "direction",
    Date: "createdAt",
};

const typeOptions = [
    { value: "PAWN", label: "Залози" },
    { value: "SALE", label: "Продажби" },
    { value: "EXPENSE", label: "Расходи" },
    { value: "CASH_REGISTER", label: "Каса" },
    { value: "STAFF_BONUS", label: "Бонуси" },
];
const directionOptions = [
    { value: "IN", label: "Влез" },
    { value: "OUT", label: "Излез" },
];

function mapTransactionResponse(r: any): TransactionRow {
    return {
        id: r.id,
        staffId: r.staffId,
        cashRegisterSessionId: r.cashRegisterSessionId,
        type: r.type,
        amount: r.amount,
        direction: r.direction,
        description: r.description ?? "",
        clientName: r.clientName ?? null,
        createdAt: r.createdAt ?? "",
    };
}

const cols = "grid-cols-[1.2fr_1.5fr_2fr_1fr_1fr_1.5fr_0.5fr]";

function SortIcon({ dir }: { dir: number }) {
    return dir === 0 ? <Minus size={14} /> : dir === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />;
}

export default function Transactions() {
    const { can } = useAuth();
    const allowed = can("TRANSACTION_READ");

    const [allTransactions, setAllTransactions] = useState<TransactionRow[]>([]);
    const [orderBy, setOrderBy] = useState("Date");
    const orderDirectionArr = useRef([0, 0, 0, 0, 0, -1, 0]);
    const [orderDirection, setOrderDirection] = useState("DESC");
    const team = useTeam();
    const [filterStaff, setFilterStaff] = useState("");
    const [filterType, setFilterType] = useState("");
    const [filterDirection, setFilterDirection] = useState("");
    const [filterClient, setFilterClient] = useState("");
    const [filterFrom, setFilterFrom] = useState("");
    const [filterTo, setFilterTo] = useState("");
    const [refresh] = useState(false);
    const page = useRef(0);
    const size = 60;
    const [isLastPage, setIsLastPage] = useState(false);
    const scrollableRef = useRef<HTMLDivElement>(null);
    const [loading, setLoading] = useState(false);
    const isFetchingRef = useRef(false);
    const fetchedIds = useRef(new Set<number>());

    const [detailed, setDetailed] = useState<TransactionDetailed | null>(null);
    const [modalReadMore, setModalReadMore] = useState(false);
    const [detailLoadingId, setDetailLoadingId] = useState<number | null>(null);

    const fetchTransactions = (pg: number, order: string, direction: string, isLoading: boolean) => {
        if (isFetchingRef.current) return;
        isFetchingRef.current = true;
        setLoading(isLoading);
        const params = new URLSearchParams({
            page: String(pg),
            size: String(size),
            sort: `${SORT_FIELD[order] ?? "createdAt"},${direction}`,
        });
        if (filterStaff) params.set("staffId", filterStaff);
        if (filterType) params.set("type", filterType);
        if (filterDirection) params.set("direction", filterDirection);
        if (filterClient) params.set("clientName", filterClient);
        if (filterFrom) params.set("createdFrom", `${filterFrom}T00:00:00Z`);
        if (filterTo) params.set("createdTo", `${filterTo}T23:59:59Z`);
        axios.get(`${API_BASE}/transactions?${params}`)
            .then(res => {
                const txns: TransactionRow[] = (res.data.content ?? []).map(mapTransactionResponse);
                const newUnique = txns.filter(t => !fetchedIds.current.has(t.id));
                newUnique.forEach(t => fetchedIds.current.add(t.id));
                setAllTransactions(prev => [...prev, ...newUnique]);
                setIsLastPage(res.data.last ?? true);
            })
            .catch(error => console.error("Error fetching transactions:", error))
            .finally(() => { setLoading(false); isFetchingRef.current = false; });
    };

    useEffect(() => {
        const el = scrollableRef.current;
        if (!el) return;
        const handleScroll = () => {
            if (el.scrollHeight - el.scrollTop - el.clientHeight <= el.scrollHeight * 0.3 && !isLastPage && !isFetchingRef.current) {
                page.current += 1;
                fetchTransactions(page.current, orderBy, orderDirection, false);
            }
        };
        el.addEventListener("scroll", handleScroll);
        return () => el.removeEventListener("scroll", handleScroll);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isLastPage, orderBy, orderDirection, filterStaff, filterType, filterDirection, filterClient, filterFrom, filterTo]);

    useEffect(() => {
        if (!allowed) return;
        fetchedIds.current.clear();
        setAllTransactions([]);
        page.current = 0;
        fetchTransactions(0, orderBy, orderDirection, true);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [allowed, refresh, orderBy, orderDirection, filterStaff, filterType, filterDirection, filterClient, filterFrom, filterTo]);

    useEffect(() => { if (detailed != null) setModalReadMore(true); }, [detailed]);

    const openDetail = (id: number) => {
        setDetailLoadingId(id);
        axios.get(`${API_BASE}/transactions/${id}`)
            .then(res => setDetailed(res.data))
            .catch(error => console.error("Error fetching transaction:", error))
            .finally(() => setDetailLoadingId(null));
    };

    const handleOrder = (by: string, index: number) => {
        const newDir = new Array(orderDirectionArr.current.length).fill(0);
        newDir[index] = orderDirectionArr.current[index] === 1 ? -1 : 1;
        orderDirectionArr.current = newDir;
        setOrderDirection(newDir[index] === -1 ? "DESC" : "ASC");
        setOrderBy(by);
    };

    const inputClass = "bg-white border-none rounded text-xs font-medium px-2 py-2 shadow-sm flex-1 min-w-[140px] md:min-w-0";

    const headers: [string, string | null, number][] = [
        ["Категорија", "Category", 0], ["Клиент", null, -1], ["Опис", null, -1], ["Износ", "Amount", 3],
        ["Насока", "Direction", 4], ["Датум", "Date", 5], ["", null, -1],
    ];

    return (
        <div className="h-screen flex md:pl-16 pt-12 md:pt-0">
            <div className="flex flex-col px-3 md:px-8 pt-2 gap-3 flex-1 overflow-hidden">
                {!allowed ? (
                    <div className="flex flex-1 flex-col items-center justify-center gap-3 text-[#666]">
                        <Lock size={40} />
                        <p className="text-sm">Немате дозвола за преглед на трансакции.</p>
                    </div>
                ) : (
                    <>
                        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-[2fr_2fr_2fr_2fr_1fr_1fr_.5fr] w-full gap-2 md:gap-3">
                            <input
                                type="search"
                                className={inputClass}
                                placeholder="Пребарувај по клиент"
                                value={filterClient}
                                onChange={e => setFilterClient(e.target.value)}
                            />
                            <select
                                className={inputClass}
                                style={{ color: filterStaff === "" ? "#888" : "#000" }}
                                value={filterStaff}
                                onChange={e => setFilterStaff(e.target.value)}
                            >
                                <option style={{ color: "#888" }} value="">Сите вработени</option>
                                {team.map(s => <option style={{ color: "#111" }} key={s.id} value={s.id}>{s.fullName}</option>)}
                            </select>
                            <select
                                className={inputClass}
                                style={{ color: filterType === "" ? "#888" : "#000" }}
                                value={filterType}
                                onChange={e => setFilterType(e.target.value)}
                            >
                                <option style={{ color: "#888" }} value="">Сите категории</option>
                                {typeOptions.map(o => <option style={{ color: "#111" }} key={o.value} value={o.value}>{o.label}</option>)}
                            </select>
                            <select
                                className={inputClass}
                                style={{ color: filterDirection === "" ? "#888" : "#000" }}
                                value={filterDirection}
                                onChange={e => setFilterDirection(e.target.value)}
                            >
                                <option style={{ color: "#888" }} value="">Сите насоки</option>
                                {directionOptions.map(o => <option style={{ color: "#111" }} key={o.value} value={o.value}>{o.label}</option>)}
                            </select>
                            <div className="flex items-center gap-2">
                                <span className="text-xs text-[#666]">Од</span>
                                <input type="date" style={{ color: filterFrom ? "#000" : "#888" }} className={inputClass} value={filterFrom} onChange={e => setFilterFrom(e.target.value)} />
                            </div>
                            <div className="flex items-center gap-2">
                                <span className="text-xs text-[#666]">До</span>
                                <input type="date" style={{ color: filterTo ? "#000" : "#888" }} className={inputClass} value={filterTo} onChange={e => setFilterTo(e.target.value)} />
                            </div>
                            {(filterStaff || filterType || filterDirection || filterClient || filterFrom || filterTo) && (
                                <button
                                    onClick={() => { setFilterStaff(""); setFilterType(""); setFilterDirection(""); setFilterClient(""); setFilterFrom(""); setFilterTo(""); }}
                                    className="text-xs text-[#666] underline hover:text-black transition-colors"
                                >
                                    Исчисти филтри
                                </button>
                            )}
                        </div>

                        <div className="flex flex-col bg-white rounded-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] overflow-x-auto flex-1 min-h-0">
                            <div className={`grid place-items-center ${cols} min-w-[880px] md:min-w-0 gap-4 px-2 py-2 border-b-2 border-black/20 text-[#eee] bg-[#666]`}>
                                {headers.map(([label, key, idx], i) => (
                                    <div
                                        key={i}
                                        className={`text-xs font-medium flex items-center ${key ? "cursor-pointer" : "cursor-default"}`}
                                        onClick={key ? () => handleOrder(key, idx) : undefined}
                                    >
                                        {label} {key && <SortIcon dir={orderDirectionArr.current[idx]} />}
                                    </div>
                                ))}
                            </div>

                            <div ref={scrollableRef} className="overflow-y-auto overflow-x-hidden flex-1 scrollbar-thin min-w-[880px] md:min-w-0 svg-hover">
                                {loading ? <Loading /> : allTransactions.map((tx, index) => (
                                    <div
                                        key={tx.id}
                                        style={{ background: index % 2 === 1 ? "#f0f0f0" : "#ffffff" }}
                                        className={`grid place-items-center text-center ${cols} gap-2 px-1 py-1 border-b border-black/20`}
                                    >
                                        <p className="text-xs">{TYPE_LABELS[tx.type] ?? tx.type}</p>
                                        <p className="text-xs">{tx.clientName || "—"}</p>
                                        <p className="text-xs">{tx.description || "—"}</p>
                                        <p className="text-xs font-bold italic">{tx.direction === "OUT" && "-"}{Number(tx.amount).toLocaleString("de-DE")}</p>
                                        <p className={`text-xs font-semibold ${tx.direction === "IN" ? "text-green" : "text-red-500"}`}>
                                            {DIRECTION_LABELS[tx.direction] ?? tx.direction}
                                        </p>
                                        <p className="text-xs">{tx.createdAt.substring(0, 19).replace("T", " ")}</p>
                                        {detailLoadingId === tx.id
                                            ? <Loading width={18} height={18} />
                                            : <Ellipsis size={18} color="#888" className="cursor-pointer" onClick={() => openDetail(tx.id)} />}
                                    </div>
                                ))}
                            </div>
                        </div>

                        <CashRegister refreshDependency={refresh} />
                    </>
                )}
            </div>

            {modalReadMore && detailed && (
                <ModalReadMoreTransaction
                    tx={detailed}
                    closeModal={() => { setModalReadMore(false); setDetailed(null); }}
                />
            )}
        </div>
    );
}
