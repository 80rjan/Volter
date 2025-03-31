import {useEffect, useState} from 'react'
import './App.css'
import Pawns from "./Pages/Pawns.jsx";

// import {createBrowserRouter, RouterProvider} from "react-router-dom";
import { HashRouter, Routes, Route } from "react-router-dom";

import Sales from "./Pages/Sales.jsx";
import Transactions from "./Pages/Transactions.jsx";
import MonthlyReport from "./Pages/MonthlyReport.jsx";
import YearlyReport from "./Pages/YearlyReport.jsx";
import DailyReport from "./Pages/DailyReport.jsx";
import LoanAgreementDocument from "./Documents/LoanAgreementDocument.jsx";

// const router = createBrowserRouter([
//     {
//         path: '/',
//         element: <Pawns />
//     },
//     {
//         path: '/sales',
//         element: <Sales />
//     },
//     {
//         path: '/transactions',
//         element: <Transactions />
//     },
//     {
//         path: '/dailyReport',
//         element: <DailyReport />
//     },
//     {
//         path: '/monthlyReport',
//         element: <MonthlyReport />
//     },
//     {
//         path: '/yearlyReport',
//         element: <YearlyReport />
//     },
//     {
//         path: '/document',
//         element: <LoanAgreementDocument />
//     }
// ])
//
// function App() {
//
//     return (
//         <RouterProvider router={router} />
//     )
// }

function App() {
    return (
        <HashRouter>
            <Routes>
                <Route path="/" element={<Pawns />} />
                <Route path="/sales" element={<Sales />} />
                <Route path="/transactions" element={<Transactions />} />
                <Route path="/dailyReport" element={<DailyReport />} />
                <Route path="/monthlyReport" element={<MonthlyReport />} />
                <Route path="/yearlyReport" element={<YearlyReport />} />
                <Route path="/document" element={<LoanAgreementDocument />} />
            </Routes>
        </HashRouter>
    );
}

export default App
