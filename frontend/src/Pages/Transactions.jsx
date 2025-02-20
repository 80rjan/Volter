import {useEffect, useRef, useState} from "react";
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
    const orderDirectionArr = useRef([0,0,0,0,0,0, 0,-1]); // -1=desc 0=normal 1=asc
    const [orderDirection, setOrderDirection] = useState("DESC");
    const [searchByName, setSearchByName] = useState("");
    const [searchByEmbg, setSearchByEmbg] = useState("");
    const [searchByDate, setSearchByDate] = useState("");
    const [refresh, setRefresh] = useState(false);
    const offset = useRef(0);
    const limit = 15;
    const [isLastPage, setIsLastPage] = useState(false);
    const prevTransactions = useRef([]);
    const scrollableTransactionsRef = useRef(null);

    const fetchTransactions = (limit, offset, order, direction, searchByName, searchByEmbg, searchByDate) => {
        axios.get(`http://localhost:3000/transactions?limit=${limit}&offset=${offset}&orderBy=${order}&orderDirection=${direction}&searchByName=${searchByName}&searchByEmbg=${searchByEmbg}&searchByDate=${searchByDate}`)
            .then(res => {
                if (JSON.stringify(prevTransactions.current) !== JSON.stringify(res.data)) {
                    setAllTransactions(prev => [...prev, ...res.data]);
                    prevTransactions.current = [...prevTransactions.current, ...res.data];
                    setIsLastPage(res.data.length < limit);
                }
            })
            .catch(error => console.error('Error fetching all pawns:', error));
    }

    useEffect(() => {
        const handleScroll = () => {
            const scrollDiv = scrollableTransactionsRef.current;
            const scrollHeight = scrollDiv.scrollHeight; // Total content height
            const scrollTop = scrollDiv.scrollTop; // Current scroll position
            const clientHeight = scrollDiv.clientHeight; // Visible height of the div

            // Check if the scrollbar is 30% up from the bottom
            if (scrollHeight - scrollTop - clientHeight <= scrollHeight * 0.3 && !isLastPage) {
                offset.current += limit; // Increase offset for the next fetch
                fetchTransactions(limit, offset.current, orderBy, orderDirection, searchByName, searchByEmbg, searchByDate);
                console.log(offset)
            }
        };

        const scrollableDiv = scrollableTransactionsRef.current;
        scrollableDiv.addEventListener("scroll", handleScroll);

        return () => {
            scrollableDiv.removeEventListener("scroll", handleScroll);
        };
    }, [isLastPage, orderBy, orderDirection, searchByName, searchByEmbg, searchByDate])

    useEffect(() => {
        setAllTransactions([]);
        prevTransactions.current = [];
        offset.current = 0;
        fetchTransactions(limit, offset.current, orderBy, orderDirection, searchByName, searchByEmbg, searchByDate);
    }, [orderBy, orderDirection, searchByName, searchByEmbg, searchByDate])


    const handleOrder = (orderBy, index) => {
        const oldDirection = [...orderDirectionArr.current];
        const newDirection = new Array(oldDirection.length).fill(0);
        newDirection[index] = oldDirection[index] === 0 ? 1 : oldDirection[index] === 1 ? -1 : 1;
        orderDirectionArr.current = newDirection;
        //0 -> 1 -> -1 -> 1
        setOrderDirection(newDirection.includes(-1) ? "DESC" : "ASC");
        setOrderBy(orderBy);
    }

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
                        <Text onClick={() => handleOrder("Client Id", 0)}>
                            Id {orderDirectionArr.current[0] === 0 ? <Minus size={14} /> : orderDirectionArr.current[0] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => handleOrder("Name", 1)}>
                            Name {orderDirectionArr.current[1] === 0 ? <Minus size={14} /> : orderDirectionArr.current[1] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text style={{cursor: "default"}}>
                            Embg
                        </Text>
                        <Text onClick={() => handleOrder("Category", 2)}>
                            Category {orderDirectionArr.current[2] === 0 ? <Minus size={14} /> : orderDirectionArr.current[2] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => handleOrder("Description", 3)}>
                            Description {orderDirectionArr.current[3] === 0 ? <Minus size={14} /> : orderDirectionArr.current[3] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => handleOrder("Given", 4)}>
                            Given {orderDirectionArr.current[4] === 0 ? <Minus size={14} /> : orderDirectionArr.current[4] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => handleOrder("Got", 5)}>
                            Got {orderDirectionArr.current[5] === 0 ? <Minus size={14} /> : orderDirectionArr.current[5] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => handleOrder("Profit", 6)}>
                            Profit {orderDirectionArr.current[6] === 0 ? <Minus size={14} /> : orderDirectionArr.current[6] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => handleOrder("Date", 7)}>
                            Date {orderDirectionArr.current[7] === 0 ? <Minus size={14} /> : orderDirectionArr.current[7] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                    </TableHeader>

                    <ScrollableTransactions ref={scrollableTransactionsRef}>
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

                <CashRegister refreshDependancy={refresh} refreshTransactionsPage={() => setRefresh(prev => !prev)}/>
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
    grid-template-columns: 2rem 1fr 1.5fr 1.5fr  repeat(4, 1.5fr) 1fr;
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
    grid-template-columns: 2rem 1fr 1.5fr 1.5fr  repeat(4, 1.5fr) 1fr;
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