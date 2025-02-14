import ReactDom from "react-dom";
import styled from "styled-components";
import {X, CircleHelp, CheckCheck, CircleCheckBig} from 'lucide-react'
import {useEffect, useState} from "react";
import axios from "axios";

export default function ModalSellItem({ closeModal, id, priceBought}) {
    const [showSuccMsg, setShowSuccMsg] = useState(false);
    const [showEnterPriceSold, setShowEnterPriceSold] = useState(true);
    const [successMsg, setSuccessMsg] = useState("");
    const [infoMsg, setInfoMsg] = useState("");
    const [priceSold, setPriceSold] = useState(null);

    const sellItem = (id, priceSold) => {

        //Put http which sends the id of the sale to sell item and close sale
        axios.put(`http://localhost:3000/sales/sellItem`, { id, priceSold })
            .then(response => {
                console.log(response)
                setSuccessMsg("Successfully sold item")
                setInfoMsg(`Added ${response.data.moneyIntoCashReg.toLocaleString("de-DE")} into cash register!`)
                setShowSuccMsg(true);
            } )
            .catch(error => console.error('Error selling item:', error));
    }

    useEffect(() => {
        if (!showEnterPriceSold)
            sellItem(id, priceSold);
    }, [showEnterPriceSold])

    return ReactDom.createPortal(
        <>
            <Overlay />
            <Wrapper >
                <X size={32} onClick={closeModal}/>
                {
                    showEnterPriceSold && (
                        <>
                            <CircleHelp size={120} />
                            <h1>What price did you sell the item?</h1>
                            <form onSubmit={e => e.preventDefault()}>
                                <span>
                                    <p>Price Bought: {priceBought}</p>
                                    <StyledInput onChange={e => setPriceSold(e.target.value)} placeholder="Enter price"
                                             required/>
                                </span>
                                <Button onClick={() => {
                                    setShowEnterPriceSold(false)
                                }}>
                                    <CheckCheck size={28}/> Confirm
                                </Button>
                            </form>
                        </>
                    )
                }
                {
                    showSuccMsg && (
                        <>
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

