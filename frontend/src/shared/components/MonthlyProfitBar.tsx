import { useEffect, useState } from "react";
import axios from "axios";
import { TrendingUp, Handshake, Tag, Coins } from "lucide-react";
import { API_BASE } from "../api/config.ts";
import { useAuth } from "../../GlobalContext.tsx";

const money = (n: number) => n.toLocaleString("de-DE");

interface Props {
    // Bump this to refetch (e.g. after a new pawn/sale is recorded).
    refreshDependency?: any;
}

/**
 * Profit earned since the first of the current month, split into pawn provision
 * (interest income) and sale profit, plus their sum. Served by the transaction
 * ledger (`/transactions/monthly-profit`), so this bar is identical on the Pawns
 * and Sales pages.
 */
export default function MonthlyProfitBar({ refreshDependency }: Props) {
    const { can } = useAuth();
    const canRead = can("TRANSACTION_READ");
    const [pawnProvision, setPawnProvision] = useState(0);
    const [saleProfit, setSaleProfit] = useState(0);
    const [total, setTotal] = useState(0);

    useEffect(() => {
        if (!canRead) return;
        axios.get(`${API_BASE}/transactions/monthly-profit`)
            .then(res => {
                setPawnProvision(res.data.pawnProvision ?? 0);
                setSaleProfit(res.data.saleProfit ?? 0);
                setTotal(res.data.total ?? 0);
            })
            .catch(err => console.error("Error fetching monthly profit:", err));
    }, [canRead, refreshDependency]);

    return (
        <div className="flex w-full flex-wrap items-center justify-between gap-x-6 gap-y-1 rounded-lg border border-green/30 bg-green/[0.06] px-3 md:px-5 py-1.5 text-xs text-[#555]">
            <span className="flex items-center gap-1.5 font-semibold text-green">
                <TrendingUp size={15} /> Профит од почеток на месецот
            </span>
            <span className="flex items-center gap-1.5"><Handshake size={14} className="text-green" /> Провизија од залози: <b className="text-[#333]">{money(pawnProvision)} ден</b></span>
            <span className="flex items-center gap-1.5"><Tag size={14} className="text-green" /> Профит од продажби: <b className="text-[#333]">{money(saleProfit)} ден</b></span>
            <span className="flex items-center gap-1.5"><Coins size={14} className="text-green" /> Вкупен профит: <b className="text-green">{money(total)} ден</b></span>
        </div>
    );
}
