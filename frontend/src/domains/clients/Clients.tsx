import { useRef, useState } from "react";
import { ChevronUp, ChevronDown, Minus, Lock } from "lucide-react";
import Loading from "../../shared/components/Loading.tsx";
import ClientRow from "./Client.tsx";
import { Customer } from "./types.ts";
import { API_BASE } from "../../shared/api/config.ts";
import { useAuth } from "../../GlobalContext.tsx";
import { useDebounce } from "../../shared/utils/useDebounce.ts";
import { useInfiniteScroll } from "../../shared/utils/useInfiniteScroll.ts";

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

    const params = new URLSearchParams();
    params.set("sort", `${SORT_FIELD[orderBy] ?? "createdAt"},${orderDirection}`);
    if (name) params.set("fullName", name);
    if (embg) params.set("nationalId", embg);
    if (phone) params.set("phone", phone);
    if (city) params.set("city", city);

    const { items: allClients, setItems: setAllClients, loading, scrollRef } = useInfiniteScroll<Customer>({
        url: `${API_BASE}/customers`,
        params,
        getId: c => c.id,
        size: 40,
        enabled: allowed,
    });

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
        <div className="h-screen flex lg:pl-16 pt-12 lg:pt-0">
            <div className="flex flex-col px-3 md:px-8 py-2 max-lg:landscape:py-1 gap-3 max-lg:landscape:gap-1 flex-1 overflow-hidden">
                {!allowed ? (
                    <div className="flex flex-1 flex-col items-center justify-center gap-3 text-[#666]">
                        <Lock size={40} />
                        <p className="text-sm">Немате дозвола за преглед на клиенти.</p>
                    </div>
                ) : (
                    <>
                        <div className="flex flex-wrap md:flex-nowrap max-lg:landscape:flex-nowrap justify-between w-full gap-2 md:gap-3 max-lg:landscape:gap-1">
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

                            <div ref={scrollRef} className="overflow-y-auto overflow-x-hidden flex-1 scrollbar-thin min-w-[880px] md:min-w-0">
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
