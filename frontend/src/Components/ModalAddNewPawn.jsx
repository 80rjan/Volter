import styled from "styled-components";
import ReactDom from "react-dom";
import { X, CircleX, CopyPlus, CheckCheck, User, Database } from 'lucide-react'
import {useState} from "react";
import axios from "axios";

export default function ModalAddNewPawn({ closeModal }) {
    const [category, setCategory] = useState("electronics_pawn")

    const renderCategoryInputs = () => {
        switch (category) {
            case "electronics_pawn": return <ElectronicsInputs />
            case "gold_pawn": return <GoldInputs />
            case "vehicle_pawn": return <VehicleInputs />
            case "watch_pawn": return <WatchInputs />
            case "other_pawn": return <OtherInputs />
        }
        console.log(category)
    }

    return ReactDom.createPortal(
        <>
            <Overlay />
            <Wrapper >
                <Header >
                    <div >
                        <CopyPlus size={32} />
                        <h1>Add New Pawn</h1>
                    </div>
                    <ButtonClose onClick={closeModal}>
                        <X size={32} />
                    </ButtonClose>
                </Header>
                <Form method="post" action="/insertPawn" >
                    <div>
                        <ClientInputs >
                            <span><User size={20} /> Enter Client Details</span>
                            <StyledInput placeholder="Client name" name="name" required />
                            <StyledInput placeholder="Client embg" name="embg" required />
                            <StyledInput placeholder="Client telephone" name="telephone" required />
                            <StyledInput placeholder="Client city" name="city" required />
                        </ClientInputs>
                        <PawnInputs>
                            <span><Database size={20}/> Enter Pawn Details</span> <p></p>
                            <select name="category" onChange={(event) => setCategory(event.target.value)}>
                                <option value="electronics_pawn">Electronics</option>
                                <option value="gold_pawn">Gold</option>
                                <option value="vehicle_pawn">Vehicle</option>
                                <option value="watch_pawn">Watch</option>
                                <option value="other_pawn">Other</option>
                            </select>
                            {renderCategoryInputs()}
                        </PawnInputs>
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

function ElectronicsInputs() {
    return (
        <>
            <StyledInput placeholder="Item brand" name="brand" required />
            <StyledInput placeholder="Item year" name="year" required />
            <StyledInput placeholder="Price pawned" name="price_pawned" required />
            <StyledInput placeholder="Monthly provision" name="provision" required />
            <StyledInput placeholder="Total days" name="total_days" required />
            <StyledInput placeholder="Item description" name="description" required />
        </>
    )
}

function GoldInputs() {
    return (
        <>
            <StyledInput placeholder="Gold weight" name="weight" required />
            <StyledInput placeholder="Gold carats" name="carats" required />
            <StyledInput placeholder="Gold type" name="type" required />
            <StyledInput placeholder="Price per gram" name="price_per_gram" required />
            <StyledInput placeholder="Monthly provision" name="provision" required />
            <StyledInput placeholder="Total days" name="total_days" required />
            <StyledInput placeholder="Item description" name="description" required />
        </>
    )
}

function VehicleInputs() {
    return (
        <>
            <StyledInput placeholder="Vehicle brand" name="brand" required />
            <StyledInput placeholder="Vehicle model" name="model" required />
            <StyledInput placeholder="Vehicle year" name="year" required />
            <StyledInput placeholder="Price pawned" name="price_pawned" required />
            <StyledInput placeholder="Monthly provision" name="provision" required />
            <StyledInput placeholder="Total days" name="total_days" required />
            <StyledInput placeholder="Item description" name="description" required />
        </>
    )
}

function WatchInputs() {
    return (
        <>
            <StyledInput placeholder="Watch brand" name="brand" />
            <StyledInput placeholder="Watch year" name="year" />
            <StyledInput placeholder="Price pawned" name="price_pawned" />
            <StyledInput placeholder="Monthly provision" name="provision" />
            <StyledInput placeholder="Total days" name="total_days" />
            <StyledInput placeholder="Item description" name="description" />
        </>
    )
}

function OtherInputs() {
    return (
        <>
            <StyledInput placeholder="Price pawned" name="price_pawned" required />
            <StyledInput placeholder="Monthly provision" name="provision" required />
            <StyledInput placeholder="Total days" name="total_days" required />
            <StyledInput placeholder="Item description" name="description" required />
        </>
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

const PawnInputs = styled.div`
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: .8rem 2rem;

    select {
        padding: 0 .5rem;
        border-radius: .2rem;
        border: 2px solid rgba(0, 0, 0, 0.6);
        cursor: pointer;
        font-size: 1rem;
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