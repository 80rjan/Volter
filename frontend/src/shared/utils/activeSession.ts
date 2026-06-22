import axios from "axios";
import { API_BASE } from "../api/config.ts";

// The register the user is currently working on. Persisted so the choice is
// shared across pages and across the cash-register bar and the action modals.
const KEY = "volter.activeRegisterId";

export function getActiveRegisterId(): number | null {
    const v = localStorage.getItem(KEY);
    return v ? Number(v) : null;
}

export function setActiveRegisterId(id: number | null) {
    if (id == null) localStorage.removeItem(KEY);
    else localStorage.setItem(KEY, String(id));
}

/**
 * The cash session new transactions (pawn/sale/expense) should record into:
 * the OPEN session of the user's selected ("active") register, falling back to
 * the first OPEN session the user can see. Returns null if none are open.
 */
export async function resolveActiveSessionId(): Promise<number | null> {
    const res = await axios.get(`${API_BASE}/cash-register-sessions`, { params: { status: "OPEN", size: 50 } });
    const open = (res.data.content ?? []).filter((s: any) => s.status === "OPEN");
    if (open.length === 0) return null;
    const active = getActiveRegisterId();
    const match = active != null ? open.find((s: any) => s.cashRegisterId === active) : null;
    return (match ?? open[0]).id;
}
