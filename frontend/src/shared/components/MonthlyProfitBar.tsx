import { useEffect, useState } from "react";
import axios from "axios";
import { TrendingUp, Handshake, Tag, Receipt, Coins } from "lucide-react";
import { API_BASE } from "../api/config.ts";
import { useAuth } from "../../GlobalContext.tsx";

const money = (n: number) => n.toLocaleString("de-DE");

interface Props {
    // Bump this to refetch (e.g. after a new pawn/sale is recorded).
    refreshDependency?: any;
}

/**
 * Net profit earned since the first of the current month: pawn provision
 * (interest income) plus sale profit, minus shop expenses over the same period.
 * Served by the transaction ledger (`/transactions/monthly-profit`) and gated by
 * the PROFIT_READ permission, so this owner-level bar is identical on the Pawns
 * and Sales pages and hidden from staff without the permission.
 */
export default function MonthlyProfitBar({ refreshDependency }: Props) {
    const { can } = useAuth();
    const canRead = can("PROFIT_READ");
    const [pawnProvision, setPawnProvision] = useState(0);
    const [saleProfit, setSaleProfit] = useState(0);
    const [totalExpenses, setTotalExpenses] = useState(0);
    const [netProfit, setNetProfit] = useState(0);

    useEffect(() => {
        if (!canRead) return;
        axios.get(`${API_BASE}/transactions/monthly-profit`)
            .then(res => {
                setPawnProvision(res.data.pawnProvision ?? 0);
                setSaleProfit(res.data.saleProfit ?? 0);
                setTotalExpenses(res.data.totalExpenses ?? 0);
                setNetProfit(res.data.netProfit ?? 0);
            })
            .catch(err => console.error("Error fetching monthly profit:", err));
    }, [canRead, refreshDependency]);

    if (!canRead) return null;

    return (
        <div className="flex w-full flex-wrap items-center justify-between gap-x-6 gap-y-1 rounded-lg border border-green/30 bg-green/[0.06] px-3 md:px-5 py-1.5 max-lg:landscape:py-0.5 text-xs text-[#555]">
            <span className="flex items-center gap-1.5 font-semibold text-green">
                <TrendingUp size={15} /> Профит од почеток на месецот
            </span>
            <span className="flex items-center gap-1.5"><Handshake size={14} className="text-green" /> Провизија од залози: <b className="text-[#333]">{money(pawnProvision)} ден</b></span>
            <span className="flex items-center gap-1.5"><Tag size={14} className="text-green" /> Профит од продажби: <b className="text-[#333]">{money(saleProfit)} ден</b></span>
            <span className="flex items-center gap-1.5"><Receipt size={14} className="text-dark-red" /> Трошоци: <b className="text-[#333]">{money(totalExpenses)} ден</b></span>
            <span className="flex items-center gap-1.5"><Coins size={14} className="text-green" /> Нето профит: <b className={netProfit < 0 ? "text-dark-red" : "text-green"}>{money(netProfit)} ден</b></span>
        </div>
    );
}
