import axios from "axios";
import { useEffect, useState } from "react";
import { Euro, RotateCcw, X, Ellipsis } from "lucide-react";
import ModalShowMessagePawn from "./ModalShowMessagePawn.tsx";
import ModalReadMorePawn from "./ModalReadMorePawn.tsx";
import Loading from "./Loading.tsx";
import ModalActions from "./ModalActions.tsx";
import { PawnRow, PawnInfo } from "../types.ts";

interface Props {
    pawn: PawnRow;
    refresh: () => void;
    isOdd: boolean;
    refreshCashReg: () => void;
}

const tableName = (category: string) => ({
    Electronics: "electronics_pawn",
    Gold: "gold_pawn",
    Vehicle: "vehicle_pawn",
    Watch: "watch_pawn",
    Other: "other_pawn",
}[category]);

const getCat: Record<string, string> = {
    Electronics: "Електроника",
    Watch: "Часовници",
    Vehicle: "Возила",
    Gold: "Злато",
    Other: "Останато",
};

export default function Pawn({ pawn, refresh, isOdd, refreshCashReg }: Props) {
    const [modalSuccessMsg, setModalSuccessMsg] = useState(false);
    const [modalReadMore, setModalReadMore] = useState(false);
    const [successMsg, setSuccessMsg] = useState("");
    const [infoMsg, setInfoMsg] = useState("");
    const [pawnInfo, setPawnInfo] = useState<PawnInfo | null>(null);
    const [loading, setLoading] = useState(false);
    const [modalClosePawn, setModalClosePawn] = useState(false);
    const [modalContinuePawn, setModalContinuePawn] = useState(false);

    const continuePawn = (id: number, category: string, provision: number, description: string, carryOverDays: number) => {
        setLoading(true);
        const table = tableName(category);
        if (!table) return console.error("Invalid category:", category);
        axios.put(`http://localhost:3000/continuePawn`, { id, tableName: table, provision, description, carryOverDays })
            .then(() => {
                setSuccessMsg("Успешно продолжен залог");
                setInfoMsg(`Додадени се ${provision.toLocaleString("de-DE")} во каса!`);
                setModalContinuePawn(false);
                setModalSuccessMsg(true);
            })
            .catch(error => console.error("Error continuing pawn:", error))
            .finally(() => setLoading(false));
    };

    const closePawn = (id: number, category: string, priceClosed: number, description: string) => {
        setLoading(true);
        const table = tableName(category);
        if (!table) return console.error("Invalid category:", category);
        axios.put(`http://localhost:3000/closePawn`, { id, tableName: table, priceClosed, description })
            .then(() => {
                setSuccessMsg("Успешно затворен залог");
                setInfoMsg(`Додадени се ${priceClosed.toLocaleString("de-DE")} во каса!`);
                setModalClosePawn(false);
                setModalSuccessMsg(true);
            })
            .catch(error => console.error("Error closing pawn:", error))
            .finally(() => setLoading(false));
    };

    const movePawnToSale = (id: number, category: string) => {
        setLoading(true);
        const table = tableName(category);
        if (!table) return console.error("Invalid category:", category);
        axios.put(`http://localhost:3000/changePawnToSale`, { id, tableName: table })
            .then(() => { setSuccessMsg("Успешно пренесен залог во продажба"); setModalSuccessMsg(true); })
            .catch(error => console.error("Error moving pawn to sale:", error))
            .finally(() => setLoading(false));
    };

    useEffect(() => {
        if (pawnInfo != null) setModalReadMore(true);
    }, [pawnInfo]);

    const fetchPawn = (clientId: number, category: string, pawnId: number) => {
        setLoading(true);
        axios.get(`http://localhost:3000/getPawn?clientId=${clientId}&category=${category}&pawnId=${pawnId}`)
            .then(res => setPawnInfo({ ...res.data.pawnInfo, "Days Left": pawn["Days Left"] }))
            .catch(error => console.error("Error fetching pawn:", error))
            .finally(() => setLoading(false));
    };

    const cols = "grid-cols-[3rem_1.5fr_1fr_2fr_repeat(4,1fr)_1.5fr_0.5fr]";

    return (
        <div style={{ background: isOdd ? "#f0f0f0" : "#ffffff" }} className={`grid place-items-center text-center ${cols} border-b border-black/20 svg-hover`}>
            <p className="text-xs">{pawn["Client Id"]}</p>
            <p className={`text-xs font-semibold ${pawn["Days Left"] < 0 ? "text-red-500" : ""}`}>{pawn.Name.toUpperCase()}</p>
            <p className="text-xs">{getCat[pawn.Category]}</p>
            <p className="text-xs">{pawn.About}</p>
            <p className="text-xs">{Number(pawn["Item Cost"]).toLocaleString("de-DE")}</p>
            <p className="text-xs font-semibold italic">{Number(pawn.Provision).toLocaleString("de-DE")}</p>
            <p className={`text-xs font-semibold ${pawn["Days Left"] < 0 ? "text-red-500" : "text-green"}`}>{pawn["Days Left"]}</p>
            <p className="text-xs">{pawn["Valid Until"].substring(0, 10)}</p>
            {loading ? (
                <Loading width={30} height={30} />
            ) : (
                <>
                    <div className="flex gap-2">
                        <X size={16} className="cursor-pointer" onClick={() => setModalClosePawn(true)} />
                        <RotateCcw size={16} color="var(--cta-color)" className="cursor-pointer" onClick={() => setModalContinuePawn(true)} />
                        <Euro size={16} color="var(--green)" className="cursor-pointer" onClick={() => movePawnToSale(pawn.Id, pawn.Category)} />
                    </div>
                    <Ellipsis size={20} color="#888" className="cursor-pointer" onClick={() => fetchPawn(pawn["Client Id"], pawn.Category, pawn.Id)} />
                </>
            )}

            {modalClosePawn && (
                <ModalActions
                    pawnAction="close"
                    action={closePawn}
                    id={pawn.Id}
                    category={pawn.Category}
                    successMsg="Успешно затворен залог!"
                    closeModal={() => setModalClosePawn(false)}
                    priceBought={Number(pawn["Item Cost"])}
                    provision={Number(pawn.Provision)}
                    dailyProvision={Math.abs(Math.round(Number(pawn.Provision)) / Number(pawn["Total Days"]))}
                    suggestedPrice={Number(pawn["Item Cost"]) + Number(pawn.Provision)}
                    daysLeft={Number(pawn["Days Left"])}
                    title="Со кој износ е затворен залогот?"
                    loading={loading}
                />
            )}

            {modalContinuePawn && (
                <ModalActions
                    pawnAction="continue"
                    action={continuePawn}
                    id={pawn.Id}
                    category={pawn.Category}
                    successMsg="Успешно продолжен залог!"
                    closeModal={() => setModalContinuePawn(false)}
                    priceBought={Number(pawn["Item Cost"])}
                    provision={Number(pawn.Provision)}
                    dailyProvision={Math.abs(Math.round(Number(pawn.Provision)) / Number(pawn["Total Days"]))}
                    suggestedPrice={Number(pawn.Provision)}
                    daysLeft={Number(pawn["Days Left"])}
                    title="Со кој износ е продолжен залогот?"
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

            {modalReadMore && pawnInfo && (
                <ModalReadMorePawn
                    category={pawn.Category}
                    pawnInfo={pawnInfo}
                    closeModal={(e: React.MouseEvent) => { e.stopPropagation(); setModalReadMore(false); }}
                    closePawn={() => setModalClosePawn(true)}
                    continuePawn={() => setModalContinuePawn(true)}
                    movePawnToSale={() => movePawnToSale(pawn.Id, pawn.Category)}
                    oldPawn={pawn}
                    refreshCashReg={refreshCashReg}
                />
            )}
        </div>
    );
}
