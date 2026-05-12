import ReactDom from "react-dom";
import { Euro, X, Tag } from 'lucide-react';
import { SaleRow } from "./types.ts";

interface Props {
    sale: SaleRow;
    closeModal: () => void;
    sellItem: () => void;
}

export default function ModalReadMoreSale({ sale, closeModal, sellItem }: Props) {
    const detailClass = "flex flex-col gap-0 font-medium";
    const labelClass = "font-normal text-[#666] -ml-1";

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" />
            <div className="flex flex-col items-center fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-4 pb-8 px-8 rounded-lg min-w-fit max-w-[90%]">
                <X size={32} className="ml-auto close-x-btn" onClick={closeModal} />
                <div className="grid gap-4 mt-2">
                    <div className="flex items-center gap-2 text-2xl font-semibold">
                        <Tag size={32} />
                        Продажба
                    </div>
                    <div className="grid grid-cols-2 gap-2 gap-x-8">
                        <span className={detailClass}>
                            <p className={labelClass}>Шифра на продажба:</p>
                            <p>{sale.Id}</p>
                        </span>
                        <span className={detailClass}>
                            <p className={labelClass}>Вредност на предметот:</p>
                            <p>{Number(sale["Item Cost"]).toLocaleString("de-DE")}</p>
                        </span>
                        <span className={detailClass}>
                            <p className={labelClass}>Опис:</p>
                            <p>{sale.About}</p>
                        </span>
                        <span className={detailClass}>
                            <p className={labelClass}>Купено на:</p>
                            <p>{sale["Date Bought"].substring(0, 10)}</p>
                        </span>
                    </div>
                </div>
                <div className="flex gap-8 mt-8">
                    <button
                        className="flex items-center gap-4 rounded px-8 py-2 text-xl text-white bg-green transition-all duration-300 hover:scale-105 shadow-[0_2px_8px_rgba(0,0,0,0.3)] w-max"
                        onClick={() => { sellItem(); closeModal(); }}
                    >
                        <Euro size={32} /> Продади Предмет
                    </button>
                </div>
            </div>
        </>,
        document.getElementById("portal")!
    );
}
