import ReactDom from "react-dom";
import styled from "styled-components";
import {X, CircleHelp, CheckCheck, CircleCheckBig} from 'lucide-react'
import {useEffect, useRef, useState} from "react";
import axios from "axios";
import jsPDF from "jspdf";
import html2canvas from "html2canvas";
import PotvrdaZaVratenPredmet from "../documents/PotvrdaZaVratenPredmet.jsx";
import Loading from "./Loading.jsx";

//This modal is for closing, continuing or moving a pawn to sale and for selling items
export default function ModalActions({ pawnAction, id, category, successMsg, action, priceBought, provision, dailyProvision, suggestedPrice, daysLeft, closeModal, title, loading}) {
    const penaltyPrice= daysLeft < 0 ? (pawnAction === "continue" ? Math.abs(daysLeft) * dailyProvision : provision) : 0;
    const [price, setPrice] = useState(Math.round(category === "sale" ? suggestedPrice : suggestedPrice + penaltyPrice));
    const [carryOverDays, setCarryOverDays] = useState(Math.round((price - (suggestedPrice + penaltyPrice)) / dailyProvision));
    const [description, setDescription] = useState("");
    const [error, setError] = useState("");
    const [showError, setShowError] = useState(true);

    const useActionFunc = () => {
        try {
            if (category === "sale")
                action(id, price, description)
            else
                pawnAction === "continue" ?
                    action(id, category, price, description, carryOverDays) :
                    action(id, category, price, description);
        } catch (error) {
            console.error('Error entering price to make action:', error);
        }
    }

    return ReactDom.createPortal(
        <>
            <Overlay />
            <Wrapper>
                <X size={32} onClick={closeModal}/>
                <CircleHelp size={120}/>
                <h1>{title}</h1>
                <form onSubmit={e => {
                    e.preventDefault()
                    if (price.length > 0 || price > 0) {
                        if (isNaN(price))
                            setError('Внеси валиден број!') && setError(true)
                        else
                            useActionFunc()
                    } else {
                        setShowError(true);
                        setError('Внеси сума!')
                    }
                }}>
                    <div>
                        <p>Исплатени пари: <span
                            style={{fontWeight: 600}}>{priceBought.toLocaleString("de-DE")}</span></p>
                        {
                            category === "sale" ? undefined : <p>Провизија: <span
                                style={{fontWeight: 600}}>{provision.toLocaleString("de-DE")}</span></p>
                        }
                        {
                            category === "sale" ? undefined : <p>Казна: <span
                                style={{fontWeight: 600}}>{Math.round(penaltyPrice).toLocaleString("de-DE")}</span></p>
                        }
                        {
                            successMsg !== "Успешно продолжен залог!" ? undefined : <p>Префрлени денови: <span style={{fontWeight: 600}}>{carryOverDays.toLocaleString("de-DE")}</span></p>
                        }
                        <div>
                            <StyledInput onChange={e => {
                                setPrice(e.target.value)
                                setCarryOverDays(Math.round((e.target.value - (suggestedPrice + penaltyPrice)) / dailyProvision))
                            }}
                                         type="number"
                                         placeholder="Внеси сума"
                                         value={price} required/>
                            <StyledInput onChange={e => setDescription(e.target.value)}
                                         placeholder="Внеси опис"
                                         value={description}/>
                        </div>
                    </div>
                    <div style={{display: "flex", gap: "1rem", alignItems: "center"}}>
                        <Button type="submit" disabled={loading}>
                            <CheckCheck size={28}/> Потврди
                        </Button>
                        {
                            loading && <Loading width={40} height={40} />
                        }
                    </div>
                </form>
                {showError && <ErrorText>{error}</ErrorText>}
            </Wrapper>
        </>,
        document.getElementById("portal")
    )
}

const Overlay = styled.div`
    position: fixed;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    background: rgba(0, 0, 0, .7);
    z-index: 1000;
`

const Wrapper = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: .8rem;
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background: #eee;
    z-index: 1000;
    padding: 1rem 2rem 2rem 2rem;
    border-radius: 8px;
    min-width: fit-content;
    max-width: 90%;
    
    h1 {
        text-align: center;
    }
    
    p {
        font-weight: 400;
        font-size: 1.2rem;
    }
    
    &>svg:first-child {
        margin-left: auto;
        transition: all 400ms ease-in-out;
        cursor: pointer;
    }
    &>svg:first-child:hover {
        transform: rotate(90deg);
    }
    
    svg:nth-child(2) {
        color: var(--green);
        margin-left: auto;
        margin-right: auto;
    }
    
    form {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 1rem;
        margin-top: 1rem;
    }
    
    form > div:first-child {
        display: flex;
        
        align-items: center;
        gap: 2rem;
        
        & > p {
            min-width: max-content;
            display: flex;
            flex-direction: column;
        }
        
        & > div {
            display: flex;
            flex-direction: column;
            gap: 1rem;
        }
    }
`

const StyledInput = styled.input`
    border: none;
    border-radius: .2rem;
    font-size: 1.2rem;
    padding: .5rem;
    box-shadow: 0 0 4px rgba(0,0,0,0.2);
    height: fit-content;
`

const Button = styled.button`
    display: flex;
    justify-content: center;
    align-items: center;
    gap: .4rem;
    padding: .6rem 8rem;
    border-radius: 4px;
    background: var(--green);
    color: white;
    font-size: 1.4rem;
    box-shadow: 0 0 8px rgba(0,0,0,0.2);
    transition: scale 400ms ease-in-out;
    
    &:disabled {
        cursor: not-allowed;
        opacity: 0.4;
        
        &:hover {
            scale: 1;
        }
    }
    
    &:hover {
        scale: 1.05;
    }
`

const ErrorText = styled.span`
    color: red;
    font-style: italic;
    font-size: 1.2rem;
    font-weight: 400;
`

