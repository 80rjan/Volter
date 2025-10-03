import styled from "styled-components";
import navImage from "../assets/volter-zalozna-kukja-3D-slika.png"
import { NavLink } from "react-router-dom";
import {
    Percent,
    Tag,
    Landmark,
    CalendarFold,
    Calendar1,
    CalendarDays,
    Handshake,
    ArrowLeftRight,
    BanknoteArrowDown,
    Users, User, UsersRound, UserRound
} from "lucide-react";
import GoldPriceLive from "./GoldPriceLive.jsx";

export default function Nav() {
    return (
        <NavWrapper >
            <Logo src={navImage} alt="Volter Zalozna Kukja"/>
            <StyledNavLink to="/"><Handshake size={24} /> Залози</StyledNavLink>
            <StyledNavLink to="/sales" ><Tag size={24} /> Продажби</StyledNavLink>
            <StyledNavLink to="/transactions" ><ArrowLeftRight size={24} /> Трансакции</StyledNavLink>
            <StyledNavLink to="/clients" ><UserRound size={24} /> Клиенти</StyledNavLink>
            <StyledNavLink to="/expenses" ><BanknoteArrowDown size={24} /> Расходи</StyledNavLink>
            <StyledNavLink to="/report" ><CalendarFold size={24} /> Извештај</StyledNavLink>
            <StyledNavLink to="/monthlyReport" ><Calendar1 size={24} /> Месечен Извештај</StyledNavLink>

            <GoldPriceLive />
        </NavWrapper>
    )
}

const Logo = styled.img`
    width: 100%;
    height: auto;
    margin-bottom: 1rem;
`

const NavWrapper = styled.nav`
    display: flex;
    background: white;
    flex-direction: column;
    gap: 1.5rem;
    align-items: center;
    padding: 1rem;
    box-shadow: 2px 0 8px rgba(0,0,0,0.2);
`

const StyledNavLink = styled(NavLink)`
    display: flex;
    justify-content: center;
    align-items: center;
    gap: .6rem;
    width: 100%;
    min-width: max-content;
    text-decoration: none;
    font-size: 1rem;
    font-weight: 600;
    color: black;
    padding: 0.6rem 0;
    border-radius: .4rem;
    transition: all 250ms ease-in-out;
    
    &:hover {
        scale: 1.05;
        background: #eee;
    }
    
    &.active {
        background: var(--green);
        color: white;
        box-shadow: -4px 2px 6px rgba(0,0,0,0.2);
    }
    &.active:hover {
        scale: 1;
    }
`

