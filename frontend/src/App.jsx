import {useEffect, useState} from 'react'
import './App.css'
import Pawns from "./Pages/Pawns.jsx";
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import Sales from "./Pages/Sales.jsx";
import Transactions from "./Pages/Transactions.jsx";

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
        element: <h1>Monthly Report</h1>
    },
    {
        path: '/yearlyReport',
        element: <h1>Yearly Report</h1>
    },
])

function App() {

    return (
        <RouterProvider router={router} />
    )
}

export default App
