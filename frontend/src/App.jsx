import {useEffect, useState} from 'react'
import './App.css'
import Pawns from "./Pages/Pawns.jsx";
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import Sales from "./Pages/Sales.jsx";
import Transactions from "./Pages/Transactions.jsx";
import MonthlyReport from "./Pages/MonthlyReport.jsx";
import YearlyReport from "./Pages/YearlyReport.jsx";

const router = createBrowserRouter([
    {
        path: '/',
        element: <Pawns />
    },
    {
        path: '/sales',
        element: <Sales />
    },
    {
        path: '/transactions',
        element: <Transactions />
    },
    {
        path: '/monthlyReport',
        element: <MonthlyReport />
    },
    {
        path: '/yearlyReport',
        element: <YearlyReport />
    },
])

function App() {

    return (
        <RouterProvider router={router} />
    )
}

export default App
