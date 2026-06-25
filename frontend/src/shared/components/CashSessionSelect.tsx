import { useEffect, useState } from "react";
import axios from "axios";
import { API_BASE } from "../api/config.ts";
import { getActiveRegisterId, setActiveRegisterId } from "../utils/activeSession.ts";
import { CashSession } from "../types.ts";

interface Props {
    value: number | null;
    onChange: (sessionId: number | null) => void;
    onLoaded?: (openCount: number) => void;
    className?: string;
    // Inline = compact bordered select to sit next to a section title (like
    // ItemTypeSelect). Default = a labeled field for grids/stacked layouts.
    inline?: boolean;
}

/**
 * Lets the user pick which OPEN cash-register session an action records into,
 * instead of silently using whichever register is "active". Defaults to the
 * active register's open session (or the first open one) and keeps the global
 * active register in sync with the choice, so the existing session resolution
 * keeps working. Renders nothing when no session is open (the caller shows its
 * own "no open register" warning).
 */
export default function CashSessionSelect({ value, onChange, onLoaded, className, inline }: Props) {
    const [sessions, setSessions] = useState<CashSession[]>([]);
    const [loaded, setLoaded] = useState(false);

    useEffect(() => {
        axios.get(`${API_BASE}/cash-register-sessions`, { params: { status: "OPEN", size: 200, sort: "cashRegister.code,ASC" } })
            .then(res => {
                const open: CashSession[] = (res.data.content ?? []).filter((s: CashSession) => s.status === "OPEN");
                setSessions(open);
                const active = getActiveRegisterId();
                const def = (active != null ? open.find(s => s.cashRegisterId === active) : undefined) ?? open[0];
                if (def) { setActiveRegisterId(def.cashRegisterId); onChange(def.id); }
                else onChange(null);
                setLoaded(true);
                onLoaded?.(open.length);
            })
            .catch(() => { setSessions([]); onChange(null); setLoaded(true); onLoaded?.(0); });
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    if (loaded && sessions.length === 0) return null;

    const pick = (sessionId: number) => {
        const s = sessions.find(x => x.id === sessionId);
        if (s) setActiveRegisterId(s.cashRegisterId);
        onChange(sessionId);
    };

    if (inline) {
        return (
            <div className={`flex items-center gap-2 ${className ?? ""}`}>
                <span className="text-[#666] text-sm">Каса:</span>
                <select
                    className="bg-white rounded text-base px-2 py-1.5 border-2 border-black/60 cursor-pointer"
                    value={value ?? ""}
                    onChange={e => pick(Number(e.target.value))}
                    disabled={!loaded}
                >
                    {sessions.map(s => <option key={s.id} value={s.id}>{s.cashRegisterCode}</option>)}
                </select>
            </div>
        );
    }

    return (
        <div className={`flex flex-col gap-1 ${className ?? ""}`}>
            <span className="text-[#666] text-xs">Каса</span>
            <select
                className="bg-white border-none rounded text-base p-2 w-full shadow-[0_0_4px_rgba(0,0,0,0.2)]"
                value={value ?? ""}
                onChange={e => pick(Number(e.target.value))}
                disabled={!loaded}
            >
                {sessions.map(s => <option key={s.id} value={s.id}>{s.cashRegisterCode}</option>)}
            </select>
        </div>
    );
}
