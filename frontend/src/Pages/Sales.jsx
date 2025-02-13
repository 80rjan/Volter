import {useEffect, useState} from "react";
import axios from "axios";
import styled from "styled-components";
import Nav from "../Components/Nav.jsx";
import { Plus, X, Euro, RotateCcw, ChevronUp, ChevronDown, Minus} from "lucide-react";
import ModalAddNewSale from "../Components/ModalAddNewSale.jsx";
import Sale from "../Components/Sale.jsx";

export default function Sales() {
    const [allSales, setAllSales] = useState([]);
    const [orderBy, setOrderBy] = useState("Date Bought");
    const [orderDirectionArr, setOrderDirectionArr] = useState([0,0,0,0,1]); // -1=desc 0=normal 1=asc
    const [orderDirection, setOrderDirection] = useState("ASC");
    const [searchByName, setSearchByName] = useState("");
    const [searchByEmbg, setSearchByEmbg] = useState("");
    const [searchByTel, setSearchByTel] = useState("");
    const [modalAddNewSale, setModalAddNewSale] = useState(false);
    const [refresh, setRefresh] = useState(false);

    const fetchSales = (order, direction, searchByName, searchByEmbg, searchByTel) => {
        axios.get(`http://localhost:3000/sales?orderBy=${order}&orderDirection=${direction}&searchByName=${searchByName}&searchByEmbg=${searchByEmbg}&searchByTel=${searchByTel}`)
            .then(res => setAllSales(res.data))
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
        fetchSales(orderBy, orderDirection, searchByName, searchByEmbg, searchByTel);
    }, [orderBy, orderDirection, searchByName, searchByEmbg, searchByTel, refresh]);


    return (
        <SalesPage >
            <Nav />
            <Container >

                <HeaderWrapper >
                    <h1>Sales</h1>
                    <ButtonAddNewSale
                        onClick={() => setModalAddNewSale(true)}
                    >
                        <Plus size={22} color="white" strokeWidth={3} />
                        Add New Sale
                    </ButtonAddNewSale>

                    {modalAddNewSale && <ModalAddNewSale
                        closeModal={() => setModalAddNewSale(false)}
                    />}
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
                    <StyledInput placeholder="Search by telephone"
                                 onKeyUp={(e) => {
                                     setSearchByTel(e.target.value)
                                 }}
                    />
                </FilterWrapper>

                <SalesWrapper>
                    <TableHeader >
                        <Text onClick={() => {
                            setOrderDirectionArr(prev => {
                                const newDirection = [...prev];
                                newDirection[0] = newDirection[0] === 0 ? 1 : newDirection[0] === 1 ? -1 : 0;
                                const res = [0,0,0,0,0]
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
                                const res = [0,0,0,0,0]
                                res[1] = newDirection[1];
                                return res;
                            });
                            setOrderBy("Name")
                        }}>
                            Name {orderDirectionArr[1] === 0 ? <Minus size={14} /> : orderDirectionArr[1] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={prev => {
                            setOrderDirectionArr(prev => {
                                const newDirection = [...prev];
                                newDirection[3] = newDirection[3] === 0 ? 1 : newDirection[3] === 1 ? -1 : 0;
                                const res = [0,0,0,0,0]
                                res[3] = newDirection[3];
                                return res;
                            });
                            setOrderBy("About")
                        }}>
                            About {orderDirectionArr[3] === 0 ? <Minus size={14} /> : orderDirectionArr[3] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => {
                            setOrderDirectionArr(prev => {
                                const newDirection = [...prev];
                                newDirection[2] = newDirection[2] === 0 ? 1 : newDirection[2] === 1 ? -1 : 0;
                                const res = [0,0,0,0,0]
                                res[2] = newDirection[2];
                                return res;
                            });
                            setOrderBy("Item Cost")
                        }}>
                            Item Cost {orderDirectionArr[2] === 0 ? <Minus size={14} /> : orderDirectionArr[2] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={prev => {
                            setOrderDirectionArr(prev => {
                                const newDirection = [...prev];
                                newDirection[4] = newDirection[4] === 0 ? 1 : newDirection[4] === 1 ? -1 : 0;
                                const res = [0,0,0,0,0,0,0]
                                res[4] = newDirection[4];
                                return res;
                            });
                            setOrderBy("Date Bought")
                        }}>
                            Date Bought {orderDirectionArr[4] === 0 ? <Minus size={14} /> : orderDirectionArr[4] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text style={{cursor: "default"}}>Actions</Text>
                        <Text style={{cursor: "default"}}>More</Text>
                    </TableHeader>

                    <ScrollableSales>
                        { allSales.map((sale, index) => (
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
                            <span> - Sell Item</span>
                        </div>
                    </TableFooter>
                </SalesWrapper>

            </Container>
        </SalesPage>
    )
}

const SalesPage = styled.div`
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

const ScrollableSales = styled.div`
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