import styled from "styled-components";
import Nav from "../Components/Nav.jsx";
import CashRegister from "./CashRegister.jsx";

export default function YearlyReport() {

    return (
        <ReportPage >
            <Nav />
            <Container >

                <HeaderWrapper >
                    <h1>Yearly Report</h1>

                </HeaderWrapper>

                <div style={{height:'100%'}}/>

                <CashRegister refreshDependancy={true} />
            </Container>
        </ReportPage>
    )
}

const ReportPage = styled.div`
    height: 100vh;
    display: grid;
    grid-template-columns: max(10%, 220px) auto;
`

const Container = styled.div`
    display: flex;
    flex-direction: column;
    padding: 2rem 2rem 0 2rem;
    gap: 1rem;
    flex-grow: 1;
    overflow: hidden;
`

const HeaderWrapper = styled.div`
    display: flex;
    justify-content: space-between;
    width: 100%;
`

const ButtonAddNewPawn = styled.button`
    background: var(--green);
    height: fit-content;
    color: white;
    border-radius: .4rem;
    display: flex;
    align-items: center;
    padding: .6rem 1.6rem;
    font-size: 1.2rem;
    box-shadow: 4px 2px 6px rgba(0,0,0,0.2);
    gap: .5rem;
    transition: all 250ms ease-in-out;
    
    svg {
        transition: all 500ms ease-in-out;
    }
    
    &:hover {
        scale: 1.05;
        
        svg {
            transform: rotate(90deg);
        }
    }
`