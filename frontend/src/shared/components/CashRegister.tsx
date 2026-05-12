import { useEffect, useState } from "react";
import axios from "axios";
import { Handshake, Tag, Sigma, CalendarClock, Plus, Minus, Coins, Percent, HandCoins } from "lucide-react";
import ModalAdjustCashRegister from "./ModalAdjustCashRegister.tsx";
import Loading from "./Loading.tsx";
import { CashRegisterData } from "../types.ts";

interface Props {
    refreshDependency?: any;
    refreshDependencyAdjustPawn?: any;
    refreshTransactions?: () => void;
}

export default function CashRegister({ refreshDependency, refreshDependencyAdjustPawn, refreshTransactions }: Props) {
    const [cashReg, setCashReg] = useState<Partial<CashRegisterData> & { profit?: number }>({});
    const [showModalInsert, setShowModalInsert] = useState(false);
    const [showModalRemove, setShowModalRemove] = useState(false);
    const [loading, setLoading] = useState(false);

    const fetchCashRegister = () => {
        setLoading(true);
        axios.get(`http://localhost:3000/cashRegister`)
            .then(res => setCashReg({ ...res.data.cashReg, profit: res.data.profit }))
            .catch(error => console.error("Error fetching cash register", error))
            .finally(() => setLoading(false));
    };

    useEffect(() => { fetchCashRegister(); }, [refreshDependency, refreshDependencyAdjustPawn]);

    const textWrapper = "flex items-center gap-2";
    const val = (bold?: boolean) => `text-[.7rem] italic${bold ? " font-bold not-italic" : ""}`;

    const onRefresh = () => {
        fetchCashRegister();
        if (refreshTransactions) refreshTransactions();
    };

    return (
        <div className="flex justify-between items-center px-4 py-2 rounded-t-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] bg-white border-2 border-black/40 border-b-0">
            <div className={textWrapper}>
                <Handshake size={24} />
                {loading ? <Loading width={30} height={30} /> : (
                    <>
                        <p className={val(true)}>{Number(cashReg.money_pawns).toLocaleString("de-DE")}</p>
                        <p className={val()}>/</p>
                        <p className={val()}>{Number(cashReg.num_pawns).toLocaleString("de-DE")}</p>
                    </>
                )}
            </div>
            <div className={textWrapper}>
                <Percent size={24} />
                {loading ? <Loading width={30} height={30} /> : (
                    <>
                        <p className={val(true)}>{Number(cashReg.total_provision).toLocaleString("de-DE")}</p>
                        <p className={val()}>/</p>
                        <p className={val()}>{(Math.round(Number(cashReg.total_provision) / Number(cashReg.money_pawns) * 100 * 100) / 100 || 0).toLocaleString("de-DE")}</p>
                    </>
                )}
            </div>
            <div className={textWrapper}>
                <Coins size={24} />
                {loading ? <Loading width={30} height={30} /> : (
                    <p className={val(true)}>{Number(cashReg.gold_grams).toLocaleString("de-DE")} g</p>
                )}
            </div>
            <div className={textWrapper}>
                <Tag size={24} />
                {loading ? <Loading width={30} height={30} /> : (
                    <>
                        <p className={val(true)}>{Number(cashReg.money_sale_items).toLocaleString("de-DE")}</p>
                        <p className={val()}>/</p>
                        <p className={val()}>{Number(cashReg.num_sale_items).toLocaleString("de-DE")}</p>
                    </>
                )}
            </div>
            <div className={textWrapper}>
                <HandCoins size={24} />
                {loading ? <Loading width={30} height={30} /> : (
                    <p className={val(true)}>{Number(cashReg.profit).toLocaleString("de-DE")}</p>
                )}
            </div>
            <div className={textWrapper}>
                <Sigma size={24} />
                {loading ? <Loading width={30} height={30} /> : (
                    <p className={val(true)}>{Number(cashReg.register_money).toLocaleString("de-DE")}</p>
                )}
            </div>
            <div className={textWrapper}>
                <CalendarClock size={24} />
                {loading ? <Loading width={30} height={30} /> : (
                    <>
                        <p className={val(true)}>{Object.keys(cashReg).length > 0 && cashReg.last_updated?.substring(0, 10)}</p>
                        <p className={val()}>/</p>
                        <p className={val()}>{Object.keys(cashReg).length > 0 && cashReg.last_updated?.substring(11, 16)}</p>
                    </>
                )}
            </div>
            <div className="flex items-center gap-2 svg-hover">
                {loading ? <Loading width={30} height={30} /> : (
                    <>
                        <Minus size={24} color="var(--dark-red)" className="cursor-pointer" onClick={() => setShowModalRemove(true)} />
                        <Plus size={24} color="var(--green)" className="cursor-pointer" onClick={() => setShowModalInsert(true)} />
                    </>
                )}
            </div>

            {showModalInsert && <ModalAdjustCashRegister closeModal={() => setShowModalInsert(false)} isInsert={true} refresh={onRefresh} />}
            {showModalRemove && <ModalAdjustCashRegister closeModal={() => setShowModalRemove(false)} isInsert={false} refresh={onRefresh} />}
        </div>
    );
}
