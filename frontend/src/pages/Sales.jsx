import {useEffect, useRef, useState} from "react";
import axios from "axios";
import styled from "styled-components";
import Nav from "../components/Nav.jsx";
import { Plus, X, Euro, RotateCcw, ChevronUp, ChevronDown, Minus} from "lucide-react";
import ModalAddNewSale from "../components/ModalAddNewSale.jsx";
import Sale from "../components/Sale.jsx";
import CashRegister from "./CashRegister.jsx";
import Loading from "../components/Loading.jsx";

export default function Sales() {
    const [allSales, setAllSales] = useState([]);
    const [orderBy, setOrderBy] = useState("Date Bought");
    const orderDirectionArr = useRef([0,0,0,0,-1]); // -1=desc 0=normal 1=asc
    const [orderDirection, setOrderDirection] = useState("DESC");
    const [searchByName, setSearchByName] = useState("");
    const [searchByEmbg, setSearchByEmbg] = useState("");
    const [searchByTel, setSearchByTel] = useState("");
    const [modalAddNewSale, setModalAddNewSale] = useState(false);
    const [refresh, setRefresh] = useState(false);
    const offset = useRef(0);
    const limit = 20;
    const [isLastPage, setIsLastPage] = useState(false);
    const prevSales = useRef([]);
    const scrollableSalesRef = useRef(null);
    const [loading, setLoading] = useState(false);
    const isFetchingRef = useRef(false);
    const [isFetching, setIsFetching] = useState(false);

    const fetchSales = (limit, offset, order, direction, searchByName, searchByEmbg, searchByTel, isLoading) => {
        if (isFetching) return;
        setIsFetching(true);
        setLoading(isLoading);
        isFetchingRef.current = true;
        axios.get(`http://localhost:3000/sales?limit=${limit}&offset=${offset}&orderBy=${order}&orderDirection=${direction}&searchByName=${searchByName}&searchByEmbg=${searchByEmbg}&searchByTel=${searchByTel}`)
            .then(res => {
                if (JSON.stringify(prevSales.current) !== JSON.stringify(res.data)) {
                    setAllSales(prev => [...prev, ...res.data]);
                    prevSales.current = [...prevSales.current, ...res.data];
                    setIsLastPage(res.data.length < limit);
                }
            })
            .catch(error => {
                console.error('Error fetching all pawns:', error)
            })
            .finally(() => {
                setLoading(false)
                isFetchingRef.current = false;
                setIsFetching(false);
            })
    }

    useEffect(() => {
        const handleScroll = () => {
            const scrollDiv = scrollableSalesRef.current;
            const scrollHeight = scrollDiv.scrollHeight; // Total content height
            const scrollTop = scrollDiv.scrollTop; // Current scroll position
            const clientHeight = scrollDiv.clientHeight; // Visible height of the div

            // Check if the scrollbar is 30% up from the bottom
            if (scrollHeight - scrollTop - clientHeight <= scrollHeight * 0.3 && !isLastPage && !isFetchingRef.current) {
                offset.current += limit; // Increase offset for the next fetch
                fetchSales(limit, offset.current, orderBy, orderDirection, searchByName, searchByEmbg, searchByTel, false);
            }
        };

        const scrollableDiv = scrollableSalesRef.current;
        scrollableDiv.addEventListener("scroll", handleScroll);

        return () => {
            scrollableDiv.removeEventListener("scroll", handleScroll);
        };
    }, [isLastPage, refresh, orderBy, orderDirection, searchByName, searchByEmbg, searchByTel])

    useEffect(() => {
        setAllSales([]);
        prevSales.current = [];
        offset.current = 0;
        fetchSales(limit, offset.current, orderBy, orderDirection, searchByName, searchByEmbg, searchByTel, true);
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
        <SalesPage >
            <Nav />
            <Container >

                <HeaderWrapper >
                    <h1>Продажба</h1>
                    <ButtonAddNewSale
                        onClick={() => setModalAddNewSale(true)}
                    >
                        <Plus size={22} color="white" strokeWidth={3} />
                        Внеси Нова Продажба
                    </ButtonAddNewSale>

                    {modalAddNewSale && <ModalAddNewSale
                        closeModal={() => setModalAddNewSale(false)}
                        refresh={() => setRefresh(prev => !prev)}
                    />}
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

                <SalesWrapper>
                    <TableHeader >
                        <Text onClick={() => handleOrder("Client Id", 0)}>
                            Код {orderDirectionArr.current[0] === 0 ? <Minus size={14} /> : orderDirectionArr.current[0] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => handleOrder("Name", 1)}>
                            Име {orderDirectionArr.current[1] === 0 ? <Minus size={14} /> : orderDirectionArr.current[1] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                            <Text onClick={() => handleOrder("About", 2)}>
                            Опис {orderDirectionArr.current[2] === 0 ? <Minus size={14} /> : orderDirectionArr.current[2] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => handleOrder("Item Cost", 3)}>
                            Вредност {orderDirectionArr.current[3] === 0 ? <Minus size={14} /> : orderDirectionArr.current[3] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => handleOrder("Date Bought", 4)}>
                            Датум Купено {orderDirectionArr.current[4] === 0 ? <Minus size={14} /> : orderDirectionArr.current[4] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text style={{cursor: "default"}}>Акции</Text>
                        <Text style={{cursor: "default"}}>Повеќе</Text>
                    </TableHeader>

                    <ScrollableSales ref={scrollableSalesRef}>
                        { loading ?
                            <Loading /> :
                            allSales.map((sale, index) => (
                            <Sale
                                sale={sale}
                                key={index}
                                refresh={() => setRefresh(prev => !prev)}
                                isOdd={index%2 !== 0}
                            />
                        )) }
                    </ScrollableSales>

                    <TableFooter>
                        <div>
                            <Euro size={18} color="var(--green)" />
                            <span> - Продади предмет</span>
                        </div>
                    </TableFooter>
                </SalesWrapper>

                <CashRegister refreshDependancy={refresh}/>
            </Container>
        </SalesPage>
    )
}

const SalesPage = styled.div`
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

const SalesWrapper = styled.div`
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
    grid-template-columns: 2rem 1fr 2fr 1fr 1fr 1.5fr .5fr;
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

const ScrollableSales = styled.div`
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