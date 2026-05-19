import { useEffect, useState } from "react";
import axios from "axios";
import { Sigma, CalendarClock, Plus, Minus, Percent, HandCoins, LockKeyhole, Landmark } from "lucide-react";
import ModalAdjustCashRegister from "./ModalAdjustCashRegister.tsx";
import ModalOpenCashRegister from "./ModalOpenCashRegister.tsx";
import ModalCloseCashRegister from "./ModalCloseCashRegister.tsx";
import Loading from "./Loading.tsx";
import { CashRegisterData } from "../types.ts";
import { API_BASE } from "../api/config.ts";

interface Props {
    refreshDependency?: any;
    refreshDependencyAdjustPawn?: any;
    refreshTransactions?: () => void;
}

export default function CashRegister({ refreshDependency, refreshDependencyAdjustPawn, refreshTransactions }: Props) {
    const [cashReg, setCashReg] = useState<CashRegisterData | null>(null);
    const [showModalInsert, setShowModalInsert] = useState(false);
    const [showModalRemove, setShowModalRemove] = useState(false);
    const [showModalOpen, setShowModalOpen] = useState(false);
    const [showModalClose, setShowModalClose] = useState(false);
    const [loading, setLoading] = useState(false);

    const fetchCashRegister = () => {
        setLoading(true);
        axios.get(`${API_BASE}/cash-register`)
            .then(res => setCashReg(res.data))
            .catch(() => setCashReg(null))
            .finally(() => setLoading(false));
    };

    useEffect(() => { fetchCashRegister(); }, [refreshDependency, refreshDependencyAdjustPawn]);

    const textWrapper = "flex items-center gap-2";
    const val = (bold?: boolean) => `text-[.7rem] italic${bold ? " font-bold not-italic" : ""}`;

    const onRefresh = () => {
        fetchCashRegister();
        if (refreshTransactions) refreshTransactions();
    };

    if (loading) {
        return (
            <div className="flex justify-center items-center px-4 py-2 rounded-t-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] bg-white border-2 border-black/40 border-b-0">
                <Loading width={30} height={30} />
            </div>
        );
    }

    if (!cashReg) {
        return (
            <>
                <div className="flex justify-between items-center px-4 py-2 rounded-t-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] bg-white border-2 border-black/40 border-b-0">
                    <p className="text-[.7rem] italic text-[#888]">Нема отворена каса</p>
                    <button
                        onClick={() => setShowModalOpen(true)}
                        className="flex items-center gap-1.5 px-3 py-1 rounded bg-green text-white text-xs shadow-[0_0_4px_rgba(0,0,0,0.2)] hover:scale-105 transition-all duration-300"
                    >
                        <Landmark size={14} /> Отвори Каса
                    </button>
                </div>
                {showModalOpen && <ModalOpenCashRegister closeModal={() => setShowModalOpen(false)} refresh={onRefresh} />}
            </>
        );
    }

    return (
        <>
            <div className="flex justify-between items-center px-4 py-2 rounded-t-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] bg-white border-2 border-black/40 border-b-0">
                <div className={textWrapper}>
                    <Sigma size={24} />
                    <p className={val(true)}>{Number(cashReg.currentBalance).toLocaleString("de-DE")}</p>
                </div>
                <div className={textWrapper}>
                    <Percent size={24} />
                    <p className={val(true)}>{Number(cashReg.expectedPawnInterest).toLocaleString("de-DE")}</p>
                </div>
                {/*<div className={textWrapper}>*/}
                {/*    <HandCoins size={24} />*/}
                {/*    <p className={val(true)}>{Number(cashReg.openingBalance).toLocaleString("de-DE")}</p>*/}
                {/*</div>*/}
                <div className={textWrapper}>
                    <CalendarClock size={24} />
                    <p className={val(true)}>{cashReg.updatedAt?.substring(0, 10)}</p>
                    <p className={val()}>/</p>
                    <p className={val()}>{cashReg.updatedAt?.substring(11, 16)}</p>
                </div>
                <div className="flex items-center gap-2 svg-hover">
                    <Minus size={24} color="var(--dark-red)" className="cursor-pointer" onClick={() => setShowModalRemove(true)} />
                    <Plus size={24} color="var(--green)" className="cursor-pointer" onClick={() => setShowModalInsert(true)} />
                    <LockKeyhole size={22} color="#888" className="cursor-pointer" onClick={() => setShowModalClose(true)} />
                </div>
            </div>

            {showModalInsert && <ModalAdjustCashRegister closeModal={() => setShowModalInsert(false)} isInsert={true} refresh={onRefresh} />}
            {showModalRemove && <ModalAdjustCashRegister closeModal={() => setShowModalRemove(false)} isInsert={false} refresh={onRefresh} />}
            {showModalClose && <ModalCloseCashRegister closeModal={() => setShowModalClose(false)} refresh={onRefresh} />}
        </>
    );
}
