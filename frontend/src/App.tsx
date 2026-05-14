import { HashRouter, Routes, Route } from "react-router-dom";
import Pawns from "./domains/pawns/Pawns.tsx";
import Sales from "./domains/sales/Sales.tsx";
import Transactions from "./domains/transactions/Transactions.tsx";
import MonthlyReport from "./domains/reports/MonthlyReport.tsx";
import YearlyReport from "./domains/reports/YearlyReport.tsx";
import PeriodReport from "./domains/reports/PeriodReport.tsx";
import Expenses from "./domains/expenses/Expenses.tsx";
import Clients from "./domains/clients/Clients.tsx";
import Login from "./domains/auth/Login.tsx";

export default function App() {
    return (
        <HashRouter>
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route path="/" element={<Pawns />} />
                <Route path="/sales" element={<Sales />} />
                <Route path="/transactions" element={<Transactions />} />
                <Route path="/clients" element={<Clients />} />
                <Route path="/expenses" element={<Expenses />} />
                <Route path="/report" element={<PeriodReport />} />
                <Route path="/monthlyReport" element={<MonthlyReport />} />
                <Route path="/yearlyReport" element={<YearlyReport />} />
            </Routes>
        </HashRouter>
    );
}
