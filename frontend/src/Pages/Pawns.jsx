import {useEffect, useState} from "react";
import axios from "axios";
import styled from "styled-components";
import Pawn from "../Components/Pawn.jsx";
import Nav from "../Components/Nav.jsx";
import { Plus, X, Euro, RotateCcw, ChevronUp, ChevronDown, Minus} from "lucide-react";

export default function Pawns() {
    const [allPawns, setAllPawns] = useState([]);
    const [orderBy, setOrderBy] = useState("Valid Until");
    const [orderDirectionArr, setOrderDirectionArr] = useState([0,0,0,0,0,0,1]); // -1=desc 0=normal 1=asc
    const [orderDirection, setOrderDirection] = useState("ASC");

    const fetchPawns = (order, direction) => {
        axios.get(`http://localhost:3000?orderBy=${order}&orderDirection=${direction}`)
            .then(res => setAllPawns(res.data))
            .catch(error => console.error('Error fetching all pawns:', error));
    }

    useEffect(() => {
        if (orderDirectionArr.includes(1)) {
            setOrderDirection("ASC");
        } else if (orderDirectionArr.includes(-1)) {
            setOrderDirection("DESC");
        } else {
            setOrderDirection("ASC");
        }
    }, [orderDirectionArr]);


    useEffect(() => {
        fetchPawns(orderBy, orderDirection);
    }, [orderBy, orderDirection]);


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
                        <Text onClick={() => {
                            setOrderDirectionArr(prev => {
                                const newDirection = [...prev];
                                newDirection[0] = newDirection[0] === 0 ? 1 : newDirection[0] === 1 ? -1 : 0;
                                const res = [0,0,0,0,0,0,0]
                                res[0] = newDirection[0];
                                return res;
                            });
                            setOrderBy("Client Id")
                        }}>
                            Id {orderDirectionArr[0] === 0 ? <Minus size={14} /> : orderDirectionArr[0] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => {
                            setOrderDirectionArr(prev => {
                                const newDirection = [...prev];
                                newDirection[1] = newDirection[1] === 0 ? 1 : newDirection[1] === 1 ? -1 : 0;
                                const res = [0,0,0,0,0,0,0]
                                res[1] = newDirection[1];
                                return res;
                            });
                            setOrderBy("Name")
                        }}>
                            Name {orderDirectionArr[1] === 0 ? <Minus size={14} /> : orderDirectionArr[1] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => {
                            setOrderDirectionArr(prev => {
                                const newDirection = [...prev];
                                newDirection[2] = newDirection[2] === 0 ? 1 : newDirection[2] === 1 ? -1 : 0;
                                const res = [0,0,0,0,0,0,0]
                                res[2] = newDirection[2];
                                return res;
                            });
                            setOrderBy("Category")
                        }}>
                            Category {orderDirectionArr[2] === 0 ? <Minus size={14} /> : orderDirectionArr[2] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={prev => {
                            setOrderDirectionArr(prev => {
                                const newDirection = [...prev];
                                newDirection[3] = newDirection[3] === 0 ? 1 : newDirection[3] === 1 ? -1 : 0;
                                const res = [0,0,0,0,0,0,0]
                                res[3] = newDirection[3];
                                return res;
                            });
                            setOrderBy("About")
                        }}>
                            About {orderDirectionArr[3] === 0 ? <Minus size={14} /> : orderDirectionArr[3] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={prev => {
                            setOrderDirectionArr(prev => {
                                const newDirection = [...prev];
                                newDirection[4] = newDirection[4] === 0 ? 1 : newDirection[4] === 1 ? -1 : 0;
                                const res = [0,0,0,0,0,0,0]
                                res[4] = newDirection[4];
                                return res;
                            });
                            setOrderBy("Item Cost")
                        }}>
                            Item Cost {orderDirectionArr[4] === 0 ? <Minus size={14} /> : orderDirectionArr[4] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={prev => {
                            setOrderDirectionArr(prev => {
                                const newDirection = [...prev];
                                newDirection[5] = newDirection[5] === 0 ? 1 : newDirection[5] === 1 ? -1 : 0;
                                const res = [0,0,0,0,0,0,0]
                                res[5] = newDirection[5];
                                return res;
                            });
                            setOrderBy("Provision")
                        }}>
                            Provision {orderDirectionArr[5] === 0 ? <Minus size={14} /> : orderDirectionArr[5] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={prev => {
                            setOrderDirectionArr(prev => {
                                const newDirection = [...prev];
                                newDirection[6] = newDirection[6] === 0 ? 1 : newDirection[6] === 1 ? -1 : 0;
                                const res = [0,0,0,0,0,0,0]
                                res[6] = newDirection[6];
                                return res;
                            });
                            setOrderBy("Days Left")
                        }}>
                            Days Left {orderDirectionArr[6] === 0 ? <Minus size={14} /> : orderDirectionArr[6] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text style={{cursor: "default"}}>Valid Until</Text>
                        <Text style={{cursor: "default"}}>Actions</Text>
                        <Text style={{cursor: "default"}}>More</Text>
                    </TableHeader>

                    <ScrollablePawns>
                        { allPawns.map((pawn, index) => (
                            <Pawn pawn={pawn} key={index} refresh={() => fetchPawns(orderBy, orderDirection)} isOdd={index%2 !== 0}/>
                        )) }
                    </ScrollablePawns>

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
    height: 100%;
    overflow: hidden;
`

const TableHeader = styled.div`
    display: grid;
    place-items: center;
    grid-template-columns: 2rem 1fr 1fr 2fr repeat(4, 1fr) 1.5fr .5fr;
    padding: 1rem .5rem;
    //color: #eeeeee;
    border-bottom: rgba(0, 0, 0, 0.2) 2px solid;
    //background: var(--green);
`

const Text = styled.div`
    font-weight: 600;
    font-size: .8rem;
    cursor: pointer;
    display: flex;
    align-items: center;
`

const ScrollablePawns = styled.div`
    overflow-y: scroll;
    
    &::-webkit-scrollbar {
        width: 4px;
    }
    &::-webkit-scrollbar-track {
        
    }
    &::-webkit-scrollbar-thumb {
        background: #888;
        border-radius: 8px;
        
        &:hover {
            background: #aaa;
        }
    }
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