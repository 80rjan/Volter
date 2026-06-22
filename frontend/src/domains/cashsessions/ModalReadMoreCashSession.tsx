import ReactDom from "react-dom";
import { useEffect, useState } from "react";
import axios from "axios";
import { X, Landmark, User, TriangleAlert, Wrench } from "lucide-react";
import Loading from "../../shared/components/Loading.tsx";
import ModalResolveDiscrepancy from "./ModalResolveDiscrepancy.tsx";
import { API_BASE } from "../../shared/api/config.ts";
import { CashSession, CashSessionOperator, Discrepancy } from "./types.ts";
import { useAuth } from "../../GlobalContext.tsx";

interface Props {
    session: CashSession;
    discrepancy: Discrepancy | null;
    onResolved: (d: Discrepancy) => void;
    closeModal: (e?: React.MouseEvent) => void;
}

const SESSION_STATUS_MK: Record<string, string> = { OPEN: "Отворена", CLOSED: "Затворена" };
const STAFF_STATUS_MK: Record<string, string> = { ACTIVE: "Активен", INACTIVE: "Неактивен", SUSPENDED: "Суспендиран" };
const DISC_TYPE_MK: Record<string, string> = { SHORTAGE: "Кусок", OVERAGE: "Вишок" };
const DISC_PHASE_MK: Record<string, string> = { OPENING: "При отворање", CLOSING: "При затворање" };
const DISC_STATUS_MK: Record<string, string> = { OPEN: "Нерешено", RESOLVED: "Решено" };

const money = (n: number | null | undefined) => (n == null ? "—" : `${Number(n).toLocaleString("de-DE")} ден`);
const datetime = (s: string | null | undefined) => (s ? String(s).substring(0, 19).replace("T", " ") : "—");

function Field({ label, value }: { label: string; value: React.ReactNode }) {
    return (
        <div className="flex flex-col gap-0.5">
            <span className="text-[#666] text-xs">{label}</span>
            <span className="text-sm font-medium break-words">{value ?? "—"}</span>
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

export default function ModalReadMoreCashSession({ session, discrepancy, onResolved, closeModal }: Props) {
    const { can } = useAuth();
    const canResolve = can("CASH_REGISTER_SESSION_DISCREPANCY_RESOLVE");

    const [operator, setOperator] = useState<CashSessionOperator | null>(null);
    const [loading, setLoading] = useState(true);
    const [showResolve, setShowResolve] = useState(false);

    useEffect(() => {
        setLoading(true);
        axios.get(`${API_BASE}/cash-register-sessions/${session.id}/detailed`)
            .then(res => setOperator(res.data.staff ?? null))
            .catch(error => console.error("Error fetching session detail:", error))
            .finally(() => setLoading(false));
    }, [session.id]);

    const over = discrepancy && discrepancy.difference > 0;

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" onClick={closeModal} />
            <div className="flex flex-col gap-6 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-8 rounded-lg w-[min(820px,92%)] max-h-[90vh] overflow-y-auto scrollbar-hidden">
                <div className="flex justify-between items-center">
                    <div className="flex items-center gap-3">
                        <span className="flex h-10 w-10 items-center justify-center rounded-full bg-green/15 text-green">
                            <Landmark size={20} />
                        </span>
                        <h1 className="text-2xl font-semibold">Сесија #{session.id}</h1>
                        <span className={`px-2 py-0.5 rounded text-xs font-semibold ${session.status == "OPEN" ? "bg-green/15 text-green" : "bg-gray-500/15 text-gray-500"}`}>
                            {SESSION_STATUS_MK[session.status] ?? session.status}
                        </span>
                    </div>
                    <button className="close-x-btn" onClick={closeModal}><X size={28} /></button>
                </div>

                <Section icon={<Landmark size={20} />} title="Сесија">
                    <Field label="Каса" value={session.cashRegisterCode} />
                    <Field label="Статус" value={SESSION_STATUS_MK[session.status] ?? session.status} />
                    <Field label="Отворена на" value={datetime(session.openedAt)} />
                    <Field label="Затворена на" value={datetime(session.closedAt)} />
                    <Field label="Почетно салдо" value={money(session.openingBalance)} />
                    <Field label="Тековно салдо" value={money(session.currentBalance)} />
                    <Field label="Завршно салдо" value={money(session.closingBalance)} />
                    <Field label="Очекувана провизија" value={money(session.expectedInterest)} />
                </Section>

                {divider}

                <Section icon={<User size={20} />} title="Оператор">
                    {loading ? (
                        <div className="col-span-full"><Loading width={28} height={28} /></div>
                    ) : operator ? (
                        <>
                            <Field label="Име и презиме" value={operator.fullName} />
                            <Field label="Корисничко име" value={operator.username} />
                            <Field label="Статус" value={STAFF_STATUS_MK[operator.status] ?? operator.status} />
                            <Field label="ID" value={`#${operator.id}`} />
                        </>
                    ) : (
                        <Field label="Оператор" value={`#${session.staffId}`} />
                    )}
                </Section>

                {discrepancy && (
                    <>
                        {divider}
                        <div className="flex flex-col gap-3">
                            <div className="flex items-center justify-between">
                                <span className="flex items-center gap-2 font-medium">
                                    <TriangleAlert size={20} className={over ? "text-green" : "text-red-500"} /> Отстапување
                                </span>
                                {discrepancy.status === "OPEN" && canResolve && (
                                    <button
                                        onClick={() => setShowResolve(true)}
                                        className="flex items-center gap-2 px-4 py-2 rounded text-sm font-medium border border-green/60 text-green hover:bg-green hover:text-white transition-all"
                                    >
                                        <Wrench size={16} /> Реши отстапување
                                    </button>
                                )}
                            </div>
                            <div className="grid grid-cols-2 md:grid-cols-4 gap-x-8 gap-y-3">
                                <Field label="Тип" value={DISC_TYPE_MK[discrepancy.type] ?? discrepancy.type} />
                                <Field label="Фаза" value={DISC_PHASE_MK[discrepancy.phase] ?? discrepancy.phase} />
                                <Field label="Статус" value={DISC_STATUS_MK[discrepancy.status] ?? discrepancy.status} />
                                <Field label="Очекувано" value={money(discrepancy.expectedAmount)} />
                                <Field label="Изброено" value={money(discrepancy.countedAmount)} />
                                <Field
                                    label="Разлика"
                                    value={<span className={over ? "text-green" : "text-red-500"}>{over ? "+" : ""}{money(discrepancy.difference)}</span>}
                                />
                                <Field label="Решено на" value={datetime(discrepancy.resolvedAt)} />
                                <div className="md:col-span-2">
                                    <Field label="Белешка за решавање" value={discrepancy.resolutionNote || "—"} />
                                </div>
                            </div>
                        </div>
                    </>
                )}
            </div>

            {showResolve && discrepancy && (
                <ModalResolveDiscrepancy
                    discrepancy={discrepancy}
                    onResolved={(d) => { onResolved(d); setShowResolve(false); }}
                    closeModal={() => setShowResolve(false)}
                />
            )}
        </>,
        document.getElementById("portal")!
    );
}
