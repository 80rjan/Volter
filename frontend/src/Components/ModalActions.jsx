import ReactDom from "react-dom";
import styled from "styled-components";
import {X, CircleHelp, CheckCheck, CircleCheckBig} from 'lucide-react'
import {useEffect, useState} from "react";
import axios from "axios";

//This modal is for closing, continuing or moving a pawn to sale and for selling items
export default function ModalActions({ id, category, successMsg, action, priceBought, provision, suggestedPrice, closeModal, title, refresh}) {
    const [showSuccMsg, setShowSuccMsg] = useState(false);
    const [showEnterPrice, setShowEnterPrice] = useState(true);
    const [infoMsg, setInfoMsg] = useState("");
    const [price, setPrice] = useState(suggestedPrice);
    const [error, setError] = useState("");
    const [showError, setShowError] = useState(true);

    const useActionFunc = () => {
        try {
            category === "sale" ?
                action(id, price) :
                action(id, category, price);
            setInfoMsg(`Added ${Number(price).toLocaleString("de-DE")} into cash register!`)
            setShowSuccMsg(true);
        } catch (error) {
            console.error('Error entering price to make action:', error);
        }
    }

    useEffect(() => {
        if (!showEnterPrice)
            useActionFunc();
    }, [showEnterPrice])

    return ReactDom.createPortal(
        <>
            <Overlay />
            <Wrapper >
                {
                    showEnterPrice && (
                        <>
                            <X size={32} onClick={closeModal}/>
                            <CircleHelp size={120} />
                            <h1>{title}</h1>
                            <form onSubmit={e => e.preventDefault()}>
                                <span>
                                    <p>Price Bought: {priceBought}</p>
                                    {category === "sale" ? undefined :<p>Provision: {provision}</p>}
                                    <StyledInput onChange={e => setPrice(e.target.value)} placeholder="Enter price" value={price} required/>
                                </span>
                                <Button onClick={() => {
                                    if (price.length > 0 || price > 0) {
                                        isNaN(price) ?
                                            setError('You must enter a number!') && setError(true) :
                                            setShowEnterPrice(false) && setShowError(false);
                                    } else {
                                        setShowError(true);
                                        setError('You must enter a price!')
                                    }
                                }}>
                                    <CheckCheck size={28}/> Confirm
                                </Button>
                            </form>
                            {showError && <ErrorText>{error}</ErrorText>}
                        </>
                    )
                }
                {
                    showSuccMsg && (
                        <>
                            <X size={32} onClick={refresh}/>
                            <CircleCheckBig size={120} />
                            <h1>{successMsg}</h1>
                            <p>{infoMsg}</p>
                        </>
                    )
                }
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
    background: rgba(0,0,0, .7);
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
    
    span {
        display: flex;
        gap: 2rem;
    }
`

const StyledInput = styled.input`
    border: none;
    border-radius: .2rem;
    font-size: 1rem;
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

