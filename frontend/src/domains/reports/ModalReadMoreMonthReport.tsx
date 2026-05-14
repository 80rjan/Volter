import ReactDom from "react-dom";
import { X, Calendar1, BanknoteArrowUp, BanknoteX, Banknote, Landmark, Sigma, Vault } from 'lucide-react';
import { MonthlyReportRow } from "./types.ts";

interface Props {
    report: MonthlyReportRow;
    closeModal: () => void;
}

export default function ModalReadMoreMonthReport({ report, closeModal }: Props) {
    const item = (icon: React.ReactNode, val: number, label: string) => (
        <div className="flex gap-4 leading-none min-w-max">
            {icon}
            <div className="flex flex-col gap-1">
                <h1 className="text-2xl">{Number(val).toLocaleString("de-DE")}</h1>
                <p className="font-normal text-base">{label}</p>
            </div>
        </div>
    );

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" />
            <div className="flex flex-col items-center fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-4 pb-8 px-8 rounded-lg min-w-fit max-w-[90%]">
                <X size={40} className="ml-auto close-x-btn" onClick={closeModal} />
                <div className="flex items-center gap-2 text-3xl font-semibold mb-8 w-full">
                    <Calendar1 size={44} />
                    Месечен Извештај за {report.month}-{report.year}
                </div>
                <div className="flex flex-col gap-6">
                    <div className="flex gap-12">
                        {item(<Vault color="var(--grey)" size={36} />, report.turnover, 'Промет')}
                        {item(<Landmark color="var(--grey)" size={36} />, report.cashOut, 'Исплати')}
                        {item(<Sigma color="var(--grey)" size={36} />, report.revenue, 'Приход')}
                    </div>
                    <div className="flex gap-12">
                        {item(<BanknoteArrowUp color="var(--grey)" size={36} />, report.grossProfit, 'Бруто Профит')}
                        {item(<BanknoteX color="var(--dark-red, red)" size={36} />, report.expenses, 'Расходи')}
                        {item(<Banknote color="var(--green)" size={36} />, report.netProfit, 'Нето Профит')}
                    </div>
                </div>
            </div>
        </>,
        document.getElementById("portal")!
    );
}
