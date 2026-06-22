import axios from "axios";
import { useEffect, useState } from "react";
import {Euro, RotateCcw, X, Ellipsis, ShoppingCart, Tag} from "lucide-react";
import ModalShowMessagePawn from "./ModalShowMessagePawn.tsx";
import ModalReadMorePawn from "./ModalReadMorePawn.tsx";
import Loading from "../../shared/components/Loading.tsx";
import ModalActions from "../../shared/components/ModalActions.tsx";
import { PawnRow, PawnDetailed } from "./types.ts";
import { API_BASE } from "../../shared/api/config.ts";
import { useAuth } from "../../GlobalContext.tsx";
import { resolveActiveSessionId } from "../../shared/utils/activeSession.ts";

interface Props {
    pawn: PawnRow;
    refresh: () => void;
    isOdd: boolean;
    refreshCashReg: () => void;
}

const getCat: Record<string, string> = {
    Electronics: "Електроника",
    Watch: "Часовници",
    Vehicle: "Возила",
    Gold: "Злато",
    Other: "Останато",
};

export default function Pawn({ pawn, refresh, isOdd, refreshCashReg }: Props) {
    const { can } = useAuth();
    const [modalSuccessMsg, setModalSuccessMsg] = useState(false);
    const [modalReadMore, setModalReadMore] = useState(false);
    const [successMsg, setSuccessMsg] = useState("");
    const [infoMsg, setInfoMsg] = useState("");
    const [pawnDetailed, setPawnDetailed] = useState<PawnDetailed | null>(null);
    const [loading, setLoading] = useState(false);
    const [modalRedeemPawn, setModalRedeemPawn] = useState(false);
    const [modalExtendPawn, setModalExtendPawn] = useState(false);
    const [modalForfeitPawn, setModalForfeitPawn] = useState(false);

    // Extend/redeem post against the active register's open session (selected in the cash bar).
    const getOpenSessionId = (): Promise<number | null> => resolveActiveSessionId();

    // provision -> interestPaid; description is no longer sent (the new /extend has no such field).
    const extendPawn = async (id: number, _category: string, provision: number, _description: string, _carryOverDays: number) => {
        setLoading(true);
        try {
            const sessionId = await getOpenSessionId();
            if (!sessionId) { setSuccessMsg("Нема отворена каса"); setInfoMsg(""); setModalExtendPawn(false); setModalSuccessMsg(true); return; }
            await axios.post(`${API_BASE}/pawns/${id}/extend`, { interestPaid: provision, fee: 0, cashRegisterSessionId: sessionId });
            setSuccessMsg("Успешно продолжен залог");
            setInfoMsg(`Додадени се ${provision.toLocaleString("de-DE")} во каса!`);
            setModalExtendPawn(false);
            setModalSuccessMsg(true);
        } catch (error) {
            console.error("Error extending pawn:", error);
        } finally { setLoading(false); }
    };

    // The staff-entered amount is the final redemption price recorded in the transaction.
    const redeemPawn = async (id: number, _category: string, priceClosed: number, _description: string) => {
        setLoading(true);
        try {
            const sessionId = await getOpenSessionId();
            if (!sessionId) { setSuccessMsg("Нема отворена каса"); setInfoMsg(""); setModalRedeemPawn(false); setModalSuccessMsg(true); return; }
            await axios.post(`${API_BASE}/pawns/${id}/redeem`, { paidAmount: priceClosed, cashRegisterSessionId: sessionId });
            setSuccessMsg("Успешно затворен залог");
            setInfoMsg("Залогот е затворен!");
            setModalRedeemPawn(false);
            setModalSuccessMsg(true);
        } catch (error) {
            console.error("Error redeeming pawn:", error);
        } finally { setLoading(false); }
    };

    const forfeitPawn = async (id: number, _category: string) => {
        setLoading(true);
        try {
            await axios.post(`${API_BASE}/pawns/${id}/forfeit`, {});
            setSuccessMsg("Успешно пренесен залог во продажба");
            setInfoMsg("");
            setModalForfeitPawn(false);
            setModalSuccessMsg(true);
        } catch (error) {
            console.error("Error forfeiting pawn:", error);
        } finally { setLoading(false); }
    };

    useEffect(() => {
        if (pawnDetailed != null) setModalReadMore(true);
    }, [pawnDetailed]);

    const fetchPawn = (_clientId: number, _category: string, pawnId: number) => {
        setLoading(true);
        axios.get(`${API_BASE}/pawns/${pawnId}`)
            .then(res => setPawnDetailed(res.data))
            .catch(error => console.error("Error fetching pawn:", error))
            .finally(() => setLoading(false));
    };

    const cols = "grid-cols-[3rem_1.5fr_1fr_2fr_repeat(4,1fr)_1.5fr_0.5fr]";

    return (
        <div style={{ background: isOdd ? "#f0f0f0" : "#ffffff" }} className={`grid place-items-center text-center ${cols} border-b border-black/20 svg-hover`}>
            <p className="text-xs">{pawn.Id}</p>
            <p className={`text-xs font-semibold ${pawn.Status === "ACTIVE" && pawn["Days Left"] < 0 ? "text-red-500" : ""}`}>{pawn.Name.toUpperCase()}</p>
            <p className="text-xs">{getCat[pawn.Category]}</p>
            <p className="text-xs">{pawn.About}</p>
            <p className="text-xs">{Number(pawn["Item Cost"]).toLocaleString("de-DE")}</p>
            <p className="text-xs font-semibold italic">{Number(pawn.Provision).toLocaleString("de-DE")}</p>
            <p className={`text-xs font-semibold ${
                pawn.Status === "ACTIVE"
                    ? (pawn["Days Left"] < 0 ? "text-red-500" : "text-green")
                    : (pawn["Days Left"] < 0 ? "text-amber-600" : "text-green")
            }`}>{pawn["Days Left"]}</p>
            <p className="text-xs">{String(pawn["Valid Until"]).substring(0, 10)}</p>
            {loading ? (
                <Loading width={30} height={30} />
            ) : (
                <>
                    {/* Actions only for active pawns, gated by permission; others are view-only. */}
                    {pawn.Status === "ACTIVE" ? (
                        <div className="flex gap-2">
                            {can("PAWN_WRITE") && <RotateCcw size={16} color="var(--green)" className="cursor-pointer" onClick={() => setModalExtendPawn(true)} />}
                            {can("PAWN_WRITE") && <X size={16} className="text-red-500 cursor-pointer" onClick={() => setModalRedeemPawn(true)} />}
                            {can("PAWN_FORFEIT") && <Tag size={16}  className="text-amber-500 cursor-pointer" onClick={() => setModalForfeitPawn(true)} />}
                        </div>
                    ) : (
                        <div />
                    )}
                    <Ellipsis size={20} color="#888" className="cursor-pointer" onClick={() => fetchPawn(pawn["Client Id"], pawn.Category, pawn.Id)} />
                </>
            )}

            {modalRedeemPawn && (
                <ModalActions
                    pawnAction="redeem"
                    action={redeemPawn}
                    id={pawn.Id}
                    category={pawn.Category}
                    successMsg="Успешно затворен залог!"
                    closeModal={() => setModalRedeemPawn(false)}
                    priceBought={Number(pawn["Item Cost"])}
                    provision={Number(pawn.Provision)}
                    dailyProvision={Math.abs(Math.round(Number(pawn.Provision)) / Number(pawn["Total Days"]))}
                    suggestedPrice={Number(pawn["Item Cost"]) + Number(pawn.Provision)}
                    daysLeft={Number(pawn["Days Left"])}
                    title="Со кој износ е затворен залогот?"
                    loading={loading}
                />
            )}

            {modalExtendPawn && (
                <ModalActions
                    pawnAction="extend"
                    action={extendPawn}
                    id={pawn.Id}
                    category={pawn.Category}
                    successMsg="Успешно продолжен залог!"
                    closeModal={() => setModalExtendPawn(false)}
                    priceBought={Number(pawn["Item Cost"])}
                    provision={Number(pawn.Provision)}
                    dailyProvision={Math.abs(Math.round(Number(pawn.Provision)) / Number(pawn["Total Days"]))}
                    suggestedPrice={Number(pawn.Provision)}
                    daysLeft={Number(pawn["Days Left"])}
                    title="Со кој износ е продолжен залогот?"
                    loading={loading}
                />
            )}

            {modalForfeitPawn && (
                <ModalActions
                    pawnAction="forfeit"
                    action={forfeitPawn}
                    id={pawn.Id}
                    category={pawn.Category}
                    successMsg="Успешно пренесен залог во продажба!"
                    closeModal={() => setModalForfeitPawn(false)}
                    priceBought={Number(pawn["Item Cost"])}
                    title="Пренеси во продажба?"
                    loading={loading}
                />
            )}

            {modalSuccessMsg && (
                <ModalShowMessagePawn
                    closeModal={() => { setModalSuccessMsg(false); refresh(); }}
                    successMsg={successMsg}
                    infoMsg={infoMsg}
                    clientName={pawn.Name}
                />
            )}

            {modalReadMore && pawnDetailed && (
                <ModalReadMorePawn
                    pawn={pawnDetailed}
                    closeModal={(e?: React.MouseEvent) => { e?.stopPropagation(); setModalReadMore(false); }}
                    refresh={refresh}
                />
            )}
        </div>
    );
}
