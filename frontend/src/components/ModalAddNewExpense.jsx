import styled from "styled-components";
import ReactDom from "react-dom";
import { X, BookmarkPlus, ClipboardPlus, CheckCheck, User, Tag } from 'lucide-react';
import {useEffect, useRef, useState} from "react";
import { Autocomplete, TextField } from '@mui/material';
import axios from "axios";
import Loading from "./Loading.jsx";

export default function ModalAddNewExpense({ closeModal, refresh }) {
    const [formData, setFormData] = useState({
        year: 0,
        month: 0,
        rent: 0,
        salaries: 0,
        bills: 0,
        other: 0,
        description: ''
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        setLoading(true);

        if (formData.year < 2000 || formData.month < 1 || formData.month > 12) {
            setError("Внеси валидна година и месец");
            setLoading(false);
            return;
        }

        axios.post('http://localhost:3000/expenses/insert', formData)
            .then(res => {
                if (res.data.message !== 'Успешно внесен расход')
                    setError(res.data.message);
                else {
                    refresh();
                    closeModal();
                }
            })
            .catch(error => {
                console.error('Error adding expense:', error);
            })
            .finally(() => setLoading(false))
    };

    return ReactDom.createPortal(
        <>
            <Overlay />
            <Wrapper>
                <Header>
                    <div>
                        <ClipboardPlus size={32} />
                        <h1>Внеси Нов Расход</h1>
                    </div>
                    <ButtonClose onClick={closeModal}>
                        <X size={32} />
                    </ButtonClose>
                </Header>
                <Form onSubmit={handleSubmit}>
                    <div>
                        <ExpenseInputs>
                            <div>
                                <p>Година</p>
                                <StyledInput name="year" onChange={handleInputChange}
                                             type="number"
                                             required/>
                            </div>
                            <div>
                                <p>Месец</p>
                                <StyledInput name="month" onChange={handleInputChange}
                                             type="number"
                                             required/>
                            </div>
                            <div>
                                <p>Кирија</p>
                                <StyledInput name="rent" onChange={handleInputChange}
                                             type="number"
                                />
                            </div>
                            <div>
                                <p>Плати</p>
                                <StyledInput name="salaries" onChange={handleInputChange}
                                             type="number"
                                />
                            </div>
                            <div>
                                <p>Сметки</p>
                                <StyledInput name="bills" onChange={handleInputChange}
                                             type="number"
                                />
                            </div>
                            <div>
                                <p>Друго</p>
                                <StyledInput name="other" onChange={handleInputChange}
                                             type="number"
                                />
                            </div>
                            <div>
                                <p>Опис</p>
                                <StyledInput name="description" onChange={handleInputChange}
                                />
                            </div>
                        </ExpenseInputs>
                    </div>
                    <div style={{display: "flex", gap: "1rem", alignItems: "center"}}>
                        <Button type="submit" disabled={loading}>
                            <CheckCheck size={28}/> Потврди
                        </Button>
                        {
                            loading && <Loading width={40} height={40} />
                        }
                    </div>
                    <Error>{error}</Error>
                </Form>
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
    background: rgba(0, 0, 0, .7);
    z-index: 1000;
`;

const Wrapper = styled.div`
    display: flex;
    flex-direction: column;
    gap: 2rem;
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background: #eee;
    z-index: 1000;
    padding: 2rem;
    border-radius: 8px;
    min-width: fit-content;
    max-width: 90%;
`;

const Header = styled.div`
    display: flex;
    justify-content: space-between;
    div {
        display: flex;
        align-items: center;
        gap: .4rem;
    }
`;

const ButtonClose = styled.button`
    svg {
        transition: all 400ms ease-in-out;
    }
    svg:hover {
        transform: rotate(90deg);
    }
`;

const Form = styled.form`
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2rem;
    
    & > div:first-child {
        display: flex;
        flex-direction: column;
        gap: 2rem;
        align-items: center;
    }
    span {
        display: flex;
        align-items: center;
        gap: .4rem;
        font-weight: 500;
        margin-bottom: .4rem;
    }
`;

const Error = styled.p`
    color: red;
    font-style: italic;
    font-size: 1.2rem;
    font-weight: 400;
`

const ExpenseInputs = styled.div`
    display: grid;
    grid-template-rows: repeat(3, 1fr);
    grid-template-columns: repeat(2, 1fr);
    gap: 1rem 4rem;

    & > div {
        display: flex;
        flex-direction: column;
        gap: .2rem;

        & > p {
            margin-left: -0.4rem;
            color: #666;
        }
    }
    
    & > div:last-child {
        grid-column: 1 / -1;
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
`;
