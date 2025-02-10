import {useEffect, useState} from 'react'
import './App.css'
import Pawns from "./Pages/Pawns.jsx";
import {createBrowserRouter, RouterProvider} from "react-router-dom";

const router = createBrowserRouter([
    {
        path: '/',
        element: <Pawns />
    },
    {
        path: '/sale',
        element: <h1>Sale</h1>
    },
    {
        path: '/cashRegister',
        element: <h1>Cash Register</h1>
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
