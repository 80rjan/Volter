import ReactDom from "react-dom";
import { X, Calendar1, Handshake, Landmark, Sigma, BanknoteArrowUp, BanknoteX, Banknote, Vault, Laptop, Coins, Watch, Car, CircleDollarSign, Tag } from 'lucide-react';
import { MonthlyReportRow } from "./types.ts";

interface Props {
    report: MonthlyReportRow;
    closeModal: () => void;
}

export default function ModalReadMoreMonthReport({ report, closeModal }: Props) {
    const reportItem = (icon: React.ReactNode, val: number | string, label: string) => (
        <div className="flex gap-4 leading-none min-w-max">
            {icon}
            <div className="flex flex-col gap-1">
                <h1 className="text-2xl">{Number(val).toLocaleString("de-DE")}</h1>
                <p className="font-normal text-base">{label}</p>
            </div>
        </div>
    );

    const detailSection = (icon: React.ReactNode, title: string, numKey: string, moneyKey: string, profitKey: string) => (
        <div className="flex flex-col min-w-max">
            <h3 className="flex items-center gap-2 mb-1 font-semibold text-base">{icon}{title}</h3>
            {[['Вкупно нови:', numKey], ['Пари добиени:', moneyKey], ['Профит остварен:', profitKey]].map(([lbl, key]) => (
                <span key={key} className="ml-4 flex flex-col font-medium gap-0">
                    <p className="font-normal text-[#666] -ml-1">{lbl}</p>
                    <p>{Number(report[key]).toLocaleString("de-DE")}</p>
                </span>
            ))}
        </div>
    );

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" />
            <div className="flex flex-col items-center fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-4 pb-8 px-8 rounded-lg min-w-fit max-w-[90%]">
                <X size={40} className="ml-auto close-x-btn" onClick={closeModal} />
                <div className="flex items-center gap-2 text-3xl font-semibold mb-8 w-full">
                    <Calendar1 size={44} />
                    Месечен Извештај за {report.Month}-{report.Year}
                </div>
                <div className="flex items-center gap-16">
                    <div className="flex flex-col justify-evenly items-start gap-8">
                        {reportItem(<Handshake color="var(--grey)" size={36} />, report["Money Given"], 'Нови Договори')}
                        {reportItem(<Landmark color="var(--grey)" size={36} />, report["Money Got"], 'Затворени договори')}
                        {reportItem(<Sigma color="var(--grey)" size={36} />, Number(report["Money Got"]) + Number(report["Gross Profit"]), 'Приход')}
                        {reportItem(<BanknoteArrowUp color="var(--grey)" size={36} />, report["Gross Profit"], 'Бруто Профит')}
                        {reportItem(<BanknoteX color="var(--grey)" size={36} />, -Math.abs(Number(report["Gross Profit"]) - Number(report["Net Profit"])), 'Расходи')}
                        {reportItem(<Banknote color="var(--grey)" size={36} />, report["Net Profit"], 'Нето Профит')}
                    </div>
                    <div className="w-px h-[500px] bg-black/20 rounded-full" />
                    <div className="grid grid-rows-2 [grid-auto-flow:column] gap-8 gap-x-16">
                        {detailSection(<Vault color="var(--grey)" size={36} />, 'Залози', 'Total Pawns', 'Money Pawns', 'Profit Pawns')}
                        {detailSection(<Laptop color="var(--blue)" size={36} />, 'Електроника', 'Num Electronics Pawns', 'Money Electronics Pawns', 'Profit Electronics Pawns')}
                        {detailSection(<Coins color="var(--gold)" size={36} />, 'Злато', 'Num Gold Pawns', 'Money Gold Pawns', 'Profit Gold Pawns')}
                        {detailSection(<Watch color="var(--gold)" size={36} />, 'Часовници', 'Num Watch Pawns', 'Money Watch Pawns', 'Profit Watch Pawns')}
                        {detailSection(<Car color="var(--dark-red)" size={36} />, 'Возила', 'Num Vehicle Pawns', 'Money Vehicle Pawns', 'Profit Vehicle Pawns')}
                        {detailSection(<CircleDollarSign color="var(--green)" size={36} />, 'Останато', 'Num Other Pawns', 'Money Other Pawns', 'Profit Other Pawns')}
                        {detailSection(<Tag color="var(--green)" size={36} />, 'Продажби', 'Total Sales', 'Money Sales', 'Profit Sales')}
                    </div>
                </div>
            </div>
        </>,
        document.getElementById("portal")!
    );
}
