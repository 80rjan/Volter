import styled from "styled-components";
import axios from "axios";
import {useEffect, useState} from "react";
import { Euro, RotateCcw, X, Ellipsis } from 'lucide-react'
import ModalShowMessagePawn from "./ModalShowMessagePawn.jsx";
import ModalReadMorePawn from "./ModalReadMorePawn.jsx";
import Loading from "./Loading.jsx";

export default function Pawn({ pawn, refresh, isOdd }) {
    const [modalSuccessMsg, setModalSuccessMsg] = useState(false);
    const [modalReadMore, setModalReadMore] = useState(false);
    const [successMsg, setSuccessMsg] = useState("");
    const [infoMsg, setInfoMsg] = useState("");
    const [pawnInfo, setPawnInfo] = useState(null);
    const [loading, setLoading] = useState(false);


    useEffect(() => {
        if (!modalSuccessMsg)
            refresh();
    }, [modalSuccessMsg])

    const continuePawn = (id, category) => {
        setLoading(true);
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
            .then(response => {
                setSuccessMsg("Successfully continued pawn")
                setInfoMsg(`Added ${response.data.profit.toLocaleString("de-DE")} into cash register!`)
                setModalSuccessMsg(true);
            })
            .catch(error => console.error('Error continuing pawn:', error))
            .finally(() => setLoading(false));
    }

    const closePawn = (id, category) => {
        setLoading(true);
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
            .then(response => {
                setSuccessMsg("Successfully closed pawn")
                setInfoMsg(`Added ${response.data.moneyIntoCashReg.toLocaleString("de-DE")} into cash register!`)
                setModalSuccessMsg(true);
            } )
            .catch(error => console.error('Error continuing pawn:', error))
            .finally(() => setLoading(false));
    }

    const movePawnToSale = (id, category) => {
        setLoading(true);
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
        axios.put(`http://localhost:3000/changePawnToSale`, { id, tableName })
            .then(() => {
                setSuccessMsg("Successfully moved pawn to sale")
                // setInfoMsg(`Added ${response.data.profit.toLocaleString("de-DE")} into cash register!`)
                setModalSuccessMsg(true);
            })
            .catch(error => console.error('Error continuing pawn:', error))
            .finally(() => setLoading(false));
    }

    useEffect(() =>{
        if (pawnInfo != null)
        setModalReadMore(true);
    }, [pawnInfo])

    const fetchPawn = (clientId, category, pawnId) => {
        setLoading(true);
        axios.get(`http://localhost:3000/getPawn?clientId=${clientId}&category=${category}&pawnId=${pawnId}`)
            .then(res => {
                setPawnInfo(res.data.pawnInfo)
            })
            .catch(error => {
                console.error('Error fetching pawn:', error);
            })
            .finally(() => setLoading(false));
    }


    return (
        <Wrapper style={isOdd ? {background: "#f0f0f0"} : {background: "#ffffff"}}>
            <Text>{pawn["Client Id"]}</Text>
            <Text>{pawn.Name}</Text>
            <Text>{pawn.Category}</Text>
            <Text>{pawn.About}</Text>
            <Text>{Number(pawn["Item Cost"]).toLocaleString("de-DE")}</Text>
            <Text className="bold" >{Number(pawn.Provision).toLocaleString("de-DE")}</Text>
            <Text>{pawn["Days Left"]}</Text>
            <Text>{pawn["Valid Until"].substring(0, 10)}</Text>
            {
                loading ? <Loading width={30} height={30} /> :
                    <>
                        <ButtonWrapper>
                            <X size={22} onClick={() => closePawn(pawn.Id, pawn.Category)} />
                            <RotateCcw size={22} color="var(--cta-color)" onClick={() => continuePawn(pawn.Id, pawn.Category)} />
                            <Euro size={22} color="var(--green)" onClick={() => movePawnToSale(pawn.Id, pawn.Category)} />
                        </ButtonWrapper>
                        <Ellipsis size={28} color="#888"
                                  onClick={() => fetchPawn(pawn["Client Id"], pawn.Category, pawn.Id)}
                        />
                    </>
            }


            {modalSuccessMsg &&
                <ModalShowMessagePawn
                    closeModal={() => setModalSuccessMsg(false)}
                    successMsg={successMsg}
                    infoMsg={infoMsg}
                />
            }

            {modalReadMore &&
                <ModalReadMorePawn
                    category={pawn.Category}
                    pawnInfo={pawnInfo}
                    closeModal={() => setModalReadMore(false)}
                    closePawn={() => closePawn(pawn.Id, pawn.Category)}
                    continuePawn={() => continuePawn(pawn.Id, pawn.Category)}
                    movePawnToSale={() => movePawnToSale(pawn.Id, pawn.Category)}
                />
            }
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
        transition: all 200ms ease-in-out;
    }
    svg:hover {
        scale: 1.2;
    }
`;

const Text = styled.p`
    font-weight: 500;
    font-size: .8rem;
    
    &.bold {
        font-weight: bold;
    }
`

const ButtonWrapper = styled.div`
    display: flex;
    gap: .5rem;
    
`