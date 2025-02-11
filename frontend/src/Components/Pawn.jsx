import styled from "styled-components";
import axios from "axios";
import {useEffect, useState} from "react";
import { Euro, RotateCcw, X, Ellipsis } from 'lucide-react'

export default function Pawn({ pawn, refresh, isOdd }) {

    const continuePawn = (id, category) => {
        //Find the name of the table based on the category
        const tableName = {
            'Electronics': 'electronics_pawn',
            'Gold': 'gold_pawn',
            'Vehicle': 'vehicle_pawn',
            'Watch': 'watch_pawn',
            'Other': 'other_pawn'
        }[category];

        if (!tableName) return console.error("Invalid category:", category);

        //Put http which sends the id of the pawn and the table name in which the pawn date is updated
        axios.put(`http://localhost:3000/continuePawn`, { id, tableName })
            .then(response => refresh() )
            .catch(error => console.error('Error continuing pawn:', error));
    }

    const closePawn = (id, category) => {
        //Find the name of the table based on the category
        const tableName = {
            'Electronics': 'electronics_pawn',
            'Gold': 'gold_pawn',
            'Vehicle': 'vehicle_pawn',
            'Watch': 'watch_pawn',
            'Other': 'other_pawn'
        }[category];

        if (!tableName) return console.error("Invalid category:", category);

        //Put http which sends the id of the pawn and the table name in which the pawn is closed
        axios.put(`http://localhost:3000/closePawn`, { id, tableName })
            .then(response => refresh() )
            .catch(error => console.error('Error continuing pawn:', error));
    }

    const movePawnToSale = (id, category) => {
        //Find the name of the pawn table based on the category
        const tableName = {
            'Electronics': 'electronics_pawn',
            'Gold': 'gold_pawn',
            'Vehicle': 'vehicle_pawn',
            'Watch': 'watch_pawn',
            'Other': 'other_pawn'
        }[category];

        if (!tableName) return console.error("Invalid category:", category);

        //Put http which sends the id of the pawn and the table name in which the pawn is closed and a new product goes for sale
        axios.put(`http://localhost:3000/addSale`, { id, tableName })
            .then(response => refresh() )
            .catch(error => console.error('Error continuing pawn:', error));
    }


    return (
        <Wrapper style={isOdd ? {background: "#f0f0f0"} : {background: "#ffffff"}}>
            <Text>{pawn["Client Id"]}</Text>
            <Text>{pawn.Name}</Text>
            <Text>{pawn.Category}</Text>
            <Text>{pawn.About}</Text>
            <Text className="bold" >{pawn["Item Cost"].toLocaleString("de-DE")}</Text>
            <Text className="bold" >{pawn.Provision.toLocaleString("de-DE")}</Text>
            <Text>{pawn["Days Left"]}</Text>
            <Text>{pawn["Valid Until"].substring(0, 10)}</Text>
            <ButtonWrapper>
                <X size={22} onClick={() => closePawn(pawn.Id, pawn.Category)} />
                <RotateCcw size={22} color="var(--cta-color)" onClick={() => continuePawn(pawn.Id, pawn.Category)} />
                <Euro size={22} color="var(--green)" onClick={() => movePawnToSale(pawn.Id, pawn.Category)} />
            </ButtonWrapper>
            <Ellipsis size={28} color="#888" />
        </Wrapper>
    )
}


const Wrapper = styled.div`
    display: grid;
    place-items: center;
    grid-template-columns: 2rem 1fr 1fr 2fr repeat(4, 1fr) 1.5fr .5fr;
    padding: .5rem;
    border-bottom: rgba(0,0,0,0.2) 2px solid;

    svg {
        cursor: pointer;
        transition: scale 100ms step-end;
    }
    svg:hover {
        scale: 1.1;
    }
`;

const Text = styled.p`
    font-weight: 500;
    font-size: .8rem;
    
    &.bold {
        font-weight: bold;
        font-style: italic;
    }
`

const ButtonWrapper = styled.div`
    display: flex;
    gap: .5rem;
    
`