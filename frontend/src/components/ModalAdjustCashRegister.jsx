import ReactDom from "react-dom";
import styled from "styled-components";
import { X, CircleHelp, CheckCheck, CircleCheckBig } from 'lucide-react';
import { useEffect, useState } from "react";
import axios from "axios";
import Loading from './Loading.jsx';

export default function ModalAdjustCashRegister({ closeModal, isInsert, refresh }) {
    const [showSuccMsg, setShowSuccMsg] = useState(false);
    const [showAdjust, setShowAdjust] = useState(true);
    const [successMsg, setSuccessMsg] = useState("");
    const [infoMsg, setInfoMsg] = useState("");
    const [amount, setAmount] = useState(0);
    const [description, setDescription] = useState("/");
    const [error, setError] = useState("");
    const [showError, setShowError] = useState(true);
    const [loading, setLoading] = useState(false);

    const insert = (amount, description) => {
        setLoading(true);
        axios.put(`http://localhost:3000/cashRegister/insert`, { amount, description })
            .then(response => {
                setSuccessMsg("Успешен внес на пари");
                setInfoMsg(`Додадени се ${Number(amount).toLocaleString("de-DE")} во каса!`);
                setShowSuccMsg(true);
                refresh();
            })
            .catch(error => console.error('Error inserting money into cash register:', error))
            .finally(() => setLoading(false));
    };

    const remove = (amount, description) => {
        setLoading(true);
        axios.put(`http://localhost:3000/cashRegister/remove`, { amount, description })
            .then(response => {
                setSuccessMsg("Успешен излез на пари");
                setInfoMsg(`Земени се ${Number(amount).toLocaleString("de-DE")} од каса!`);
                setShowSuccMsg(true);
                refresh();
            })
            .catch(error => console.error('Error removing money from cash register:', error))
            .finally(() => setLoading(false));
    };

    useEffect(() => {
        if (!showAdjust)
            isInsert ? insert(amount, description) : remove(amount, description);
    }, [showAdjust]);

    return ReactDom.createPortal(
        <>
            <Overlay />
            <Wrapper>
                <X size={32} onClick={closeModal} />
                {
                    showAdjust && (
                        <>
                            <CircleHelp size={120} />
                            <h1>Внеси сума за {isInsert ? 'влез во' : 'излез од'} каса</h1>
                            <form onSubmit={e => {
                                e.preventDefault()
                                if (amount.length > 0) {
                                    isNaN(amount) ?
                                        setError('Внеси валиден број!') && setError(true) :
                                        setShowAdjust(false) && setShowError(false);
                                } else {
                                    setShowError(true);
                                    setError('Внеси валидна сума!');
                                }
                            }}>
                                <span>
                                    <StyledInput onChange={e => setAmount(e.target.value)} placeholder="Внеси сума" required />
                                    <StyledInput onChange={e => setDescription(e.target.value)} placeholder="Внеси причина" required />
                                </span>
                                <Button type="submit">
                                    <CheckCheck size={28} /> Потврди
                                </Button>
                            </form>
                            {showError && <ErrorText>{error}</ErrorText>}
                        </>
                    )
                }
                {
                    loading ? <Loading /> :
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
    );
}

const Overlay = styled.div`
    position: fixed;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    background: rgba(0,0,0, .7);
    z-index: 1000;
`;

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
`;

const StyledInput = styled.input`
    border: none;
    border-radius: .2rem;
    font-size: 1rem;
    padding: .5rem;
    box-shadow: 0 0 4px rgba(0,0,0,0.2);
    height: fit-content;
`;

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
`;

const ErrorText = styled.span`
    color: red;
    font-style: italic;
    font-size: 1.2rem;
    font-weight: 400;
`;