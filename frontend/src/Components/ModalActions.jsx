import ReactDom from "react-dom";
import styled from "styled-components";
import {X, CircleHelp, CheckCheck, CircleCheckBig} from 'lucide-react'
import {useEffect, useRef, useState} from "react";
import axios from "axios";
import jsPDF from "jspdf";
import html2canvas from "html2canvas";
import PotvrdaZaVratenPredmet from "../Documents/PotvrdaZaVratenPredmet.jsx";

//This modal is for closing, continuing or moving a pawn to sale and for selling items
export default function ModalActions({ id, category, clientName, successMsg, action, priceBought, provision, dailyProvision, suggestedPrice, daysLeft, closeModal, title, refresh}) {
    const [showSuccMsg, setShowSuccMsg] = useState(false);
    const [showEnterPrice, setShowEnterPrice] = useState(true);
    const [infoMsg, setInfoMsg] = useState("");
    const penaltyPrice= daysLeft < 0 ? Math.abs(daysLeft) * dailyProvision : 0;
    const [price, setPrice] = useState(category === "sale" ? suggestedPrice : suggestedPrice + penaltyPrice);
    const [description, setDescription] = useState("");
    const [error, setError] = useState("");
    const [showError, setShowError] = useState(true);
    const hiddenDocForClosingPawn = useRef(null);

    const useActionFunc = () => {
        try {
            category === "sale" ?
                action(id, price, description) :
                action(id, category, price, description);
            setInfoMsg(`Успешно внесени ${Number(price).toLocaleString("de-DE")} во каса!`)
            setShowSuccMsg(true);
        } catch (error) {
            console.error('Error entering price to make action:', error);
        }
    }

    const handlePrintDoc = async (ref) => {
        const element = ref.current;
        if (!element) return;
        element.style.width = "1000px"

        const pdf = new jsPDF({
            orientation: "portrait",
            unit: "mm",
            format: "a4",
        });

        try {
            const canvas = await html2canvas(element, { scale: 2, useCORS: true });
            const imgData = canvas.toDataURL("image/png");

            const imgWidth = 210; // A4 width in mm
            const pageHeight = 297; // A4 height in mm
            const imgHeight = (canvas.height * imgWidth) / canvas.width;

            let heightLeft = imgHeight;
            let position = 0;

            pdf.addImage(imgData, "PNG", 0, position, imgWidth, imgHeight);

            while (heightLeft > pageHeight) {
                position -= pageHeight;
                pdf.addPage();
                pdf.addImage(imgData, "PNG", 0, position, imgWidth, imgHeight);
                heightLeft -= pageHeight;
            }

            pdf.save(`Potvrda_za_vrakanje_na_zalozhen_predmet.pdf`);
        } catch (error) {
            console.error(error);
        }
    };

    const callHandlePrintDoc = () => {
        try {
            handlePrintDoc(hiddenDocForClosingPawn);
        } catch (error) {
            console.error('Error printing document:', error);
        }
    }


    useEffect(() => {
        if (!showEnterPrice) {
            useActionFunc();
            if (successMsg === "Успешно затворен залог!")
                callHandlePrintDoc();
        }
    }, [showEnterPrice])

    return ReactDom.createPortal(
        <>
            <Overlay />
            <Wrapper>
                {
                    showEnterPrice && (
                        <>
                            <X size={32} onClick={closeModal}/>
                            <CircleHelp size={120}/>
                            <h1>{title}</h1>
                            <form onSubmit={e => e.preventDefault()}>
                                <div>
                                    <p>Исплатени Пари: <span
                                        style={{fontWeight: 600}}>{priceBought.toLocaleString("de-DE")}</span></p>
                                    {category === "sale" ? undefined : <p>Провизија: <span style={{fontWeight: 600}}>{provision.toLocaleString("de-DE")}</span></p>}
                                    {category === "sale" ? undefined : <p>Казна: <span style={{fontWeight: 600}}>{penaltyPrice.toLocaleString("de-DE")}</span></p>}
                                    <StyledInput onChange={e => setPrice(e.target.value)}
                                                 type="number"
                                                 placeholder="Внеси сума"
                                                 value={price} required />
                                    <StyledInput onChange={e => setDescription(e.target.value)}
                                                 placeholder="Внеси опис"
                                                 value={description} />
                                </div>
                                <Button onClick={() => {
                                    if (price.length > 0 || price > 0) {
                                        isNaN(price) ?
                                            setError('Внеси валиден број!') && setError(true) :
                                            setShowEnterPrice(false) && setShowError(false);
                                    } else {
                                        setShowError(true);
                                        setError('Внеси сума!')
                                    }
                                }}>
                                    <CheckCheck size={28}/> Потврди
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
                            <CircleCheckBig size={120}/>
                            <h1>{successMsg}</h1>
                            <p>{infoMsg}</p>
                        </>
                    )
                }
                <div style={{height: "0px", width: "0px", overflowY: "clip"}}>
                    <PotvrdaZaVratenPredmet
                        ref={hiddenDocForClosingPawn}
                        fullName={clientName}
                    />
                </div>
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

