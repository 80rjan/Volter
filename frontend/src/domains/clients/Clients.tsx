import { useEffect, useRef, useState } from "react";
import axios from "axios";
import { ChevronUp, ChevronDown, Minus, Lock } from "lucide-react";
import CashRegister from "../../shared/components/CashRegister.tsx";
import Loading from "../../shared/components/Loading.tsx";
import ClientRow from "./Client.tsx";
import { Customer } from "./types.ts";
import { API_BASE } from "../../shared/api/config.ts";
import { useAuth } from "../../GlobalContext.tsx";
import { useDebounce } from "../../shared/utils/useDebounce.ts";

const SORT_FIELD: Record<string, string> = {
    Id: "id",
    Name: "fullName",
    Embg: "nationalId",
    City: "city",
    Date: "createdAt",
};

function SortIcon({ dir }: { dir: number }) {
    return dir === 0 ? <Minus size={14} /> : dir === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />;
}

const cols = "grid-cols-[3rem_1.6fr_1.2fr_1.5fr_1fr_1.1fr_0.5fr]";

export default function Clients() {
    const { can } = useAuth();
    const allowed = can("CUSTOMER_READ");

    const [allClients, setAllClients] = useState<Customer[]>([]);
    const [orderBy, setOrderBy] = useState("Date");
    const orderDirectionArr = useRef([0, 0, 0, 0, 0, -1, 0]);
    const [orderDirection, setOrderDirection] = useState("DESC");
    const [searchName, setSearchName] = useState("");
    const [searchEmbg, setSearchEmbg] = useState("");
    const [searchPhone, setSearchPhone] = useState("");
    const [searchCity, setSearchCity] = useState("");
    const name = useDebounce(searchName, 350);
    const embg = useDebounce(searchEmbg, 350);
    const phone = useDebounce(searchPhone, 350);
    const city = useDebounce(searchCity, 350);

    const page = useRef(0);
    const size = 40;
    const [isLastPage, setIsLastPage] = useState(false);
    const scrollableRef = useRef<HTMLDivElement>(null);
    const [loading, setLoading] = useState(false);
    const isFetchingRef = useRef(false);
    const fetchedIds = useRef(new Set<number>());

    const fetchClients = (pg: number, order: string, direction: string, isLoading: boolean) => {
        if (isFetchingRef.current) return;
        isFetchingRef.current = true;
        setLoading(isLoading);
        const params = new URLSearchParams({
            page: String(pg),
            size: String(size),
            sort: `${SORT_FIELD[order] ?? "createdAt"},${direction}`,
        });
        if (name) params.set("fullName", name);
        if (embg) params.set("nationalId", embg);
        if (phone) params.set("phone", phone);
        if (city) params.set("city", city);
        axios.get(`${API_BASE}/customers?${params}`)
            .then(res => {
                const newUnique = (res.data.content ?? []).filter((c: Customer) => !fetchedIds.current.has(c.id));
                newUnique.forEach((c: Customer) => fetchedIds.current.add(c.id));
                setAllClients(prev => [...prev, ...newUnique]);
                setIsLastPage(res.data.page ? res.data.page.number >= res.data.page.totalPages - 1 : true);
            })
            .catch(error => console.error("Error fetching customers:", error))
            .finally(() => { setLoading(false); isFetchingRef.current = false; });
    };

    useEffect(() => {
        const el = scrollableRef.current;
        if (!el) return;
        const handleScroll = () => {
            if (el.scrollHeight - el.scrollTop - el.clientHeight <= el.scrollHeight * 0.3 && !isLastPage && !isFetchingRef.current) {
                page.current += 1;
                fetchClients(page.current, orderBy, orderDirection, false);
            }
        };
        el.addEventListener("scroll", handleScroll);
        return () => el.removeEventListener("scroll", handleScroll);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isLastPage, orderBy, orderDirection, name, embg, phone, city]);

    useEffect(() => {
        if (!allowed) return;
        fetchedIds.current.clear();
        setAllClients([]);
        page.current = 0;
        fetchClients(0, orderBy, orderDirection, true);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [allowed, orderBy, orderDirection, name, embg, phone, city]);

    const handleOrder = (by: string, index: number) => {
        const newDir = new Array(orderDirectionArr.current.length).fill(0);
        newDir[index] = orderDirectionArr.current[index] === 1 ? -1 : 1;
        orderDirectionArr.current = newDir;
        setOrderDirection(newDir[index] === -1 ? "DESC" : "ASC");
        setOrderBy(by);
    };

    // Replace a customer in place after an edit, keeping scroll position.
    const onUpdated = (updated: Customer) =>
        setAllClients(prev => prev.map(c => (c.id === updated.id ? updated : c)));

    const inputClass = "bg-white border-none rounded text-xs font-medium px-2 py-2 shadow-sm flex-1 min-w-[140px] md:min-w-0";

    const headers: [string, string | null, number][] = [
        ["Код", "Id", 0], ["Име", "Name", 1], ["ЕМБГ", "Embg", 2], ["Телефон", null, -1],
        ["Град", "City", 4], ["Креиран", "Date", 5], ["Повеќе", null, -1],
    ];

    return (
        <div className="h-screen flex md:pl-16 pt-12 md:pt-0">
            <div className="flex flex-col px-3 md:px-8 py-2 gap-3 flex-1 overflow-hidden">
                {!allowed ? (
                    <div className="flex flex-1 flex-col items-center justify-center gap-3 text-[#666]">
                        <Lock size={40} />
                        <p className="text-sm">Немате дозвола за преглед на клиенти.</p>
                    </div>
                ) : (
                    <>
                        <div className="flex flex-wrap md:flex-nowrap justify-between w-full gap-2 md:gap-3">
                            <input className={inputClass} type="search" placeholder="Пребарувај по име" value={searchName} onChange={e => setSearchName(e.target.value)} />
                            <input className={inputClass} type="search" placeholder="Пребарувај по ембг" value={searchEmbg} onChange={e => setSearchEmbg(e.target.value)} />
                            <input className={inputClass} type="search" placeholder="Пребарувај по телефон" value={searchPhone} onChange={e => setSearchPhone(e.target.value)} />
                            <input className={inputClass} type="search" placeholder="Пребарувај по град" value={searchCity} onChange={e => setSearchCity(e.target.value)} />
                        </div>

                        <div className="flex flex-col bg-white rounded-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] overflow-x-auto flex-1 min-h-0">
                            <div className={`grid place-items-center ${cols} min-w-[880px] md:min-w-0 px-2 py-2 border-b-2 border-black/20 text-[#eee] bg-[#666]`}>
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

                            <div ref={scrollableRef} className="overflow-y-auto overflow-x-hidden flex-1 scrollbar-thin min-w-[880px] md:min-w-0">
                                {loading ? <Loading /> : allClients.map((client, index) => (
                                    <ClientRow key={client.id} client={client} isOdd={index % 2 === 1} onUpdated={onUpdated} cols={cols} />
                                ))}
                            </div>
                        </div>

                    </>
                )}
            </div>
        </div>
    );
}
