import {useEffect, useState} from "react";
import axios from "axios";
import styled from "styled-components";
import Pawn from "../Components/Pawn.jsx";

export default function Pawns() {
    const [allPawns, setAllPawns] = useState([]);

    const fetchPawns = () => {
        axios.get('http://localhost:3000')
            .then(res => setAllPawns(res.data))
            .catch(error => console.error('Error fetching all pawns:', error));
    }

    useEffect(() => {
        fetchPawns();
    }, []);

    return (
        <PawnsWrapper >
            {
                allPawns.map((pawn, index) => (
                    <Pawn pawn={pawn} key={index} refresh={fetchPawns}/>
                ))
            }
        </PawnsWrapper>
    )
}

const PawnsWrapper = styled.div`
    width: 100%;
`