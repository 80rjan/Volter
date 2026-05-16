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

export const SORT_FIELD: Record<string, string> = {
    'Valid Until': 'period.maturityDate',
    Name: 'customer.name',
    'Client Id': 'customer.id',
    Category: 'item.itemType',
    About: 'item.description',
    'Item Cost': 'amount.amount',
    Provision: 'interest.amount',
    'Days Left': 'period.maturityDate',
};

function mapPawnResponse(r: any): PawnRowType {
    const today = new Date();
    const daysLeft = Math.floor((new Date(r.maturityDate).getTime() - today.getTime()) / 86400000);

    var about:string = "";
    switch (r.item.itemType) {
        case 'GOLD':
            about = `${r.item.pieceType ?? ''} ${r.item.weightGrams ?? ''}гр ${r.item.carats ?? ''}к`.trim();
            break;
        case 'ELECTRONIC':
            about = `${r.item.brand ?? ''} ${r.item.category ?? ''} ${r.item.year ?? ''}`.trim();
            break;
        case 'VEHICLE':
            about = `${r.item.brand ?? ''} ${r.item.model ?? ''} ${r.item.year ?? ''}`.trim();
            break;
        case 'WATCH':
            about = `${r.item.brand ?? ''} ${r.item.model ?? ''} ${r.item.material ?? ''}`.trim();
            break;
        case 'OTHER':
            about = `${r.item.category ?? ''} ${r.item.material ?? ''}`.trim();
            break;
    }
    return {
        Id: r.id,
        'Client Id': r.customerId,
        Name: r.customerName,
        Category: ITEM_TYPE_TO_CATEGORY[r.item?.itemType] ?? 'Other',
        About: about,
        'Item Cost': r.amount,
        Provision: r.interest,
        'Days Left': daysLeft,
        'Valid Until': r.maturityDate,
        'Total Days': r.defaultDurationDays,
    };
}

export function usePawns() {
    const [allPawns, setAllPawns] = useState<PawnRowType[]>([]);
    const [orderBy, setOrderBy] = useState("Valid Until");
    const orderDirectionArr = useRef([0, 0, 0, 0, 0, 0, 1]);
    const [orderDirection, setOrderDirection] = useState("ASC");
    const [searchByName, setSearchByName] = useState("");
    const [searchByEmbg, setSearchByEmbg] = useState("");
    const [searchByTel, setSearchByTel] = useState("");
    const [searchByCategory, setSearchByCategory] = useState("");
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
            sort: `${SORT_FIELD[order] ?? 'period.maturityDate'},${direction}`,
            active: 'true',
        });
        if (name) params.set('customerName', name);
        if (embg) params.set('customerEmbg', embg);
        if (tel) params.set('customerPhoneNumber', tel);
        if (cat) params.set('itemType', CATEGORY_TO_ITEM_TYPE[cat as PawnRowType['Category']] ?? cat);

        axios.get(`${API_BASE}/pawns?${params}`)
            .then(res => {
                const pawns: PawnRowType[] = (res.data.content ?? []).map(mapPawnResponse);
                const newUnique = pawns.filter(p => !fetchedPawnIds.current.has(`${p.Category}_${p.Id}`));
                newUnique.forEach(p => fetchedPawnIds.current.add(`${p.Category}_${p.Id}`));
                setAllPawns(prev => [...prev, ...newUnique]);
                setIsLastPage(res.data.page ? res.data.page.number >= res.data.page.totalPages - 1 : true);
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

    return {
        allPawns,
        loading,
        searchByCategory,
        setSearchByCategory,
        setSearchByName,
        setSearchByEmbg,
        setSearchByTel,
        scrollablePawnsRef,
        orderDirectionArr,
        handleOrder,
        onRefresh: () => setRefresh(prev => !prev),
        onRefreshCashReg: () => setRefreshCashReg(prev => !prev),
        refreshDependency: refresh,
        refreshCashRegDependency: refreshCashReg,
    };
}
