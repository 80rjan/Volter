import {useEffect, useState} from 'react'
import './App.css'
import Pawns from "./pages/Pawns.jsx";

// import {createBrowserRouter, RouterProvider} from "react-router-dom";
import { HashRouter, Routes, Route } from "react-router-dom";

import Sales from "./pages/Sales.jsx";
import Transactions from "./pages/Transactions.jsx";
import MonthlyReport from "./pages/MonthlyReport.jsx";
import YearlyReport from "./pages/YearlyReport.jsx";
import DailyReport from "./pages/DailyReport.jsx";
import LoanAgreementDocument from "./documents/LoanAgreementDocument.jsx";
import Expenses from "./pages/Expenses.jsx";
import Clients from "./pages/Clients.jsx";

function App() {
    return (
        <HashRouter>
            <Routes>
                <Route path="/" element={<Pawns />} />
                <Route path="/sales" element={<Sales />} />
                <Route path="/transactions" element={<Transactions />} />
                <Route path="/clients" element={<Clients />} />
                <Route path="/expenses" element={<Expenses />} />
                <Route path="/dailyReport" element={<DailyReport />} />
                <Route path="/monthlyReport" element={<MonthlyReport />} />
                <Route path="/yearlyReport" element={<YearlyReport />} />
                <Route path="/document" element={<LoanAgreementDocument />} />
            </Routes>
        </HashRouter>
    );
}

export default App
