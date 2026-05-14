import ReactDom from "react-dom";
import { X, ClipboardList } from 'lucide-react';
import { ExpenseRow } from "./types.ts";

interface Props {
    expense: ExpenseRow;
    expenseTypeLabels: Record<string, string>;
    closeModal: () => void;
}

export default function ModalReadMoreExpense({ expense, expenseTypeLabels, closeModal }: Props) {
    const detailClass = "flex flex-col gap-0 font-medium";
    const labelClass = "font-normal text-[#666] -ml-1";

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" />
            <div className="flex flex-col items-center fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-4 pb-8 px-8 rounded-lg min-w-fit max-w-[90%]">
                <X size={32} className="ml-auto close-x-btn" onClick={closeModal} />
                <div className="flex items-center gap-2 text-2xl font-semibold mb-6">
                    <ClipboardList size={32} />
                    Расход
                </div>
                <div className="grid grid-cols-2 gap-4 gap-x-12">
                    <span className={detailClass}>
                        <p className={labelClass}>Шифра:</p>
                        <p>{expense.id}</p>
                    </span>
                    <span className={detailClass}>
                        <p className={labelClass}>Тип на расход:</p>
                        <p>{expenseTypeLabels[expense.expenseType] ?? expense.expenseType}</p>
                    </span>
                    <span className={detailClass}>
                        <p className={labelClass}>Износ:</p>
                        <p>{Number(expense.amount).toLocaleString("de-DE")}</p>
                    </span>
                    <span className={detailClass}>
                        <p className={labelClass}>Датум:</p>
                        <p>{expense.date}</p>
                    </span>
                    <span className="col-span-2 flex flex-col gap-0 font-medium">
                        <p className={labelClass}>Опис:</p>
                        <p>{expense.description || "/"}</p>
                    </span>
                </div>
            </div>
        </>,
        document.getElementById("portal")!
    );
}
