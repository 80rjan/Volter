// @ts-ignore
import navImage from "../../assets/volter-zalozna-kukja-3D-slika.png";
import { NavLink, useNavigate } from "react-router-dom";
import axios from "axios";
import {
    Tag, CalendarFold, Calendar1, Handshake,
    ArrowLeftRight, BanknoteArrowDown, UserRound, LogOut,
} from "lucide-react";
import GoldPriceLive from "./GoldPriceLive.tsx";

const linkClass = ({ isActive }: { isActive: boolean }) =>
    `flex justify-center items-center gap-2 w-full min-w-max no-underline text-base font-semibold py-2 px-0 rounded transition-all duration-250 ${
        isActive
            ? 'bg-green text-white shadow-[-4px_2px_6px_rgba(0,0,0,0.2)]'
            : 'text-black hover:scale-105 hover:bg-[#eee]'
    }`;

export default function Nav() {
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem('token');
        delete axios.defaults.headers.common['Authorization'];
        navigate('/login');
    };

    return (
        <nav className="flex bg-white flex-col gap-6 items-center p-4 shadow-[2px_0_8px_rgba(0,0,0,0.2)]">
            <img src={navImage} alt="Volter Zalozna Kukja" className="w-full h-auto mb-4" />
            <NavLink to="/" className={linkClass}><Handshake size={24} /> Залози</NavLink>
            <NavLink to="/sales" className={linkClass}><Tag size={24} /> Продажби</NavLink>
            <NavLink to="/transactions" className={linkClass}><ArrowLeftRight size={24} /> Трансакции</NavLink>
            <NavLink to="/clients" className={linkClass}><UserRound size={24} /> Клиенти</NavLink>
            <NavLink to="/expenses" className={linkClass}><BanknoteArrowDown size={24} /> Расходи</NavLink>
            <NavLink to="/report" className={linkClass}><CalendarFold size={24} /> Извештај</NavLink>
            <NavLink to="/monthlyReport" className={linkClass}><Calendar1 size={24} /> Месечен Извештај</NavLink>
            <GoldPriceLive />
            <button
                onClick={handleLogout}
                className="flex justify-center items-center gap-2 w-full min-w-max text-base font-semibold py-2 px-0 rounded transition-all duration-250 text-black hover:scale-105 hover:bg-[#eee] mt-auto cursor-pointer border-none bg-transparent"
            >
                <LogOut size={24} /> Одјави се
            </button>
        </nav>
    );
}
