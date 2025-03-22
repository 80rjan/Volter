import ReactDom from "react-dom";
import styled from "styled-components";
import { UserRound, Euro, RotateCcw, X, CircleDollarSign } from 'lucide-react'

export default function ModalReadMorePawn({ category, pawnInfo, closeModal, closePawn, continuePawn, movePawnToSale }) {
    const client = pawnInfo.client;
    const pawn = pawnInfo.pawn;
    console.log(pawn)

    return ReactDom.createPortal(
        <>
            <Overlay />
            <Wrapper >
                <X size={32} onClick={closeModal} />
                <InformationWrapper >
                    <ClientWrapper >
                        <div>
                            <UserRound size={32} />
                            {client.name}
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

                    <Separator />

                    <PawnWrapper>
                        <div>
                            <CircleDollarSign size={32}/>
                            {category}
                        </div>
                        {category === 'Electronics' && renderElectronicsOrWatch(pawn)}
                        {category === 'Watch' && renderElectronicsOrWatch(pawn)}
                        {category === 'Vehicle' && renderVehicle(pawn)}
                        {category === 'Gold' && renderGold(pawn)}
                        {category === 'Other' && renderOther(pawn)}
                    </PawnWrapper>
                </InformationWrapper>
                <ButtonWrapper>
                    <button onClick={() => {
                        closePawn();
                        closeModal();
                    }}><X size={32}/> Close Pawn </button>
                    <button onClick={() => {
                        continuePawn();
                        closeModal();
                    }}><RotateCcw size={32}/> Continue Pawn </button>
                    <button onClick={() => {
                        movePawnToSale();
                        closeModal();
                    }}><Euro size={32}/> Move Pawn To Sale </button>
                </ButtonWrapper>

            </Wrapper>
        </>,
        document.getElementById("portal")
    )
}

const renderElectronicsOrWatch = (pawn) => {
    return (
        <PawnDetailsWrapper>
            <span>
                <p>Pawn Id:</p>
                <p>{pawn.id}</p>
            </span>
            <span>
                <p>Brand:</p>
                <p>{pawn.brand}</p>
            </span>
            <span>
                <p>Year:</p>
                <p>{pawn.year}</p>
            </span>
            <span>
                <p>Description:</p>
                <p>{pawn.description}</p>
            </span>
            <span>
                <p>Monthly Payment:</p>
                <p>{(Number(pawn.price_to_redeem) - Number(pawn.price_pawned)).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Price Pawned:</p>
                <p>{Number(pawn.price_pawned).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Price To Redeem:</p>
                <p>{Number(pawn.price_to_redeem).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Provision:</p>
                <p>{pawn.provision}%</p>
            </span>
            <span>
                <p>Date From:</p>
                <p>{pawn.date_from.substring(0, 10)}</p>
            </span>
            <span>
                <p>Date To:</p>
                <p>{pawn.date_to.substring(0, 10)}</p>
            </span>
            <span>
                <p>Total Days:</p>
                <p>{pawn.total_days}</p>
            </span>
        </PawnDetailsWrapper>
    )
}
const renderGold = (pawn) => {
    return (
        <PawnDetailsWrapper>
            <span>
                <p>Pawn Id:</p>
                <p>{pawn.id}</p>
            </span>
            <span>
                <p>Type:</p>
                <p>{pawn.type}</p>
            </span>
            <span>
                <p>Weight:</p>
                <p>{pawn.weight}</p>
            </span>
            <span>
                <p>Carats:</p>
                <p>{pawn.carats}</p>
            </span>
            <span>
                <p>Description:</p>
                <p>{pawn.description}</p>
            </span>
            <span>
                <p>Price per gram:</p>
                <p>{Number(pawn.price_per_gram).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Monthly Payment:</p>
                <p>{(Number(pawn.price_to_redeem) - Number(pawn.price_pawned)).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Price Pawned:</p>
                <p>{Number(pawn.price_pawned).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Price To Redeem:</p>
                <p>{Number(pawn.price_to_redeem).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Provision:</p>
                <p>{pawn.provision}%</p>
            </span>
            <span>
                <p>Date From:</p>
                <p>{pawn.date_from.substring(0, 10)}</p>
            </span>
            <span>
                <p>Date To:</p>
                <p>{pawn.date_to.substring(0, 10)}</p>
            </span>
            <span>
                <p>Total Days:</p>
                <p>{pawn.total_days}</p>
            </span>
        </PawnDetailsWrapper>
    )
}
const renderVehicle = (pawn) => {
    return (
        <PawnDetailsWrapper>
            <span>
                <p>Pawn Id:</p>
                <p>{pawn.id}</p>
            </span>
            <span>
                <p>Brand:</p>
                <p>{pawn.brand}</p>
            </span>
            <span>
                <p>Model:</p>
                <p>{pawn.model}</p>
            </span>
            <span>
                <p>Year:</p>
                <p>{pawn.year}</p>
            </span>
            <span>
                <p>Description:</p>
                <p>{pawn.description}</p>
            </span>
            <span>
                <p>Monthly Payment:</p>
                <p>{(Number(pawn.price_to_redeem) - Number(pawn.price_pawned)).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Price Pawned:</p>
                <p>{Number(pawn.price_pawned).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Price To Redeem:</p>
                <p>{Number(pawn.price_to_redeem).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Provision:</p>
                <p>{pawn.provision}%</p>
            </span>
            <span>
                <p>Date From:</p>
                <p>{pawn.date_from.substring(0, 10)}</p>
            </span>
            <span>
                <p>Date To:</p>
                <p>{pawn.date_to.substring(0, 10)}</p>
            </span>
            <span>
                <p>Total Days:</p>
                <p>{pawn.total_days}</p>
            </span>
        </PawnDetailsWrapper>
    )
}
const renderOther = (pawn) => {
    return (
        <PawnDetailsWrapper>
            <span>
                <p>Pawn Id:</p>
                <p>{pawn.id}</p>
            </span>
            <span>
                <p>Description:</p>
                <p>{pawn.description}</p>
            </span>
            <span>
                <p>Monthly Payment:</p>
                <p>{(Number(pawn.price_to_redeem) - Number(pawn.price_pawned)).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Price Pawned:</p>
                <p>{Number(pawn.price_pawned).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Price To Redeem:</p>
                <p>{Number(pawn.price_to_redeem).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Provision:</p>
                <p>{pawn.provision}%</p>
            </span>
            <span>
                <p>Date From:</p>
                <p>{pawn.date_from.substring(0, 10)}</p>
            </span>
            <span>
                <p>Date To:</p>
                <p>{pawn.date_to.substring(0, 10)}</p>
            </span>
            <span>
                <p>Total Days:</p>
                <p>{pawn.total_days}</p>
            </span>
        </PawnDetailsWrapper>
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
    width: 100%;
    //gap: 8rem;
    justify-content: space-around;
    align-items: center;
`

const ClientWrapper = styled.div`
    display: flex;
    flex-direction: column;
    gap: .4rem;

    & > div {
        display: flex;
        flex-direction: column;
        gap: .4rem;
    }
    
    & > div:first-child {
        display: flex;
        flex-direction: row;
        align-items: flex-start;
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

const Separator = styled.div`
    width: 2px;
    height: 250px;
    background: rgba(0,0,0,0.2);
    border-radius: 100px;
`

const PawnWrapper = styled.div`
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

const PawnDetailsWrapper = styled.div`
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
    }
    button:hover {
        scale: 1.05;
    }
    &>button:first-child {
        background: #444;
    }
    &>button:nth-child(2) {
        background: var(--cta-color);
    }
    &>button:nth-child(3) {
        background: var(--green);
    }
`