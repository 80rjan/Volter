import { useEffect, useState } from "react";
import axios from "axios";
import {Landmark, CalendarClock, Plus, Minus, LockKeyhole, Coins, Wallet, Percent, HandCoins} from "lucide-react";
import ModalAdjustCashRegister from "./ModalAdjustCashRegister.tsx";
import ModalOpenCashRegister from "./ModalOpenCashRegister.tsx";
import ModalCloseCashRegister from "./ModalCloseCashRegister.tsx";
import ModalTakeBonus from "./ModalTakeBonus.tsx";
import Loading from "./Loading.tsx";
import { CashSession } from "../types.ts";
import { API_BASE } from "../api/config.ts";
import { useAuth } from "../../GlobalContext.tsx";
import { getActiveRegisterId, setActiveRegisterId } from "../utils/activeSession.ts";

interface RegisterOption { id: number; code: string; }

interface Props {
    refreshDependency?: any;
    refreshDependencyAdjustPawn?: any;
    refreshTransactions?: () => void;
}

const money = (n: number | null | undefined) => (n == null ? "—" : Number(n).toLocaleString("de-DE"));

export default function CashRegister({ refreshDependency, refreshDependencyAdjustPawn, refreshTransactions }: Props) {
    const { can } = useAuth();
    const canRead = can("CASH_REGISTER_SESSION_READ");
    const canWrite = can("CASH_REGISTER_SESSION_WRITE");
    const canManage = can("CASH_REGISTER_SESSION_MANAGE");

    const [registers, setRegisters] = useState<RegisterOption[]>([]);
    const [openSessions, setOpenSessions] = useState<CashSession[]>([]);
    const [registerId, setRegisterId] = useState<number | null>(getActiveRegisterId());
    const [loading, setLoading] = useState(false);
    const [showDeposit, setShowDeposit] = useState(false);
    const [showWithdraw, setShowWithdraw] = useState(false);
    const [showOpen, setShowOpen] = useState(false);
    const [showClose, setShowClose] = useState(false);
    const [showBonus, setShowBonus] = useState(false);

    const fetchData = () => {
        if (!canRead) return;
        setLoading(true);
        Promise.all([
            axios.get(`${API_BASE}/cash-registers`),
            axios.get(`${API_BASE}/cash-register-sessions`, { params: { status: "OPEN", size: 50 } }),
        ])
            .then(([reg, ses]) => {
                const regs: RegisterOption[] = reg.data ?? [];
                const open: CashSession[] = (ses.data.content ?? []).filter((s: CashSession) => s.status === "OPEN");
                setRegisters(regs);
                setOpenSessions(open);
                // Keep the current register if still valid; otherwise default to the
                // first register that has an open session, else the first register.
                setRegisterId(prev => {
                    const valid = prev != null && regs.some(r => r.id === prev);
                    return valid ? prev : (open[0]?.cashRegisterId ?? regs[0]?.id ?? null);
                });
            })
            .catch(() => { setRegisters([]); setOpenSessions([]); })
            .finally(() => setLoading(false));
    };

    useEffect(() => { fetchData(); }, [canRead, refreshDependency, refreshDependencyAdjustPawn]);
    // Persist the chosen register so the action modals record into its session.
    useEffect(() => { if (registerId != null) setActiveRegisterId(registerId); }, [registerId]);

    const onRefresh = () => { fetchData(); refreshTransactions?.(); };

    if (!canRead) return null;

    const session = openSessions.find(s => s.cashRegisterId === registerId) ?? null;

    const barClass = "flex justify-between items-center gap-4 px-4 py-2 rounded-t-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] bg-white border-2 border-black/40 border-b-0";
    const item = "flex items-center gap-1.5";
    const val = "text-[.72rem] font-bold";
    const selectCls = "bg-[#f4f4f4] border-none rounded text-[.72rem] font-bold px-2 py-1 shadow-[0_0_3px_rgba(0,0,0,0.2)] cursor-pointer";

    const registerSelect = (
        <select className={selectCls} value={registerId ?? ""} onChange={e => setRegisterId(Number(e.target.value))} title="Избери каса">
            {registers.length === 0 && <option value="">—</option>}
            {registers.map(r => {
                const hasOpen = openSessions.some(s => s.cashRegisterId === r.id);
                return <option key={r.id} value={r.id}>{r.code}{hasOpen ? " ●" : ""}</option>;
            })}
        </select>
    );

    if (loading) {
        return <div className={`${barClass} justify-center`}><Loading width={28} height={28} /></div>;
    }

    return (
        <>
            <div className={barClass}>
                {!session ? (
                    <>
                        <div className="flex items-center gap-3">
                            {registerSelect}
                            <p className="text-[.72rem] italic text-[#888]">Нема отворена каса</p>
                        </div>
                        {canManage && registerId != null && (
                            <button
                                onClick={() => setShowOpen(true)}
                                className="flex items-center gap-1.5 px-3 py-1 rounded bg-green text-white text-xs shadow-[0_0_4px_rgba(0,0,0,0.2)] hover:scale-105 transition-all duration-300"
                            >
                                <Landmark size={14} /> Отвори каса
                            </button>
                        )}
                    </>
                ) : (
                    <div className="grid grid-cols-1 md:grid-cols-[3fr_1fr] gap-2 md:gap-0 w-full">
                        <div className="flex items-center justify-between gap-5 flex-wrap w-full">
                            <div className={item} title="Каса">
                                {registerSelect}
                                <p className={val + " font-normal opacity-60"}>/ {money(session.openingBalance)}</p>
                            </div>
                            <div className={item} title="Тековно салдо"><Wallet size={20} /><p className={val}>{money(session.currentBalance)}</p></div>
                            <div className={item} title="Очекувана провизија"><Percent size={20} /><p className={val}>{money(session.expectedInterest)}</p></div>
                            <div className={item} title="Отворена">
                                <CalendarClock size={20} />
                                <p className={val}>{session.openedAt?.substring(0, 10)} {session.openedAt?.substring(11, 16)}</p>
                            </div>
                        </div>

                        <div className="flex items-center justify-end gap-3 svg-hover">
                            <HandCoins size={20} color="var(--green)" className="cursor-pointer" onClick={() => setShowBonus(true)} aria-label="Земи бонус" />
                            {canWrite && <Minus size={22} color="var(--dark-red)" className="cursor-pointer" onClick={() => setShowWithdraw(true)} />}
                            {canWrite && <Plus size={22} color="var(--green)" className="cursor-pointer" onClick={() => setShowDeposit(true)} />}
                            {canManage && <LockKeyhole size={20} color="#666" className="cursor-pointer" onClick={() => setShowClose(true)} />}
                        </div>
                    </div>
                )}
            </div>

            {showOpen && registerId != null && <ModalOpenCashRegister registerId={registerId} closeModal={() => setShowOpen(false)} refresh={onRefresh} />}
            {session && showDeposit && <ModalAdjustCashRegister sessionId={session.id} isInsert={true} closeModal={() => setShowDeposit(false)} refresh={onRefresh} />}
            {session && showWithdraw && <ModalAdjustCashRegister sessionId={session.id} isInsert={false} closeModal={() => setShowWithdraw(false)} refresh={onRefresh} />}
            {session && showClose && <ModalCloseCashRegister registerId={session.cashRegisterId} expectedBalance={session.currentBalance} closeModal={() => setShowClose(false)} refresh={onRefresh} />}
            {session && showBonus && <ModalTakeBonus sessionId={session.id} closeModal={() => setShowBonus(false)} refresh={onRefresh} />}
        </>
    );
}
