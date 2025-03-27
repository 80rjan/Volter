import React, {useState} from "react";
import ReactDom from "react-dom";
import axios from "axios";
import styled from "styled-components";
import {UserRound, Euro, RotateCcw, X, CircleDollarSign, Printer, UserPen, Check} from 'lucide-react';
import LoanAgreementDocument from "./LoanAgreementDocument.jsx";
import html2canvas from "html2canvas";
import jsPDF from "jspdf";
import PawnAgreementDocument from "./PawnAgreementDocument.jsx";

export default function ModalReadMorePawn({category, pawnInfo, closeModal, closePawn, continuePawn, movePawnToSale, oldPawn}) {
    const [modalPrintDocument, setModalPrintDocument] = React.useState(false);
    const [clientAddress, setClientAddress] = React.useState("");
    const [idCard, setIdCard] = React.useState("");
    const [moneyStr, setMoneyStr] = React.useState("");
    const [daysPawnStr, setDaysPawnStr] = React.useState("");
    const [pawnDescription, setPawnDescription] = React.useState("");
    const client = pawnInfo.client;
    const [pawn, setPawn] = useState(pawnInfo.pawn);
    const [isDownloading, setIsDownloading] = React.useState(false);
    const hiddenDocRefLoan = React.useRef(null);
    const hiddenDocRefPawn = React.useRef(null);
    const [isEditing, setIsEditing] = React.useState(false);
    const editPawn = React.useRef({pricePawned: pawn.price_pawned, provision: pawn.provision, description: pawn.description });
    console.log(oldPawn)

    const handlePrintDoc = async (ref, isLoan) => {
        setIsDownloading(true);
        const element = ref.current;
        if (!element) return;

        const pdf = new jsPDF({
            orientation: "portrait",
            unit: "mm",
            format: "a4",
        });

        try {
            const canvas = await html2canvas(element, { scale: 2, useCORS: true });
            const imgData = canvas.toDataURL("image/png");

            const imgWidth = 210; // A4 width in mm
            const pageHeight = 297; // A4 height in mm
            const imgHeight = (canvas.height * imgWidth) / canvas.width;

            let heightLeft = imgHeight;
            let position = 0;

            pdf.addImage(imgData, "PNG", 0, position, imgWidth, imgHeight);

            while (heightLeft > pageHeight) {
                position -= pageHeight;
                pdf.addPage();
                pdf.addImage(imgData, "PNG", 0, position, imgWidth, imgHeight);
                heightLeft -= pageHeight;
            }

            pdf.save(`${isLoan ? "loan_agreement" : "pawn_agreement"}.pdf`);
        } catch (error) {
            console.error(error);
        } finally {
            setIsDownloading(false);
        }
    };

    const handleUpdatePawn = () => {
        const tableName = {
            Electronics: "electronics_pawn",
            Watch: "watch_pawn",
            Vehicle: "vehicle_pawn",
            Gold: "gold_pawn",
            Other: "other_pawn"
        }
        axios.put(`http://localhost:3000/updatePawn`, { tableName: tableName[category], id: pawn.id, pricePawned: editPawn.current.pricePawned, provision: editPawn.current.provision, description: editPawn.current.description })
            .then(res => {
                const data = res.data.pawn
                oldPawn.About = data.description
                oldPawn["Item Cost"] = data.price_pawned
                oldPawn.Provision = data.price_pawned * data.provision / 100
                setPawn(data);
            })
            .catch(err => console.error("Error updating pawn " + err));
    }


    return ReactDom.createPortal(
        <>
            <Overlay/>
            <Wrapper>
                <X size={32} onClick={closeModal}/>
                {
                    !modalPrintDocument ?
                        <>
                            <InformationWrapper style={{gap: isEditing ? "4rem" : "0"}}>
                                <ClientWrapper>
                                    <div>
                                        <UserRound size={32}/>
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
                                <Separator/>
                                <PawnWrapper>
                                    <div>
                                        <CircleDollarSign size={32}/>
                                        {category}
                                    </div>
                                    {category === 'Electronics' && renderElectronicsOrWatch(pawn, isEditing, editPawn)}
                                    {category === 'Watch' && renderElectronicsOrWatch(pawn, isEditing, editPawn)}
                                    {category === 'Vehicle' && renderVehicle(pawn, isEditing, editPawn)}
                                    {category === 'Gold' && renderGold(pawn, isEditing, editPawn)}
                                    {category === 'Other' && renderOther(pawn, isEditing, editPawn)}
                                </PawnWrapper>
                            </InformationWrapper>
                            <ButtonWrapper>
                                {
                                    isEditing ?
                                        <button style={{background: "var(--green)"}} onClick={() => {
                                            handleUpdatePawn();
                                            setIsEditing(false)
                                        }}><Check size={32}/> Confirm Edits</button> :
                                        <>
                                            <button onClick={() => {
                                                closePawn();
                                                closeModal();
                                            }}><X size={32}/> Close Pawn
                                            </button>
                                            <button onClick={() => {
                                                continuePawn();
                                                closeModal();
                                            }}><RotateCcw size={32}/> Continue Pawn
                                            </button>
                                            <button onClick={() => {
                                                movePawnToSale();
                                                closeModal();
                                            }}><Euro size={32}/> Move Pawn To Sale
                                            </button>
                                            <UserPen size={40} color="#444"
                                                     onClick={() => {
                                                         setIsEditing(true)
                                                     }}/>
                                            <Printer
                                                size={40}
                                                color="#444"
                                                onClick={() => setModalPrintDocument(true)}
                                            />
                                        </>
                                }
                            </ButtonWrapper>
                        </> :
                        <>
                            <div style={{height: "0px", overflowY: "clip"}}>
                                <LoanAgreementDocument
                                    ref={hiddenDocRefLoan}
                                    fullName={client.name}
                                    city={client.city}
                                    address={clientAddress}
                                    embg={client.embg}
                                    idCard={idCard}
                                    telephone={client.telephone}
                                    moneyGiven={pawn.price_pawned}
                                    moneyGivenStr={moneyStr}
                                    pawnDays={pawn.total_days}
                                    pawnDaysStr={daysPawnStr}
                                    dateFrom={pawn.date_from.substring(0, 10)}
                                    dateTo={pawn.date_to.substring(0, 10)}
                                />
                            </div>
                            <div style={{height: "0px", overflowY: "clip"}}>
                                <PawnAgreementDocument
                                    ref={hiddenDocRefPawn}
                                    fullName={client.name}
                                    city={client.city}
                                    address={clientAddress}
                                    embg={client.embg}
                                    idCard={idCard}
                                    telephone={client.telephone}
                                    moneyGiven={pawn.price_pawned}
                                    moneyGivenStr={moneyStr}
                                    pawnDays={pawn.total_days}
                                    pawnDaysStr={daysPawnStr}
                                    dateFrom={pawn.date_from.substring(0, 10)}
                                    dateTo={pawn.date_to.substring(0, 10)}
                                    pawnInfo={pawnDescription}
                                />
                            </div>
                            <form>
                                <span>
                                    Enter description of pawn:
                                    <input onChange={(e) => setPawnDescription(e.target.value)}/>
                                </span>
                                <span>
                                    Enter address and number of address:
                                    <input onChange={(e) => setClientAddress(e.target.value)}/>
                                </span>
                                <span>
                                    Enter card id number:
                                    <input onChange={(e) => setIdCard(e.target.value)}/>
                                </span>
                                <span>
                                    Enter amount of money in words:
                                    <input placeholder={pawn.price_pawned}
                                           onChange={(e) => setMoneyStr(e.target.value)}/>
                                </span>
                                <span>
                                    Enter days of pawn validity in words:
                                    <input placeholder={pawn.total_days}
                                           onChange={(e) => setDaysPawnStr(e.target.value)}/>
                                </span>
                                <button
                                    type="button"
                                    onClick={() => {
                                        handlePrintDoc(hiddenDocRefLoan, true);
                                        handlePrintDoc(hiddenDocRefPawn, false);
                                    }}
                                >{isDownloading ? "Downloading..." : "Download"}</button>
                            </form>
                        </>
                }
            </Wrapper>
        </>,
        document.getElementById("portal")
    );
}

const renderElectronicsOrWatch = (pawn, isEditing, editPawn) => {
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
                {
                    isEditing ?
                        <input type="text" defaultValue={pawn.description} onChange={e => editPawn.current.description = e.target.value}/>
                        :
                        <p>{pawn.description}</p>
                }
            </span>
            <span>
                <p>Monthly Payment:</p>
                <p>{(Number(pawn.price_to_redeem) - Number(pawn.price_pawned)).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Price Pawned:</p>
                {
                    isEditing ?
                        <input type="number" defaultValue={pawn.price_pawned} onChange={e => editPawn.current.pricePawned = e.target.value}/>
                        :
                        <p>{Number(pawn.price_pawned).toLocaleString("de-DE")}</p>
                }
            </span>
            <span>
                <p>Price To Redeem:</p>
                <p>{Number(pawn.price_to_redeem).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Provision:</p>
                {
                    isEditing ?
                        <input type="number" defaultValue={pawn.provision} onChange={e => editPawn.current.provision = e.target.value}/>
                        :
                        <p>{pawn.provision}%</p>
                }
            </span>
            <span>
                <p>Daily Provision:</p>
                <p>{Math.round(pawn.provision / pawn.total_days * 100) / 100}%</p>
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
const renderGold = (pawn, isEditing, editPawn) => {
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
                {
                    isEditing ?
                        <input type="text" defaultValue={pawn.description} onChange={e => editPawn.current.description = e.target.value}/>
                        :
                        <p>{pawn.description}</p>
                }
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
                {
                    isEditing ?
                        <input type="number" defaultValue={pawn.price_pawned} onChange={e => editPawn.current.pricePawned = e.target.value}/>
                        :
                        <p>{Number(pawn.price_pawned).toLocaleString("de-DE")}</p>
                }
            </span>
            <span>
                <p>Price To Redeem:</p>
                <p>{Number(pawn.price_to_redeem).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Provision:</p>
                {
                    isEditing ?
                        <input type="number" defaultValue={pawn.provision} onChange={e => editPawn.current.provision = e.target.value}/>
                        :
                        <p>{pawn.provision}%</p>
                }
            </span>
            <span>
                <p>Daily Provision:</p>
                <p>{Math.round(pawn.provision / pawn.total_days * 100) / 100}%</p>
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
const renderVehicle = (pawn, isEditing, editPawn) => {
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
                {
                    isEditing ?
                        <input type="text" defaultValue={pawn.description} onChange={e => editPawn.current.description = e.target.value}/>
                        :
                        <p>{pawn.description}</p>
                }
            </span>
            <span>
                <p>Monthly Payment:</p>
                <p>{(Number(pawn.price_to_redeem) - Number(pawn.price_pawned)).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Price Pawned:</p>
                {
                    isEditing ?
                        <input type="number" defaultValue={pawn.price_pawned} onChange={e => editPawn.current.pricePawned = e.target.value}/>
                        :
                        <p>{Number(pawn.price_pawned).toLocaleString("de-DE")}</p>
                }
            </span>
            <span>
                <p>Price To Redeem:</p>
                <p>{Number(pawn.price_to_redeem).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Provision:</p>
                {
                    isEditing ?
                        <input type="number" defaultValue={pawn.provision} onChange={e => editPawn.current.provision = e.target.value}/>
                        :
                        <p>{pawn.provision}%</p>
                }
            </span>
            <span>
                <p>Daily Provision:</p>
                <p>{Math.round(pawn.provision / pawn.total_days * 100) / 100}%</p>
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
const renderOther = (pawn, isEditing, editPawn) => {
    return (
        <PawnDetailsWrapper>
            <span>
                <p>Pawn Id:</p>
                <p>{pawn.id}</p>
            </span>
            <span>
                <p>Description:</p>
                {
                    isEditing ?
                        <input type="text" defaultValue={pawn.description} onChange={e => editPawn.current.description = e.target.value}/>
                        :
                        <p>{pawn.description}</p>
                }
            </span>
            <span>
                <p>Monthly Payment:</p>
                <p>{(Number(pawn.price_to_redeem) - Number(pawn.price_pawned)).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Price Pawned:</p>
                {
                    isEditing ?
                        <input type="number" defaultValue={pawn.price_pawned} onChange={e => editPawn.current.pricePawned = e.target.value}/>
                        :
                        <p>{Number(pawn.price_pawned).toLocaleString("de-DE")}</p>
                }
            </span>
            <span>
                <p>Price To Redeem:</p>
                <p>{Number(pawn.price_to_redeem).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Provision:</p>
                {
                    isEditing ?
                        <input type="number" defaultValue={pawn.provision} onChange={e => editPawn.current.provision = e.target.value}/>
                        :
                        <p>{pawn.provision}%</p>
                }
            </span>
            <span>
                <p>Daily Provision:</p>
                <p>{Math.round(pawn.provision / pawn.total_days * 100) / 100}%</p>
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
    
    & form {
        display: flex;
        flex-direction: column;
        gap: .4rem;
        
        & > span {
            display: flex;
            flex-direction: column;
        }

        & > button {
            width: 100%;
            background: var(--green);
            color: white;
            padding: .4rem;
            border-radius: 2px;
            font-size: 1.2rem;
            margin-top: 1rem;
        }
    }
`

const InformationWrapper = styled.div`
    display: flex;
    width: 100%;
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
    background: rgba(0, 0, 0, 0.2);
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
    //flex-wrap: wrap;
    //justify-content: center;

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

    & > button:first-child {
        background: #444;
    }

    & > button:nth-child(2) {
        background: var(--cta-color);
    }

    & > button:nth-child(3) {
        background: var(--green);
    }
    
    & > button:nth-child(4) {
        background: var(--dark-blue);
        //border: 2px solid var(--green);
        //color: var(--green);
    }

    & > svg {
        cursor: pointer;
        transition: scale 400ms ease-in-out;

        &:hover {
            scale: 1.1;
        }
    }
`