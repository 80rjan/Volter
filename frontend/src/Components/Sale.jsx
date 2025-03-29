import styled from "styled-components";
import axios from "axios";
import {useEffect, useState} from "react";
import { Euro, RotateCcw, X, Ellipsis } from 'lucide-react'
import ModalReadMoreSale from "./ModalReadMoreSale.jsx";
import Loading from "./Loading.jsx";
import ModalActions from "./ModalActions.jsx";

export default function Sale({ sale, refresh, isOdd }) {
    const [modalSellItem, setModalSellItem] = useState(false);
    const [modalReadMore, setModalReadMore] = useState(false);
    const [saleInfo, setSaleInfo] = useState(null);
    const [loading, setLoading] = useState(false);
    console.log(sale)

    // useEffect(() => {
    //     if (!modalSellItem)
    //         refresh();
    // }, [modalSellItem])

    useEffect(() =>{
        if (saleInfo != null)
            setModalReadMore(true);
    }, [saleInfo])

    const fetchSale = (clientId, saleId) => {
        setLoading(true);
        axios.get(`http://localhost:3000/sales/getSale?clientId=${clientId}&saleId=${saleId}`)
            .then(res => {
                setSaleInfo(res.data.saleInfo)
            })
            .catch(error => {
                console.error('Error fetching sale:', error);
            })
            .finally(() => setLoading(false))
    }

    const sellItem = (id, priceSold) => {
        //Put http which sends the id of the sale to sell item and close sale
        axios.put(`http://localhost:3000/sales/sellItem`, { id, priceSold })
            .then()
            .catch(error => console.error('Error selling item:', error))
            .finally(() => setLoading(false));
    }


    return (
        <Wrapper style={isOdd ? {background: "#f0f0f0"} : {background: "#ffffff"}}>
            <Text>{sale["Client Id"]}</Text>
            <Text>{sale.Name}</Text>
            <Text>{sale.About}</Text>
            <Text className="bold color">{Number(sale["Item Cost"]).toLocaleString("de-DE")}</Text>
            <Text>{sale["Date Bought"].substring(0, 10)}</Text>
            {
                loading ? <Loading width={30} height={30} /> :
                    <>
                        <ButtonWrapper>
                            <Euro size={22} color="var(--green)" onClick={() => setModalSellItem(true)} />
                        </ButtonWrapper>
                        <Ellipsis size={28} color="#888"
                                  onClick={() => fetchSale(sale["Client Id"], sale.Id)}
                        />
                    </>
            }

            {modalSellItem &&
                <ModalActions
                    action={sellItem}
                    id={sale.Id}
                    category={"sale"}
                    successMsg="Успешно продадено!"
                    closeModal={() => setModalSellItem(false)}
                    priceBought={Number(sale["Item Cost"])}
                    provision={undefined}
                    dailyProvision={undefined}
                    suggestedPrice={undefined}
                    daysLeft={undefined}
                    title={"По која цена е продаден предметот?"}
                    refresh={refresh}
                />
            }

            {modalReadMore &&
                <ModalReadMoreSale
                    saleInfo={saleInfo}
                    closeModal={() => setModalReadMore(false)}
                    sellItem={() => setModalSellItem(true)}
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
    transition: all 200ms ease-in-out;
    z-index: 1;

    &:hover {
        padding: 1rem;
        box-shadow: 0 0 8px rgba(0,0,0,0.6);
        z-index: 10;
        scale: 1.001;
        //border: none;
    }

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
    &.color {
        font-style: italic;
    }
`

const ButtonWrapper = styled.div`
    display: flex;
    gap: .5rem;

`