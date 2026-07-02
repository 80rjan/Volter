import ReactDom from "react-dom";
import { useEffect, useState } from "react";
import axios from "axios";
import { X, UserRound, BadgeEuro, ShieldCheck, Pencil, CheckCheck, Trash2, Store, Plus, Ban, BarChart3, Search } from "lucide-react";
import Loading from "../../shared/components/Loading.tsx";
import { API_BASE } from "../../shared/api/config.ts";
import { StaffDetailed, StaffRow, StaffStatus, STAFF_STATUS_LABEL, StaffShopAssignment, ShopOption, RoleOption, StaffPerformance } from "./types.ts";
import { useAuth } from "../../GlobalContext.tsx";

interface Props {
    staffId: number;
    managers: StaffRow[];
    managerName: Map<number, string>;
    closeModal: (e?: React.MouseEvent) => void;
    refresh: () => void;
}

const money = (n: number | null | undefined) => (n == null ? "—" : `${Number(n).toLocaleString("de-DE")} ден`);
const date = (s: string | null | undefined) => (s ? String(s).substring(0, 10) : "—");
const inputCls = "bg-white border-none rounded text-base p-2 w-full shadow-[0_0_4px_rgba(0,0,0,0.2)]";

function Field({ label, value }: { label: string; value: React.ReactNode }) {
    return (
        <div className="flex flex-col gap-0.5">
            <span className="text-[#666] text-xs">{label}</span>
            <span className="text-sm font-medium break-words">{value || "—"}</span>
        </div>
    );
}
function Section({ icon, title, children }: { icon: React.ReactNode; title: string; children: React.ReactNode }) {
    return (
        <div className="flex flex-col gap-3">
            <span className="flex items-center gap-2 font-medium">{icon}{title}</span>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-x-8 gap-y-3">{children}</div>
        </div>
    );
}
const divider = <hr className="border-black/15" />;

export default function ModalReadMoreStaff({ staffId, managers, managerName, closeModal, refresh }: Props) {
    const { can } = useAuth();
    const canIam = can("IAM_MANAGE");
    const canReports = can("REPORT_READ");
    const today = new Date().toISOString().split("T")[0];
    const monthStart = `${today.substring(0, 7)}-01`;

    const [detail, setDetail] = useState<StaffDetailed | null>(null);
    const [loading, setLoading] = useState(true);
    const [busy, setBusy] = useState(false);
    // Which specific action is running, so only that button shows the spinner.
    const [busyAction, setBusyAction] = useState("");
    const [editing, setEditing] = useState(false);
    const [error, setError] = useState("");
    const [confirmDelete, setConfirmDelete] = useState(false);
    const [form, setForm] = useState({ phonePrimary: "", phoneSecondary: "", baseSalary: "", profitSharePercent: "", managerId: "" });
    // Profit-share bonus the staff member still has left to take (current shop).
    const [bonusLeft, setBonusLeft] = useState<number | null>(null);

    // shops & roles (IAM)
    const [assignments, setAssignments] = useState<StaffShopAssignment[]>([]);
    const [shopOptions, setShopOptions] = useState<ShopOption[]>([]);
    const [roleOptions, setRoleOptions] = useState<RoleOption[]>([]);
    const [assignShopId, setAssignShopId] = useState("");
    const [assignRoleId, setAssignRoleId] = useState("");

    // performance (REPORT_READ)
    const [perf, setPerf] = useState<StaffPerformance | null>(null);
    const [perfLoading, setPerfLoading] = useState(false);
    const [perfFrom, setPerfFrom] = useState(monthStart);
    const [perfTo, setPerfTo] = useState(today);

    const load = (initial = false) => {
        if (initial) setLoading(true);
        return axios.get(`${API_BASE}/admin/staff/${staffId}`)
            .then(res => {
                const d: StaffDetailed = res.data;
                setDetail(d);
                setForm({
                    phonePrimary: d.phonePrimary ?? "",
                    phoneSecondary: d.phoneSecondary ?? "",
                    baseSalary: String(d.baseSalary ?? ""),
                    profitSharePercent: String(d.profitSharePercent ?? ""),
                    managerId: d.managerId != null ? String(d.managerId) : "",
                });
            })
            .catch(error => console.error("Error fetching staff detail:", error))
            .finally(() => setLoading(false));
    };

    const loadAssignments = () => {
        if (!canIam) return Promise.resolve();
        return axios.get(`${API_BASE}/admin/staff/${staffId}/shops`)
            .then(res => setAssignments(res.data ?? []))
            .catch(err => console.error("Error fetching assignments:", err));
    };

    const loadPerformance = () => {
        if (!canReports) return;
        setPerfLoading(true);
        axios.get(`${API_BASE}/reports/staff/${staffId}/period`, { params: { dateFrom: perfFrom, dateTo: perfTo } })
            .then(res => setPerf(res.data))
            .catch(err => console.error("Error fetching staff performance:", err))
            .finally(() => setPerfLoading(false));
    };

    const loadBonusLeft = () => {
        axios.get(`${API_BASE}/staff-bonus/available/${staffId}`)
            .then(res => setBonusLeft(res.data.available ?? 0))
            .catch(() => setBonusLeft(null));
    };

    useEffect(() => {
        load(true);
        if (canIam) {
            loadAssignments();
            axios.get(`${API_BASE}/admin/staff/shop-options`).then(r => setShopOptions(r.data ?? [])).catch(() => {});
            axios.get(`${API_BASE}/admin/roles`, { params: { size: 1000, sort: "name,ASC" } }).then(r => setRoleOptions(r.data.content ?? [])).catch(() => {});
        }
        if (canReports) loadPerformance();
        loadBonusLeft();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [staffId]);

    // Shop/role mutations reload the assignment list and notify the page.
    const runAssign = (req: Promise<unknown>, action: string) => {
        setBusy(true);
        setBusyAction(action);
        setError("");
        req.then(() => loadAssignments()).then(() => refresh())
            .catch(err => setError(err?.response?.data?.message || "Дејството не успеа, обидете се повторно."))
            .finally(() => { setBusy(false); setBusyAction(""); });
    };
    const assignShop = () => {
        if (!assignShopId || !assignRoleId) { setError("Изберете продавница и улога."); return; }
        runAssign(axios.post(`${API_BASE}/admin/staff/${staffId}/shops`, { shopId: Number(assignShopId), roleId: Number(assignRoleId) })
            .then(() => { setAssignShopId(""); setAssignRoleId(""); }), "assign");
    };
    const revokeRole = (staffRoleId: number) => runAssign(axios.delete(`${API_BASE}/admin/staff/${staffId}/roles/${staffRoleId}`), `role-${staffRoleId}`);
    const revokeShop = (shopId: number) => runAssign(axios.delete(`${API_BASE}/admin/staff/${staffId}/shops/${shopId}`), `shop-${shopId}`);

    // Run an action, then reload the detail and notify the page.
    const run = (req: Promise<unknown>, action: string) => {
        setBusy(true);
        setBusyAction(action);
        setError("");
        req.then(() => load()).then(() => refresh())
            .catch(err => setError(err?.response?.data?.message || "Дејството не успеа, обидете се повторно."))
            .finally(() => { setBusy(false); setBusyAction(""); });
    };

    const changeStatus = (action: "activate" | "deactivate" | "suspend") =>
        run(axios.post(`${API_BASE}/admin/staff/${staffId}/${action}`, {}), action);

    const softDelete = () => {
        setBusy(true);
        setBusyAction("delete");
        setError("");
        axios.delete(`${API_BASE}/admin/staff/${staffId}`)
            .then(() => { refresh(); closeModal(); })
            .catch(err => { setError(err?.response?.data?.message || "Бришењето не успеа."); setBusy(false); setBusyAction(""); });
    };

    const save = () => {
        if (!form.phonePrimary.trim() || !form.baseSalary.trim()) { setError("Пополни ги задолжителните полиња."); return; }
        run(axios.patch(`${API_BASE}/admin/staff/${staffId}`, {
            phonePrimary: form.phonePrimary,
            phoneSecondary: form.phoneSecondary || null,
            baseSalary: Number(form.baseSalary),
            profitSharePercent: Number(form.profitSharePercent),
            managerId: form.managerId ? Number(form.managerId) : null,
        }).then(() => setEditing(false)), "save");
    };

    // Spinner shown in place of a button's content while that specific action runs.
    const spin = <Loading width={18} height={18} />;

    const statusBtn = "flex items-center justify-center gap-2 px-4 py-2 rounded text-sm font-medium transition-all disabled:opacity-40 disabled:cursor-not-allowed";

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" onClick={busy ? undefined : closeModal} />
            <div className="flex flex-col gap-6 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-8 rounded-lg w-[min(880px,92%)] max-h-[90vh] overflow-y-auto scrollbar-hidden">
                {loading || !detail ? (
                    <div className="flex justify-center py-10"><Loading /></div>
                ) : (
                    <>
                        <div className="flex justify-between items-center">
                            <div className="flex items-center gap-3">
                                <span className="flex h-10 w-10 items-center justify-center rounded-full bg-green/15 text-green">
                                    <UserRound size={20} />
                                </span>
                                <h1 className="text-2xl font-semibold">{detail.fullName}</h1>
                                <span className={`px-2 py-0.5 rounded text-xs font-semibold ${detail.status === "ACTIVE" ? "bg-green/15 text-green" : detail.status === "SUSPENDED" ? "bg-red-500/15 text-red-500" : "bg-black/10 text-[#555]"}`}>
                                    {STAFF_STATUS_LABEL[detail.status]}
                                </span>
                                {detail.deletedAt && <span className="px-2 py-0.5 rounded text-xs font-semibold bg-red-500/15 text-red-500">Избришан</span>}
                            </div>
                            <div className="flex items-center gap-3">
                                {!editing && !detail.deletedAt && (
                                    <button onClick={() => setEditing(true)} disabled={busy}
                                            className="flex items-center gap-2 px-4 py-2 rounded text-sm font-medium border border-green/60 text-green hover:bg-green hover:text-white transition-all disabled:opacity-40 disabled:cursor-not-allowed">
                                        <Pencil size={16} /> Измени
                                    </button>
                                )}
                                <button className="close-x-btn disabled:opacity-40 disabled:cursor-not-allowed" onClick={closeModal} disabled={busy}><X size={28} /></button>
                            </div>
                        </div>

                        {!editing ? (
                            <>
                                <Section icon={<UserRound size={20} />} title="Лични податоци">
                                    <Field label="Име и презиме" value={detail.fullName} />
                                    <Field label="Корисничко име" value={detail.username} />
                                    <Field label="ЕМБГ" value={detail.nationalId} />
                                    <Field label="Телефон" value={detail.phonePrimary} />
                                    <Field label="Телефон 2" value={detail.phoneSecondary} />
                                    <Field label="Менаџер" value={detail.managerId ? (managerName.get(detail.managerId) ?? `#${detail.managerId}`) : "—"} />
                                    <Field label="Креиран" value={date(detail.createdAt)} />
                                    <Field label="Изменет" value={date(detail.updatedAt)} />
                                </Section>

                                {divider}

                                <Section icon={<BadgeEuro size={20} />} title="Плата">
                                    <Field label="Основна плата" value={money(detail.baseSalary)} />
                                    <Field label="Профит удел" value={`${detail.profitSharePercent}%`} />
                                    <Field label="Бонус за подигнување" value={bonusLeft == null ? "—" : money(bonusLeft)} />
                                </Section>
                            </>
                        ) : (
                            <div className="flex flex-col gap-4">
                                <div className="grid grid-cols-2 md:grid-cols-3 gap-x-8 gap-y-4">
                                    <div className="flex flex-col gap-1">
                                        <span className="text-[#666] text-xs">Телефон *</span>
                                        <input className={inputCls} value={form.phonePrimary} onChange={e => setForm(p => ({ ...p, phonePrimary: e.target.value }))} />
                                    </div>
                                    <div className="flex flex-col gap-1">
                                        <span className="text-[#666] text-xs">Телефон 2</span>
                                        <input className={inputCls} value={form.phoneSecondary} onChange={e => setForm(p => ({ ...p, phoneSecondary: e.target.value }))} />
                                    </div>
                                    <div className="flex flex-col gap-1">
                                        <span className="text-[#666] text-xs">Основна плата *</span>
                                        <input className={inputCls} type="number" value={form.baseSalary} onChange={e => setForm(p => ({ ...p, baseSalary: e.target.value }))} />
                                    </div>
                                    <div className="flex flex-col gap-1">
                                        <span className="text-[#666] text-xs">Профит удел (%)</span>
                                        <input className={inputCls} type="number" value={form.profitSharePercent} onChange={e => setForm(p => ({ ...p, profitSharePercent: e.target.value }))} />
                                    </div>
                                    <div className="flex flex-col gap-1">
                                        <span className="text-[#666] text-xs">Менаџер</span>
                                        <select className={inputCls} value={form.managerId} onChange={e => setForm(p => ({ ...p, managerId: e.target.value }))}>
                                            <option value="">— Без менаџер —</option>
                                            {managers.map(m => <option key={m.id} value={m.id}>{m.fullName}</option>)}
                                        </select>
                                    </div>
                                </div>
                                <div className="flex gap-3 items-center justify-end">
                                    <button onClick={() => { setEditing(false); setError(""); }} disabled={busy}
                                            className="px-5 py-2 rounded bg-black/10 text-sm hover:bg-black/15 transition-colors">Откажи</button>
                                    {busyAction === "save" ? <Loading width={26} height={26} /> : (
                                        <button onClick={save} disabled={busy}
                                                className="group relative overflow-hidden flex items-center justify-center gap-2 px-6 py-2 rounded text-white font-medium bg-green shadow-[4px_2px_6px_rgba(0,0,0,0.2)] transition-all duration-300 hover:scale-105 disabled:opacity-40 disabled:cursor-not-allowed">
                                            <span className="pointer-events-none absolute inset-0 -translate-x-full bg-gradient-to-r from-transparent via-white/25 to-transparent transition-transform duration-700 ease-out group-hover:translate-x-[180%]" />
                                            <CheckCheck size={20} /> Зачувај
                                        </button>
                                    )}
                                </div>
                            </div>
                        )}

                        {/* Performance (transactions the staff performed in the range) */}
                        {canReports && !editing && (
                            <>
                                {divider}
                                <div className="flex flex-col gap-3">
                                    <div className="flex items-center justify-between gap-3 flex-wrap">
                                        <span className="flex items-center gap-2 font-medium"><BarChart3 size={20} /> Перформанси</span>
                                        <div className="flex items-center gap-2">
                                            <input type="date" className="bg-white border-none rounded text-xs p-1.5 shadow-[0_0_4px_rgba(0,0,0,0.2)]" value={perfFrom} onChange={e => setPerfFrom(e.target.value)} />
                                            <span className="text-xs text-[#666]">—</span>
                                            <input type="date" className="bg-white border-none rounded text-xs p-1.5 shadow-[0_0_4px_rgba(0,0,0,0.2)]" value={perfTo} onChange={e => setPerfTo(e.target.value)} />
                                            <button onClick={loadPerformance} disabled={perfLoading}
                                                    className="flex items-center gap-1.5 px-3 py-1.5 rounded bg-green text-white text-xs font-medium hover:scale-105 transition-all disabled:opacity-40">
                                                <Search size={14} /> Прикажи
                                            </button>
                                        </div>
                                    </div>
                                    {perfLoading ? (
                                        <div className="flex justify-center py-2"><Loading width={28} height={28} /></div>
                                    ) : perf && (
                                        <div className="grid grid-cols-2 md:grid-cols-4 gap-x-8 gap-y-3">
                                            <Field label="Приход" value={<span className="text-green font-semibold">{money(perf.revenue)}</span>} />
                                            <Field label="Дадено на клиенти" value={money(perf.moneyGiven)} />
                                            <Field label="Профит (залози)" value={money(perf.pawnProfit)} />
                                            <Field label="Профит (продажби)" value={money(perf.saleProfit)} />
                                            <Field label="Вкупен профит" value={<span className="text-green font-semibold">{money(perf.totalProfit)}</span>} />
                                            <Field label="Отворени залози" value={String(perf.pawnsOpened)} />
                                            <Field label="Продолжени залози" value={String(perf.pawnsExtended)} />
                                            <Field label="Затворени залози" value={String(perf.pawnsRedeemed)} />
                                            <Field label="Просечен залог" value={money(perf.avgLoanSize)} />
                                            <Field label="Креирани продажби" value={String(perf.salesCreated)} />
                                            <Field label="Продадени" value={String(perf.salesSold)} />
                                            <Field label="Расходи внесени" value={String(perf.expensesRecorded)} />
                                            <Field label="Ризик знаменца" value={<span className={perf.riskFlags > 0 ? "text-red-500 font-semibold" : ""}>{String(perf.riskFlags)}</span>} />
                                            <Field label="Потценети продажби" value={String(perf.underpricedSales)} />
                                            <Field label="Подплатени откупи" value={String(perf.underpaidRedemptions)} />
                                            <Field label="Каса отстапувања" value={`${perf.discrepancyCount} (${money(perf.discrepancyTotal)})`} />
                                        </div>
                                    )}
                                </div>
                            </>
                        )}

                        {/* Shops & roles (assign staff to a shop with a role; one role per shop) */}
                        {canIam && (
                            <>
                                {divider}
                                <div className="flex flex-col gap-3">
                                    <span className="flex items-center gap-2 font-medium"><ShieldCheck size={20} /> Продавници и улоги</span>
                                    {assignments.length === 0 ? (
                                        <p className="text-sm text-[#888]">Не е доделен на ниедна продавница.</p>
                                    ) : assignments.map(a => (
                                        <div key={a.shopId} className="flex items-center justify-between gap-3 bg-white rounded px-3 py-2 shadow-[0_0_4px_rgba(0,0,0,0.12)]">
                                            <div className="flex items-center gap-2 flex-wrap">
                                                <Store size={16} className="text-green" />
                                                <span className="font-semibold text-sm">{a.shopName}</span>
                                                <span className="text-xs text-[#666]">({a.shopCode})</span>
                                                {a.roleName
                                                    ? <span className="text-xs bg-green/10 text-green rounded px-2 py-0.5 font-medium">{a.roleName}</span>
                                                    : <span className="text-xs text-red-500">Без улога</span>}
                                            </div>
                                            {!detail.deletedAt && (
                                                <div className="flex items-center gap-2 shrink-0">
                                                    {a.staffRoleId != null && (
                                                        <button onClick={() => revokeRole(a.staffRoleId!)} disabled={busy}
                                                                className="flex items-center justify-center gap-1.5 px-3 py-1.5 rounded text-xs font-medium border border-amber-500/60 text-amber-600 hover:bg-amber-500 hover:text-white transition-all disabled:opacity-40">
                                                            {busyAction === `role-${a.staffRoleId}` ? spin : <><Ban size={14} /> Одземи улога</>}
                                                        </button>
                                                    )}
                                                    <button onClick={() => revokeShop(a.shopId)} disabled={busy}
                                                            className="flex items-center justify-center gap-1.5 px-3 py-1.5 rounded text-xs font-medium border border-red-500/60 text-red-500 hover:bg-red-500 hover:text-white transition-all disabled:opacity-40">
                                                        {busyAction === `shop-${a.shopId}` ? spin : <><Trash2 size={14} /> Отстрани</>}
                                                    </button>
                                                </div>
                                            )}
                                        </div>
                                    ))}

                                    {!detail.deletedAt && (
                                        <div className="flex items-end gap-3 flex-wrap mt-1">
                                            <div className="flex flex-col gap-1">
                                                <span className="text-[#666] text-xs">Продавница</span>
                                                <select className="bg-white border-none rounded text-sm p-2 shadow-[0_0_4px_rgba(0,0,0,0.2)] min-w-[12rem]" value={assignShopId} onChange={e => setAssignShopId(e.target.value)}>
                                                    <option value="">Избери продавница</option>
                                                    {shopOptions.map(s => <option key={s.id} value={s.id}>{s.name} ({s.code})</option>)}
                                                </select>
                                            </div>
                                            <div className="flex flex-col gap-1">
                                                <span className="text-[#666] text-xs">Улога</span>
                                                <select className="bg-white border-none rounded text-sm p-2 shadow-[0_0_4px_rgba(0,0,0,0.2)] min-w-[10rem]" value={assignRoleId} onChange={e => setAssignRoleId(e.target.value)}>
                                                    <option value="">Избери улога</option>
                                                    {roleOptions.map(r => <option key={r.id} value={r.id}>{r.name}</option>)}
                                                </select>
                                            </div>
                                            {busyAction === "assign" ? <Loading width={24} height={24} /> : (
                                                <button onClick={assignShop} disabled={busy}
                                                        className="flex items-center justify-center gap-2 px-4 py-2 rounded bg-green text-white text-sm font-medium shadow-[0_0_4px_rgba(0,0,0,0.2)] hover:scale-105 transition-all disabled:opacity-40">
                                                    <Plus size={16} /> Додели
                                                </button>
                                            )}
                                        </div>
                                    )}
                                </div>
                            </>
                        )}

                        {error && <p className="text-red-500 text-sm">{error}</p>}

                        {/* Status & lifecycle actions */}
                        {!detail.deletedAt && (
                            <>
                                {divider}
                                <div className="flex flex-wrap items-center gap-3">
                                    {detail.status !== "ACTIVE" && (
                                        <button onClick={() => changeStatus("activate")} disabled={busy}
                                                className={`${statusBtn} border border-green/60 text-green hover:bg-green hover:text-white`}>{busyAction === "activate" ? spin : "Активирај"}</button>
                                    )}
                                    {detail.status !== "INACTIVE" && (
                                        <button onClick={() => changeStatus("deactivate")} disabled={busy}
                                                className={`${statusBtn} border border-black/30 text-[#555] hover:bg-black/10`}>{busyAction === "deactivate" ? spin : "Деактивирај"}</button>
                                    )}
                                    {detail.status !== "SUSPENDED" && (
                                        <button onClick={() => changeStatus("suspend")} disabled={busy}
                                                className={`${statusBtn} border border-amber-500/60 text-amber-600 hover:bg-amber-500 hover:text-white`}>{busyAction === "suspend" ? spin : "Суспендирај"}</button>
                                    )}
                                    <div className="ml-auto flex items-center gap-3">
                                        {confirmDelete ? (
                                            <div className="flex items-center gap-2">
                                                <span className="text-sm text-red-500">Сигурно?</span>
                                                {busyAction === "delete" ? <Loading width={24} height={24} /> : (
                                                    <button onClick={softDelete} disabled={busy}
                                                            className={`${statusBtn} bg-red-500 text-white hover:bg-red-600`}>Да, избриши</button>
                                                )}
                                                <button onClick={() => setConfirmDelete(false)} disabled={busy}
                                                        className={`${statusBtn} bg-black/10 hover:bg-black/15`}>Откажи</button>
                                            </div>
                                        ) : (
                                            <button onClick={() => setConfirmDelete(true)} disabled={busy}
                                                    className={`${statusBtn} border border-red-500/60 text-red-500 hover:bg-red-500 hover:text-white`}>
                                                <Trash2 size={16} /> Избриши
                                            </button>
                                        )}
                                    </div>
                                </div>
                            </>
                        )}
                    </>
                )}
            </div>
        </>,
        document.getElementById("portal")!
    );
}
