import { useEffect, useState } from "react";
import axios from "axios";
import {Handshake, Tag, Sigma, CalendarClock, Plus, Minus, Coins, Percent, HandCoins} from "lucide-react";
import styled from "styled-components";
import ModalAdjustCashRegister from "../components/ModalAdjustCashRegister.jsx";
import Loading from "../components/Loading.jsx";

export default function CashRegister({ refreshDependency, refreshDependencyAdjustPawn, refreshTransactions }) {
    const [cashReg, setCashReg] = useState({});
    const [showModalInsert, setShowModalInsert] = useState(false);
    const [showModalRemove, setShowModalRemove] = useState(false);
    const [loading, setLoading] = useState(false);

    const fetchCashRegister = () => {
        setLoading(true)
        axios.get(`http://localhost:3000/cashRegister`)
            .then(res => {
                setCashReg({...res.data.cashReg, profit: res.data.profit});
            })
            .catch(error => console.error('Error fetching cash register', error))
            .finally(() => setLoading(false));
    };

    useEffect(() => {
        fetchCashRegister();
    }, [refreshDependency, refreshDependencyAdjustPawn]);

    useEffect(() => {console.log(cashReg)}, [cashReg])
    return (
        <Wrapper>
            <TextWrapper>
                <Handshake size={24} />
                {
                    loading ? <Loading width={30} height={30}/> :
                        <>
                            <Value className="bold">{Number(cashReg.money_pawns).toLocaleString("de-DE")}</Value>
                            <Value>/</Value>
                            <Value>{Number(cashReg.num_pawns).toLocaleString("de-DE")}</Value>
                        </>
                }
            </TextWrapper>
            <TextWrapper>
                <Percent size={24} />
                {
                    loading ? <Loading width={30} height={30}/> :
                        <>
                            <Value className="bold">{Number(cashReg.total_provision).toLocaleString("de-DE")}</Value>
                            <Value>/</Value>
                            <Value>{(Math.round(cashReg.total_provision / cashReg.money_pawns * 100 * 100) / 100 || 0).toLocaleString("de-DE")}</Value>
                        </>
                }
            </TextWrapper>
            <TextWrapper>
                <Coins size={24} />
                {
                    loading ? <Loading width={30} height={30}/> :
                        <>
                            <Value className="bold">{Number(cashReg.gold_grams).toLocaleString("de-DE")} g</Value>
                        </>
                }
            </TextWrapper>
            <TextWrapper>
                <Tag size={24} />
                {
                    loading ? <Loading width={30} height={30}/> :
                        <>
                            <Value className="bold">{Number(cashReg.money_sale_items).toLocaleString("de-DE")}</Value>
                            <Value>/</Value>
                            <Value>{Number(cashReg.num_sale_items).toLocaleString("de-DE")}</Value>
                        </>
                }
            </TextWrapper>
            <TextWrapper>
                <HandCoins size={24} />
                {
                    loading ? <Loading width={30} height={30}/> :
                        <Value className="bold">{Number(cashReg.profit).toLocaleString("de-DE")}</Value>
                }
            </TextWrapper>
            <TextWrapper>
                <Sigma size={24} />
                {
                    loading ? <Loading width={30} height={30}/> :
                        <>
                            <Value className="bold">{Number(cashReg.register_money).toLocaleString("de-DE")}</Value>
                        </>
                }
            </TextWrapper>
            <TextWrapper>
                <CalendarClock size={24} />
                {
                    loading ? <Loading width={30} height={30}/> :
                        <>
                            <Value className="bold">{Object.keys(cashReg).length > 0 && cashReg.last_updated.substring(0, 10)}</Value>
                            <Value>/</Value>
                            <Value>{Object.keys(cashReg).length > 0 && cashReg.last_updated.substring(11, 16)}</Value>
                        </>
                }
            </TextWrapper>
            <ActionsWrapper>
                {
                    loading ? <Loading width={30} height={30}/> :
                        <>
                            <Minus size={24} color="var(--dark-red)" onClick={() => setShowModalRemove(true)} />
                            <Plus size={24} color="var(--green)" onClick={() => setShowModalInsert(true)} />
                        </>
                }
            </ActionsWrapper>

            {showModalInsert && <ModalAdjustCashRegister
                closeModal={() => setShowModalInsert(false)}
                isInsert={true}
                refresh={() => {
                    fetchCashRegister()
                    if (refreshTransactions)
                        refreshTransactions()
                }}
            />}
            {showModalRemove && <ModalAdjustCashRegister
                closeModal={() => setShowModalRemove(false)}
                isInsert={false}
                refresh={() => {
                    fetchCashRegister()
                    if (refreshTransactions)
                        refreshTransactions()
                }}
            />}
        </Wrapper>
    );
}

const Wrapper = styled.div`
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: .6rem 1rem;
    border-radius: 8px 8px 0 0;
    box-shadow: 0 0 8px rgba(0,0,0,0.2);
    background: #fff;
    border: 2px solid rgba(0,0,0,0.4);
    border-bottom: none;
);  

    .bold {
        font-weight: bold;
        font-style: normal;
    }
`;
const TextWrapper = styled.div`
    display: flex;
    align-items: center;
    gap: .4rem;
`;
const ActionsWrapper = styled.div`
    display: flex;
    align-items: center;
    gap: .4rem;

    svg {
        transition: scale 200ms ease-in-out;
        cursor: pointer;
    }
    svg:hover {
        scale: 1.1;
    }
`;
const Value = styled.p`
    font-size: .7rem;
    font-style: italic;
`;