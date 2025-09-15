import React, {useEffect, useRef, useState} from "react";
import axios from "axios";
import styled from "styled-components";
import Pawn from "../components/Pawn.jsx";
import Nav from "../components/Nav.jsx";
import {Plus, X, Euro, RotateCcw, ChevronUp, ChevronDown, Minus} from "lucide-react";
import ModalAddNewPawn from "../components/ModalAddNewPawn.jsx";
import CashRegister from "./CashRegister.jsx";
import Loading from "../components/Loading.jsx";

export default function Pawns() {
    const [allPawns, setAllPawns] = useState([]);
    const [summary, setSummary] = useState({});
    const [orderBy, setOrderBy] = useState("Valid Until");
    const orderDirectionArr = useRef([0, 0, 0, 0, 0, 0, 1]); // -1=desc 0=normal 1=asc
    const [orderDirection, setOrderDirection] = useState("ASC");
    const [searchByName, setSearchByName] = useState("");
    const [searchByEmbg, setSearchByEmbg] = useState("");
    const [searchByTel, setSearchByTel] = useState("");
    const [searchByCategory, setSearchByCategory] = useState("");
    const [modalAddNewPawn, setModalAddNewPawn] = useState(false);
    const [refresh, setRefresh] = useState(false);
    const offset = useRef(0);
    const limit = 40;
    const [isLastPage, setIsLastPage] = useState(false);
    const scrollablePawnsRef = useRef(null);
    const [loading, setLoading] = useState(false);
    const isFetchingRef = useRef(false);
    const [isFetching, setIsFetching] = useState(false);
    const [refreshCashReg, setRefreshCashReg] = useState(false);

    const filterSelectOptions = [
        {value: "Gold", label: "Залог злато"},
        {value: "Electronics", label: "Залог електроника"},
        {value: "Watch", label: "Залог часовници"},
        {value: "Vehicle", label: "Залог возила"},
        {value: "Other", label: "Залог останато"}
    ]

    const fetchedPawnIds = useRef(new Set());

    const fetchPawns = (limit, offset, order, direction, searchByName, searchByEmbg, searchByTel, searchByCategory, isLoading) => {
        if (isFetching) return;
        setIsFetching(true);
        setLoading(isLoading);
        isFetchingRef.current = true;
        axios.get(`http://localhost:3000?limit=${limit}&offset=${offset}&orderBy=${order}&orderDirection=${direction}&searchByName=${searchByName}&searchByEmbg=${searchByEmbg}&searchByTel=${searchByTel}&searchByCategory=${searchByCategory}`)
            .then(res => {
                setSummary(res.data.summary);

                const pawns = res.data.pawns
                const newUnique = pawns.filter(p => !fetchedPawnIds.current.has(`${p.Category}_${p.Id}`));
                newUnique.forEach(p => fetchedPawnIds.current.add(`${p.Category}_${p.Id}`));

                setAllPawns(prev => [...prev, ...newUnique]);
                setIsLastPage(pawns.length < limit);
            })
            .catch(error => {
                console.error('Error fetching all pawns:', error)
            })
            .finally(() => {
                setLoading(false)
                isFetchingRef.current = false;
                setIsFetching(false);
            });
    }

    useEffect(() => {
        const handleScroll = () => {
            const scrollDiv = scrollablePawnsRef.current;
            const scrollHeight = scrollDiv.scrollHeight; // Total content height
            const scrollTop = scrollDiv.scrollTop; // Current scroll position
            const clientHeight = scrollDiv.clientHeight; // Visible height of the div

            // Check if the scrollbar is 30% up from the bottom
            if (scrollHeight - scrollTop - clientHeight <= scrollHeight * 0.3 && !isLastPage && !isFetchingRef.current) {
                offset.current += limit; // Increase offset for the next fetch
                fetchPawns(limit, offset.current, orderBy, orderDirection, searchByName, searchByEmbg, searchByTel, searchByCategory, false);
            }
        };

        const scrollableDiv = scrollablePawnsRef.current;
        scrollableDiv.addEventListener("scroll", handleScroll);

        return () => {
            scrollableDiv.removeEventListener("scroll", handleScroll);
        };
    }, [isLastPage, refresh, orderBy, orderDirection, searchByName, searchByEmbg, searchByTel, searchByCategory])

    useEffect(() => {
        fetchedPawnIds.current.clear();
        setAllPawns([]);
        offset.current = 0;
        fetchPawns(limit, offset.current, orderBy, orderDirection, searchByName, searchByEmbg, searchByTel, searchByCategory, true);
    }, [refresh, orderBy, orderDirection, searchByName, searchByEmbg, searchByTel, searchByCategory])


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
        <PawnsPage>
            <Nav/>
            <Container>

                <HeaderWrapper>
                    <h1>Залози</h1>
                    <ButtonAddNewPawn
                        onClick={() => setModalAddNewPawn(true)}
                    >
                        <Plus size={22} color="white" strokeWidth={3}/>
                        Внеси Нов Залог
                    </ButtonAddNewPawn>

                    {modalAddNewPawn && <ModalAddNewPawn
                        closeModal={() => setModalAddNewPawn(false)}
                        refresh={() => setRefresh(prev => !prev)}
                    />}
                </HeaderWrapper>

                <FilterWrapper>
                    <StyledSelect
                        style={{
                            color: searchByCategory === "" ? "#888" : "#000",
                        }}
                        onChange={(e) => setSearchByCategory(e.target.value)}>
                        <option
                            style={{color: "#888"}}
                            value="">Пребарубај по
                        </option>
                        {
                            filterSelectOptions.map((option, index) => (
                                <option style={{color: "#111"}} key={index} value={option.value}>{option.label}</option>
                            ))
                        }
                    </StyledSelect>
                    <StyledInput
                        type={"search"}
                        placeholder="Пребарувај по име"
                        onChange={(e) => {
                            setSearchByName(e.target.value)
                        }}
                    />
                    <StyledInput
                        type={"search"}
                        placeholder="Пребарувај по ембг"
                        onChange={(e) => {
                            setSearchByEmbg(e.target.value)
                        }}
                    />
                    <StyledInput
                        type={"search"}
                        placeholder="Пребарувај по телефон"
                        onChange={(e) => {
                            setSearchByTel(e.target.value)
                        }}
                    />
                </FilterWrapper>

                <PawnsWrapper>
                    <TableHeader>
                        <Text onClick={() => handleOrder("Client Id", 0)}>
                            Ид {orderDirectionArr.current[0] === 0 ?
                            <Minus size={14}/> : orderDirectionArr.current[0] === -1 ? <ChevronDown size={14}/> :
                                <ChevronUp size={14}/>}
                        </Text>
                        <Text onClick={() => handleOrder("Name", 1)}>
                            Име {orderDirectionArr.current[1] === 0 ?
                            <Minus size={14}/> : orderDirectionArr.current[1] === -1 ? <ChevronDown size={14}/> :
                                <ChevronUp size={14}/>}
                        </Text>
                        <Text onClick={() => handleOrder("Category", 2)}>
                            Категорија {orderDirectionArr.current[2] === 0 ?
                            <Minus size={14}/> : orderDirectionArr.current[2] === -1 ? <ChevronDown size={14}/> :
                                <ChevronUp size={14}/>}
                        </Text>
                        <Text onClick={() => handleOrder("About", 3)}>
                            Опис {orderDirectionArr.current[3] === 0 ?
                            <Minus size={14}/> : orderDirectionArr.current[3] === -1 ? <ChevronDown size={14}/> :
                                <ChevronUp size={14}/>}
                        </Text>
                        <Text onClick={() => handleOrder("Item Cost", 4)}>
                            Вредност {orderDirectionArr.current[4] === 0 ?
                            <Minus size={14}/> : orderDirectionArr.current[4] === -1 ? <ChevronDown size={14}/> :
                                <ChevronUp size={14}/>}
                        </Text>
                        <Text onClick={() => handleOrder("Provision", 5)}>
                            Провизија {orderDirectionArr.current[5] === 0 ?
                            <Minus size={14}/> : orderDirectionArr.current[5] === -1 ? <ChevronDown size={14}/> :
                                <ChevronUp size={14}/>}
                        </Text>
                        <Text onClick={() => handleOrder("Days Left", 6)}>
                            Рок {orderDirectionArr.current[6] === 0 ?
                            <Minus size={14}/> : orderDirectionArr.current[6] === -1 ? <ChevronDown size={14}/> :
                                <ChevronUp size={14}/>}
                        </Text>
                        <Text style={{cursor: "default"}}>Валидно до</Text>
                        <Text style={{cursor: "default"}}>Акции</Text>
                        <Text style={{cursor: "default"}}>Повеќе</Text>
                    </TableHeader>

                    <ScrollablePawns ref={scrollablePawnsRef}>
                        {loading ?
                            <Loading/> :
                            allPawns.map((pawn, index) => (
                                <Pawn
                                    pawn={pawn}
                                    key={index}
                                    refresh={() => setRefresh(prev => !prev)}
                                    isOdd={index % 2 !== 0}
                                    refreshCashReg={() => setRefreshCashReg(prev => !prev)}
                                />
                            ))}
                    </ScrollablePawns>

                    <TableFooter>
                        {
                            searchByTel.length > 0 || searchByEmbg.length > 0 || searchByName.length > 0 || searchByCategory.length > 0 ?
                                (
                                    <>
                                        <div>
                                            <span>Бр. залози:</span>
                                            {loading ?
                                                <Loading width={20} height={20}/> :
                                                Number(summary["Num Pawns"]).toLocaleString("de-DE")}
                                        </div>
                                        <div>
                                            <span>Исплатени средства:</span>
                                            {loading ?
                                                <Loading width={20} height={20}/> :
                                                Number(summary["Money Pawns"]).toLocaleString("de-DE")}
                                        </div>
                                        <div>
                                            <span>Очекуван приход:</span>
                                            {loading ? (
                                                <Loading width={20} height={20}/>
                                            ) : Number(summary["Provision"]).toLocaleString("de-DE")}
                                            <span>/</span>
                                            <span>
                                                {loading ? (
                                                    <Loading width={20} height={20}/>
                                                ) : ((summary["Provision"] / summary["Money Pawns"] * 100) || 0).toFixed(2).toLocaleString("de-DE") + '%'}
                                            </span>
                                        </div>
                                    </>
                                ) : (
                                    <>
                                        <div>
                                            <X size={18} color="#000"/>
                                            <span> - Затвори залог</span>
                                        </div>
                                        <div>
                                            <RotateCcw size={18} color="var(--cta-color)"/>
                                            <span> - Продолжи залог</span>
                                        </div>
                                        <div>
                                            <Euro size={18} color="var(--green)"/>
                                            <span> - Премести залог во продажба</span>
                                        </div>
                                    </>
                                )
                        }
                    </TableFooter>
                </PawnsWrapper>

                <CashRegister refreshDependency={refresh} refreshDependencyAdjustPawn={refreshCashReg}/>
            </Container>
        </PawnsPage>
    )
}

const PawnsPage = styled.div`
    height: 100vh;
    display: grid;
    grid-template-columns: max(15%, 240px) auto;
`

const Container = styled.div`
    display: flex;
    flex-direction: column;
    padding: 2rem 2rem 0 2rem;
    gap: .8rem;
    flex-grow: 1;
    overflow: hidden;
`

const HeaderWrapper = styled.div`
    display: flex;
    justify-content: space-between;
    width: 100%;
    
    & > h1 {
        font-size: 1.8rem;
    }
`

const ButtonAddNewPawn = styled.button`
    background: var(--green);
    height: fit-content;
    color: white;
    border-radius: .4rem;
    display: flex;
    align-items: center;
    padding: .6rem 1.6rem;
    font-size: 1rem;
    box-shadow: 4px 2px 6px rgba(0, 0, 0, 0.2);
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

const StyledSelect = styled.select`
    border: none;
    border-radius: .2rem;
    font-size: .8rem;
    font-weight: 500;
    width: 20%;
    padding: .4rem;
    box-shadow: 0 0 8px rgba(0, 0, 0, 0.2);
`;

const StyledInput = styled.input`
    border: none;
    border-radius: .2rem;
    font-size: .8rem;
    font-weight: 500;
    width: 20%;
    padding: .4rem;
    box-shadow: 0 0 8px rgba(0, 0, 0, 0.2);
`

const PawnsWrapper = styled.div`
    display: flex;
    flex-direction: column;
    background: white;
    border-radius: .5rem;
    box-shadow: 0 0 8px rgba(0, 0, 0, 0.2);
    overflow: hidden;
    flex-grow: 1;
    min-height: 0;
`

const TableHeader = styled.div`
    display: grid;
    place-items: center;
    grid-template-columns: 3rem 1.5fr 1fr 2fr repeat(4, 1fr) 1.5fr .5fr;
    padding: .6rem .5rem;
    border-bottom: rgba(0, 0, 0, 0.2) 2px solid;
    color: #eee;
    background: #666;
`

const Text = styled.div`
    font-weight: 500;
    font-size: .7rem;
    cursor: pointer;
    display: flex;
    align-items: center;
`

const ScrollablePawns = styled.div`
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
    padding: .6rem 1rem;
    margin-top: auto;
    box-shadow: 0 -2px 6px rgba(0, 0, 0, 0.2);
    font-size: .7rem;
    font-weight: 500;

    div {
        display: flex;
        gap: .4rem;
        align-items: center;
    }

    span {
        white-space: nowrap;
        color: #444;
        font-weight: 400;
    }
`