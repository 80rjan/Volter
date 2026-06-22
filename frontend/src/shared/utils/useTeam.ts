import { useEffect, useState } from "react";
import axios from "axios";
import { API_BASE } from "../api/config.ts";

export interface StaffOption {
    id: number;
    fullName: string;
}

/**
 * The logged-in user's team (themselves + subordinates), for the per-page
 * "filter by staff member" dropdowns. Fetched once.
 */
export function useTeam(): StaffOption[] {
    const [team, setTeam] = useState<StaffOption[]>([]);
    useEffect(() => {
        axios.get(`${API_BASE}/staff/team`)
            .then(res => setTeam(res.data ?? []))
            .catch(() => setTeam([]));
    }, []);
    return team;
}
