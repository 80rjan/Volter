import { useEffect, useRef, useState } from "react";
import axios from "axios";
import { PawnRow as PawnRowType } from "./types.ts";
import { API_BASE } from "../../shared/api/config.ts";

const ITEM_TYPE_TO_CATEGORY: Record<string, PawnRowType['Category']> = {
    GOLD: 'Gold', ELECTRONIC: 'Electronics', VEHICLE: 'Vehicle', WATCH: 'Watch', OTHER: 'Other',
};

const CATEGORY_TO_ITEM_TYPE: Record<PawnRowType['Category'], string> = {
    Electronics: 'ELECTRONIC', Gold: 'GOLD', Watch: 'WATCH', Vehicle: 'VEHICLE', Other: 'OTHER',
};

// JPA property paths on the PawnContract entity (used as Spring Data `sort`).
// These must match the entity exactly or Spring Data throws -> HTTP 500.
export const SORT_FIELD: Record<string, string> = {
    'Valid Until': 'dueDate',
    Name: 'customer.fullName',
    'Client Id': 'customer.id',
    Category: 'item.type',
    About: 'item.description',
    'Item Cost': 'principalAmount.amount',
    Provision: 'interestAmount.amount',
    'Days Left': 'dueDate',
};

// One column in the (possibly multi-column) sort chain. First = primary.
export type SortItem = { key: string; dir: "ASC" | "DESC" };

function mapPawnResponse(r: any): PawnRowType {
    const DAY = 86400000;
    // Whole-calendar-day difference (later - earlier), ignoring time-of-day, so a
    // date column (midnight) and a timestamp on the same day count as 0 days apart.
    const dayDiff = (later: string, earlier: string) =>
        Math.round((Date.parse(later.substring(0, 10)) - Date.parse(earlier.substring(0, 10))) / DAY);

    const now = new Date();
    const todayIso = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;

    // ACTIVE: days remaining until due (negative = overdue).
    // Settled (REDEEMED/FORFEITED): due - settlement date, so settling late
    // (after the due date) is negative.
    let daysLeft: number;
    if (r.status === 'ACTIVE') {
        daysLeft = dayDiff(r.dueDate, todayIso);
    } else {
        const settledAt = r.status === 'REDEEMED' ? r.redeemedAt : r.forfeitedAt;
        daysLeft = settledAt ? dayDiff(r.dueDate, settledAt) : 0;
    }

    // Type-specific details live in the item's free-form `attributes` JSON map.
    const attr = r.item?.attributes ?? {};
    let about = "";
    switch (r.item?.type) {
        case 'GOLD':
            about = `${attr.weightGrams ?? attr.grams ?? ''}гр ${attr.carats ?? attr.karat ?? ''}к`.trim();
            break;
        case 'WATCH':
            about = `${attr.brand ?? ''} ${attr.model ?? ''}`.trim();
            break;
        case 'ELECTRONIC':
            about = `${attr.brand ?? ''} ${r.item?.description ?? ''}`.trim();
            break;
        case 'VEHICLE':
            about = `${attr.brand ?? attr.make ?? ''} ${attr.model ?? attr.plate ?? ''}`.trim();
            break;
        default:
            about = `${r.item?.description ?? ''}`.trim();
    }
    return {
        Id: r.id,
        'Client Id': r.customerId,
        Name: r.customerName,
        Category: ITEM_TYPE_TO_CATEGORY[r.item?.type] ?? 'Other',
        Status: r.status,
        About: about,
        'Item Cost': r.principalAmount,
        Provision: r.interestAmount,
        'Days Left': daysLeft,
        'Valid Until': r.dueDate,
        'Total Days': r.termDays,
    };
}

export function usePawns() {
    const [allPawns, setAllPawns] = useState<PawnRowType[]>([]);
    const [summary, setSummary] = useState({ count: 0, totalPrincipal: 0, totalInterest: 0, totalGoldGrams: 0 });
    const [sorts, setSorts] = useState<SortItem[]>([{ key: "Valid Until", dir: "ASC" }]);
    const [searchByName, setSearchByName] = useState("");
    const [searchByEmbg, setSearchByEmbg] = useState("");
    const [searchByTel, setSearchByTel] = useState("");
    const [searchByCategory, setSearchByCategory] = useState("");
    const [searchByStatus, setSearchByStatus] = useState("ACTIVE");
    const [searchByStaff, setSearchByStaff] = useState("");
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

    const fetchPawns = (pg: number, sortList: SortItem[], name: string, embg: string, tel: string, cat: string, status: string, staff: string, isLoading: boolean) => {
        if (isFetching) return;
        setIsFetching(true);
        setLoading(isLoading);
        isFetchingRef.current = true;

        const params = new URLSearchParams({ page: String(pg), size: String(size) });
        // Multiple `sort` params => multi-column ordering (first = primary, then tie-breakers).
        const active = sortList.length ? sortList : [{ key: 'Valid Until', dir: 'ASC' as const }];
        active.forEach(s => params.append('sort', `${SORT_FIELD[s.key] ?? 'dueDate'},${s.dir}`));
        // Param names must match PawnFilterRequest exactly or the filter is ignored.
        if (status) params.set('status', status);
        if (name) params.set('customerFullName', name);
        if (embg) params.set('customerNationalId', embg);
        if (tel) params.set('customerPhone', tel);
        if (cat) params.set('itemType', CATEGORY_TO_ITEM_TYPE[cat as PawnRowType['Category']] ?? cat);
        if (staff) params.set('createdByStaffId', staff);

        axios.get(`${API_BASE}/pawns?${params}`)
            .then(res => {
                const pawns: PawnRowType[] = (res.data.content ?? []).map(mapPawnResponse);
                const newUnique = pawns.filter(p => !fetchedPawnIds.current.has(`${p.Category}_${p.Id}`));
                newUnique.forEach(p => fetchedPawnIds.current.add(`${p.Category}_${p.Id}`));
                setAllPawns(prev => [...prev, ...newUnique]);
                // Backend returns a flat PageResponse ({ page, totalPages, last, ... }),
                // so trust its `last` flag rather than a nested page object.
                setIsLastPage(res.data.last ?? (res.data.page >= res.data.totalPages - 1));
            })
            .catch(error => console.error("Error fetching pawns:", error))
            .finally(() => { setLoading(false); isFetchingRef.current = false; setIsFetching(false); });
    };

    useEffect(() => {
        const el = scrollablePawnsRef.current!;
        const handleScroll = () => {
            if (el.scrollHeight - el.scrollTop - el.clientHeight <= el.scrollHeight * 0.3 && !isLastPage && !isFetchingRef.current) {
                page.current += 1;
                fetchPawns(page.current, sorts, searchByName, searchByEmbg, searchByTel, searchByCategory, searchByStatus, searchByStaff, false);
            }
        };
        el.addEventListener("scroll", handleScroll);
        return () => el.removeEventListener("scroll", handleScroll);
    }, [isLastPage, refresh, sorts, searchByName, searchByEmbg, searchByTel, searchByCategory, searchByStatus, searchByStaff]);

    useEffect(() => {
        fetchedPawnIds.current.clear();
        setAllPawns([]);
        page.current = 0;
        fetchPawns(0, sorts, searchByName, searchByEmbg, searchByTel, searchByCategory, searchByStatus, searchByStaff, true);
    }, [refresh, sorts, searchByName, searchByEmbg, searchByTel, searchByCategory, searchByStatus, searchByStaff]);

    // Totals over the whole filtered set (server-side, not just loaded pages).
    const fetchSummary = () => {
        const params = new URLSearchParams();
        if (searchByStatus) params.set("status", searchByStatus);
        if (searchByName) params.set("customerFullName", searchByName);
        if (searchByEmbg) params.set("customerNationalId", searchByEmbg);
        if (searchByTel) params.set("customerPhone", searchByTel);
        if (searchByCategory) params.set("itemType", CATEGORY_TO_ITEM_TYPE[searchByCategory as PawnRowType["Category"]] ?? searchByCategory);
        if (searchByStaff) params.set("createdByStaffId", searchByStaff);
        axios.get(`${API_BASE}/pawns/summary?${params}`)
            .then(res => setSummary(res.data))
            .catch(error => console.error("Error fetching pawn summary:", error));
    };

    useEffect(() => {
        fetchSummary();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [refresh, searchByName, searchByEmbg, searchByTel, searchByCategory, searchByStatus, searchByStaff]);

    // Plain click = sort by this column alone (toggling its direction).
    // Shift+click = add/cycle this column in the chain (ASC -> DESC -> removed),
    // so e.g. due date + name keeps same-due-date pawns grouped and name-ordered.
    const handleOrder = (key: string, additive: boolean) => {
        setSorts(prev => {
            const idx = prev.findIndex(s => s.key === key);
            if (!additive) {
                if (prev.length === 1 && idx === 0) {
                    return [{ key, dir: prev[0].dir === "ASC" ? "DESC" : "ASC" }];
                }
                return [{ key, dir: "ASC" }];
            }
            if (idx === -1) return [...prev, { key, dir: "ASC" }];
            if (prev[idx].dir === "ASC") return prev.map((s, i) => (i === idx ? { ...s, dir: "DESC" } : s));
            const filtered = prev.filter(s => s.key !== key);
            return filtered.length ? filtered : [{ key: "Valid Until", dir: "ASC" }];
        });
    };

    return {
        allPawns,
        summary,
        loading,
        searchByCategory,
        setSearchByCategory,
        searchByStatus,
        setSearchByStatus,
        searchByStaff,
        setSearchByStaff,
        setSearchByName,
        setSearchByEmbg,
        setSearchByTel,
        scrollablePawnsRef,
        sorts,
        handleOrder,
        onRefresh: () => setRefresh(prev => !prev),
        onRefreshCashReg: () => setRefreshCashReg(prev => !prev),
        refreshDependency: refresh,
        refreshCashRegDependency: refreshCashReg,
    };
}
