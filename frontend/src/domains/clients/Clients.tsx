import { useEffect, useRef, useState } from "react";
import axios from "axios";
import Nav from "../../shared/components/Nav.tsx";
import { ChevronUp, ChevronDown, Minus } from "lucide-react";
import CashRegister from "../../shared/components/CashRegister.tsx";
import Loading from "../../shared/components/Loading.tsx";
import ClientRow from "./Client.tsx";
import { ClientRow as ClientRowType } from "./types.ts";

function SortIcon({ dir }: { dir: number }) {
    return dir === 0 ? <Minus size={14} /> : dir === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />;
}

export default function Clients() {
    const [allClients, setAllClients] = useState<ClientRowType[]>([]);
    const [orderBy, setOrderBy] = useState("Active Pawns");
    const orderDirectionArr = useRef([0, 0, 0, 0, -1, 0, 0]);
    const [orderDirection, setOrderDirection] = useState("DESC");
    const [searchByName, setSearchByName] = useState("");
    const [searchByEmbg, setSearchByEmbg] = useState("");
    const [searchByTel, setSearchByTel] = useState("");
    const [refresh, setRefresh] = useState(false);
    const offset = useRef(0);
    const limit = 40;
    const [isLastPage, setIsLastPage] = useState(false);
    const scrollableClientsRef = useRef<HTMLDivElement>(null);
    const [loading, setLoading] = useState(false);
    const isFetchingRef = useRef(false);
    const [isFetching, setIsFetching] = useState(false);
    const fetchedClientIds = useRef(new Set<number>());

    const fetchClients = (limit: number, off: number, order: string, direction: string, name: string, embg: string, tel: string, isLoading: boolean) => {
        if (isFetching) return;
        setIsFetching(true);
        setLoading(isLoading);
        isFetchingRef.current = true;
        axios.get(`http://localhost:3000/clients?limit=${limit}&offset=${off}&orderBy=${order}&orderDirection=${direction}&searchByName=${name}&searchByEmbg=${embg}&searchByTel=${tel}`)
            .then(res => {
                const newUnique = res.data.filter((c: ClientRowType) => !fetchedClientIds.current.has(c.Id));
                newUnique.forEach((c: ClientRowType) => fetchedClientIds.current.add(c.Id));
                setAllClients(prev => [...prev, ...newUnique]);
                setIsLastPage(res.data.length < limit);
            })
            .catch(error => console.error("Error fetching clients:", error))
            .finally(() => { setLoading(false); isFetchingRef.current = false; setIsFetching(false); });
    };

    useEffect(() => {
        const el = scrollableClientsRef.current!;
        const handleScroll = () => {
            if (el.scrollHeight - el.scrollTop - el.clientHeight <= el.scrollHeight * 0.3 && !isLastPage && !isFetchingRef.current) {
                offset.current += limit;
                fetchClients(limit, offset.current, orderBy, orderDirection, searchByName, searchByEmbg, searchByTel, false);
            }
        };
        el.addEventListener("scroll", handleScroll);
        return () => el.removeEventListener("scroll", handleScroll);
    }, [isLastPage, refresh, orderBy, orderDirection, searchByName, searchByEmbg, searchByTel]);

    useEffect(() => {
        fetchedClientIds.current.clear();
        setAllClients([]);
        offset.current = 0;
        fetchClients(limit, 0, orderBy, orderDirection, searchByName, searchByEmbg, searchByTel, true);
    }, [refresh, orderBy, orderDirection, searchByName, searchByEmbg, searchByTel]);

    const handleOrder = (by: string, index: number) => {
        const newDir = new Array(orderDirectionArr.current.length).fill(0);
        newDir[index] = orderDirectionArr.current[index] === 0 ? 1 : orderDirectionArr.current[index] === 1 ? -1 : 1;
        orderDirectionArr.current = newDir;
        setOrderDirection(newDir.includes(-1) ? "DESC" : "ASC");
        setOrderBy(by);
    };

    const inputClass = "border-none rounded text-sm font-medium w-1/4 p-2 shadow-[0_0_8px_rgba(0,0,0,0.2)]";

    const headers: [string, string | null, number][] = [
        ["Ид", "Id", 0], ["Име", "Name", 1], ["Телефон", null, -1], ["Град", "City", 2],
        ["Вкупно залози", "Total Pawns", 3], ["Активни залози", "Active Pawns", 4],
        ["Вредност на залози", "Money Pawns", 5], ["Приход од провизија", "Money Provision", 6], ["Повеќе", null, -1],
    ];

    return (
        <div className="h-screen grid grid-cols-[max(15%,240px)_auto]">
            <Nav />
            <div className="flex flex-col px-8 pt-2 gap-3 flex-1 overflow-hidden">
                <div className="flex justify-between w-full">
                    <input className={inputClass} type="search" placeholder="Пребарувај по име" onChange={e => setSearchByName(e.target.value)} />
                    <input className={inputClass} type="search" placeholder="Пребарувај по ембг" onChange={e => setSearchByEmbg(e.target.value)} />
                    <input className={inputClass} type="search" placeholder="Пребарувај по телефон" onChange={e => setSearchByTel(e.target.value)} />
                </div>

                <div className="flex flex-col bg-white rounded-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] overflow-hidden flex-1 min-h-0">
                    <div className="grid place-items-center grid-cols-[3rem_repeat(8,1fr)] px-2 py-2 border-b-2 border-black/20 text-[#eee] bg-[#666]">
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

                    <div ref={scrollableClientsRef} className="overflow-y-auto overflow-x-hidden flex-1 scrollbar-thin">
                        {loading ? <Loading /> : allClients.map((client, index) => (
                            <ClientRow
                                key={index}
                                client={client}
                                index={index}
                                updateTelephones={(tel1, tel2) => setAllClients(
                                    prev => prev.map(c => c.Id === client.Id ? { ...c, "Telephone 1": tel1, "Telephone 2": tel2 } : c)
                                )}
                            />
                        ))}
                    </div>
                </div>

                <CashRegister refreshDependency={refresh} />
            </div>
        </div>
    );
}
