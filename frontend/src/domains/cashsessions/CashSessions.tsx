import { useEffect, useMemo, useRef, useState } from "react";
import axios from "axios";
import { ChevronUp, ChevronDown, Minus, Ellipsis, Lock, Plus } from "lucide-react";
import Loading from "../../shared/components/Loading.tsx";
import CashRegister from "../../shared/components/CashRegister.tsx";
import ModalReadMoreCashSession from "./ModalReadMoreCashSession.tsx";
import ModalCreateCashRegister from "./ModalCreateCashRegister.tsx";
import { CashSession, Discrepancy } from "./types.ts";
import { API_BASE } from "../../shared/api/config.ts";
import { useAuth } from "../../GlobalContext.tsx";
import { useTeam } from "../../shared/utils/useTeam.ts";

const STATUS_MK: Record<string, { label: string; cls: string }> = {
    OPEN: { label: "Отворена", cls: "text-green" },
    CLOSED: { label: "Затворена", cls: "text-[#555]" },
};

const SORT_FIELD: Record<string, string> = {
    Code: "cashRegister.code",
    Status: "status",
    Opened: "openedAt",
    Closed: "closedAt",
};

const money = (n: number | null | undefined) => (n == null ? "—" : Number(n).toLocaleString("de-DE"));
const datetime = (s: string | null | undefined) => (s ? String(s).substring(0, 16).replace("T", " ") : "—");

function DiscrepancyCell({ d }: { d: Discrepancy | undefined }) {
    if (!d) return <p className="text-xs text-[#aaa]">—</p>;
    const over = d.difference > 0;
    return (
        <p className={`text-xs font-bold ${over ? "text-green" : "text-red-500"}`}>
            {over ? "+" : ""}{Number(d.difference).toLocaleString("de-DE")}
        </p>
    );
}

function SortIcon({ dir }: { dir: number }) {
    return dir === 0 ? <Minus size={14} /> : dir === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />;
}

interface RegisterOption { id: number; code: string; }
const cols = "grid-cols-[1fr_1fr_1.4fr_1.4fr_1fr_1fr_1.2fr_0.5fr]";

export default function CashSessions() {
    const { can } = useAuth();
    const allowed = can("CASH_REGISTER_SESSION_READ");
    const canDiscRead = can("CASH_REGISTER_SESSION_DISCREPANCY_READ");
    const canManage = can("CASH_REGISTER_MANAGE");

    const [sessions, setSessions] = useState<CashSession[]>([]);
    const [discMap, setDiscMap] = useState<Record<number, Discrepancy>>({});
    const [registers, setRegisters] = useState<RegisterOption[]>([]);
    const [orderBy, setOrderBy] = useState("Opened");
    const orderDirectionArr = useRef([0, 0, -1, 0, 0, 0, 0, 0]);
    const [orderDirection, setOrderDirection] = useState("DESC");

    // filters
    const team = useTeam();
    const [filterStaff, setFilterStaff] = useState("");
    const [filterRegister, setFilterRegister] = useState("");
    const [filterStatus, setFilterStatus] = useState("");
    const [filterDiscrepancy, setFilterDiscrepancy] = useState(""); // "" | WITH | WITHOUT
    const [filterFrom, setFilterFrom] = useState("");
    const [filterTo, setFilterTo] = useState("");

    const page = useRef(0);
    const size = 40;
    const [isLastPage, setIsLastPage] = useState(false);
    const scrollableRef = useRef<HTMLDivElement>(null);
    const [loading, setLoading] = useState(false);
    const isFetchingRef = useRef(false);
    const fetchedIds = useRef(new Set<number>());

    const [selected, setSelected] = useState<CashSession | null>(null);
    const [modalDetail, setModalDetail] = useState(false);
    const [showCreate, setShowCreate] = useState(false);

    const fetchRegisters = () => {
        axios.get(`${API_BASE}/cash-registers`)
            .then(res => setRegisters(res.data ?? []))
            .catch(() => setRegisters([]));
    };

    useEffect(() => {
        if (!allowed) return;
        fetchRegisters();
    }, [allowed]);

    const fetchSessions = (pg: number, order: string, direction: string, isLoading: boolean) => {
        if (isFetchingRef.current) return;
        isFetchingRef.current = true;
        setLoading(isLoading);
        const params = new URLSearchParams({
            page: String(pg),
            size: String(size),
            sort: `${SORT_FIELD[order] ?? "openedAt"},${direction}`,
        });
        if (filterStaff) params.set("staffId", filterStaff);
        if (filterRegister) params.set("cashRegisterId", filterRegister);
        if (filterStatus) params.set("status", filterStatus);
        if (filterFrom) params.set("openedFrom", `${filterFrom}T00:00:00Z`);
        if (filterTo) params.set("openedTo", `${filterTo}T23:59:59Z`);
        axios.get(`${API_BASE}/cash-register-sessions?${params}`)
            .then(res => {
                const fresh: CashSession[] = (res.data.content ?? []).filter((s: CashSession) => !fetchedIds.current.has(s.id));
                fresh.forEach(s => fetchedIds.current.add(s.id));
                setSessions(prev => [...prev, ...fresh]);
                setIsLastPage(res.data.last ?? true);
            })
            .catch(error => console.error("Error fetching cash sessions:", error))
            .finally(() => { setLoading(false); isFetchingRef.current = false; });
    };

    const fetchDiscrepancies = () => {
        if (!canDiscRead) return;
        axios.get(`${API_BASE}/cash-register-session-discrepancies`, { params: { size: 500 } })
            .then(res => {
                const map: Record<number, Discrepancy> = {};
                (res.data.content ?? []).forEach((d: Discrepancy) => {
                    if (!map[d.sessionId] || d.status === "OPEN") map[d.sessionId] = d;
                });
                setDiscMap(map);
            })
            .catch(error => console.error("Error fetching discrepancies:", error));
    };

    useEffect(() => {
        const el = scrollableRef.current;
        if (!el) return;
        const handleScroll = () => {
            if (el.scrollHeight - el.scrollTop - el.clientHeight <= el.scrollHeight * 0.3 && !isLastPage && !isFetchingRef.current) {
                page.current += 1;
                fetchSessions(page.current, orderBy, orderDirection, false);
            }
        };
        el.addEventListener("scroll", handleScroll);
        return () => el.removeEventListener("scroll", handleScroll);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isLastPage, orderBy, orderDirection, filterStaff, filterRegister, filterStatus, filterFrom, filterTo]);

    useEffect(() => {
        if (!allowed) return;
        fetchedIds.current.clear();
        setSessions([]);
        page.current = 0;
        fetchSessions(0, orderBy, orderDirection, true);
        fetchDiscrepancies();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [allowed, orderBy, orderDirection, filterStaff, filterRegister, filterStatus, filterFrom, filterTo]);

    const handleOrder = (by: string, index: number) => {
        const newDir = new Array(orderDirectionArr.current.length).fill(0);
        newDir[index] = orderDirectionArr.current[index] === 1 ? -1 : 1;
        orderDirectionArr.current = newDir;
        setOrderDirection(newDir[index] === -1 ? "DESC" : "ASC");
        setOrderBy(by);
    };

    // Reload the table + discrepancies after a cash-register action from the bar below.
    const reloadAll = () => {
        fetchedIds.current.clear();
        setSessions([]);
        page.current = 0;
        setIsLastPage(false);
        fetchSessions(0, orderBy, orderDirection, true);
        fetchDiscrepancies();
    };

    const onResolved = (d: Discrepancy) => setDiscMap(prev => ({ ...prev, [d.sessionId]: d }));

    // Discrepancy present/absent is a client-side filter over the loaded rows.
    const visibleSessions = useMemo(() => {
        if (!filterDiscrepancy) return sessions;
        return sessions.filter(s => filterDiscrepancy === "WITH" ? !!discMap[s.id] : !discMap[s.id]);
    }, [sessions, discMap, filterDiscrepancy]);

    const clearFilters = () => { setFilterStaff(""); setFilterRegister(""); setFilterStatus(""); setFilterDiscrepancy(""); setFilterFrom(""); setFilterTo(""); };
    const hasFilters = filterStaff || filterRegister || filterStatus || filterDiscrepancy || filterFrom || filterTo;
    const inputClass = "bg-white border-none rounded text-xs font-medium px-2 py-2 shadow-sm flex-1 min-w-[140px] md:min-w-0";

    const headers: [string, string | null, number][] = [
        ["Каса", "Code", 0], ["Статус", "Status", 1], ["Отворена", "Opened", 2], ["Затворена", "Closed", 3],
        ["Почетно", null, -1], ["Состојба", null, -1], ["Отстапување", null, -1], ["", null, -1],
    ];

    return (
        <div className="h-screen flex lg:pl-16 pt-12 lg:pt-0">
            <div className="flex flex-col px-3 md:px-8 pt-2 max-lg:landscape:pt-1 gap-3 max-lg:landscape:gap-1 flex-1 overflow-hidden">
                {!allowed ? (
                    <div className="flex flex-1 flex-col items-center justify-center gap-3 text-[#666]">
                        <Lock size={40} />
                        <p className="text-sm">Немате дозвола за преглед на сесии на каса.</p>
                    </div>
                ) : (
                    <>
                        <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-[1.5fr_1fr_1fr_1fr_1fr_1fr_1fr_1.5fr] max-lg:landscape:grid-cols-[1.5fr_1fr_1fr_1fr_1fr_1fr_1fr_1.5fr] w-full gap-2 md:gap-3">
                            <select className={inputClass} style={{ color: filterStaff ? "#000" : "#888" }} value={filterStaff} onChange={e => setFilterStaff(e.target.value)}>
                                <option value="">Сите вработени</option>
                                {team.map(s => <option key={s.id} value={s.id}>{s.fullName}</option>)}
                            </select>
                            <select className={inputClass} style={{ color: filterRegister ? "#000" : "#888" }} value={filterRegister} onChange={e => setFilterRegister(e.target.value)}>
                                <option value="">Сите каси</option>
                                {registers.map(r => <option key={r.id} value={r.id}>{r.code}</option>)}
                            </select>
                            <select className={inputClass} style={{ color: filterStatus ? "#000" : "#888" }} value={filterStatus} onChange={e => setFilterStatus(e.target.value)}>
                                <option value="">Сите статуси</option>
                                <option value="OPEN">Отворена</option>
                                <option value="CLOSED">Затворена</option>
                            </select>
                            {canDiscRead && (
                                <select className={inputClass} style={{ color: filterDiscrepancy ? "#000" : "#888" }} value={filterDiscrepancy} onChange={e => setFilterDiscrepancy(e.target.value)}>
                                    <option value="">Сите отстапувања</option>
                                    <option value="WITH">Со отстапување</option>
                                    <option value="WITHOUT">Без отстапување</option>
                                </select>
                            )}
                            <div className="flex items-center gap-2">
                                <span className="text-xs text-[#666]">Од</span>
                                <input type="date" style={{color: filterFrom ? "#000": "#888"}} className={inputClass} value={filterFrom} onChange={e => setFilterFrom(e.target.value)} />
                            </div>
                            <div className="flex items-center gap-2">
                                <span className="text-xs text-[#666]">До</span>
                                <input type="date" style={{color: filterTo ? "#000": "#888"}} className={inputClass} value={filterTo} onChange={e => setFilterTo(e.target.value)} />
                            </div>
                            {hasFilters && (
                                <button onClick={clearFilters} className="whitespace-nowrap text-xs text-[#666] underline hover:text-black transition-colors">Исчисти филтри</button>
                            )}
                            {canManage && (
                                <div className="flex xl:col-start-8 max-lg:landscape:col-start-8 justify-center">
                                    <button onClick={() => setShowCreate(true)}
                                            className="group relative overflow-hidden whitespace-nowrap flex items-center gap-2 px-4 py-2 rounded bg-green text-white text-sm font-semibold shadow-[4px_2px_6px_rgba(0,0,0,0.2)] transition-all duration-300 hover:scale-105">
                                        <span className="pointer-events-none absolute inset-0 -translate-x-full bg-gradient-to-r from-transparent via-white/25 to-transparent transition-transform duration-700 ease-out group-hover:translate-x-[180%]" />
                                        <Plus size={16} color="white" strokeWidth={3} className="transition-transform duration-500 group-hover:rotate-90" />
                                        Креирај нова каса
                                    </button>
                                </div>
                            )}
                        </div>

                        <div className="flex flex-col bg-white rounded-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] overflow-x-auto flex-1 min-h-0">
                            <div className={`grid place-items-center ${cols} min-w-[880px] md:min-w-0 gap-2 px-2 py-2 border-b-2 border-black/20 text-[#eee] bg-[#666]`}>
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
                                {loading ? <Loading /> : visibleSessions.length === 0 ? (
                                    <p className="text-center text-sm text-[#888] py-6">Нема сесии за прикажување.</p>
                                ) : visibleSessions.map((s, index) => {
                                    const status = STATUS_MK[s.status] ?? { label: s.status, cls: "" };
                                    return (
                                        <div
                                            key={s.id}
                                            style={{ background: index % 2 === 1 ? "#f0f0f0" : "#ffffff" }}
                                            className={`grid place-items-center text-center ${cols} gap-2 px-2 py-1 border-b border-black/20`}
                                        >
                                            <p className="text-xs font-semibold">{s.cashRegisterCode}</p>
                                            <p className={`text-xs font-semibold ${status.cls}`}>{status.label}</p>
                                            <p className="text-xs">{datetime(s.openedAt)}</p>
                                            <p className="text-xs">{datetime(s.closedAt)}</p>
                                            <p className="text-xs">{money(s.openingBalance)}</p>
                                            <p className="text-xs font-bold italic">{money(s.status === "CLOSED" ? s.closingBalance : s.currentBalance)}</p>
                                            <DiscrepancyCell d={discMap[s.id]} />
                                            <Ellipsis size={18} color="#888" className="cursor-pointer" onClick={() => { setSelected(s); setModalDetail(true); }} />
                                        </div>
                                    );
                                })}
                            </div>
                        </div>

                        {/* Live current-session drawer (open/deposit/withdraw/close), like the other pages. */}
                        <CashRegister refreshTransactions={reloadAll} />
                    </>
                )}
            </div>

            {modalDetail && selected && (
                <ModalReadMoreCashSession
                    session={selected}
                    discrepancy={discMap[selected.id] ?? null}
                    onResolved={onResolved}
                    closeModal={() => { setModalDetail(false); setSelected(null); }}
                />
            )}

            {showCreate && (
                <ModalCreateCashRegister
                    closeModal={() => setShowCreate(false)}
                    onCreated={() => { fetchRegisters(); reloadAll(); }}
                />
            )}
        </div>
    );
}
