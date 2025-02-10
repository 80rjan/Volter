import styled from "styled-components";
import navImage from "../assets/Volter zalozna kukja 3D slika.png"
import { NavLink } from "react-router-dom";

export default function Nav() {
    return (
        <NavWrapper >
            <Logo src={navImage} alt="Volter Zalozna Kukja"/>
            <StyledNavLink to="/">Pawns</StyledNavLink>
            <StyledNavLink to="/sale" >Sales</StyledNavLink>
            <StyledNavLink to="/cashRegister" >Cash Register</StyledNavLink>
            <StyledNavLink to="/monthlyReport" >Monthly Reports</StyledNavLink>
            <StyledNavLink to="/yearlyReport" >Yearly Reports</StyledNavLink>
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
    gap: 2rem;
    align-items: center;
    padding: 1rem;
    box-shadow: 2px 0 8px rgba(0,0,0,0.2);
`

const StyledNavLink = styled(NavLink)`
    width: 100%;
    text-decoration: none;
    font-size: 1.2rem;
    font-weight: 600;
    color: black;
    
    &.active {
        background: var(--cta-color);
        color: white;
        padding: 0.6rem 0;
        border-radius: .4rem;
        box-shadow: -4px 2px 6px rgba(0,0,0,0.2);
    }
`

