import { HashRouter, Routes, Route, Navigate } from "react-router-dom";
import Pawns from "./domains/pawns/Pawns.tsx";
import Sales from "./domains/sales/Sales.tsx";
import Transactions from "./domains/transactions/Transactions.tsx";
import MonthlyReport from "./domains/reports/MonthlyReport.tsx";
import YearlyReport from "./domains/reports/YearlyReport.tsx";
import PeriodReport from "./domains/reports/PeriodReport.tsx";
import Expenses from "./domains/expenses/Expenses.tsx";
import Clients from "./domains/clients/Clients.tsx";
import Login from "./domains/auth/Login.tsx";

function ProtectedRoute({ children }: { children: React.ReactNode }) {
    if (!localStorage.getItem('token')) return <Navigate to="/login" replace />;
    return <>{children}</>;
}

export default function App() {
    return (
        <HashRouter>
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route path="/" element={<ProtectedRoute><Pawns /></ProtectedRoute>} />
                <Route path="/sales" element={<ProtectedRoute><Sales /></ProtectedRoute>} />
                <Route path="/transactions" element={<ProtectedRoute><Transactions /></ProtectedRoute>} />
                <Route path="/clients" element={<ProtectedRoute><Clients /></ProtectedRoute>} />
                <Route path="/expenses" element={<ProtectedRoute><Expenses /></ProtectedRoute>} />
                <Route path="/report" element={<ProtectedRoute><PeriodReport /></ProtectedRoute>} />
                <Route path="/monthlyReport" element={<ProtectedRoute><MonthlyReport /></ProtectedRoute>} />
                <Route path="/yearlyReport" element={<ProtectedRoute><YearlyReport /></ProtectedRoute>} />
            </Routes>
        </HashRouter>
    );
}
