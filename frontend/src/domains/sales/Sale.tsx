import axios from "axios";
import { useState } from "react";
import { Euro, Ellipsis } from "lucide-react";
import ModalReadMoreSale from "./ModalReadMoreSale.tsx";
import Loading from "../../shared/components/Loading.tsx";
import ModalActions from "../../shared/components/ModalActions.tsx";
import { SaleRow } from "./types.ts";

interface Props {
    sale: SaleRow;
    refresh: () => void;
    isOdd: boolean;
}

export default function Sale({ sale, refresh, isOdd }: Props) {
    const [modalSellItem, setModalSellItem] = useState(false);
    const [modalReadMore, setModalReadMore] = useState(false);
    const [loading, setLoading] = useState(false);

    const sellItem = (id: number, priceSold: number, description: string) => {
        setLoading(true);
        axios.put(`http://localhost:3000/sales/sellItem`, { id, priceSold, description })
            .catch(error => console.error("Error selling item:", error))
            .finally(() => { setLoading(false); setModalSellItem(false); refresh(); });
    };

    return (
        <div
            style={{ background: isOdd ? "#f0f0f0" : "#ffffff" }}
            className="grid place-items-center text-center grid-cols-[3fr_1fr_1fr_1.5fr_.5fr] border-b border-black/20 svg-hover"
        >
            <p className="text-sm">{sale.About}</p>
            <p className="text-sm font-medium italic">{Number(sale["Item Cost"]).toLocaleString("de-DE")}</p>
            <p className="text-sm">{sale["Date Bought"].substring(0, 10)}</p>
            {loading ? <Loading width={30} height={30} /> : (
                <>
                    <div className="flex gap-2">
                        <Euro size={22} color="var(--green)" className="cursor-pointer" onClick={() => setModalSellItem(true)} />
                    </div>
                    <Ellipsis size={28} color="#888" className="cursor-pointer" onClick={() => setModalReadMore(true)} />
                </>
            )}

            {modalSellItem && (
                <ModalActions
                    pawnAction={null}
                    action={sellItem}
                    id={sale.Id}
                    category="sale"
                    successMsg="Успешно продадено!"
                    closeModal={() => setModalSellItem(false)}
                    priceBought={Number(sale["Item Cost"])}
                    provision={undefined}
                    dailyProvision={undefined}
                    suggestedPrice={undefined}
                    daysLeft={undefined}
                    title="По која цена е продаден предметот?"
                    refresh={refresh}
                    loading={loading}
                />
            )}

            {modalReadMore && (
                <ModalReadMoreSale
                    sale={sale}
                    closeModal={() => setModalReadMore(false)}
                    sellItem={() => setModalSellItem(true)}
                />
            )}
        </div>
    );
}
