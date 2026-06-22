import ReactDom from "react-dom";
import { useEffect, useState } from "react";
import axios from "axios";
import { X, Bell, FileText, Tag, CheckCheck } from "lucide-react";
import Loading from "../../shared/components/Loading.tsx";
import { API_BASE } from "../../shared/api/config.ts";
import { NotificationRow, NotificationDetail, NOTIFICATION_TYPE_LABEL } from "./types.ts";

interface Props {
    notificationId: number;
    closeModal: () => void;
    onRead: (n: NotificationRow) => void;
}

const money = (n: number | null | undefined) => (n == null ? "—" : `${Number(n).toLocaleString("de-DE")} ден`);
const date = (s: string | null | undefined) => (s ? String(s).substring(0, 10) : "—");
const datetime = (s: string | null | undefined) => (s ? String(s).substring(0, 16).replace("T", " ") : "—");

const PAWN_STATUS_MK: Record<string, string> = { ACTIVE: "Активен", REDEEMED: "Откупен", FORFEITED: "Пренесен во продажба" };
const SALE_STATUS_MK: Record<string, string> = { AVAILABLE: "Достапна", SOLD: "Продадена", CANCELED: "Откажана" };

function Field({ label, value }: { label: string; value: React.ReactNode }) {
    return (
        <div className="flex flex-col gap-0.5">
            <span className="text-[#666] text-xs">{label}</span>
            <span className="text-sm font-medium break-words">{value ?? "—"}</span>
        </div>
    );
}

export default function ModalReadMoreNotification({ notificationId, closeModal, onRead }: Props) {
    const [detail, setDetail] = useState<NotificationDetail | null>(null);
    const [entity, setEntity] = useState<any | null>(null);
    const [loading, setLoading] = useState(true);
    const [busy, setBusy] = useState(false);

    useEffect(() => {
        setLoading(true);
        axios.get(`${API_BASE}/notifications/${notificationId}/detail`)
            .then(res => {
                const d: NotificationDetail = res.data;
                setDetail(d);
                if (d.entityKind === "PAWN" && d.entityId != null) {
                    return axios.get(`${API_BASE}/pawns/${d.entityId}`).then(r => setEntity(r.data));
                }
                if (d.entityKind === "SALE" && d.entityId != null) {
                    return axios.get(`${API_BASE}/sales/${d.entityId}`).then(r => setEntity(r.data));
                }
            })
            .catch(err => console.error("Error fetching notification detail:", err))
            .finally(() => setLoading(false));
    }, [notificationId]);

    const markRead = () => {
        setBusy(true);
        axios.post(`${API_BASE}/notifications/${notificationId}/read`)
            .then(res => { onRead(res.data); setDetail(d => d ? { ...d, notification: res.data } : d); })
            .catch(err => console.error("Error marking notification read:", err))
            .finally(() => setBusy(false));
    };

    const n = detail?.notification;

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" onClick={closeModal} />
            <div className="flex flex-col gap-6 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-8 rounded-lg w-[min(720px,92%)] max-h-[90vh] overflow-y-auto scrollbar-hidden">
                {loading || !n ? (
                    <div className="flex justify-center py-10"><Loading /></div>
                ) : (
                    <>
                        <div className="flex justify-between items-start gap-3">
                            <div className="flex items-center gap-3">
                                <span className="flex h-10 w-10 items-center justify-center rounded-full bg-green/15 text-green"><Bell size={20} /></span>
                                <div className="flex flex-col">
                                    <h1 className="text-xl font-semibold">{n.title}</h1>
                                    <div className="flex items-center gap-2">
                                        <span className="px-2 py-0.5 rounded text-xs font-semibold bg-black/10 text-[#555]">{NOTIFICATION_TYPE_LABEL[n.type] ?? n.type}</span>
                                        <span className={`px-2 py-0.5 rounded text-xs font-semibold ${n.read ? "bg-black/10 text-[#555]" : "bg-green/15 text-green"}`}>
                                            {n.read ? "Прочитано" : "Непрочитано"}
                                        </span>
                                    </div>
                                </div>
                            </div>
                            <button className="close-x-btn" onClick={closeModal}><X size={28} /></button>
                        </div>

                        <div className="flex flex-col gap-3">
                            <div className="grid grid-cols-2 md:grid-cols-3 gap-x-8 gap-y-3">
                                <Field label="Создадено" value={datetime(n.createdAt)} />
                                <Field label="Прочитано на" value={datetime(n.readAt)} />
                            </div>
                            {n.description && <p className="text-sm text-[#333] bg-white rounded p-3 shadow-[0_0_4px_rgba(0,0,0,0.1)]">{n.description}</p>}
                        </div>

                        {/* Linked entity */}
                        {detail?.entityKind === "PAWN" && entity && (
                            <div className="flex flex-col gap-3">
                                <span className="flex items-center gap-2 font-medium"><FileText size={20} /> Поврзан залог #{entity.id}</span>
                                <div className="grid grid-cols-2 md:grid-cols-3 gap-x-8 gap-y-3">
                                    <Field label="Клиент" value={entity.customer?.fullName} />
                                    <Field label="Предмет" value={entity.item?.description} />
                                    <Field label="Статус" value={PAWN_STATUS_MK[entity.status] ?? entity.status} />
                                    <Field label="Вредност" value={money(entity.principalAmount)} />
                                    <Field label="Провизија" value={money(entity.interestAmount)} />
                                    <Field label="Важи до" value={date(entity.dueDate)} />
                                </div>
                            </div>
                        )}
                        {detail?.entityKind === "SALE" && entity && (
                            <div className="flex flex-col gap-3">
                                <span className="flex items-center gap-2 font-medium"><Tag size={20} /> Поврзана продажба #{entity.id}</span>
                                <div className="grid grid-cols-2 md:grid-cols-3 gap-x-8 gap-y-3">
                                    <Field label="Клиент" value={entity.customer?.fullName ?? entity.customerName} />
                                    <Field label="Предмет" value={entity.item?.description} />
                                    <Field label="Статус" value={SALE_STATUS_MK[entity.status] ?? entity.status} />
                                    <Field label="Откупна цена" value={money(entity.purchasePrice)} />
                                    <Field label="Продажна цена" value={money(entity.salePrice)} />
                                    <Field label="Профит" value={money(entity.profit)} />
                                </div>
                            </div>
                        )}
                        {detail && detail.entityKind == null && (
                            <p className="text-sm text-[#888]">Нема поврзан запис.</p>
                        )}

                        {!n.read && (
                            <div className="flex justify-end">
                                <button onClick={markRead} disabled={busy}
                                    className="flex items-center justify-center gap-2 px-6 py-2 rounded text-white font-medium bg-green shadow-[4px_2px_6px_rgba(0,0,0,0.2)] transition-all hover:scale-105 disabled:opacity-40">
                                    {busy ? <Loading width={20} height={20} /> : <><CheckCheck size={20} /> Означи како прочитано</>}
                                </button>
                            </div>
                        )}
                    </>
                )}
            </div>
        </>,
        document.getElementById("portal")!
    );
}
