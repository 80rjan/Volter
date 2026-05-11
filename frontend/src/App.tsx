import { HashRouter, Routes, Route } from "react-router-dom";
import Pawns from "./pages/Pawns.tsx";
import Sales from "./pages/Sales.tsx";
import Transactions from "./pages/Transactions.tsx";
import MonthlyReport from "./pages/MonthlyReport.tsx";
import YearlyReport from "./pages/YearlyReport.tsx";
import PeriodReport from "./pages/PeriodReport.tsx";
import Expenses from "./pages/Expenses.tsx";
import Clients from "./pages/Clients.tsx";

export default function App() {
    return (
        <HashRouter>
            <Routes>
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
