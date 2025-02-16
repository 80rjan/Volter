import {useEffect, useState} from "react";
import axios from "axios";
import { Handshake, Tag, Sigma, CalendarClock, Plus, Minus } from "lucide-react"
import styled from "styled-components";
import ModalAdjustCashRegister from "../Components/ModalAdjustCashRegister.jsx";

export default function CashRegister({ refreshDependancy }) {
    const [cashReg, setCashReg] = useState({});
    const [showModalInsert, setShowModalInsert] = useState(false);
    const [showModalRemove, setShowModalRemove] = useState(false);
    const [refresh, setRefresh] = useState(refreshDependancy);

    const fetchCashRegister = () => {
        axios.get(`http://localhost:3000/cashRegister`)
            .then(res => {
                setCashReg(res.data.cashReg)
            })
            .catch(error => console.error('Error fetching cash register', error));
    }

    useEffect(() => {
        fetchCashRegister();
    }, [refreshDependancy, refresh]);

    return (
        <Wrapper >
            <TextWrapper>
                <Handshake size={24} />
                <Value className="bold">{ Number(cashReg.money_pawns).toLocaleString("de-DE") }</Value>
                <Value >/</Value>
                <Value >{ Number(cashReg.num_pawns).toLocaleString("de-DE") }</Value>
            </TextWrapper>
            <TextWrapper>
                <Tag size={24} />
                <Value className="bold">{ Number(cashReg.money_sale_items).toLocaleString("de-DE") }</Value>
                <Value >/</Value>
                <Value >{ Number(cashReg.num_sale_items).toLocaleString("de-DE") }</Value>
            </TextWrapper>
            <TextWrapper>
                <Sigma size={24} />
                <Value className="bold">{ Number(cashReg.register_money).toLocaleString("de-DE") }</Value>
            </TextWrapper>
            <TextWrapper >
                <CalendarClock size={24} />
                <Value className="bold">{ Object.keys(cashReg).length > 0 && cashReg.last_updated.substring(0, 10)} </Value>
                <Value >/</Value>
                <Value >{ Object.keys(cashReg).length > 0 && cashReg.last_updated.substring(11, 16) }</Value>
            </TextWrapper>
            <ActionsWrapper >
                <Minus size={24} color="var(--dark-red)" onClick={() => setShowModalRemove(true)}/>
                <Plus size={24} color="var(--green)" onClick={() => setShowModalInsert(true)}/>
            </ActionsWrapper>

            {showModalInsert && <ModalAdjustCashRegister
                closeModal={() => setShowModalInsert(false)}
                isInsert={true}
                refresh={() => setRefresh(prev => !prev)}
            /> }
            {showModalRemove && <ModalAdjustCashRegister
                closeModal={() => setShowModalRemove(false)}
                isInsert={false}
                refresh={() => setRefresh(prev => !prev)}
            /> }
        </Wrapper>
    )
}

const Wrapper = styled.div`
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem 1rem;
    background: #fff;
    border-radius: 8px 8px 0 0;
    box-shadow: 0 0 8px rgba(0,0,0,0.2);

    .bold {
        font-weight: bold;
        font-style: normal;
    }
`
const TextWrapper = styled.div`
    display: flex;
    align-items: center;
    gap: .4rem;
`

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
`

const Value = styled.p`
    font-size: .8rem;
    font-style: italic;
`