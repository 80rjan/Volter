import {useEffect, useRef, useState} from "react";
import axios from "axios";
import styled from "styled-components";
import Pawn from "../components/Pawn.jsx";
import Nav from "../components/Nav.jsx";
import { Plus, X, Euro, RotateCcw, ChevronUp, ChevronDown, Minus} from "lucide-react";
import ModalAddNewPawn from "../components/ModalAddNewPawn.jsx";
import CashRegister from "./CashRegister.jsx";
import Loading from "../components/Loading.jsx";
import Client from "../components/Client.jsx";

export default function Clients() {
    const [allClients, setAllClients] = useState([]);
    const [orderBy, setOrderBy] = useState("Id");
    const orderDirectionArr = useRef([1,0,0,0,0,0,0]); // -1=desc 0=normal 1=asc
    const [orderDirection, setOrderDirection] = useState("ASC");
    const [searchByName, setSearchByName] = useState("");
    const [searchByEmbg, setSearchByEmbg] = useState("");
    const [searchByTel, setSearchByTel] = useState("");
    const [refresh, setRefresh] = useState(false);
    const offset = useRef(0);
    const limit = 20;
    const [isLastPage, setIsLastPage] = useState(false);
    const scrollableClientsRef = useRef(null);
    const [loading, setLoading] = useState(false);
    const isFetchingRef = useRef(false);
    const [isFetching, setIsFetching] = useState(false);
    const [refreshCashReg, setRefreshCashReg] = useState(false);

    const fetchClients = (limit, offset, order, direction, searchByName, searchByEmbg, searchByTel, isLoading) => {
        if (isFetching) return;
        setIsFetching(true);
        setLoading(isLoading);
        isFetchingRef.current = true;
        axios.get(`http://localhost:3000/clients?limit=${limit}&offset=${offset}&orderBy=${order}&orderDirection=${direction}&searchByName=${searchByName}&searchByEmbg=${searchByEmbg}&searchByTel=${searchByTel}`)
            .then(res => {
                setAllClients(prev => {
                    const ids = new Set(prev.map(c => `${c.Id}`));
                    const newUnique = res.data.filter(c => !ids.has(`${c.Id}`));
                    return [...prev, ...newUnique];
                });

                setIsLastPage(res.data.length < limit);
            })
            .catch(error => {
                console.error('Error fetching all clients:', error)
            })
            .finally(() => {
                setLoading(false)
                isFetchingRef.current = false;
                setIsFetching(false);
            });
    }

    useEffect(() => {
        const handleScroll = () => {
            const scrollDiv = scrollableClientsRef.current;
            const scrollHeight = scrollDiv.scrollHeight; // Total content height
            const scrollTop = scrollDiv.scrollTop; // Current scroll position
            const clientHeight = scrollDiv.clientHeight; // Visible height of the div

            // Check if the scrollbar is 30% up from the bottom
            if (scrollHeight - scrollTop - clientHeight <= scrollHeight * 0.3 && !isLastPage && !isFetchingRef.current) {
                offset.current += limit; // Increase offset for the next fetch
                fetchClients(limit, offset.current, orderBy, orderDirection, searchByName, searchByEmbg, searchByTel, false);
            }
        };

        const scrollableDiv = scrollableClientsRef.current;
        scrollableDiv.addEventListener("scroll", handleScroll);

        return () => {
            scrollableDiv.removeEventListener("scroll", handleScroll);
        };
    }, [isLastPage, refresh, orderBy, orderDirection, searchByName, searchByEmbg, searchByTel])

    useEffect(() => {
        setAllClients([]);
        offset.current = 0;
        fetchClients(limit, offset.current, orderBy, orderDirection, searchByName, searchByEmbg, searchByTel, true);
    }, [refresh, orderBy, orderDirection, searchByName, searchByEmbg, searchByTel])


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
        <ClientsPage >
            <Nav />
            <Container >

                <HeaderWrapper >
                    <h1>Клиенти</h1>
                    {/*/>}*/}
                </HeaderWrapper>

                <FilterWrapper >
                    <StyledInput placeholder="Пребарувај по име"
                                 onKeyUp={(e) => {
                                     setSearchByName(e.target.value)
                                 }}
                    />
                    <StyledInput placeholder="Пребарувај по ембг"
                                 onKeyUp={(e) => {
                                     setSearchByEmbg(e.target.value)
                                 }}
                    />
                    <StyledInput placeholder="Пребарувај по телефон"
                                 onKeyUp={(e) => {
                                     setSearchByTel(e.target.value)
                                 }}
                    />
                </FilterWrapper>

                <ClientsWrapper>
                    <TableHeader >
                        <Text onClick={() => handleOrder("Id", 0)} >
                            Ид {orderDirectionArr.current[0] === 0 ? <Minus size={14} /> : orderDirectionArr.current[0] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => handleOrder("Name", 1)} >
                            Име {orderDirectionArr.current[1] === 0 ? <Minus size={14} /> : orderDirectionArr.current[1] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text style={{cursor: "default"}}>
                            Телефон
                        </Text>
                        <Text onClick={() => handleOrder("City", 2)} >
                            Град {orderDirectionArr.current[2] === 0 ? <Minus size={14} /> : orderDirectionArr.current[2] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => handleOrder("Total Pawns", 3)} >
                            Вкупно залози {orderDirectionArr.current[3] === 0 ? <Minus size={14} /> : orderDirectionArr.current[3] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => handleOrder("Active Pawns", 4)} >
                            Активни залози {orderDirectionArr.current[4] === 0 ? <Minus size={14} /> : orderDirectionArr.current[4] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => handleOrder("Money Pawns", 5)} >
                            Вредност на залози {orderDirectionArr.current[5] === 0 ? <Minus size={14} /> : orderDirectionArr.current[5] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => handleOrder("Money Provision", 6)} >
                            Приход од провизија {orderDirectionArr.current[6] === 0 ? <Minus size={14} /> : orderDirectionArr.current[6] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                    </TableHeader>

                    <ScrollableClients ref={scrollableClientsRef}>
                        { loading ?
                            <Loading /> :
                            allClients.map((client, index) => (
                                <Client key={index} client={client} index={index} />
                            )) }
                    </ScrollableClients>

                </ClientsWrapper>

                <CashRegister refreshDependancy={refresh} refreshDependancyAdjustPawn={refreshCashReg} />
            </Container>
        </ClientsPage>
    )
}

const ClientsPage = styled.div`
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

const ClientsWrapper = styled.div`
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
    grid-template-columns: 2rem repeat(7, 1fr);
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

const ScrollableClients = styled.div`
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
