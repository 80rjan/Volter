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

interface GlobalContextType {
    user: CurrentUser | null;
    loading: boolean;
    /** True if the current user has the given permission (e.g. "PAWN_WRITE"). */
    can: (permission: string) => boolean;
    /** True if the user has at least one of the given permissions. */
    canAny: (...permissions: string[]) => boolean;
    /** True if the user has the given role (e.g. "SUPER_ADMIN"). */
    hasRole: (role: string) => boolean;
    /** Re-fetch the current user (call after login). */
    refreshUser: () => Promise<void>;
    /** Drop the cached user (call on logout). */
    clearUser: () => void;
}

const GlobalContext = createContext<GlobalContextType>({
    user: null,
    loading: true,
    can: () => false,
    canAny: () => false,
    hasRole: () => false,
    refreshUser: async () => {},
    clearUser: () => {},
});

export const GlobalProvider = ({ children }: { children: ReactNode }) => {
    const [user, setUser] = useState<CurrentUser | null>(null);
    const [loading, setLoading] = useState(true);

    const refreshUser = useCallback(async () => {
        if (!localStorage.getItem('token')) {
            setUser(null);
            setLoading(false);
            return;
        }
        setLoading(true);
        try {
            const res = await axios.get<CurrentUser>(`${API_BASE}/auth/me`);
            setUser(res.data);
        } catch {
            setUser(null);
        } finally {
            setLoading(false);
        }
    }, []);

    const clearUser = useCallback(() => setUser(null), []);

    useEffect(() => { refreshUser(); }, [refreshUser]);

    const permissions = useMemo(() => new Set(user?.permissions ?? []), [user]);
    const roles = useMemo(() => new Set(user?.roles ?? []), [user]);

    const value = useMemo<GlobalContextType>(() => ({
        user,
        loading,
        can: (p: string) => permissions.has(p),
        canAny: (...ps: string[]) => ps.some(p => permissions.has(p)),
        hasRole: (r: string) => roles.has(r),
        refreshUser,
        clearUser,
    }), [user, loading, permissions, roles, refreshUser, clearUser]);

    return (
        <GlobalContext.Provider value={value}>
            {children}
        </GlobalContext.Provider>
    );
};

export const useGlobalContext = () => useContext(GlobalContext);
export const useAuth = useGlobalContext;
