import { useEffect, useRef, useState } from "react";
import axios from "axios";
import { Bell, BellOff, Check, CheckCheck, AlertTriangle, Landmark, Info, FilePen, HandCoins } from "lucide-react";
import Loading from "../../shared/components/Loading.tsx";
import ModalReadMoreNotification from "./ModalReadMoreNotification.tsx";
import { NotificationRow, NotificationType, NOTIFICATION_TYPE_LABEL } from "./types.ts";
import { API_BASE } from "../../shared/api/config.ts";
import { useAuth } from "../../GlobalContext.tsx";

const datetime = (s: string | null | undefined) => (s ? String(s).substring(0, 16).replace("T", " ") : "—");

const TYPE_ICON: Record<NotificationType, React.ReactNode> = {
    RISK_FLAG: <AlertTriangle size={18} className="text-amber-500" />,
    CASH_REGISTER_SESSION_DISCREPANCY: <Landmark size={18} className="text-red-500" />,
    PAWN_UPDATED: <FilePen size={18} className="text-blue-500" />,
    STAFF_BONUS: <HandCoins size={18} className="text-green" />,
    SYSTEM: <Info size={18} className="text-[#888]" />,
};

export default function Notifications() {
    const { refreshUnreadCount } = useAuth();
    const [items, setItems] = useState<NotificationRow[]>([]);
    const [filterType, setFilterType] = useState("");
    const [filterRead, setFilterRead] = useState(""); // "" | "false" | "true"
    const [loading, setLoading] = useState(false);
    const [selectedId, setSelectedId] = useState<number | null>(null);

    const page = useRef(0);
    const size = 30;
    const [isLastPage, setIsLastPage] = useState(false);
    const isFetchingRef = useRef(false);
    const fetchedIds = useRef(new Set<number>());
    const scrollRef = useRef<HTMLDivElement>(null);

    const fetchPage = (pg: number, isLoading: boolean) => {
        if (isFetchingRef.current) return;
        isFetchingRef.current = true;
        setLoading(isLoading);
        const params = new URLSearchParams({ page: String(pg), size: String(size), sort: "createdAt,DESC" });
        if (filterType) params.set("type", filterType);
        if (filterRead) params.set("read", filterRead);
        axios.get(`${API_BASE}/notifications?${params}`)
            .then(res => {
                const fresh: NotificationRow[] = (res.data.content ?? []).filter((n: NotificationRow) => !fetchedIds.current.has(n.id));
                fresh.forEach(n => fetchedIds.current.add(n.id));
                setItems(prev => [...prev, ...fresh]);
                setIsLastPage(res.data.last ?? true);
            })
            .catch(err => console.error("Error fetching notifications:", err))
            .finally(() => { setLoading(false); isFetchingRef.current = false; });
    };

    const reload = () => {
        fetchedIds.current.clear();
        setItems([]);
        page.current = 0;
        setIsLastPage(false);
        fetchPage(0, true);
    };

    useEffect(() => {
        reload();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [filterType, filterRead]);

    useEffect(() => {
        const el = scrollRef.current;
        if (!el) return;
        const onScroll = () => {
            if (el.scrollHeight - el.scrollTop - el.clientHeight <= el.scrollHeight * 0.3 && !isLastPage && !isFetchingRef.current) {
                page.current += 1;
                fetchPage(page.current, false);
            }
        };
        el.addEventListener("scroll", onScroll);
        return () => el.removeEventListener("scroll", onScroll);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isLastPage, filterType, filterRead]);

    const markRead = (id: number) => {
        axios.post(`${API_BASE}/notifications/${id}/read`)
            .then(res => { setItems(prev => prev.map(n => n.id === id ? res.data : n)); refreshUnreadCount(); })
            .catch(err => console.error("Error marking read:", err));
    };

    const markAllRead = () => {
        axios.post(`${API_BASE}/notifications/read-all`)
            .then(() => { setItems(prev => prev.map(n => ({ ...n, read: true }))); refreshUnreadCount(); })
            .catch(err => console.error("Error marking all read:", err));
    };

    const onReadFromModal = (updated: NotificationRow) => {
        setItems(prev => prev.map(n => n.id === updated.id ? updated : n));
        refreshUnreadCount();
    };

    const hasUnread = items.some(n => !n.read);
    const inputClass = "bg-white border-none rounded text-xs font-medium px-2 py-2 shadow-sm w-full sm:w-auto sm:max-w-[200px]";

    return (
        <div className="h-screen flex md:pl-16 pt-12 md:pt-0">
            <div className="flex flex-col px-3 md:px-8 pt-2 gap-3 flex-1 overflow-hidden">
                <div className="flex items-center gap-3 flex-wrap">
                    <h1 className="flex items-center gap-2 text-xl font-semibold mr-auto"><Bell size={22} /> Известувања</h1>
                    <select className={inputClass} style={{ color: filterType ? "#000" : "#888" }} value={filterType} onChange={e => setFilterType(e.target.value)}>
                        <option value="">Сите типови</option>
                        {(Object.keys(NOTIFICATION_TYPE_LABEL) as NotificationType[]).map(t => <option key={t} value={t}>{NOTIFICATION_TYPE_LABEL[t]}</option>)}
                    </select>
                    <select className={inputClass} style={{ color: filterRead ? "#000" : "#888" }} value={filterRead} onChange={e => setFilterRead(e.target.value)}>
                        <option value="">Сите</option>
                        <option value="false">Непрочитани</option>
                        <option value="true">Прочитани</option>
                    </select>
                    <button onClick={markAllRead} disabled={!hasUnread}
                        className="flex items-center gap-2 px-4 py-2 rounded bg-green text-white text-sm font-medium shadow-[0_0_4px_rgba(0,0,0,0.2)] hover:scale-105 transition-all disabled:opacity-40 disabled:hover:scale-100">
                        <CheckCheck size={16} /> Означи ги сите како прочитани
                    </button>
                </div>

                <div ref={scrollRef} className="flex flex-col gap-2 flex-1 overflow-y-auto overflow-x-hidden scrollbar-thin pb-4">
                    {loading ? <Loading /> : items.length === 0 ? (
                        <div className="flex flex-1 flex-col items-center justify-center gap-3 text-[#888]">
                            <BellOff size={40} />
                            <p className="text-sm">Нема известувања.</p>
                        </div>
                    ) : items.map(n => (
                        <div key={n.id}
                            onClick={() => setSelectedId(n.id)}
                            className={`flex items-center gap-3 rounded-lg px-4 py-3 cursor-pointer shadow-[0_0_6px_rgba(0,0,0,0.12)] transition-colors ${n.read ? "bg-white hover:bg-black/[0.03]" : "bg-green/[0.06] hover:bg-green/10"}`}>
                            {!n.read && <span className="h-2 w-2 rounded-full bg-green shrink-0" />}
                            <span className="shrink-0">{TYPE_ICON[n.type]}</span>
                            <div className="flex flex-col min-w-0 flex-1">
                                <span className={`text-sm truncate ${n.read ? "font-medium" : "font-semibold"}`}>{n.title}</span>
                                {n.description && <span className="text-xs text-[#666] truncate">{n.description}</span>}
                            </div>
                            <span className="text-xs text-[#888] shrink-0">{datetime(n.createdAt)}</span>
                            {!n.read && (
                                <button title="Означи како прочитано"
                                    onClick={e => { e.stopPropagation(); markRead(n.id); }}
                                    className="shrink-0 text-[#888] hover:text-green transition-colors">
                                    <Check size={18} />
                                </button>
                            )}
                        </div>
                    ))}
                </div>
            </div>

            {selectedId != null && (
                <ModalReadMoreNotification
                    notificationId={selectedId}
                    closeModal={() => setSelectedId(null)}
                    onRead={onReadFromModal}
                />
            )}
        </div>
    );
}
