import ReactDom from "react-dom";
import styled from "styled-components";
import { X, CircleCheckBig } from 'lucide-react'

export default function ModalShowMessagePawn({ closeModal, successMsg, infoMsg }) {

    return ReactDom.createPortal(
        <>
            <Overlay />
            <Wrapper >
                <X size={32} onClick={closeModal}/>
                <CircleCheckBig size={120} />
                <h1>{successMsg}</h1>
                <p>{infoMsg}</p>
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

