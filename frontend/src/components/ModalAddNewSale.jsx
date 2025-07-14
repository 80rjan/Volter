import styled from "styled-components";
import ReactDom from "react-dom";
import { X, BookmarkPlus, CheckCheck } from 'lucide-react';
import {useState} from "react";
import axios from "axios";
import Loading from "./Loading.jsx";

export default function ModalAddNewSale({ closeModal, refresh }) {
    const [formData, setFormData] = useState({
        price_bought: 0,
        description: ''
    });
    const [loading, setLoading] = useState(false);

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
        axios.post('http://localhost:3000/sales/insertSale', formData)
            .then(() => {
                refresh();
                closeModal();
            })
            .catch(error => {
                console.error('Error adding sale:', error);
            })
            .finally(() => setLoading(false))
    };

    return ReactDom.createPortal(
        <>
            <Overlay />
                <Wrapper>
                    {
                        loading ? <Loading /> :
                            <>
                                <Header>
                                    <div>
                                        <BookmarkPlus size={32} />
                                        <h1>Внеси Нова Продажба</h1>
                                    </div>
                                    <ButtonClose onClick={closeModal}>
                                        <X size={32} />
                                    </ButtonClose>
                                </Header>
                                <Form onSubmit={handleSubmit}>
                                    <SaleInputs>
                                        <div>
                                            <p>Вредност на предметот</p>
                                            <StyledInput name="price_bought" onChange={handleInputChange}
                                                         type="number"
                                                         required/>
                                        </div>
                                        <div>
                                            <p>Опис</p>
                                            <StyledInput name="description" onChange={handleInputChange} required/>
                                        </div>
                                    </SaleInputs>
                                    <Button type="submit">
                                        <CheckCheck size={28}/> Потврди
                                    </Button>
                                </Form>
                            </>
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
    
    span {
        display: flex;
        align-items: center;
        gap: .4rem;
        font-weight: 500;
        margin-bottom: .4rem;
    }
`;

const SaleInputs = styled.div`
    display: flex;
    gap: 2rem;

    & > div {
        display: flex;
        flex-direction: column;
        gap: .2rem;

        & > p {
            margin-left: -0.4rem;
            color: #666;
        }
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
