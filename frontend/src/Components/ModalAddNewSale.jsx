import styled from "styled-components";
import ReactDom from "react-dom";
import { X, CopyPlus, CirclePlus, BookmarkPlus, CheckCheck, User, Tag } from 'lucide-react'
import {useState} from "react";
import axios from "axios";

export default function ModalAddNewSale({ closeModal }) {

    return ReactDom.createPortal(
        <>
            <Overlay />
            <Wrapper >
                <Header >
                    <div >
                        <BookmarkPlus size={32} />
                        <h1>Add New Sale</h1>
                    </div>
                    <ButtonClose onClick={closeModal}>
                        <X size={32} />
                    </ButtonClose>
                </Header>
                <Form method="post" action="/sales/insertSale" >
                    <div>
                        <ClientInputs >
                            <span><User size={20} /> Enter Client Details</span>
                            <StyledInput placeholder="Client name" name="name" required />
                            <StyledInput placeholder="Client embg" name="embg" required />
                            <StyledInput placeholder="Client telephone" name="telephone" required />
                            <StyledInput placeholder="Client city" name="city" required />
                        </ClientInputs>
                        <SaleInputs>
                            <span><Tag size={20}/> Enter Sale Details</span> <p></p>
                            <div>
                                <StyledInput placeholder="Price Bought" name="price_bought" required />
                                <StyledInput placeholder="Item description" name="description" required />
                            </div>
                        </SaleInputs>
                    </div>
                    <Button type="submit">
                        <CheckCheck size={28} /> Confirm
                    </Button>
                </Form>
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
`

const Heading = styled.div`
    
`

const Header = styled.div`
    display: flex;
    justify-content: space-between;
    
    div {
        display: flex;
        align-items: center;
        gap: .4rem;
    }
`

const ButtonClose = styled.button`
    
    svg {
        transition: all 400ms ease-in-out;
    }
    svg:hover {
        transform: rotate(90deg);
    }
`

const Form = styled.form`
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2rem;
    
    &>div {
        display: flex;
        gap: 2rem;
        align-items: flex-start;
    }
    
    span {
        display: flex;
        align-items: center;
        gap: .4rem;
        font-weight: 500;
    }
`

const ClientInputs = styled.div`
    display: flex;
    flex-direction: column;
    gap: .8rem;
`

const SaleInputs = styled.div`
    display: flex;
    flex-direction: column;
    gap: .4rem;

    div {
        display: flex;
        flex-direction: column;
        gap: .8rem;
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