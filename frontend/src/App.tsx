import { HashRouter, Routes, Route, Navigate, Outlet } from "react-router-dom";
import Pawns from "./domains/pawns/Pawns.tsx";
import Sales from "./domains/sales/Sales.tsx";
import Transactions from "./domains/transactions/Transactions.tsx";
import MonthlyReport from "./domains/reports/MonthlyReport.tsx";
import YearlyReport from "./domains/reports/YearlyReport.tsx";
import PeriodReport from "./domains/reports/PeriodReport.tsx";
import StaffReports from "./domains/reports/StaffReports.tsx";
import Notifications from "./domains/notifications/Notifications.tsx";
import Expenses from "./domains/expenses/Expenses.tsx";
import Clients from "./domains/clients/Clients.tsx";
import CashSessions from "./domains/cashsessions/CashSessions.tsx";
import Staff from "./domains/staff/Staff.tsx";
import Login from "./domains/auth/Login.tsx";
import Nav from "./shared/components/Nav.tsx";

// Persistent shell for authenticated pages: the Nav is mounted once here, above
// the <Outlet>, so it survives route changes (no collapse/re-open flicker).
function ProtectedLayout() {
    if (!localStorage.getItem('token')) return <Navigate to="/login" replace />;
    return (
        <>
            <Nav />
            <Outlet />
        </>
    );
}

export default function App() {
    return (
        <HashRouter>
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route element={<ProtectedLayout />}>
                    <Route path="/" element={<Pawns />} />
                    <Route path="/sales" element={<Sales />} />
                    <Route path="/transactions" element={<Transactions />} />
                    <Route path="/clients" element={<Clients />} />
                    <Route path="/cash-sessions" element={<CashSessions />} />
                    <Route path="/staff" element={<Staff />} />
                    <Route path="/notifications" element={<Notifications />} />
                    <Route path="/expenses" element={<Expenses />} />
                    <Route path="/report" element={<PeriodReport />} />
                    <Route path="/monthlyReport" element={<MonthlyReport />} />
                    <Route path="/staffReports" element={<StaffReports />} />
                    <Route path="/yearlyReport" element={<YearlyReport />} />
                </Route>
                <Route path="*" element={<Navigate to="/login" replace />} />
            </Routes>
        </HashRouter>
    );
}
