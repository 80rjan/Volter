import { useEffect, useRef, useState } from "react";
import axios from "axios";
import { Lock } from "lucide-react";
import Loading from "../../shared/components/Loading.tsx";
import { API_BASE } from "../../shared/api/config.ts";
import { useAuth } from "../../GlobalContext.tsx";
import { useTeam } from "../../shared/utils/useTeam.ts";
import { AuthEvent, AuthEventType } from "./types.ts";

const TYPE_MK: Record<AuthEventType, { label: string; cls: string }> = {
    LOGIN_SUCCESS: { label: "Најава", cls: "text-green" },
    LOGIN_FAILURE: { label: "Неуспешна најава", cls: "text-red-500" },
    LOGOUT: { label: "Одјава", cls: "text-[#555]" },
};

const datetime = (s: string | null | undefined) => (s ? String(s).substring(0, 19).replace("T", " ") : "—");

const cols = "grid-cols-[1.4fr_1.2fr_1.2fr_3fr_1.6fr]";

export default function AuthEvents() {
    const { can } = useAuth();
    const allowed = can("AUDIT_READ");
    const team = useTeam();

    const staffName = (id: number) => team.find(s => s.id === id)?.fullName ?? `#${id}`;

    const [events, setEvents] = useState<AuthEvent[]>([]);
    const page = useRef(0);
    const size = 50;
    const [isLastPage, setIsLastPage] = useState(false);
    const [loading, setLoading] = useState(false);
    const isFetchingRef = useRef(false);
    const fetchedIds = useRef(new Set<number>());
    const scrollableRef = useRef<HTMLDivElement>(null);

    const fetchEvents = (pg: number, isLoading: boolean) => {
        if (isFetchingRef.current) return;
        isFetchingRef.current = true;
        setLoading(isLoading);
        axios.get(`${API_BASE}/admin/auth-events`, { params: { page: pg, size, sort: "occurredAt,DESC" } })
            .then(res => {
                const fresh: AuthEvent[] = (res.data.content ?? []).filter((e: AuthEvent) => !fetchedIds.current.has(e.id));
                fresh.forEach(e => fetchedIds.current.add(e.id));
                setEvents(prev => [...prev, ...fresh]);
                setIsLastPage(res.data.last ?? true);
            })
            .catch(error => console.error("Error fetching auth events:", error))
            .finally(() => { setLoading(false); isFetchingRef.current = false; });
    };

    useEffect(() => {
        if (!allowed) return;
        fetchedIds.current.clear();
        setEvents([]);
        page.current = 0;
        fetchEvents(0, true);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [allowed]);

    useEffect(() => {
        const el = scrollableRef.current;
        if (!el) return;
        const handleScroll = () => {
            if (el.scrollHeight - el.scrollTop - el.clientHeight <= el.scrollHeight * 0.3 && !isLastPage && !isFetchingRef.current) {
                page.current += 1;
                fetchEvents(page.current, false);
            }
        };
        el.addEventListener("scroll", handleScroll);
        return () => el.removeEventListener("scroll", handleScroll);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isLastPage]);

    const headers = ["Вработен", "Тип", "IP адреса", "Уред", "Време"];

    return (
        <div className="h-screen flex lg:pl-16 pt-12 lg:pt-0">
            <div className="flex flex-col px-3 md:px-8 pt-2 max-lg:landscape:pt-1 gap-3 max-lg:landscape:gap-1 flex-1 overflow-hidden">
                {!allowed ? (
                    <div className="flex flex-1 flex-col items-center justify-center gap-3 text-[#666]">
                        <Lock size={40} />
                        <p className="text-sm">Немате дозвола за преглед на записи за најава.</p>
                    </div>
                ) : (
                    <div className="flex flex-col bg-white rounded-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] overflow-x-auto flex-1 min-h-0">
                        <div className={`grid place-items-center ${cols} min-w-[880px] md:min-w-0 gap-4 px-2 py-2 border-b-2 border-black/20 text-[#eee] bg-[#666] text-xs font-medium`}>
                            {headers.map((h, i) => <div key={i} className={i === 0 ? "justify-self-start pl-2" : ""}>{h}</div>)}
                        </div>
                        <div ref={scrollableRef} className="overflow-y-auto overflow-x-hidden flex-1 scrollbar-thin min-w-[880px] md:min-w-0">
                            {loading ? <Loading /> : events.length === 0 ? (
                                <p className="text-center text-sm text-[#888] py-6">Нема записи за прикажување.</p>
                            ) : events.map((e, index) => {
                                const t = TYPE_MK[e.type] ?? { label: e.type, cls: "" };
                                return (
                                    <div
                                        key={e.id}
                                        style={{ background: index % 2 === 1 ? "#f0f0f0" : "#ffffff" }}
                                        className={`grid place-items-center text-center ${cols} gap-4 px-2 py-1.5 border-b border-black/20`}
                                    >
                                        <p className="text-xs font-medium justify-self-start pl-2">{staffName(e.staffId)}</p>
                                        <p className={`text-xs font-semibold ${t.cls}`}>{t.label}</p>
                                        <p className="text-xs">{e.ipAddress || "—"}</p>
                                        <p className="text-xs truncate max-w-full" title={e.userAgent ?? ""}>{e.userAgent || "—"}</p>
                                        <p className="text-xs">{datetime(e.occurredAt)}</p>
                                    </div>
                                );
                            })}
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}
