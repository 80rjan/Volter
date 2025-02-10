import {useEffect, useState} from "react";
import axios from "axios";
import styled from "styled-components";
import Pawn from "../Components/Pawn.jsx";
import Nav from "../Components/Nav.jsx";
import {Plus} from "lucide-react";

export default function Pawns() {
    const [allPawns, setAllPawns] = useState([]);

    const fetchPawns = () => {
        axios.get('http://localhost:3000')
            .then(res => setAllPawns(res.data))
            .catch(error => console.error('Error fetching all pawns:', error));
    }

    useEffect(() => {
        fetchPawns();
    }, []);

    return (
        <PawnsPage >
            <Nav />
            <Container >

                <HeaderWrapper >
                    <h1>Pawns</h1>
                    <ButtonAddNewPawn >
                        <Plus size={20} color="white" strokeWidth={3} />
                        Add New Pawn
                    </ButtonAddNewPawn>
                </HeaderWrapper>

                <FilterWrapper >
                    <StyledInput placeholder="Search by name"/>
                    <StyledInput placeholder="Search by embg"/>
                    <StyledInput placeholder="Search by telephone"/>
                </FilterWrapper>

                <PawnsWrapper>
                    { allPawns.map((pawn, index) => (
                            <Pawn pawn={pawn} key={index} refresh={fetchPawns}/>
                    )) }
                </PawnsWrapper>

            </Container>
        </PawnsPage>
    )
}

const PawnsPage = styled.div`
    height: 100%;
    display: grid;
    grid-template-columns: max(15%, 250px) auto;
`

const Container = styled.div`
    display: flex;
    flex-direction: column;
    padding: 2rem 2rem;
    gap: 2rem;
`

const HeaderWrapper = styled.div`
    display: flex;
    justify-content: space-between;
    width: 100%;
`

const ButtonAddNewPawn = styled.button`
    background: var(--cta-color);
    color: white;
    border-radius: .5rem;
    display: flex;
    align-items: center;
    padding: .5rem 2rem;
    font-size: 1.1rem;
    box-shadow: 4px 2px 6px rgba(0,0,0,0.2);
`

const FilterWrapper = styled.div`
    display: flex;
    justify-content: space-between;
    width: 100%;
`

const StyledInput = styled.input`
    border: none;
    border-radius: .2rem;
    font-size: 1rem;
    width: 25%;
    padding: .5rem;
    box-shadow: 0 0 8px rgba(0,0,0,0.2);
`

const PawnsWrapper = styled.div`
    display: flex;
    flex-direction: column;
    background: white;
    padding: 1rem;
    border-radius: .5rem;
    box-shadow: 0 0 8px rgba(0,0,0,0.2);
`