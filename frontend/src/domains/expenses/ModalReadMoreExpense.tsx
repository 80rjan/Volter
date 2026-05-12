import ReactDom from "react-dom";
import { X, Calendar1, House, Users, ReceiptText, HandCoins } from 'lucide-react';

interface Transaction {
    Description: string;
    'Money Given': number;
    Date: string;
    [key: string]: any;
}

interface ExpenseInfo {
    expense: { Month: number; Year: number; Rent: number; Salaries: number; Bills: number; Other: number };
    transactions: Transaction[];
}

interface Props {
    expenseInfo: ExpenseInfo;
    closeModal: () => void;
}

function extractExpenses(transactions: Transaction[]) {
    const types = ['Плати', 'Кирија', 'Сметки', 'Останато'];
    return transactions.flatMap(tx => {
        const descriptionMatch = tx.Description.match(/Опис:\s*(.*)/);
        const description = descriptionMatch ? descriptionMatch[1].trim() : '';
        return types
            .filter(type => tx.Description.includes(type))
            .map(type => ({
                date: new Date(tx.Date).toISOString().split('T')[0],
                typeOfExpense: type,
                expenseMoney: tx["Money Given"],
                description,
            }));
    });
}

export default function ModalReadMoreExpense({ expenseInfo, closeModal }: Props) {
    const { expense, transactions } = expenseInfo;
    const expenses = extractExpenses(transactions);

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" />
            <div className="flex flex-col items-center fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-4 pb-8 px-8 rounded-lg min-w-fit max-w-[90%]">
                <X size={32} className="ml-auto close-x-btn" onClick={closeModal} />
                <div className="flex items-center gap-2 text-3xl font-semibold mb-8 w-full">
                    <Calendar1 size={44} />
                    Расходи за {expense.Month}-{expense.Year}
                </div>
                <div className="flex items-center gap-16">
                    <div className="flex flex-col justify-evenly items-start gap-8">
                        {[
                            { icon: <House color="var(--grey)" size={36} />, val: expense.Rent, label: 'Ќирија' },
                            { icon: <Users color="var(--grey)" size={36} />, val: expense.Salaries, label: 'Плати' },
                            { icon: <ReceiptText color="var(--grey)" size={36} />, val: expense.Bills, label: 'Сметки' },
                            { icon: <HandCoins color="var(--grey)" size={36} />, val: expense.Other, label: 'Останато' },
                        ].map(({ icon, val, label }) => (
                            <div key={label} className="flex gap-4 leading-none min-w-max">
                                {icon}
                                <div className="flex flex-col gap-1">
                                    <h1 className="text-2xl">{Number(val).toLocaleString("de-DE")}</h1>
                                    <p className="font-normal text-base">{label}</p>
                                </div>
                            </div>
                        ))}
                    </div>
                    <div className="w-px h-80 bg-black/20 rounded-full" />
                    <div className="flex flex-col bg-white rounded-lg shadow-[0_0_8px_rgba(0,0,0,0.2)] overflow-hidden grow min-h-0 w-[800px]">
                        <div className="grid place-items-center grid-cols-[1fr_1fr_1fr_2fr] gap-x-16 px-2 py-4 border-b-2 border-black/20 text-[#eee] bg-[#666]">
                            {['Датум', 'Тип на разход', 'Износ', 'Опис'].map(h => (
                                <div key={h} className="text-xs font-semibold flex items-center">{h}</div>
                            ))}
                        </div>
                        <div className="overflow-y-auto flex flex-col max-h-[280px] scrollbar-thin">
                            {expenses.map((exp, i) => (
                                <div
                                    key={i}
                                    style={{ background: i % 2 === 1 ? '#f0f0f0' : '#ffffff' }}
                                    className="grid place-items-center grid-cols-[1fr_1fr_1fr_2fr] gap-x-16 px-2 py-2"
                                >
                                    <span className="text-sm">{exp.date}</span>
                                    <span className="text-sm">{exp.typeOfExpense}</span>
                                    <span className="text-sm">{Number(exp.expenseMoney).toLocaleString("de-DE")}</span>
                                    <span className="text-sm">{exp.description || "/"}</span>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </div>
        </>,
        document.getElementById("portal")!
    );
}
