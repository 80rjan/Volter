import styled from "styled-components";
import axios from "axios";
import {useEffect, useState} from "react";

export default function Pawn({ pawn, refresh }) {

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
            .then(response => refresh())
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
            .then(response => refresh())
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
            .then(response => refresh())
            .catch(error => console.error('Error continuing pawn:', error));
    }



    return (
        <Wrapper>
            <h5>{pawn["Client Id"]}</h5>
            <h5>{pawn.Name}</h5>
            <h5>{pawn.Category}</h5>
            <h5>{pawn.About}</h5>
            <h5>{pawn["Item Cost"]}</h5>
            <h5>{pawn.Provision}</h5>
            <h5>{pawn["Days Left"]}</h5>
            <h5>{pawn["Valid Until"].substring(0, 10)}</h5>
            <button onClick={() => continuePawn(pawn.Id, pawn.Category)}>Continue Pawn</button>
            <button onClick={() => closePawn(pawn.Id, pawn.Category)}>Close Pawn</button>
            <button onClick={() => movePawnToSale(pawn.Id, pawn.Category)}>Move Item to Sale</button>
        </Wrapper>
    )
}


const Wrapper = styled.div`
    display: grid;
    grid-template-columns: repeat(11, 1fr);
    
    
    h5 {
        margin: 0;
        padding: 0;
    }
`;