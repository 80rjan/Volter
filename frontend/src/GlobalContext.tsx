import { createContext, useContext, useCallback, useEffect, useMemo, useState, ReactNode } from 'react';
import axios from 'axios';
import { API_BASE } from './shared/api/config.ts';

export interface CurrentUser {
    staffId: number;
    username: string;
    shopId: number | null;
    roles: string[];
    permissions: string[];
}

export interface Shop {
    id: number;
    name: string;
    code: string;
}

interface GlobalContextType {
    user: CurrentUser | null;
    loading: boolean;
    /** Shops the current user may switch to (their active assignments). */
    shops: Shop[];
    /** True if the current user has the given permission (e.g. "PAWN_WRITE"). */
    can: (permission: string) => boolean;
    /** True if the user has at least one of the given permissions. */
    canAny: (...permissions: string[]) => boolean;
    /** True if the user has the given role (e.g. "SUPER_ADMIN"). */
    hasRole: (role: string) => boolean;
    /** Re-fetch the current user (call after login). */
    refreshUser: () => Promise<void>;
    /** Swap the active shop without logging out (re-issues the token and reloads). */
    switchShop: (shopId: number) => Promise<void>;
    /** Number of unread notifications (drives the nav badge). */
    unreadCount: number;
    /** Re-fetch the unread notification count (call after reading notifications). */
    refreshUnreadCount: () => Promise<void>;
    /** Drop the cached user (call on logout). */
    clearUser: () => void;
}

const GlobalContext = createContext<GlobalContextType>({
    user: null,
    loading: true,
    shops: [],
    can: () => false,
    canAny: () => false,
    hasRole: () => false,
    refreshUser: async () => {},
    switchShop: async () => {},
    unreadCount: 0,
    refreshUnreadCount: async () => {},
    clearUser: () => {},
});

export const GlobalProvider = ({ children }: { children: ReactNode }) => {
    const [user, setUser] = useState<CurrentUser | null>(null);
    const [shops, setShops] = useState<Shop[]>([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [loading, setLoading] = useState(true);

    const refreshUnreadCount = useCallback(async () => {
        if (!localStorage.getItem('token')) { setUnreadCount(0); return; }
        try {
            const res = await axios.get<{ count: number }>(`${API_BASE}/notifications/unread-count`);
            setUnreadCount(res.data.count ?? 0);
        } catch {
            /* leave the last known count on a transient error */
        }
    }, []);

    const refreshUser = useCallback(async () => {
        if (!localStorage.getItem('token')) {
            setUser(null);
            setShops([]);
            setUnreadCount(0);
            setLoading(false);
            return;
        }
        setLoading(true);
        try {
            const [me, myShops] = await Promise.all([
                axios.get<CurrentUser>(`${API_BASE}/auth/me`),
                axios.get<Shop[]>(`${API_BASE}/auth/my-shops`),
            ]);
            setUser(me.data);
            setShops(myShops.data);
            refreshUnreadCount();
        } catch {
            setUser(null);
            setShops([]);
        } finally {
            setLoading(false);
        }
    }, [refreshUnreadCount]);

    // Swap the active shop: exchange the current token for one scoped to the new
    // shop, then hard-reload so every page refetches its (per-tenant) data cleanly.
    const switchShop = useCallback(async (shopId: number) => {
        const res = await axios.post(`${API_BASE}/auth/switch-shop`, { shopId });
        localStorage.setItem('token', res.data.accessToken);
        window.location.hash = '#/';
        window.location.reload();
    }, []);

    const clearUser = useCallback(() => { setUser(null); setShops([]); setUnreadCount(0); }, []);

    useEffect(() => { refreshUser(); }, [refreshUser]);

    const permissions = useMemo(() => new Set(user?.permissions ?? []), [user]);
    const roles = useMemo(() => new Set(user?.roles ?? []), [user]);

    const value = useMemo<GlobalContextType>(() => ({
        user,
        loading,
        shops,
        can: (p: string) => permissions.has(p),
        canAny: (...ps: string[]) => ps.some(p => permissions.has(p)),
        hasRole: (r: string) => roles.has(r),
        refreshUser,
        switchShop,
        unreadCount,
        refreshUnreadCount,
        clearUser,
    }), [user, loading, shops, unreadCount, permissions, roles, refreshUser, switchShop, refreshUnreadCount, clearUser]);

    return (
        <GlobalContext.Provider value={value}>
            {children}
        </GlobalContext.Provider>
    );
};

export const useGlobalContext = () => useContext(GlobalContext);
export const useAuth = useGlobalContext;
