import {useEffect, useState} from "react";
import axios from "axios";
import styled from "styled-components";
import Pawn from "../Components/Pawn.jsx";
import Nav from "../Components/Nav.jsx";
import { Plus, X, Euro, RotateCcw} from "lucide-react";

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
                        <Plus size={22} color="white" strokeWidth={3} />
                        Add New Pawn
                    </ButtonAddNewPawn>
                </HeaderWrapper>

                <FilterWrapper >
                    <StyledInput placeholder="Search by name"/>
                    <StyledInput placeholder="Search by embg"/>
                    <StyledInput placeholder="Search by telephone"/>
                </FilterWrapper>

                <PawnsWrapper>
                    <TableHeader >
                        <Text>Id</Text>
                        <Text>Name</Text>
                        <Text>Category</Text>
                        <Text>About</Text>
                        <Text>Item Cost</Text>
                        <Text>Provision</Text>
                        <Text>Days Left</Text>
                        <Text>Valid Until</Text>
                        <Text>Actions</Text>
                        <Text>More</Text>
                    </TableHeader>
                    { allPawns.map((pawn, index) => (
                            <Pawn pawn={pawn} key={index} refresh={fetchPawns} isOdd={index%2 !== 0}/>
                    )) }
                    <TableFooter>
                        <div>
                            <X size={18} color="#000"/>
                            <span> - Close Pawn</span>
                        </div>
                        <div>
                            <RotateCcw size={18} color="var(--cta-color)" />
                            <span> - Continue Pawn</span>
                        </div>
                        <div>
                            <Euro size={18} color="var(--green)" />
                            <span> - Move To Sale</span>
                        </div>
                    </TableFooter>
                </PawnsWrapper>

            </Container>
        </PawnsPage>
    )
}

const PawnsPage = styled.div`
    height: 100%;
    display: grid;
    grid-template-columns: max(10%, 200px) auto;
`

const Container = styled.div`
    display: flex;
    flex-direction: column;
    padding: 2rem 2rem;
    gap: 1rem;
`

const HeaderWrapper = styled.div`
    display: flex;
    justify-content: space-between;
    width: 100%;
`

const ButtonAddNewPawn = styled.button`
    background: var(--cta-color);
    height: fit-content;
    color: white;
    border-radius: .4rem;
    display: flex;
    align-items: center;
    padding: .6rem 1.6rem;
    font-size: 1.2rem;
    box-shadow: 4px 2px 6px rgba(0,0,0,0.2);
    gap: .5rem;
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
    border-radius: .5rem;
    box-shadow: 0 0 8px rgba(0,0,0,0.2);
`

const TableHeader = styled.div`
    display: grid;
    place-items: center;
    grid-template-columns: 2rem 1fr 1fr 2fr repeat(4, 1fr) 1.5fr .5fr;
    padding: 1rem .5rem;
    color: #888;
    text-decoration: underline;
    border-bottom: rgba(0,0,0,0.2) 2px solid;
`

const Text = styled.p`
    font-weight: 600;
    font-size: .8rem;
`

const TableFooter = styled.div`
    display: flex;
    justify-content: space-between;
    padding: 1rem;
    color: #444;
    font-weight: 400;
    
    div {
        display: flex;
        gap: .4rem
    }
    span {
        font-size: .8rem;
    }
`