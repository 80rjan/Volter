import { useEffect, useRef, useState } from "react";
import axios from "axios";
import { Plus, ChevronUp, ChevronDown, Minus, Tag, Banknote, Gem } from "lucide-react";
import ModalAddNewSale from "./ModalAddNewSale.tsx";
import SaleRowComponent from "./Sale.tsx";
import CashRegister from "../../shared/components/CashRegister.tsx";
import Loading from "../../shared/components/Loading.tsx";
import { SaleRow } from "./types.ts";
import { API_BASE } from "../../shared/api/config.ts";
import { useAuth } from "../../GlobalContext.tsx";
import { useTeam } from "../../shared/utils/useTeam.ts";

const ITEM_TYPE_TO_CATEGORY: Record<string, SaleRow['Category']> = {
    GOLD: 'Gold', ELECTRONIC: 'Electronics', VEHICLE: 'Vehicle', WATCH: 'Watch', OTHER: 'Other',
};

// JPA property paths on the Sale entity (Spring Data `sort`).
const SORT_FIELD: Record<string, string> = {
    Customer: 'customer.fullName',
    About: 'item.description',
    'Item Cost': 'purchasePrice.amount',
    'Sale Price': 'salePrice.amount',
    'Date Bought': 'createdAt',
};

const statusOptions = [
    { value: "AVAILABLE", label: "Достапни" },
    { value: "SOLD", label: "Продадени" },
    { value: "CANCELED", label: "Откажани" },
    { value: "", label: "Сите статуси" },
];

function mapSaleResponse(r: any): SaleRow {
    const attr = r.item?.attributes ?? {};
    let about = "";
    switch (r.item?.type) {
        case 'GOLD': about = `${attr.weightGrams ?? attr.grams ?? ''}гр ${attr.carats ?? attr.karat ?? ''}к`.trim(); break;
        case 'WATCH': about = `${attr.brand ?? ''} ${attr.model ?? ''}`.trim(); break;
        case 'ELECTRONIC': about = `${attr.brand ?? ''} ${r.item?.description ?? ''}`.trim(); break;
        case 'VEHICLE': about = `${attr.brand ?? attr.make ?? ''} ${attr.model ?? attr.plate ?? ''}`.trim(); break;
        default: about = `${r.item?.description ?? ''}`.trim();
    }
    return {
        Id: r.id,
        Customer: r.customerName,
        Status: r.status,
        Category: ITEM_TYPE_TO_CATEGORY[r.item?.type] ?? 'Other',
        About: about,
        'Item Cost': r.purchasePrice,
        'Sale Price': r.salePrice,
        Profit: r.profit,
        'Date Bought': r.createdAt,
        'Sold At': r.soldAt,
    };
}

function SortIcon({ dir }: { dir: number }) {
    return dir === 0 ? <Minus size={14} /> : dir === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />;
}

export default function Sales() {
    const { can } = useAuth();
    const team = useTeam();
    const [allSales, setAllSales] = useState<SaleRow[]>([]);
    const [summary, setSummary] = useState({ count: 0, totalPurchase: 0, totalGoldGrams: 0 });
    const [orderBy, setOrderBy] = useState("Date Bought");
    const orderDirectionArr = useRef([0, 0, 0, 0, 0, 0, -1]);
    const [orderDirection, setOrderDirection] = useState("DESC");
    const [searchByStatus, setSearchByStatus] = useState("AVAILABLE");
    const [searchByName, setSearchByName] = useState("");
    const [searchByStaff, setSearchByStaff] = useState("");
    const [modalAddNewSale, setModalAddNewSale] = useState(false);
    const [refresh, setRefresh] = useState(false);
    const page = useRef(0);
    const size = 60;
    const [isLastPage, setIsLastPage] = useState(false);
    const scrollableSalesRef = useRef<HTMLDivElement>(null);
    const [loading, setLoading] = useState(false);
    const isFetchingRef = useRef(false);
    const [isFetching, setIsFetching] = useState(false);
    const fetchedSaleIds = useRef(new Set<number>());

    const fetchSales = (pg: number, order: string, direction: string, status: string, name: string, staff: string, isLoading: boolean) => {
        if (isFetching) return;
        setIsFetching(true);
        setLoading(isLoading);
        isFetchingRef.current = true;
        const params = new URLSearchParams({
            page: String(pg),
            size: String(size),
            sort: `${SORT_FIELD[order] ?? 'createdAt'},${direction}`,
        });
        if (status) params.set('status', status);
        if (name) params.set('customerFullName', name);
        if (staff) params.set('createdByStaffId', staff);

        axios.get(`${API_BASE}/sales?${params}`)
            .then(res => {
                const sales: SaleRow[] = (res.data.content ?? []).map(mapSaleResponse);
                const newUnique = sales.filter(s => !fetchedSaleIds.current.has(s.Id));
                newUnique.forEach(s => fetchedSaleIds.current.add(s.Id));
                setAllSales(prev => [...prev, ...newUnique]);
                setIsLastPage(res.data.last ?? (res.data.page >= res.data.totalPages - 1));
            })
            .catch(error => console.error("Error fetching sales:", error))
            .finally(() => { setLoading(false); isFetchingRef.current = false; setIsFetching(false); });
    };

    useEffect(() => {
        const el = scrollableSalesRef.current!;
        const handleScroll = () => {
            if (el.scrollHeight - el.scrollTop - el.clientHeight <= el.scrollHeight * 0.3 && !isLastPage && !isFetchingRef.current) {
                page.current += 1;
                fetchSales(page.current, orderBy, orderDirection, searchByStatus, searchByName, searchByStaff, false);
            }
        };
        el.addEventListener("scroll", handleScroll);
        return () => el.removeEventListener("scroll", handleScroll);
    }, [isLastPage, refresh, orderBy, orderDirection, searchByStatus, searchByName, searchByStaff]);

    useEffect(() => {
        fetchedSaleIds.current.clear();
        setAllSales([]);
        page.current = 0;
        fetchSales(0, orderBy, orderDirection, searchByStatus, searchByName, searchByStaff, true);
    }, [refresh, orderBy, orderDirection, searchByStatus, searchByName, searchByStaff]);

    // Totals over the whole filtered set (server-side, not just loaded pages).
    const fetchSummary = () => {
        const params = new URLSearchParams();
        if (searchByStatus) params.set("status", searchByStatus);
        if (searchByName) params.set("customerFullName", searchByName);
        if (searchByStaff) params.set("createdByStaffId", searchByStaff);
        axios.get(`${API_BASE}/sales/summary?${params}`)
            .then(res => setSummary(res.data))
            .catch(error => console.error("Error fetching sale summary:", error));
    };

    useEffect(() => {
        fetchSummary();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [refresh, searchByStatus, searchByName, searchByStaff]);

    const handleOrder = (by: string, index: number) => {
        const newDir = new Array(orderDirectionArr.current.length).fill(0);
        newDir[index] = orderDirectionArr.current[index] === 0 ? 1 : orderDirectionArr.current[index] === 1 ? -1 : 1;
        orderDirectionArr.current = newDir;
        setOrderDirection(newDir.includes(-1) ? "DESC" : "ASC");
        setOrderBy(by);
    };

    const inputClass = "bg-white border-none rounded text-xs font-medium px-2 py-2 w-full shadow-sm";
    const cols = "grid-cols-[1.5fr_2fr_1fr_1fr_1fr_1.1fr_1.1fr_1fr_0.5fr]";
    const headers: [string, string | null, number][] = [
        ["Клиент", "Customer", 0], ["Опис", "About", 1], ["Откупна", "Item Cost", 2],
        ["Продажна", "Sale Price", 3], ["Профит", null, -1], ["Статус", null, -1],
        ["Датум", "Date Bought", 6], ["Акции", null, -1], ["Повеќе", null, -1],
    ];

    return (
        <div className="h-screen flex pl-16">
            <div className="flex flex-col px-8 pt-2 gap-3 flex-1 overflow-hidden">
                <div className="flex justify-between w-full gap-6">
                    <select className={inputClass} value={searchByStatus} onChange={e => setSearchByStatus(e.target.value)}>
                        {statusOptions.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
                    </select>
                    <input className={inputClass} type="search" placeholder="Пребарувај по клиент" onChange={e => setSearchByName(e.target.value)} />
                    <select
                        className={inputClass}
                        style={{ color: searchByStaff === "" ? "#888" : "#000" }}
                        value={searchByStaff}
                        onChange={e => setSearchByStaff(e.target.value)}
                    >
                        <option style={{ color: "#888" }} value="">Сите вработени</option>
                        {team.map(s => <option style={{ color: "#111" }} key={s.id} value={s.id}>{s.fullName}</option>)}
                    </select>
                    {can("SALE_WRITE") && (
                        <button
                            onClick={() => setModalAddNewSale(true)}
                            className="group relative overflow-hidden flex shrink-0 items-center gap-2 bg-green h-full text-white rounded px-5 py-2 whitespace-nowrap text-xs font-semibold shadow-[4px_2px_6px_rgba(0,0,0,0.2)] transition-all duration-300 hover:scale-105"
                        >
                            <span className="pointer-events-none absolute inset-0 -translate-x-full bg-gradient-to-r from-transparent via-white/25 to-transparent transition-transform duration-700 ease-out group-hover:translate-x-[180%]" />
                            <Plus size={16} color="white" strokeWidth={3} className="transition-transform duration-500 group-hover:rotate-90" />
                            Внеси Нова Продажба
                        </button>
                    )}
                    {modalAddNewSale && <ModalAddNewSale closeModal={() => setModalAddNewSale(false)} refresh={() => setRefresh(prev => !prev)} />}
                </div>

                <div className="flex flex-col bg-white rounded-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] overflow-hidden flex-1 min-h-0">
                    <div className={`grid place-items-center ${cols} px-2 py-2 border-b-2 border-black/20 text-[#eee] bg-[#666]`}>
                        {headers.map(([label, key, idx]) => (
                            <div key={label} className={`text-xs font-medium flex items-center ${key ? "cursor-pointer" : "cursor-default"}`}
                                 onClick={key ? () => handleOrder(key, idx) : undefined}>
                                {label} {key && <SortIcon dir={orderDirectionArr.current[idx]} />}
                            </div>
                        ))}
                    </div>

                    <div ref={scrollableSalesRef} className="overflow-y-auto overflow-x-hidden flex-1 scrollbar-thin">
                        {loading ? <Loading /> : allSales.map((sale, index) => (
                            <SaleRowComponent key={index} sale={sale} refresh={() => setRefresh(prev => !prev)} isOdd={index % 2 !== 0} />
                        ))}
                    </div>
                </div>

                {/* Totals for the currently filtered (available) sales. */}
                <div className="flex w-full justify-between flex-wrap items-center gap-x-6 gap-y-1 bg-[#f4f4f4] border border-black/10 rounded-lg px-5 py-1.5 text-xs text-[#666]">
                    <span className="flex items-center gap-1.5"><Tag size={15} className="text-green" /> Продажби: <b className="text-[#333]">{summary.count}</b></span>
                    <span className="flex items-center gap-1.5"><Banknote size={15} className="text-green" /> Дадени пари: <b className="text-[#333]">{summary.totalPurchase.toLocaleString("de-DE")} ден</b></span>
                    <span className="flex items-center gap-1.5"><Gem size={15} className="text-green" /> Злато: <b className="text-[#333]">{summary.totalGoldGrams.toLocaleString("de-DE", { maximumFractionDigits: 2 })} гр</b></span>
                </div>

                <CashRegister refreshDependency={refresh} />
            </div>
        </div>
    );
}
