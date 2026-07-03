import {useEffect, useMemo, useState} from "react";
import axios from "axios";
import {Ellipsis, Lock, Plus, ChevronUp, ChevronDown, Minus} from "lucide-react";
import CashRegister from "../../shared/components/CashRegister.tsx";
import Loading from "../../shared/components/Loading.tsx";
import ModalAddNewStaff from "./ModalAddNewStaff.tsx";
import ModalReadMoreStaff from "./ModalReadMoreStaff.tsx";
import {StaffRow, StaffStatus, STAFF_STATUS_LABEL} from "./types.ts";
import {API_BASE} from "../../shared/api/config.ts";
import {useAuth} from "../../GlobalContext.tsx";

const STATUS_CLS: Record<StaffStatus, string> = {
    ACTIVE: "text-green",
    INACTIVE: "text-[#555]",
    SUSPENDED: "text-red-500",
};

const cols = "grid-cols-[3rem_1.6fr_1.2fr_1fr_1.4fr_1.1fr_0.5fr]";

const sortValue = (s: StaffRow, by: string): number | string =>
    by === "Id" ? s.id
        : by === "Name" ? s.fullName.toLowerCase()
            : by === "Username" ? s.username.toLowerCase()
                : by === "Status" ? s.status
                    : by === "Date" ? s.createdAt
                        : s.id;

function SortIcon({dir}: { dir: number }) {
    return dir === 0 ? <Minus size={14}/> : dir === -1 ? <ChevronDown size={14}/> : <ChevronUp size={14}/>;
}

export default function Staff() {
    const {can} = useAuth();
    const allowed = can("STAFF_MANAGE");

    const [allStaff, setAllStaff] = useState<StaffRow[]>([]);
    const [loading, setLoading] = useState(false);
    const [searchName, setSearchName] = useState("");
    const [searchUsername, setSearchUsername] = useState("");
    const [filterStatus, setFilterStatus] = useState("");
    const [includeDeleted, setIncludeDeleted] = useState(false);
    const [refresh, setRefresh] = useState(false);

    const [modalAdd, setModalAdd] = useState(false);
    const [selectedId, setSelectedId] = useState<number | null>(null);

    // Staff is a small dataset: fetch everyone once (incl. deleted, for manager
    // name resolution) and filter client-side.
    useEffect(() => {
        if (!allowed) return;
        setLoading(true);
        axios.get(`${API_BASE}/admin/staff`, {params: {size: 1000, includeDeleted: true, sort: "fullName,ASC"}})
            .then(res => setAllStaff(res.data.content ?? []))
            .catch(error => console.error("Error fetching staff:", error))
            .finally(() => setLoading(false));
    }, [allowed, refresh]);

    const managerName = useMemo(() => {
        const m = new Map<number, string>();
        allStaff.forEach(s => m.set(s.id, s.fullName));
        return m;
    }, [allStaff]);

    const rows = useMemo(() => {
        const name = searchName.trim().toLowerCase();
        const uname = searchUsername.trim().toLowerCase();
        return allStaff.filter(s =>
            (includeDeleted || !s.deletedAt) &&
            (!filterStatus || s.status === filterStatus) &&
            (!name || s.fullName.toLowerCase().includes(name)) &&
            (!uname || s.username.toLowerCase().includes(uname))
        );
    }, [allStaff, searchName, searchUsername, filterStatus, includeDeleted]);

    const [orderBy, setOrderBy] = useState("Name");
    const [orderDir, setOrderDir] = useState(1); // 1 asc, -1 desc

    const sorted = useMemo(() => [...rows].sort((a, b) => {
        const va = sortValue(a, orderBy), vb = sortValue(b, orderBy);
        return (va < vb ? -1 : va > vb ? 1 : 0) * orderDir;
    }), [rows, orderBy, orderDir]);

    const handleOrder = (by: string) => {
        if (orderBy === by) setOrderDir(d => -d);
        else {
            setOrderBy(by);
            setOrderDir(1);
        }
    };
    const dirOf = (by: string) => (orderBy === by ? orderDir : 0);

    const doRefresh = () => setRefresh(p => !p);
    const inputClass = "bg-white border-none rounded text-xs font-medium px-2 py-2 shadow-sm flex-1 min-w-[140px] md:min-w-0";

    return (
        <div className="h-screen flex lg:pl-16 pt-12 lg:pt-0">
            <div className="flex flex-col px-3 md:px-8 py-2 gap-3 flex-1 overflow-hidden">
                {!allowed ? (
                    <div className="flex flex-1 flex-col items-center justify-center gap-3 text-[#666]">
                        <Lock size={40}/>
                        <p className="text-sm">Немате дозвола за управување со вработени.</p>
                    </div>
                ) : (
                    <>
                        <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-[2fr_2fr_2fr_1.5fr_1fr_2fr] w-full gap-2 md:gap-3">
                            <input className={inputClass} type="search" placeholder="Пребарувај по име"
                                   value={searchName} onChange={e => setSearchName(e.target.value)}/>
                            <input className={inputClass} type="search" placeholder="Пребарувај по корисничко"
                                   value={searchUsername} onChange={e => setSearchUsername(e.target.value)}/>
                            <select className={inputClass} style={{color: filterStatus === "" ? "#888" : "#000"}}
                                    value={filterStatus} onChange={e => setFilterStatus(e.target.value)}>
                                <option value="">Сите статуси</option>
                                {(Object.keys(STAFF_STATUS_LABEL) as StaffStatus[]).map(s => <option key={s}
                                                                                                     value={s}>{STAFF_STATUS_LABEL[s]}</option>)}
                            </select>
                            <label
                                className="flex justify-center items-center gap-2 text-xs text-[#666] cursor-pointer">
                                <input type="checkbox" checked={includeDeleted}
                                       onChange={e => setIncludeDeleted(e.target.checked)}/>
                                Прикажи избришани
                            </label>
                            {(searchName || searchUsername || filterStatus || includeDeleted) && (
                                <button
                                    onClick={() => {
                                        setSearchName("");
                                        setSearchUsername("");
                                        setFilterStatus("");
                                        setIncludeDeleted(false);
                                    }}
                                    className="text-xs text-[#666] underline hover:text-black transition-colors"
                                >
                                    Исчисти филтри
                                </button>
                            )}

                            <div className="w-full flex justify-center xl:col-start-6">
                                <button
                                    className="w-fit group relative overflow-hidden flex items-center gap-2 bg-green h-fit text-white rounded px-5 py-2 text-sm shadow-[4px_2px_6px_rgba(0,0,0,0.2)] transition-all duration-300 hover:scale-105 shrink-0"
                                    onClick={() => setModalAdd(true)}
                                >
                                <span
                                    className="pointer-events-none absolute inset-0 -translate-x-full bg-gradient-to-r from-transparent via-white/25 to-transparent transition-transform duration-700 ease-out group-hover:translate-x-[180%]"/>
                                    <Plus size={18} color="white" strokeWidth={3}
                                          className="transition-transform duration-500 group-hover:rotate-90"/>
                                    Внеси нов вработен
                                </button>
                            </div>
                        </div>

                        <div
                            className="flex flex-col bg-white rounded-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] overflow-x-auto flex-1 min-h-0">
                            <div
                                className={`grid place-items-center ${cols} min-w-[880px] md:min-w-0 gap-2 px-2 py-2 border-b-2 border-black/20 text-[#eee] bg-[#666] text-xs font-medium`}>
                                <div className="flex items-center cursor-pointer"
                                     onClick={() => handleOrder("Id")}>Код <SortIcon dir={dirOf("Id")}/></div>
                                <div className="flex items-center cursor-pointer"
                                     onClick={() => handleOrder("Name")}>Име <SortIcon dir={dirOf("Name")}/></div>
                                <div className="flex items-center cursor-pointer"
                                     onClick={() => handleOrder("Username")}>Корисничко име<SortIcon
                                    dir={dirOf("Username")}/></div>
                                <div className="flex items-center cursor-pointer"
                                     onClick={() => handleOrder("Status")}>Статус <SortIcon dir={dirOf("Status")}/>
                                </div>
                                <div>Менаџер</div>
                                <div className="flex items-center cursor-pointer"
                                     onClick={() => handleOrder("Date")}>Креиран <SortIcon dir={dirOf("Date")}/></div>
                                <div></div>
                            </div>

                            <div className="overflow-y-auto overflow-x-hidden flex-1 scrollbar-thin min-w-[880px] md:min-w-0 svg-hover">
                                {loading ? <Loading/> : sorted.length === 0 ? (
                                    <p className="text-center text-sm text-[#888] py-6">Нема вработени за
                                        прикажување.</p>
                                ) : sorted.map((s, index) => (
                                    <div
                                        key={s.id}
                                        style={{background: index % 2 === 1 ? "#f0f0f0" : "#ffffff"}}
                                        className={`grid place-items-center text-center ${cols} gap-2 px-2 py-1 border-b border-black/20 ${s.deletedAt ? "opacity-50" : ""}`}
                                    >
                                        <p className="text-xs">{s.id}</p>
                                        <p className="text-xs font-semibold">{s.fullName}</p>
                                        <p className="text-xs">{s.username}</p>
                                        <p className={`text-xs font-semibold ${STATUS_CLS[s.status]}`}>{STAFF_STATUS_LABEL[s.status]}</p>
                                        <p className="text-xs">{s.managerId ? (managerName.get(s.managerId) ?? `#${s.managerId}`) : "—"}</p>
                                        <p className="text-xs">{String(s.createdAt).substring(0, 10)}</p>
                                        <Ellipsis size={18} color="#888" className="cursor-pointer"
                                                  onClick={() => setSelectedId(s.id)}/>
                                    </div>
                                ))}
                            </div>
                        </div>

                    </>
                )}
            </div>

            {modalAdd && (
                <ModalAddNewStaff
                    managers={allStaff.filter(s => !s.deletedAt)}
                    closeModal={() => setModalAdd(false)}
                    refresh={doRefresh}
                />
            )}
            {selectedId != null && (
                <ModalReadMoreStaff
                    staffId={selectedId}
                    managers={allStaff.filter(s => !s.deletedAt && s.id !== selectedId)}
                    managerName={managerName}
                    closeModal={() => setSelectedId(null)}
                    refresh={doRefresh}
                />
            )}
        </div>
    );
}
