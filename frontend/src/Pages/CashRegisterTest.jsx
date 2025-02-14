import {useEffect, useState} from "react";
import axios from "axios";
import styled from "styled-components";
import Pawn from "../Components/Pawn.jsx";
import Nav from "../Components/Nav.jsx";
import {Plus, X, Euro, RotateCcw, ChevronUp, ChevronDown, Minus, Ellipsis} from "lucide-react";
import ModalAddNewPawn from "../Components/ModalAddNewPawn.jsx";

export default function CashRegisterTest() {
    const [cashReg, setCashReg] = useState({});
    const [modalAddNewPawn, setModalAddNewPawn] = useState(false);
    const [refresh, setRefresh] = useState(false);

    const fetchCashRegister = () => {
        axios.get(`http://localhost:3000/cashRegister`)
            .then(res => {
                setCashReg(res.data.cashReg)
                console.log(res.data.cashReg)
            })
            .catch(error => console.error('Error fetching cash register', error));
    }

    useEffect(() => {
        fetchCashRegister();
    }, []);


    return (
        <RegisterPage >
            <Nav />
            <Container >

                <HeaderWrapper >
                    <h1>Cash Register</h1>
                    <Button
                        onClick={() => setModalAddNewPawn(true)}
                    >
                        <Plus size={22} color="white" strokeWidth={3} />
                        Add Money
                    </Button>
                    <Button
                        onClick={() => setModalAddNewPawn(true)}
                    >
                        <Plus size={22} color="white" strokeWidth={3} />
                        Remove Money
                    </Button>

                    {modalAddNewPawn && <ModalAddNewPawn
                        closeModal={() => setModalAddNewPawn(false)}
                    />}
                </HeaderWrapper>

                <RegisterWrapper>
                    <TableHeader >
                        <Text>
                            Number of active pawns
                        </Text>
                        <Text>
                            Money in active pawns
                        </Text>
                        <Text>
                            Number of active sales
                        </Text>
                        <Text>
                            Money in active sales
                        </Text>
                        <Text>
                            Register money
                        </Text>
                        <Text>
                            Last updated
                        </Text>
                    </TableHeader>

                    <TableRow>
                        <Text>{Number(cashReg.num_pawns).toLocaleString("de-DE")}</Text>
                        <Text>{Number(cashReg.money_pawns).toLocaleString("de-DE")}</Text>
                        <Text>{Number(cashReg.num_sale_items).toLocaleString("de-DE")}</Text>
                        <Text>{Number(cashReg.money_sale_items).toLocaleString("de-DE")}</Text>
                        <Text>{Number(cashReg.register_money).toLocaleString("de-DE")}</Text>
                        <Text>{cashReg.last_updated.substring(0, 10) + " / " + cashReg.last_updated.substring(11, 19)}</Text>
                    </TableRow>

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
                </RegisterWrapper>

            </Container>
        </RegisterPage>
    )
}

const RegisterPage = styled.div`
    height: 100vh;
    display: grid;
    grid-template-columns: max(10%, 220px) auto;
`

const Container = styled.div`
    display: flex;
    flex-direction: column;
    padding: 2rem 2rem;
    gap: 1rem;
    flex-grow: 1;
    overflow: hidden;
`

const HeaderWrapper = styled.div`
    display: flex;
    justify-content: space-between;
    width: 100%;
`

const Button = styled.button`
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

const RegisterWrapper = styled.div`
    display: flex;
    flex-direction: column;
    background: white;
    border-radius: .5rem;
    box-shadow: 0 0 8px rgba(0,0,0,0.2);
    overflow: hidden;
    flex-grow: 1;
    min-height: 0;
`

const TableHeader = styled.div`
    display: grid;
    place-items: center;
    grid-template-columns: repeat(6, 1fr);
    padding: 1rem .5rem;
    //color: #eeeeee;
    border-bottom: rgba(0, 0, 0, 0.2) 2px solid;
    //background: var(--green);
`

const Text = styled.div`
    font-weight: 600;
    font-size: .8rem;
    display: flex;
    align-items: center;
`

const TableRow = styled.div`
    display: grid;
    place-items: center;
    grid-template-columns: repeat(6, 1fr);
    padding: .5rem;
    border-bottom: rgba(0,0,0,0.2) 2px solid;
`

const TableFooter = styled.div`
    display: flex;
    justify-content: space-between;
    padding: 1rem;
    color: #444;
    font-weight: 400;
    margin-top: auto;
    box-shadow: 0 -2px 6px rgba(0,0,0,0.2);
    //background: #ccc;
    
    div {
        display: flex;
        gap: .4rem
    }
    span {
        font-size: .8rem;
    }
`