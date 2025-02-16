import {useEffect, useState} from "react";
import axios from "axios";
import styled from "styled-components";
import Pawn from "../Components/Pawn.jsx";
import Nav from "../Components/Nav.jsx";
import {Plus, X, Euro, RotateCcw, ChevronUp, ChevronDown, Minus, Ellipsis} from "lucide-react";
import ModalAddNewPawn from "../Components/ModalAddNewPawn.jsx";
import CashRegister from "./CashRegister.jsx";
import ModalShowMessagePawn from "../Components/ModalShowMessagePawn.jsx";
import ModalReadMorePawn from "../Components/ModalReadMorePawn.jsx";

export default function Transactions() {
    const [allTransactions, setAllTransactions] = useState([]);
    const [orderBy, setOrderBy] = useState("Date");
    const [orderDirectionArr, setOrderDirectionArr] = useState([0,0,0,0,0,0, 0,-1]); // -1=desc 0=normal 1=asc
    const [orderDirection, setOrderDirection] = useState("DESC");
    const [searchByName, setSearchByName] = useState("");
    const [searchByEmbg, setSearchByEmbg] = useState("");
    const [searchByDate, setSearchByDate] = useState("");
    const [refresh, setRefresh] = useState(false);

    const fetchTransactions = (order, direction, searchByName, searchByEmbg, searchByDate) => {
        axios.get(`http://localhost:3000/transactions?orderBy=${order}&orderDirection=${direction}&searchByName=${searchByName}&searchByEmbg=${searchByEmbg}&searchByDate=${searchByDate}`)
            .then(res => {
                setAllTransactions(res.data)
                console.log(res.data)
            })
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
        fetchTransactions(orderBy, orderDirection, searchByName, searchByEmbg, searchByDate);
    }, [orderBy, orderDirection, searchByName, searchByEmbg, searchByDate, refresh]);


    return (
        <TransactionsPage >
            <Nav />
            <Container >

                <HeaderWrapper >
                    <h1>Transactions</h1>
                </HeaderWrapper>

                <FilterWrapper >
                    <StyledInput placeholder="Search by name"
                                 onKeyUp={(e) => {
                                     setSearchByName(e.target.value)
                                 }}
                    />
                    <StyledInput placeholder="Search by embg"
                                 onKeyUp={(e) => {
                                     setSearchByEmbg(e.target.value)
                                 }}
                    />
                    <StyledInput placeholder="Search by date"
                                 onKeyUp={(e) => {
                                     setSearchByDate(e.target.value)
                                 }}
                    />
                </FilterWrapper>

                <TransactionsWrapper>
                    <TableHeader >
                        <Text onClick={() => {
                            setOrderDirectionArr(prev => {
                                const newDirection = [...prev];
                                newDirection[0] = newDirection[0] === 0 ? 1 : newDirection[0] === 1 ? -1 : 0;
                                const res = [0,0,0,0,0,0,0,0]
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
                                const res = [0,0,0,0,0,0,0,0]
                                res[1] = newDirection[1];
                                return res;
                            });
                            setOrderBy("Name")
                        }}>
                            Name {orderDirectionArr[1] === 0 ? <Minus size={14} /> : orderDirectionArr[1] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text style={{cursor: "default"}}>
                            Embg
                        </Text>
                        <Text onClick={() => {
                            setOrderDirectionArr(prev => {
                                const newDirection = [...prev];
                                newDirection[2] = newDirection[2] === 0 ? 1 : newDirection[2] === 1 ? -1 : 0;
                                const res = [0,0,0,0,0,0,0,0]
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
                                const res = [0,0,0,0,0,0,0,0]
                                res[3] = newDirection[3];
                                return res;
                            });
                            setOrderBy("Description")
                        }}>
                            Description {orderDirectionArr[3] === 0 ? <Minus size={14} /> : orderDirectionArr[3] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={prev => {
                            setOrderDirectionArr(prev => {
                                const newDirection = [...prev];
                                newDirection[4] = newDirection[4] === 0 ? 1 : newDirection[4] === 1 ? -1 : 0;
                                const res = [0,0,0,0,0,0,0,0]
                                res[4] = newDirection[4];
                                return res;
                            });
                            setOrderBy("Given")
                        }}>
                            Given {orderDirectionArr[4] === 0 ? <Minus size={14} /> : orderDirectionArr[4] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={prev => {
                            setOrderDirectionArr(prev => {
                                const newDirection = [...prev];
                                newDirection[5] = newDirection[5] === 0 ? 1 : newDirection[5] === 1 ? -1 : 0;
                                const res = [0,0,0,0,0,0,0,0]
                                res[5] = newDirection[5];
                                return res;
                            });
                            setOrderBy("Got")
                        }}>
                            Got {orderDirectionArr[5] === 0 ? <Minus size={14} /> : orderDirectionArr[5] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={prev => {
                            setOrderDirectionArr(prev => {
                                const newDirection = [...prev];
                                newDirection[6] = newDirection[6] === 0 ? 1 : newDirection[6] === 1 ? -1 : 0;
                                const res = [0,0,0,0,0,0,0,0]
                                res[6] = newDirection[6];
                                return res;
                            });
                            setOrderBy("Profit")
                        }}>
                            Profit {orderDirectionArr[6] === 0 ? <Minus size={14} /> : orderDirectionArr[6] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={prev => {
                            setOrderDirectionArr(prev => {
                                const newDirection = [...prev];
                                newDirection[7] = newDirection[7] === 0 ? 1 : newDirection[7] === 1 ? -1 : 0;
                                const res = [0,0,0,0,0,0,0,0]
                                res[7] = newDirection[7];
                                return res;
                            });
                            setOrderBy("Date")
                        }}>
                            Date {orderDirectionArr[7] === 0 ? <Minus size={14} /> : orderDirectionArr[7] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                    </TableHeader>

                    <ScrollableTransactions>
                        { allTransactions.map((transaction, index) => (
                            <Transaction key={index} style={index % 2 === 1 ? {background: "#f0f0f0"} : {background: "#ffffff"}}>
                                <TextTransaction>{transaction["Client Id"]}</TextTransaction>
                                <TextTransaction>{transaction.Name}</TextTransaction>
                                <TextTransaction>{transaction.Embg}</TextTransaction>
                                <TextTransaction>{transaction.Category}</TextTransaction>
                                <TextTransaction>{transaction.Description}</TextTransaction>
                                <TextTransaction className="bold">{Number(transaction.Given).toLocaleString("de-DE")}</TextTransaction>
                                <TextTransaction className="bold">{Number(transaction.Got).toLocaleString("de-DE")}</TextTransaction>
                                <TextTransaction className="bold">{Number(transaction.Profit).toLocaleString("de-DE")}</TextTransaction>
                                <TextTransaction>{transaction.Date.substring(0, 10)}</TextTransaction>
                            </Transaction>
                        )) }
                    </ScrollableTransactions>

                </TransactionsWrapper>

                <CashRegister refreshDependancy={refresh} />
            </Container>
        </TransactionsPage>
    )
}

const TransactionsPage = styled.div`
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

const TransactionsWrapper = styled.div`
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
    grid-template-columns: 2rem repeat(3, 1fr) repeat(4, 1.5fr) 1fr;
    gap: 1rem;
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

const ScrollableTransactions = styled.div`
    overflow-y: auto;
    flex-grow: 1;
    
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

const Transaction = styled.div`
    display: grid;
    place-items: center;
    grid-template-columns: 2rem repeat(3, 1fr) repeat(4, 1.5fr) 1fr;
    gap: .4rem;
    padding: .8rem;
    border-bottom: rgba(0,0,0,0.2) 2px solid;
`

const TextTransaction = styled.p`
    font-weight: 500;
    font-size: .8rem;
    
    &.bold {
        font-weight: bold;
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