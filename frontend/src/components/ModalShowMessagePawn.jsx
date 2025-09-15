import ReactDom from "react-dom";
import styled from "styled-components";
import { X, CircleCheckBig } from 'lucide-react'
import jsPDF from "jspdf";
import html2canvas from "html2canvas";
import {useRef} from "react";
import PotvrdaZaVratenPredmet from "../documents/PotvrdaZaVratenPredmet.jsx";

export default function ModalShowMessagePawn({ closeModal, successMsg, infoMsg, clientName }) {
    const hiddenDocForClosingPawn = useRef(null);

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

    return ReactDom.createPortal(
        <>
            <Overlay />
            <Wrapper >
                <X size={32} onClick={() => {
                    if (successMsg.includes("затворен залог"))
                        handlePrintDoc(hiddenDocForClosingPawn)
                            .catch(err => console.error('Error printing document:', error))
                    closeModal()
                }}/>
                <CircleCheckBig size={120} />
                <h1>{successMsg}</h1>
                <p>{infoMsg}</p>
            </Wrapper>
            <div style={{height: "0px", width: "0px", overflowY: "clip"}}>
                <PotvrdaZaVratenPredmet
                    ref={hiddenDocForClosingPawn}
                    fullName={clientName}
                />
            </div>
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
    
    svg:first-child {
        margin-left: auto;
        transition: all 400ms ease-in-out;
        cursor: pointer;
    }
    svg:first-child:hover {
        transform: rotate(90deg);
    }
    
    svg:nth-child(2) {
        color: var(--green);
        margin-left: auto;
        margin-right: auto;
    }
`

