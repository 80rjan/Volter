import { useEffect, useRef, useState } from "react";
import axios from "axios";
import Nav from "../../shared/components/Nav.tsx";
import { Plus, ChevronUp, ChevronDown, Minus } from "lucide-react";
import ModalAddNewSale from "./ModalAddNewSale.tsx";
import SaleRow from "./Sale.tsx";
import CashRegister from "../../shared/components/CashRegister.tsx";
import Loading from "../../shared/components/Loading.tsx";
import { SaleRow as SaleRowType } from "./types.ts";

function SortIcon({ dir }: { dir: number }) {
    return dir === 0 ? <Minus size={14} /> : dir === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />;
}

export default function Sales() {
    const [allSales, setAllSales] = useState<SaleRowType[]>([]);
    const [orderBy, setOrderBy] = useState("Date Bought");
    const orderDirectionArr = useRef([0, 0, -1]);
    const [orderDirection, setOrderDirection] = useState("DESC");
    const [modalAddNewSale, setModalAddNewSale] = useState(false);
    const [refresh, setRefresh] = useState(false);
    const offset = useRef(0);
    const limit = 60;
    const [isLastPage, setIsLastPage] = useState(false);
    const scrollableSalesRef = useRef<HTMLDivElement>(null);
    const [loading, setLoading] = useState(false);
    const isFetchingRef = useRef(false);
    const [isFetching, setIsFetching] = useState(false);
    const fetchedSaleIds = useRef(new Set<number>());

    const fetchSales = (limit: number, off: number, order: string, direction: string, isLoading: boolean) => {
        if (isFetching) return;
        setIsFetching(true);
        setLoading(isLoading);
        isFetchingRef.current = true;
        axios.get(`http://localhost:3000/sales?limit=${limit}&offset=${off}&orderBy=${order}&orderDirection=${direction}`)
            .then(res => {
                const newUnique = res.data.filter((s: SaleRowType) => !fetchedSaleIds.current.has(s.Id));
                newUnique.forEach((s: SaleRowType) => fetchedSaleIds.current.add(s.Id));
                setAllSales(prev => [...prev, ...newUnique]);
                setIsLastPage(res.data.length < limit);
            })
            .catch(error => console.error("Error fetching sales:", error))
            .finally(() => { setLoading(false); isFetchingRef.current = false; setIsFetching(false); });
    };

    useEffect(() => {
        const el = scrollableSalesRef.current!;
        const handleScroll = () => {
            if (el.scrollHeight - el.scrollTop - el.clientHeight <= el.scrollHeight * 0.3 && !isLastPage && !isFetchingRef.current) {
                offset.current += limit;
                fetchSales(limit, offset.current, orderBy, orderDirection, false);
            }
        };
        el.addEventListener("scroll", handleScroll);
        return () => el.removeEventListener("scroll", handleScroll);
    }, [isLastPage, refresh]);

    useEffect(() => {
        fetchedSaleIds.current.clear();
        setAllSales([]);
        offset.current = 0;
        fetchSales(limit, 0, orderBy, orderDirection, true);
    }, [refresh, orderBy, orderDirection]);

    const handleOrder = (by: string, index: number) => {
        const newDir = new Array(orderDirectionArr.current.length).fill(0);
        newDir[index] = orderDirectionArr.current[index] === 0 ? 1 : orderDirectionArr.current[index] === 1 ? -1 : 1;
        orderDirectionArr.current = newDir;
        setOrderDirection(newDir.includes(-1) ? "DESC" : "ASC");
        setOrderBy(by);
    };

    const headers: [string, string | null, number][] = [
        ["Опис", "About", 0], ["Вредност", "Item Cost", 1], ["Датум Купено", "Date Bought", 2],
        ["Акции", null, -1], ["Повеќе", null, -1],
    ];

    return (
        <div className="h-screen grid grid-cols-[max(15%,240px)_auto]">
            <Nav />
            <div className="flex flex-col px-8 pt-8 gap-3 flex-1 overflow-hidden">
                <div className="flex justify-between w-full">
                    <button
                        className="flex items-center gap-2 bg-green h-fit text-white rounded px-5 py-2 text-sm ml-auto shadow-[4px_2px_6px_rgba(0,0,0,0.2)] transition-all duration-300 hover:scale-105 group"
                        onClick={() => setModalAddNewSale(true)}
                    >
                        <Plus size={22} color="white" strokeWidth={3} className="transition-transform duration-500 group-hover:rotate-90" />
                        Внеси Нова Продажба
                    </button>
                    {modalAddNewSale && <ModalAddNewSale closeModal={() => setModalAddNewSale(false)} refresh={() => setRefresh(prev => !prev)} />}
                </div>

                <div className="flex flex-col bg-white rounded-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] overflow-hidden flex-1 min-h-0">
                    <div className="grid place-items-center grid-cols-[3fr_1fr_1fr_1.5fr_.5fr] px-2 py-2 border-b-2 border-black/20 text-[#eee] bg-[#666]">
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

                    <div ref={scrollableSalesRef} className="overflow-y-auto overflow-x-hidden flex-1 scrollbar-thin">
                        {loading ? <Loading /> : allSales.map((sale, index) => (
                            <SaleRow key={index} sale={sale} refresh={() => setRefresh(prev => !prev)} isOdd={index % 2 !== 0} />
                        ))}
                    </div>
                </div>

                <CashRegister refreshDependency={refresh} />
            </div>
        </div>
    );
}
