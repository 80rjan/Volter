import styled from "styled-components";
import axios from "axios";
import {useEffect, useState} from "react";
import { Euro, RotateCcw, X, Ellipsis } from 'lucide-react'
import ModalShowMessagePawn from "./ModalShowMessagePawn.jsx";
import ModalReadMorePawn from "./ModalReadMorePawn.jsx";
import Loading from "./Loading.jsx";
import ModalActions from "./ModalActions.jsx";

export default function Pawn({ pawn, refresh, isOdd, refreshCashReg }) {
    const [modalSuccessMsg, setModalSuccessMsg] = useState(false);
    const [modalReadMore, setModalReadMore] = useState(false);
    const [successMsg, setSuccessMsg] = useState("");
    const [infoMsg, setInfoMsg] = useState("");
    const [pawnInfo, setPawnInfo] = useState(null);
    const [loading, setLoading] = useState(false);
    const [modalClosePawn, setModalClosePawn] = useState(false);
    const [modalContinuePawn, setModalContinuePawn] = useState(false);

    const getCat = {
        "Electronics": "Електроника",
        "Watch": "Часовници",
        "Vehicle": "Возила",
        "Gold": "Злато",
        "Other": "Останато"
    }

    const continuePawn = (id, category, provision, description, carryOverDays) => {
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
        axios.put(`http://localhost:3000/continuePawn`, { id, tableName, provision, description, carryOverDays })
            .then(() => {
                setSuccessMsg("Успешно продолжен залог")
                setInfoMsg(`Додадени се ${provision.toLocaleString("de-DE")} во каса!`)
                setModalContinuePawn(false)
                setModalSuccessMsg(true);
            })
            .catch(error => {
                console.error('Error continuing pawn:', error)
            })
            .finally(() => setLoading(false));
    }

    const closePawn = (id, category, priceClosed, description) => {
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
        axios.put(`http://localhost:3000/closePawn`, { id, tableName, priceClosed, description })
            .then(() => {
                setSuccessMsg("Успешно затворен залог")
                setInfoMsg(`Додадени се ${priceClosed.toLocaleString("de-DE")} во каса!`)
                setModalClosePawn(false)
                setModalSuccessMsg(true);
            })
            .catch(error => {
                console.error('Error closing pawn:', error)
            })
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
                setSuccessMsg("Успешно пренесен залог во продажба")
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
                setPawnInfo({...res.data.pawnInfo, "Days Left": pawn["Days Left"]})
            })
            .catch(error => {
                console.error('Error fetching pawn:', error);
            })
            .finally(() => setLoading(false));
    }


    return (
        <Wrapper style={isOdd ? {background: "#f0f0f0"} : {background: "#ffffff"}}>
            <Text>{pawn["Client Id"]}</Text>
            <Text className="bold">{pawn.Name}</Text>
            <Text>{getCat[pawn.Category]}</Text>
            <Text>{pawn.About}</Text>
            <Text>{Number(pawn["Item Cost"]).toLocaleString("de-DE")}</Text>
            <Text className="bold color" >{Number(pawn.Provision).toLocaleString("de-DE")}</Text>
            <Text className={
                pawn["Days Left"] < 0 ? "red" : pawn["Days Left"] < 5 ? "orange" : "green"
            }>{pawn["Days Left"]}</Text>
            <Text>{pawn["Valid Until"].substring(0, 10)}</Text>
            {
                loading ? <Loading width={30} height={30} /> :
                    <>
                        <ButtonWrapper>
                            <X size={22} onClick={() => setModalClosePawn(true)} />
                            <RotateCcw size={22} color="var(--cta-color)" onClick={() => setModalContinuePawn(true)} />
                            <Euro size={22} color="var(--green)" onClick={() => movePawnToSale(pawn.Id, pawn.Category)} />
                        </ButtonWrapper>
                        <Ellipsis size={28} color="#888"
                                  onClick={() => fetchPawn(pawn["Client Id"], pawn.Category, pawn.Id)}
                        />
                    </>
            }

            {modalClosePawn &&
                <ModalActions
                    pawnAction={"close"}
                    action={closePawn}
                    id={pawn.Id}
                    category={pawn.Category}
                    successMsg="Успешно затворен залог!"
                    closeModal={() => setModalClosePawn(false)}
                    priceBought={Number(pawn["Item Cost"])}
                    provision={Number(pawn.Provision)}
                    dailyProvision={Math.abs(Math.round(pawn.Provision) / Number(pawn["Total Days"]))}
                    suggestedPrice={Number(pawn["Item Cost"]) + Number(pawn.Provision)}
                    daysLeft={Number(pawn["Days Left"])}
                    title={"Со кој износ е затворен залогот?"}
                    loading={loading}
                />
            }

            {modalContinuePawn &&
                <ModalActions
                    pawnAction={"continue"}
                    action={continuePawn}
                    id={pawn.Id}
                    category={pawn.Category}
                    successMsg="Успешно продолжен залог!"
                    closeModal={() => setModalContinuePawn(false)}
                    priceBought={Number(pawn["Item Cost"])}
                    provision={Number(pawn.Provision)}
                    dailyProvision={Math.abs(Math.round(pawn.Provision) / Number(pawn["Total Days"]))}
                    suggestedPrice={Number(pawn.Provision)}
                    daysLeft={Number(pawn["Days Left"])}
                    title={"Со кој износ е продолжен залогот?"}
                    loading={loading}
                />
            }

            {modalSuccessMsg &&
                <ModalShowMessagePawn
                    closeModal={() => {
                        setModalSuccessMsg(false)
                        refresh()
                    }}
                    successMsg={successMsg}
                    infoMsg={infoMsg}
                    clientName={pawn.Name}
                />
            }

            {modalReadMore &&
                <ModalReadMorePawn
                    category={pawn.Category}
                    pawnInfo={pawnInfo}
                    closeModal={() => setModalReadMore(false)}
                    closePawn={() => setModalClosePawn(true)}
                    continuePawn={() => setModalContinuePawn(true)}
                    movePawnToSale={() => movePawnToSale(pawn.Id, pawn.Category)}
                    oldPawn={pawn}
                    refreshCashReg={refreshCashReg}
                />
            }
        </Wrapper>
    )
}


const Wrapper = styled.div`
    display: grid;
    place-items: center;
    grid-template-columns: 3rem 1.5fr 1fr 2fr repeat(4, 1fr) 1.5fr .5fr;
    padding: .2rem;
    border-bottom: rgba(0,0,0,0.2) 2px solid;
    //transition: all 200ms ease-in-out;
    //z-index: 1;
    //
    //&:hover {
    //    padding: 1rem;
    //    box-shadow: 0 0 8px rgba(0,0,0,0.6);
    //    z-index: 10;
    //    scale: 1.001;
    //    //border: none;
    //}
    svg {
        cursor: pointer;
        transition: all 300ms ease-in-out;
    }
    svg:hover {
        scale: 1.2;
    }
`;

const Text = styled.p`
    font-weight: 500;
    font-size: .7rem;
    
    &.bold {
        font-weight: bold;
    }
    &.color {
        font-style: italic;
    }
    &.red {
        color: red;
    }
    &.orange {
        color: orangered;
    }
    &.green {
        color: var(--green);
    }
    
`

const ButtonWrapper = styled.div`
    display: flex;
    gap: .5rem;
    
`