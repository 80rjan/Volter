// @ts-ignore
import miniLogo from "../../assets/v-3D-slika.png";
// @ts-ignore
import bigLogo from "../../assets/volter-zalozna-kukja-3D-slika.png";
import { NavLink, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import axios from "axios";
import {
    Tag, CalendarFold, Calendar1, Handshake,
    ArrowLeftRight, BanknoteArrowDown, UserRound, LogOut, Landmark, Users, BarChart3, Bell, ScrollText,
    Store, ChevronRight, Menu, X,
} from "lucide-react";
import GoldPriceLive from "./GoldPriceLive.tsx";
import { useAuth } from "../../GlobalContext.tsx";

// `perm` is the permission required to see the link; omit it for links open to
// every authenticated user. The nav hides links the user has no access to.
const links: { to: string; label: string; Icon: any; end?: boolean; perm?: string }[] = [
    { to: "/", label: "Залози", Icon: Handshake, end: true, perm: "PAWN_READ" },
    { to: "/sales", label: "Продажби", Icon: Tag, perm: "SALE_READ" },
    { to: "/transactions", label: "Трансакции", Icon: ArrowLeftRight, perm: "TRANSACTION_READ" },
    { to: "/clients", label: "Клиенти", Icon: UserRound, perm: "CUSTOMER_READ" },
    { to: "/cash-sessions", label: "Каса", Icon: Landmark, perm: "CASH_REGISTER_SESSION_READ" },
    { to: "/staff", label: "Вработени", Icon: Users, perm: "STAFF_MANAGE" },
    { to: "/auth-events", label: "Записи за најава", Icon: ScrollText, perm: "AUDIT_READ" },
    { to: "/notifications", label: "Известувања", Icon: Bell },
    { to: "/expenses", label: "Расходи", Icon: BanknoteArrowDown, perm: "EXPENSE_READ" },
    { to: "/report", label: "Извештај", Icon: CalendarFold, perm: "REPORT_READ" },
    { to: "/monthlyReport", label: "Месечен Извештај", Icon: Calendar1, perm: "REPORT_READ" },
    { to: "/staffReports", label: "Извештаи Вработени", Icon: BarChart3, perm: "REPORT_READ" },
];

// px-5 keeps the 24px icon centered in the 64px collapsed rail. Labels are kept
// in the DOM but faded out (and clipped by overflow-hidden) until hover.
const linkClass = ({ isActive }: { isActive: boolean }) =>
    `flex items-center gap-4 h-11 px-5 no-underline text-base font-semibold transition-colors duration-200 ${
        isActive
            ? "bg-green text-white shadow-[-4px_2px_6px_rgba(0,0,0,0.2)]"
            : "text-black hover:bg-[#eee]"
    }`;

const labelClass = "whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity duration-200";

export default function Nav() {
    const navigate = useNavigate();
    const { user, can, clearUser, shops, switchShop, unreadCount, refreshUnreadCount } = useAuth();
    const [shopMenuOpen, setShopMenuOpen] = useState(false);
    const [mobileOpen, setMobileOpen] = useState(false);

    // Keep the unread badge fresh: poll while the nav is mounted (page reads also
    // refresh it directly for an immediate update).
    useEffect(() => {
        const id = setInterval(() => refreshUnreadCount(), 45000);
        return () => clearInterval(id);
    }, [refreshUnreadCount]);

    const activeShop = shops.find(s => s.id === user?.shopId);
    const otherShops = shops.filter(s => s.id !== user?.shopId);
    const canSwitchShop = otherShops.length > 0;

    // Icon + current shop name (with a chevron only when switching is possible).
    // Wrapped as a clickable button only when there is another shop to switch to;
    // otherwise it's plain, static text (no pointer, no hover).
    const shopLabel = activeShop && (
        <>
            <Store size={24} className="shrink-0 text-green" />
            <div className={`${labelClass} flex flex-1 items-center justify-between gap-2 overflow-hidden`}>
                <div className="flex flex-col items-start leading-tight overflow-hidden">
                    <span className="text-[11px] text-[#888]">Продавница</span>
                    <span className="text-sm font-semibold truncate">{activeShop.name}</span>
                </div>
                {canSwitchShop && (
                    <ChevronRight
                        size={16}
                        className={`shrink-0 text-[#888] transition-transform duration-200 ${shopMenuOpen ? "rotate-90" : ""}`}
                    />
                )}
            </div>
        </>
    );

    const handleLogout = () => {
        localStorage.removeItem("token");
        delete axios.defaults.headers.common["Authorization"];
        clearUser();
        navigate("/login");
    };

    const visibleLinks = links.filter(l => !l.perm || can(l.perm));

    // A single nav link with its optional unread badge; `showLabel` controls whether
    // the label is always visible (mobile drawer) or hover-gated (desktop rail).
    const renderLink = ({ to, label, Icon, end }: typeof links[number], showLabel: boolean, onClick?: () => void) => (
        <NavLink key={to} to={to} end={end} className={linkClass} onClick={onClick}>
            <span className="relative shrink-0">
                <Icon size={24} className="shrink-0" />
                {to === "/notifications" && unreadCount > 0 && (
                    <span className="absolute -top-1.5 -right-1.5 min-w-[16px] h-4 px-1 flex items-center justify-center rounded-full bg-red-500 text-white text-[10px] font-bold leading-none">
                        {unreadCount > 99 ? "99+" : unreadCount}
                    </span>
                )}
            </span>
            <span className={showLabel ? "whitespace-nowrap" : labelClass}>{label}</span>
        </NavLink>
    );

    return (
        <>
            {/* ---------- Desktop: fixed hover-expand rail (md and up) ---------- */}
            {/* Fixed 4rem rail rendered once by the layout (so it never unmounts while
                navigating); the <nav> widens on hover and overlays the page content to
                its right (which reserves the rail with md:pl-16) instead of pushing it. */}
            <div className="hidden md:block fixed left-0 top-0 h-screen w-16 group z-50" onMouseLeave={() => setShopMenuOpen(false)}>
                <nav className="absolute inset-y-0 left-0 w-16 group-hover:w-64 bg-white shadow-[2px_0_8px_rgba(0,0,0,0.2)] overflow-hidden transition-[width] duration-300 ease-in-out flex flex-col py-4">
                    {/* Mini "V" logo when collapsed, crossfading to the full logo on hover. */}
                    <div className="relative h-12 mb-4 shrink-0 flex items-center justify-center">
                        <img
                            src={miniLogo}
                            alt="Volter"
                            className="h-10 w-auto object-contain opacity-100 group-hover:opacity-0 transition-opacity duration-200"
                        />
                        <img
                            src={bigLogo}
                            alt="Volter Zalozna Kukja"
                            className="absolute inset-0 m-auto max-h-12 w-auto object-contain px-3 opacity-0 group-hover:opacity-100 transition-opacity duration-300"
                        />
                    </div>

                    <div className="flex flex-col gap-1.5 overflow-y-auto scrollbar-hidden">
                        {visibleLinks.map(l => renderLink(l, false))}
                    </div>

                    <div className="mt-auto flex flex-col gap-2 pt-2 border-t border-black/10">
                        {/* Gold price only when expanded (no reserved space when collapsed). */}
                        <div className="px-5 hidden group-hover:block">
                            <GoldPriceLive />
                        </div>

                        {/* Active shop: a clickable switcher only when there's another shop to
                            switch to (the flyout is rendered outside <nav>, below); otherwise
                            it's plain static text — no pointer, no hover. */}
                        {activeShop && (canSwitchShop ? (
                            <button
                                type="button"
                                onClick={() => setShopMenuOpen(o => !o)}
                                className="flex items-center gap-4 h-11 px-5 border-none bg-transparent text-left transition-colors duration-200 hover:bg-[#eee] cursor-pointer"
                                aria-label="Продавница"
                            >
                                {shopLabel}
                            </button>
                        ) : (
                            <div className="flex items-center gap-4 h-11 px-5">
                                {shopLabel}
                            </div>
                        ))}

                        {/* Current user: avatar initial when collapsed, name + role when expanded. */}
                        {user && (
                            <div className="flex items-center gap-4 h-11 px-5">
                                <span className="shrink-0 flex h-6 w-6 items-center justify-center rounded-full bg-green text-white text-xs font-bold">
                                    {(user.username?.[0] ?? "?").toUpperCase()}
                                </span>
                                <div className={`${labelClass} flex flex-col items-start leading-tight overflow-hidden`}>
                                    <span className="text-sm font-semibold truncate">{user.username}</span>
                                    {user.roles?.length > 0 && <span className="text-xs text-[#888] truncate">{user.roles.join(", ")}</span>}
                                </div>
                            </div>
                        )}

                        <button
                            onClick={handleLogout}
                            className="flex items-center gap-4 h-11 px-5 text-base font-semibold transition-colors duration-200 text-black hover:bg-[#eee] cursor-pointer border-none bg-transparent text-left"
                        >
                            <LogOut size={24} className="shrink-0" />
                            <span className={labelClass}>Одјави се</span>
                        </button>
                    </div>
                </nav>

                {/* Shop-switch flyout: floats to the right of the rail, over the page, so
                    opening it never reflows the navbar. Rendered outside <nav> to escape its
                    overflow-hidden, but still inside the `group` container — so hovering the
                    flyout keeps the rail expanded; leaving the whole rail closes it. The `pl-1`
                    bridges the gap to the rail edge without breaking that hover. */}
                {activeShop && shopMenuOpen && otherShops.length > 0 && (
                    <div className="hidden group-hover:block absolute left-64 bottom-[7.5rem] pl-1 z-50">
                        <div className="w-56 rounded-md bg-white shadow-[2px_2px_12px_rgba(0,0,0,0.25)] overflow-hidden py-1">
                            <p className="px-3 py-1.5 text-xs text-[#888]">Смени продавница</p>
                            {otherShops.map(s => (
                                <button
                                    key={s.id}
                                    type="button"
                                    onClick={() => { setShopMenuOpen(false); switchShop(s.id); }}
                                    className="flex w-full items-center gap-2 px-3 py-2 text-left text-sm border-none bg-transparent hover:bg-[#eee] cursor-pointer"
                                >
                                    <Store size={16} className="shrink-0 text-green" />
                                    <span className="truncate">{s.name}</span>
                                </button>
                            ))}
                        </div>
                    </div>
                )}
            </div>

            {/* ---------- Mobile: fixed top bar with a hamburger (below md) ---------- */}
            <div className="md:hidden fixed top-0 inset-x-0 h-12 z-40 bg-white shadow-[0_2px_8px_rgba(0,0,0,0.15)] flex items-center gap-3 px-3">
                <button onClick={() => setMobileOpen(true)} aria-label="Отвори мени" className="border-none bg-transparent p-1 -ml-1 cursor-pointer">
                    <Menu size={24} />
                </button>
                <img src={miniLogo} alt="Volter" className="h-7 w-auto object-contain" />
                {activeShop && <span className="text-sm font-semibold truncate">{activeShop.name}</span>}
                <NavLink to="/notifications" className="ml-auto relative p-1" aria-label="Известувања">
                    <Bell size={22} />
                    {unreadCount > 0 && (
                        <span className="absolute -top-0.5 -right-0.5 min-w-[16px] h-4 px-1 flex items-center justify-center rounded-full bg-red-500 text-white text-[10px] font-bold leading-none">
                            {unreadCount > 99 ? "99+" : unreadCount}
                        </span>
                    )}
                </NavLink>
            </div>

            {/* ---------- Mobile: slide-in drawer + backdrop (below md) ---------- */}
            {mobileOpen && (
                <div className="md:hidden fixed inset-0 z-[60]">
                    <div className="absolute inset-0 bg-black/50" onClick={() => setMobileOpen(false)} />
                    <div className="absolute left-0 top-0 h-full w-72 max-w-[85%] bg-white shadow-[2px_0_16px_rgba(0,0,0,0.3)] flex flex-col py-4 overflow-y-auto scrollbar-thin">
                        <div className="flex items-center justify-between px-4 mb-3 shrink-0">
                            <img src={bigLogo} alt="Volter Zalozna Kukja" className="max-h-11 w-auto object-contain" />
                            <button onClick={() => setMobileOpen(false)} aria-label="Затвори мени" className="border-none bg-transparent p-1 cursor-pointer">
                                <X size={24} />
                            </button>
                        </div>

                        <div className="flex flex-col gap-1">
                            {visibleLinks.map(l => renderLink(l, true, () => setMobileOpen(false)))}
                        </div>

                        <div className="mt-auto flex flex-col gap-2 pt-3 border-t border-black/10">
                            <div className="px-5">
                                <GoldPriceLive />
                            </div>

                            {activeShop && (
                                <div className="px-5">
                                    <div className="flex items-center gap-2 py-1">
                                        <Store size={20} className="shrink-0 text-green" />
                                        <div className="flex flex-col items-start leading-tight overflow-hidden">
                                            <span className="text-[11px] text-[#888]">Продавница</span>
                                            <span className="text-sm font-semibold truncate">{activeShop.name}</span>
                                        </div>
                                    </div>
                                    {otherShops.length > 0 && (
                                        <div className="mt-1 flex flex-col gap-0.5">
                                            {otherShops.map(s => (
                                                <button
                                                    key={s.id}
                                                    type="button"
                                                    onClick={() => { setMobileOpen(false); switchShop(s.id); }}
                                                    className="flex w-full items-center gap-2 px-2 py-1.5 text-left text-sm rounded border-none bg-transparent hover:bg-[#eee] cursor-pointer"
                                                >
                                                    <ChevronRight size={14} className="shrink-0 text-[#888]" />
                                                    <span className="truncate">{s.name}</span>
                                                </button>
                                            ))}
                                        </div>
                                    )}
                                </div>
                            )}

                            {user && (
                                <div className="flex items-center gap-3 px-5 py-1">
                                    <span className="shrink-0 flex h-7 w-7 items-center justify-center rounded-full bg-green text-white text-xs font-bold">
                                        {(user.username?.[0] ?? "?").toUpperCase()}
                                    </span>
                                    <div className="flex flex-col items-start leading-tight overflow-hidden">
                                        <span className="text-sm font-semibold truncate">{user.username}</span>
                                        {user.roles?.length > 0 && <span className="text-xs text-[#888] truncate">{user.roles.join(", ")}</span>}
                                    </div>
                                </div>
                            )}

                            <button
                                onClick={() => { setMobileOpen(false); handleLogout(); }}
                                className="flex items-center gap-4 h-11 px-5 text-base font-semibold transition-colors duration-200 text-black hover:bg-[#eee] cursor-pointer border-none bg-transparent text-left"
                            >
                                <LogOut size={24} className="shrink-0" />
                                <span className="whitespace-nowrap">Одјави се</span>
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
}
