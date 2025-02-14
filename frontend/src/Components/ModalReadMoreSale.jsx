import ReactDom from "react-dom";
import styled from "styled-components";
import { UserRound, Euro, X, Tag } from 'lucide-react'

export default function ModalReadMoreSale({ saleInfo, closeModal, sellItem }) {
    const client = saleInfo.client;
    const sale = saleInfo.sale;
    console.log(sale)

    return ReactDom.createPortal(
        <>
            <Overlay />
            <Wrapper >
                <X size={32} onClick={closeModal} />
                <InformationWrapper >

                    <ClientWrapper >
                        <div>
                            <UserRound size={32} />
                            <p>{client.name}</p>
                        </div>
                        <div>
                            <span>
                                <p>Client Id:</p>
                                <p>{client.id}</p>
                            </span>
                            <span>
                                <p>Embg:</p>
                                <p>{client.embg}</p>
                            </span>
                            <span>
                                <p>Telephone:</p>
                                <p>{client.telephone}</p>
                            </span>
                            <span>
                                <p>City:</p>
                                <p>{client.city}</p>
                            </span>
                        </div>

                    </ClientWrapper>

                    <SaleWrapper>
                        <div>
                            <Tag size={32}/>
                            Sale
                        </div>
                        <SaleDetailsWrapper>
                            <span>
                                <p>Sale Id:</p>
                                <p>{sale.id}</p>
                            </span>
                                            <span>
                                <p>Item Cost:</p>
                                <p>{Number(sale.price_bought).toLocaleString("de-DE")}</p>
                            </span>
                                            <span>
                                <p>Description:</p>
                                <p>{sale.description}</p>
                            </span>
                            <span>
                                <p>Date Bought:</p>
                                <p>{sale.date_from.substring(0, 10)}</p>
                            </span>
                        </SaleDetailsWrapper>
                    </SaleWrapper>

                </InformationWrapper>
                <ButtonWrapper>
                    <button onClick={() => {
                        sellItem();
                        closeModal();
                    }}><Euro size={32}/> Sell Item </button>
                </ButtonWrapper>

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
    background: rgba(0, 0, 0, .7);
    z-index: 1000;
`

const Wrapper = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background: #eee;
    z-index: 1000;
    padding: 1rem 2rem 2rem 2rem;
    border-radius: 8px;
    min-width: fit-content;
    max-width: 90%;

    & > svg {
        margin-left: auto;
        transition: all 400ms ease-in-out;
        cursor: pointer;
    }

    & > svg:hover {
        transform: rotate(90deg);
    }
`

const InformationWrapper = styled.div`
    display: flex;
    //flex-direction: column;
    gap: 4rem;
`

const ClientWrapper = styled.div`
    display: flex;
    flex-direction: column;
    gap: .4rem;

    & > div {
        display: flex;
        flex-direction: column;
        align-items: flex-start;
        gap: .4rem;
    }
    & > div:first-child {
        flex-direction: row;
        align-items: center;
    }
    & > div > p {
        font-size: 1.6rem;
        font-weight: 600;
        text-wrap: wrap;
    }
    

    span {
        display: flex;
        flex-direction: column;
        gap: 0;
        font-weight: 500;
    }

    span > p:first-child {
        font-weight: 400;
        color: #666;
        margin-left: -.4rem;
    }
`

const SaleWrapper = styled.div`
    display: flex;
    flex-direction: column;
    gap: .4rem;

    div:first-child {
        display: flex;
        align-items: center;
        gap: .4rem;
        font-size: 1.6rem;
        font-weight: 600;
    }

    span {
        display: flex;
        flex-direction: column;
        gap: 0;
        font-weight: 500;
        
    }

    span > p:first-child {
        font-weight: 400;
        color: #666;
        margin-left: -.4rem;
    }
`

const SaleDetailsWrapper = styled.div`
    display: grid;
    grid-template-rows: repeat(4, max-content);
    gap: .4rem 2rem;
    grid-auto-flow: column;
    
    p {
        min-width: fit-content;
    }
`

const ButtonWrapper = styled.div`
    display: flex;
    gap: 2rem;
    margin-top: 2rem;
    
    button {
        display: flex;
        align-items: center;
        gap: 1rem;
        border-radius: 4px;
        padding: .5rem 2rem;
        font-size: 1.2rem;
        color: white;
        width: max-content;
        transition: scale 400ms ease-in-out;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
        background: var(--green);
    }
    button:hover {
        scale: 1.05;
    }
`