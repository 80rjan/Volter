import styled from "styled-components";
import axios from "axios";
import {useEffect, useState} from "react";
import { Euro, RotateCcw, X, Ellipsis } from 'lucide-react'
import ModalShowMessage from "./ModalShowMessage.jsx";
import ModalReadMoreSale from "./ModalReadMoreSale.jsx";

export default function Sale({ sale, refresh, isOdd }) {
    const [modalSuccessMsg, setModalSuccessMsg] = useState(false);
    const [modalReadMore, setModalReadMore] = useState(false);
    const [successMsg, setSuccessMsg] = useState("");
    const [infoMsg, setInfoMsg] = useState("");
    const [saleInfo, setSaleInfo] = useState(null);
    console.log(sale);

    useEffect(() => {
        if (!modalSuccessMsg)
            refresh();
    }, [modalSuccessMsg])

    const sellItem = (id, category) => {

        //Put http which sends the id of the sale to sell item and close sale
        axios.put(`http://localhost:3000/sales/saleItem`, { id })
            .then(response => {
                setSuccessMsg("Successfully sold item")
                setInfoMsg(`Added ${response.data.moneyIntoCashReg.toLocaleString("de-DE")} into cash register!`)
                setModalSuccessMsg(true);
            } )
            .catch(error => console.error('Error continuing pawn:', error));
    }

    useEffect(() =>{
        if (saleInfo != null)
            setModalReadMore(true);
    }, [saleInfo])

    const fetchSale = (clientId, saleId) => {
        axios.get(`http://localhost:3000/sales/getSale?clientId=${clientId}&saleId=${saleId}`)
            .then(res => {
                console.log(res.data.saleInfo)
                setSaleInfo(res.data.saleInfo)
            })
            .catch(error => {
                console.error('Error fetching sale:', error);
            });
    }


    return (
        <Wrapper style={isOdd ? {background: "#f0f0f0"} : {background: "#ffffff"}}>
            <Text>{sale["Client Id"]}</Text>
            <Text>{sale.Name}</Text>
            <Text>{sale.About}</Text>
            <Text className="bold">{Number(sale["Item Cost"]).toLocaleString("de-DE")}</Text>
            <Text>{sale["Date Bought"].substring(0, 10)}</Text>
            <ButtonWrapper>
                <Euro size={22} color="var(--green)" onClick={() => sellItem(sale.Id)} />
            </ButtonWrapper>
            <Ellipsis size={28} color="#888"
                      onClick={() => fetchSale(sale["Client Id"], sale.Id)}
            />

            {modalSuccessMsg &&
                <ModalShowMessage
                    closeModal={() => setModalSuccessMsg(false)}
                    successMsg={successMsg}
                    infoMsg={infoMsg}
                />
            }

            {modalReadMore &&
                <ModalReadMoreSale
                    saleInfo={saleInfo}
                    closeModal={() => setModalReadMore(false)}
                    sellItem={() => sellItem(sale.id)}
                />
            }
        </Wrapper>
    )
}


const Wrapper = styled.div`
    display: grid;
    place-items: center;
    grid-template-columns: 2rem 1fr 2fr 1fr 1fr 1.5fr .5fr;
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