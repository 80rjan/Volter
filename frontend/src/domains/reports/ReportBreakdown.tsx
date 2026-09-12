import { HandCoins, Tag, Wallet, Landmark } from "lucide-react";
import { ReportPayload, CashFlow } from "./types.ts";

const ITEM_TYPE_MK: Record<string, string> = {
    GOLD: "Злато", ELECTRONIC: "Електроника", WATCH: "Часовник", VEHICLE: "Возило", OTHER: "Останато",
};
const EXPENSE_CAT_MK: Record<string, string> = {
    SUPPLIES: "Материјали", RENT: "Кирија", UTILITIES: "Комуналии", SALARY: "Плата", MAINTENANCE: "Одржување", OTHER: "Останато",
};

export const money = (n: number | null | undefined) => (n == null ? "0" : Number(n).toLocaleString("de-DE"));

export function ReportStats({ totalRevenue, totalExpenses, netProfit, moneyGivenToClients }:
    { totalRevenue: number; totalExpenses: number; netProfit: number; moneyGivenToClients: number }) {
    const Stat = ({ label, value, cls }: { label: string; value: React.ReactNode; cls?: string }) => (
        <div className="flex flex-col bg-white rounded px-3 py-2 shadow-[0_0_4px_rgba(0,0,0,0.15)] min-w-[9rem] flex-1">
            <span className="text-[#666] text-xs">{label}</span>
            <span className={`text-base font-bold ${cls ?? ""}`}>{value}</span>
        </div>
    );
    // Gross profit = pawn provision + sale margin (i.e. before expenses). Net is
    // gross − expenses, so gross = net + expenses.
    const grossProfit = netProfit + totalExpenses;
    return (
        <div className="flex flex-wrap gap-3">
            <Stat label="Приход" value={`${money(totalRevenue)} ден`} cls="text-green" />
            <Stat label="Дадено на клиенти" value={`${money(moneyGivenToClients)} ден`} />
            <Stat label="Бруто профит" value={`${money(grossProfit)} ден`} cls={grossProfit < 0 ? "text-red-500" : "text-green"} />
            <Stat label="Расходи" value={`${money(totalExpenses)} ден`} cls="text-red-500" />
            <Stat label="Нето профит" value={`${money(netProfit)} ден`} cls={netProfit < 0 ? "text-red-500" : "text-green"} />
        </div>
    );
}

// A pawn/sale cash-flow breakdown table keyed by item type.
function CashFlowTable({ icon, title, data }: { icon: React.ReactNode; title: string; data: Record<string, CashFlow> | undefined }) {
    const entries = Object.entries(data ?? {}).filter(([, v]) => v && (v.totalTransactions > 0 || v.inflow > 0 || v.outflow > 0));
    return (
        <div className="flex flex-col gap-2">
            <span className="flex items-center gap-2 font-medium">{icon}{title}</span>
            {entries.length === 0 ? <p className="text-sm text-[#888]">Нема активност.</p> : (
                <div className="bg-white rounded-lg shadow-[0_0_4px_rgba(0,0,0,0.15)] overflow-hidden">
                    <div className="grid grid-cols-[1.4fr_1fr_1fr_1fr_0.8fr] gap-2 px-3 py-1.5 bg-[#666] text-[#eee] text-xs font-medium">
                        <div>Тип</div><div>Влез</div><div>Излез</div><div>Нето</div><div>Бр.</div>
                    </div>
                    {entries.map(([k, v], i) => (
                        <div key={k} style={{ background: i % 2 === 1 ? "#f0f0f0" : "#fff" }}
                             className="grid grid-cols-[1.4fr_1fr_1fr_1fr_0.8fr] gap-2 px-3 py-1 text-xs border-b border-black/10 last:border-b-0">
                            <div className="font-medium">{ITEM_TYPE_MK[k] ?? k}</div>
                            <div className="text-green">{money(v.inflow)}</div>
                            <div className="text-red-500">{money(v.outflow)}</div>
                            <div className={`font-semibold ${v.net < 0 ? "text-red-500" : ""}`}>{money(v.net)}</div>
                            <div>{v.totalTransactions}</div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

const divider = <hr className="border-black/15" />;

/**
 * @param periodNoun what to call the reported span in labels — "месецот" from the
 *   monthly report, "периодот" for an arbitrary range.
 */
export function ReportBreakdown({ payload, periodNoun = "периодот" }: { payload: ReportPayload; periodNoun?: string }) {
    const expenses = Object.entries(payload.expenses ?? {}).filter(([, v]) => v && (v.count > 0 || v.amount > 0));
    const sessions = payload.sessions ?? [];
    const cr = payload.cashRegister;
    // Absent on reports generated before these figures existed and not yet backfilled.
    const hasPrincipal = payload.pawnPrincipalAtPeriodStart != null || payload.pawnPrincipalGiven != null;

    return (
        <>
            {hasPrincipal && (
                <div className="flex flex-wrap gap-3">
                    <div className="flex flex-col bg-white rounded px-3 py-2 shadow-[0_0_4px_rgba(0,0,0,0.15)] min-w-[9rem] flex-1">
                        <span className="text-[#666] text-xs">{`Поделени пари до почеток на ${periodNoun}`}</span>
                        <span className="text-base font-bold">{money(payload.pawnPrincipalAtPeriodStart)} ден</span>
                    </div>
                    <div className="flex flex-col bg-white rounded px-3 py-2 shadow-[0_0_4px_rgba(0,0,0,0.15)] min-w-[9rem] flex-1">
                        <span className="text-[#666] text-xs">{`Поделени пари во ${periodNoun}`}</span>
                        <span className="text-base font-bold">{money(payload.pawnPrincipalGiven)} ден</span>
                    </div>
                </div>
            )}
            <CashFlowTable icon={<HandCoins size={20} />} title="Залози" data={payload.pawns} />
            <CashFlowTable icon={<Tag size={20} />} title="Продажби" data={payload.sales} />

            {divider}
            <div className="flex flex-col gap-2">
                <span className="flex items-center gap-2 font-medium"><Wallet size={20} /> Расходи</span>
                {expenses.length === 0 ? <p className="text-sm text-[#888]">Нема расходи.</p> : (
                    <div className="bg-white rounded-lg shadow-[0_0_4px_rgba(0,0,0,0.15)] overflow-hidden">
                        <div className="grid grid-cols-[2fr_1fr_0.8fr] gap-2 px-3 py-1.5 bg-[#666] text-[#eee] text-xs font-medium">
                            <div>Категорија</div><div>Износ</div><div>Бр.</div>
                        </div>
                        {expenses.map(([k, v], i) => (
                            <div key={k} style={{ background: i % 2 === 1 ? "#f0f0f0" : "#fff" }}
                                 className="grid grid-cols-[2fr_1fr_0.8fr] gap-2 px-3 py-1 text-xs border-b border-black/10 last:border-b-0">
                                <div className="font-medium">{EXPENSE_CAT_MK[k] ?? k}</div>
                                <div className="text-red-500">{money(v.amount)}</div>
                                <div>{v.count}</div>
                            </div>
                        ))}
                    </div>
                )}
            </div>

            {cr && (
                <>
                    {divider}
                    <div className="flex flex-col gap-3">
                        <span className="flex items-center gap-2 font-medium"><Landmark size={20} /> Каса</span>
                        <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                            {[
                                ["Влез", money(cr.inflow), "text-green"], ["Излез", money(cr.outflow), "text-red-500"],
                                ["Нето", money(cr.net), ""], ["Трансакции", String(cr.totalTransactions), ""],
                                ["Депозити", money(cr.deposits), ""], ["Подигања", money(cr.withdrawals), ""],
                                ["Прилагодувања", money(cr.adjustments), ""],
                            ].map(([label, value, cls]) => (
                                <div key={label} className="flex flex-col bg-white rounded px-3 py-2 shadow-[0_0_4px_rgba(0,0,0,0.15)]">
                                    <span className="text-[#666] text-xs">{label}</span>
                                    <span className={`text-base font-bold ${cls}`}>{value}</span>
                                </div>
                            ))}
                        </div>
                    </div>
                </>
            )}

            {sessions.length > 0 && (
                <>
                    {divider}
                    <div className="flex flex-col gap-2">
                        <span className="flex items-center gap-2 font-medium"><Landmark size={20} /> Сесии ({sessions.length})</span>
                        <div className="bg-white rounded-lg shadow-[0_0_4px_rgba(0,0,0,0.15)] overflow-hidden">
                            <div className="grid grid-cols-[0.8fr_1.2fr_1fr_1fr_1fr_0.8fr] gap-2 px-3 py-1.5 bg-[#666] text-[#eee] text-xs font-medium">
                                <div>Сесија</div><div>Датум</div><div>Влез</div><div>Излез</div><div>Нето</div><div>Бр.</div>
                            </div>
                            {sessions.map((s, i) => (
                                <div key={s.sessionId} style={{ background: i % 2 === 1 ? "#f0f0f0" : "#fff" }}
                                     className="grid grid-cols-[0.8fr_1.2fr_1fr_1fr_1fr_0.8fr] gap-2 px-3 py-1 text-xs border-b border-black/10 last:border-b-0">
                                    <div>#{s.sessionId}</div>
                                    <div>{s.date}</div>
                                    <div className="text-green">{money(s.inflow)}</div>
                                    <div className="text-red-500">{money(s.outflow)}</div>
                                    <div className={s.net < 0 ? "text-red-500" : ""}>{money(s.net)}</div>
                                    <div>{s.totalTransactions}</div>
                                </div>
                            ))}
                        </div>
                    </div>
                </>
            )}
        </>
    );
}
