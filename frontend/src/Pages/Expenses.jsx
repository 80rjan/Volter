import {useEffect, useRef, useState} from "react";
import axios from "axios";
import styled from "styled-components";
import Nav from "../Components/Nav.jsx";
import { Plus, X, Euro, RotateCcw, ChevronUp, ChevronDown, Minus} from "lucide-react";
import CashRegister from "./CashRegister.jsx";
import Loading from "../Components/Loading.jsx";
import ModalAddNewExpense from "../Components/ModalAddNewExpense.jsx";

export default function Expenses() {
    const [allExpenses, setAllExpenses] = useState([]);
    const [orderBy, setOrderBy] = useState("Year");
    const orderDirectionArr = useRef([-1,0,0,0,0,0]); // -1=desc 0=normal 1=asc
    const [orderDirection, setOrderDirection] = useState("DESC");
    const [searchByMonth, setSearchByMonth] = useState("");
    const [searchByYear, setSearchByYear] = useState("");
    const [modalAddNewExpense, setModalAddNewExpense] = useState(false);
    const [refresh, setRefresh] = useState(false);
    const offset = useRef(0);
    const limit = 20;
    const [isLastPage, setIsLastPage] = useState(false);
    const prevExpenses = useRef([]);
    const scrollableExpensesRef = useRef(null);
    const [loading, setLoading] = useState(false);
    const isFetchingRef = useRef(false);
    const [isFetching, setIsFetching] = useState(false);

    const fetchExpenses = (limit, offset, order, direction, searchByMonth, searchByYear, isLoading) => {
        if (isFetching) return;
        setIsFetching(true);
        setLoading(isLoading);
        isFetchingRef.current = true;
        axios.get(`http://localhost:3000/expenses?limit=${limit}&offset=${offset}&orderBy=${order}&orderDirection=${direction}&searchByMonth=${searchByMonth}&searchByYear=${searchByYear}`)
            .then(res => {
                if (JSON.stringify(prevExpenses.current) !== JSON.stringify(res.data)) {
                    setAllExpenses(prev => [...prev, ...res.data]);
                    prevExpenses.current = [...prevExpenses.current, ...res.data];
                    setIsLastPage(res.data.length < limit);
                }
            })
            .catch(error => {
                console.error('Error fetching all expenses:', error)
            })
            .finally(() => {
                setLoading(false)
                isFetchingRef.current = false;
                setIsFetching(false);
            })
    }

    useEffect(() => {
        const handleScroll = () => {
            const scrollDiv = scrollableExpensesRef.current;
            const scrollHeight = scrollDiv.scrollHeight; // Total content height
            const scrollTop = scrollDiv.scrollTop; // Current scroll position
            const clientHeight = scrollDiv.clientHeight; // Visible height of the div

            // Check if the scrollbar is 30% up from the bottom
            if (scrollHeight - scrollTop - clientHeight <= scrollHeight * 0.3 && !isLastPage && !isFetchingRef.current) {
                offset.current += limit; // Increase offset for the next fetch
                fetchExpenses(limit, offset.current, orderBy, orderDirection, searchByMonth, searchByYear, false);
            }
        };

        const scrollableDiv = scrollableExpensesRef.current;
        scrollableDiv.addEventListener("scroll", handleScroll);

        return () => {
            scrollableDiv.removeEventListener("scroll", handleScroll);
        };
    }, [isLastPage, refresh, orderBy, orderDirection, searchByMonth, searchByYear])

    useEffect(() => {
        setAllExpenses([]);
        prevExpenses.current = [];
        offset.current = 0;
        fetchExpenses(limit, offset.current, orderBy, orderDirection, searchByMonth, searchByYear, true);
    }, [refresh, orderBy, orderDirection, searchByMonth, searchByYear])


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
        <ExpensesPage >
            <Nav />
            <Container >

                <HeaderWrapper >
                    <h1>Расходи</h1>
                    <ButtonAddNewSale
                        onClick={() => setModalAddNewExpense(true)}
                    >
                        <Plus size={22} color="white" strokeWidth={3} />
                        Внеси Нов Расход
                    </ButtonAddNewSale>

                    {modalAddNewExpense && <ModalAddNewExpense
                        closeModal={() => setModalAddNewExpense(false)}
                        refresh={() => setRefresh(prev => !prev)}
                    />}
                </HeaderWrapper>

                <FilterWrapper >
                    <StyledInput placeholder="Пребарувај по месец (број)"
                                 type="number"
                                 onKeyUp={(e) => {
                                     setSearchByMonth(e.target.value)
                                 }}
                    />
                    <StyledInput placeholder="Пребарувај по година (број)"
                                 type="number"
                                 onKeyUp={(e) => {
                                     setSearchByYear(e.target.value)
                                 }}
                    />
                </FilterWrapper>

                <ExpensesWrapper>
                    <TableHeader >
                        <Text onClick={() => handleOrder("Year", 0)}>
                            Година {orderDirectionArr.current[0] === 0 ? <Minus size={14} /> : orderDirectionArr.current[0] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => handleOrder("Month", 1)}>
                            Месец {orderDirectionArr.current[1] === 0 ? <Minus size={14} /> : orderDirectionArr.current[1] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => handleOrder("Rent", 2)}>
                            Ќирија {orderDirectionArr.current[2] === 0 ? <Minus size={14} /> : orderDirectionArr.current[2] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => handleOrder("Salaries", 3)}>
                            Плати {orderDirectionArr.current[3] === 0 ? <Minus size={14} /> : orderDirectionArr.current[3] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => handleOrder("Bills", 4)}>
                            Сметки {orderDirectionArr.current[4] === 0 ? <Minus size={14} /> : orderDirectionArr.current[4] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => handleOrder("Other", 5)}>
                            Друго {orderDirectionArr.current[5] === 0 ? <Minus size={14} /> : orderDirectionArr.current[5] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text style={{cursor: "default"}}>Опис</Text>
                    </TableHeader>

                    <ScrollableExpenses ref={scrollableExpensesRef}>
                        { loading ?
                            <Loading /> :
                            allExpenses.map((expense, index) => (
                                <Expense key={index} style={index % 2 === 1 ? { background: "#f0f0f0" } : { background: "#ffffff" }}>
                                    <TextExpense>{expense.Year}</TextExpense>
                                    <TextExpense>{expense.Month}</TextExpense>
                                    <TextExpense>{Number(expense.Rent).toLocaleString("de-DE")}</TextExpense>
                                    <TextExpense>{Number(expense.Salaries).toLocaleString("de-DE")}</TextExpense>
                                    <TextExpense>{Number(expense.Bills).toLocaleString("de-DE")}</TextExpense>
                                    <TextExpense>{Number(expense.Other).toLocaleString("de-DE")}</TextExpense>
                                    <TextExpense>{expense.Description}</TextExpense>
                                </Expense>
                            )) }
                    </ScrollableExpenses>
                </ExpensesWrapper>

                <CashRegister refreshDependancy={refresh}/>
            </Container>
        </ExpensesPage>
    )
}

const ExpensesPage = styled.div`
    height: 100vh;
    display: grid;
    grid-template-columns: max(15%, 240px) auto;
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

const ButtonAddNewSale = styled.button`
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
    justify-content: space-evenly;
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

const ExpensesWrapper = styled.div`
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
    grid-template-columns: repeat(7, 1fr);
    padding: 1rem .5rem;
    border-bottom: rgba(0, 0, 0, 0.2) 2px solid;
    color: #eee;
    background: #666;
`

const Text = styled.div`
    font-weight: 600;
    font-size: .8rem;
    cursor: pointer;
    display: flex;
    align-items: center;
`

const ScrollableExpenses = styled.div`
    overflow-y: auto;
    overflow-x: hidden;
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

const Expense = styled.div`
    display: grid;
    place-items: center;
    grid-template-columns: repeat(7, 1fr);
    gap: .4rem;
    padding: .8rem;
    border-bottom: rgba(0,0,0,0.2) 2px solid;
    transition: all 200ms ease-in-out;
    z-index: 1;

    //&:hover {
    //    padding: 1rem;
    //    box-shadow: 0 0 8px rgba(0,0,0,0.6);
    //    z-index: 10;
    //    scale: 1.001;
    //    //border: none;
    //}
`;

const TextExpense = styled.p`
    font-weight: 500;
    font-size: .8rem;

    &.bold {
        font-weight: bold;
    }
    &.color {
        font-style: italic;
    }
`;