import axios from "axios";
import { useEffect, useState } from "react";
import { Euro, Ban, Ellipsis } from "lucide-react";
import ModalReadMoreSale from "./ModalReadMoreSale.tsx";
import ModalActions from "../../shared/components/ModalActions.tsx";
import ModalShowMessagePawn from "../pawns/ModalShowMessagePawn.tsx";
import Loading from "../../shared/components/Loading.tsx";
import { SaleRow, SaleDetailed } from "./types.ts";
import { API_BASE } from "../../shared/api/config.ts";
import { useAuth } from "../../GlobalContext.tsx";
import { resolveActiveSessionId } from "../../shared/utils/activeSession.ts";

interface Props {
    sale: SaleRow;
    refresh: () => void;
    isOdd: boolean;
}

const STATUS_MK: Record<string, { label: string; cls: string }> = {
    AVAILABLE: { label: "Достапен", cls: "text-green" },
    SOLD: { label: "Продаден", cls: "text-[#555]" },
    CANCELED: { label: "Откажан", cls: "text-red-500" },
};

const num = (n: number | null) => (n == null ? "—" : Number(n).toLocaleString("de-DE"));

export default function Sale({ sale, refresh, isOdd }: Props) {
    const { can } = useAuth();
    const [modalSell, setModalSell] = useState(false);
    const [modalReadMore, setModalReadMore] = useState(false);
    const [modalSuccess, setModalSuccess] = useState(false);
    const [successMsg, setSuccessMsg] = useState("");
    const [detailed, setDetailed] = useState<SaleDetailed | null>(null);
    const [loading, setLoading] = useState(false);

    const getOpenSessionId = (): Promise<number | null> => resolveActiveSessionId();

    const sellItem = async (id: number, _category: string, salePrice: number, _description: string) => {
        setLoading(true);
        try {
            const sessionId = await getOpenSessionId();
            if (!sessionId) { setSuccessMsg("Нема отворена каса"); setModalSell(false); setModalSuccess(true); return; }
            await axios.post(`${API_BASE}/sales/${id}/sell`, { salePrice, cashRegisterSessionId: sessionId });
            setSuccessMsg("Успешно продаден предмет");
            setModalSell(false);
            setModalSuccess(true);
        } catch (error) {
            console.error("Error selling item:", error);
        } finally { setLoading(false); }
    };

    useEffect(() => { if (detailed != null) setModalReadMore(true); }, [detailed]);

    const fetchSale = (id: number) => {
        setLoading(true);
        axios.get(`${API_BASE}/sales/${id}`)
            .then(res => setDetailed(res.data))
            .catch(error => console.error("Error fetching sale:", error))
            .finally(() => setLoading(false));
    };

    const status = STATUS_MK[sale.Status] ?? { label: sale.Status, cls: "bg-black/10" };
    const cols = "grid-cols-[1.5fr_2fr_1fr_1fr_1fr_1.1fr_1.1fr_1fr_0.5fr]";

    return (
        <div style={{ background: isOdd ? "#f0f0f0" : "#ffffff" }} className={`grid place-items-center text-center ${cols} border-b border-black/20 svg-hover`}>
            <p className="text-xs font-semibold">{sale.Customer.toUpperCase()}</p>
            <p className="text-xs">{sale.About}</p>
            <p className="text-xs">{num(sale["Item Cost"])}</p>
            <p className="text-xs">{num(sale["Sale Price"])}</p>
            <p className={`text-xs font-semibold ${sale.Profit == null ? "" : sale.Profit < 0 ? "text-red-500" : "text-green"}`}>{num(sale.Profit)}</p>
            <span className={`px-2 py-0.5 rounded text-xs font-semibold ${status.cls}`}>{status.label}</span>
            <p className="text-xs">{String(sale["Date Bought"]).substring(0, 10)}</p>
            {loading ? (
                <Loading width={28} height={28} />
            ) : (
                <>
                    {sale.Status === "AVAILABLE" ? (
                        <div className="flex gap-2">
                            {can("SALE_WRITE") && <Euro size={16} color="var(--green)" className="cursor-pointer" onClick={() => setModalSell(true)} />}
                        </div>
                    ) : (
                        <div />
                    )}
                    <Ellipsis size={20} color="#888" className="cursor-pointer" onClick={() => fetchSale(sale.Id)} />
                </>
            )}

            {modalSell && (
                <ModalActions
                    pawnAction={null}
                    action={sellItem}
                    id={sale.Id}
                    category="sale"
                    successMsg="Успешно продаден предмет!"
                    closeModal={() => setModalSell(false)}
                    priceBought={Number(sale["Item Cost"])}
                    suggestedPrice={Number(sale["Item Cost"])}
                    title="По која цена е продаден предметот?"
                    loading={loading}
                />
            )}

            {modalSuccess && (
                <ModalShowMessagePawn
                    closeModal={() => { setModalSuccess(false); refresh(); }}
                    successMsg={successMsg}
                    infoMsg=""
                    clientName={sale.Customer}
                />
            )}

            {modalReadMore && detailed && (
                <ModalReadMoreSale
                    sale={detailed}
                    closeModal={() => setModalReadMore(false)}
                    refresh={refresh}
                />
            )}
        </div>
    );
}
