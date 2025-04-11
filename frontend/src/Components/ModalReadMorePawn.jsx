import React, {useState} from "react";
import ReactDom from "react-dom";
import axios from "axios";
import styled from "styled-components";
import {
    UserRound,
    Euro,
    RotateCcw,
    X,
    CircleDollarSign,
    Printer,
    UserPen,
    Check,
    ArrowLeft,
    ArrowDownToLine
} from 'lucide-react';
import html2canvas from "html2canvas";
import jsPDF from "jspdf";
import DogovorZaZaem from "../Documents/DogovorZaZaem.jsx";
import DogovorZaRacenZalog from "../Documents/DogovorZaRacenZalog.jsx";
import Loading from "./Loading.jsx";
import {parse} from "dotenv";

export default function ModalReadMorePawn({category, pawnInfo, closeModal, closePawn, continuePawn, movePawnToSale, oldPawn, refreshCashReg}) {
    const [modalPrintDocument, setModalPrintDocument] = React.useState(false);
    const [clientAddress, setClientAddress] = React.useState("");
    const [idCard, setIdCard] = React.useState("");
    const [moneyStr, setMoneyStr] = React.useState("");
    const [daysPawnStr, setDaysPawnStr] = React.useState("");
    const [pawnDescription, setPawnDescription] = React.useState("");
    const client = pawnInfo.client;
    const [pawn, setPawn] = useState({...pawnInfo.pawn, "Days Left": pawnInfo["Days Left"]});
    const [isDownloading, setIsDownloading] = React.useState(false);
    const hiddenDocRefLoan = React.useRef(null);
    const hiddenDocRefPawn = React.useRef(null);
    const [isEditing, setIsEditing] = React.useState(false);
    const editPawn = React.useRef({pricePawned: pawn.price_pawned, provision: pawn.provision, description: pawn.description, goldGramsDiff: 0 });
    const [isLoading, setIsLoading] = React.useState(false);

    const getCat = {
        "Electronics": "Електроника",
        "Watch": "Часовници",
        "Vehicle": "Возила",
        "Gold": "Злато",
        "Other": "Останато"
    }

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

            pdf.save(`${isLoan ? "Dogovor_za_zaem" : "Dogovor_za_racen_zalog"}.pdf`);
        } catch (error) {
            console.error(error);
        } finally {
            setIsDownloading(false);
        }
    };

    const handleUpdatePawn = () => {
        setIsLoading(true);
        const tableName = {
            Electronics: "electronics_pawn",
            Watch: "watch_pawn",
            Vehicle: "vehicle_pawn",
            Gold: "gold_pawn",
            Other: "other_pawn"
        }
        axios.put(`http://localhost:3000/updatePawn`, { tableName: tableName[category], id: pawn.id, pricePawned: editPawn.current.pricePawned, provision: editPawn.current.provision, description: editPawn.current.description, goldGramsDiff: editPawn.current.goldGramsDiff })
            .then(res => {
                const data = res.data.pawn
                oldPawn.About = data.description
                oldPawn["Item Cost"] = data.price_pawned
                oldPawn.Provision = data.price_pawned * data.provision / 100
                editPawn.current = {...editPawn.current, goldGramsDiff: 0 }
                setPawn(data);
            })
            .catch(err => {
                console.log(err)
                console.error("Error updating pawn " + err)
            })
            .finally(() => {
                setIsLoading(false)
                refreshCashReg()
            });
    }


    return ReactDom.createPortal(
        <>
            <Overlay/>
            <Wrapper>
                <X size={32} onClick={closeModal}/>
                {
                    !modalPrintDocument ?
                        isLoading ? <Loading /> :
                        <>
                            <InformationWrapper style={{gap: isEditing ? "4rem" : "0"}}>
                                <ClientWrapper>
                                    <div>
                                        <UserRound size={32}/>
                                        {client.name}
                                    </div>
                                    <div>
                                        <span>
                                            <p>Шифра на клиент:</p>
                                            <p>{client.id}</p>
                                        </span>
                                        <span>
                                            <p>Ембг:</p>
                                            <p>{client.embg}</p>
                                        </span>
                                        <span>
                                            <p>Телефон:</p>
                                            <p>{client.telephone}</p>
                                        </span>
                                        <span>
                                            <p>Град:</p>
                                            <p>{client.city}</p>
                                        </span>
                                    </div>
                                </ClientWrapper>
                                <Separator/>
                                <PawnWrapper>
                                    <div>
                                        <CircleDollarSign size={32}/>
                                        {getCat[category]}
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
                                        <>
                                            <button onClick={() => setIsEditing(false)}>
                                                <ArrowLeft size={32}/> Врати се назад</button>
                                            <button style={{background: "var(--green)"}} onClick={() => {
                                                handleUpdatePawn();
                                                setIsEditing(false)
                                            }}><Check size={32}/> Потврди промени
                                            </button>
                                        </> :
                                        <>
                                            <button onClick={() => {
                                                closePawn();
                                                closeModal();
                                            }}><X size={32}/> Затвори Залог
                                            </button>
                                            <button onClick={() => {
                                                continuePawn();
                                                closeModal();
                                            }}><RotateCcw size={32}/> Продолжи Залог
                                            </button>
                                            <button onClick={() => {
                                                movePawnToSale();
                                                closeModal();
                                            }}><Euro size={32}/> Премести Залог во Продажба
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
                                <DogovorZaZaem
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
                                <DogovorZaRacenZalog
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
                                    Внеси опис на залогот:
                                    <input onChange={(e) => setPawnDescription(e.target.value)}/>
                                </span>
                                <span>
                                    Внеси адреса и број на адреса:
                                    <input onChange={(e) => setClientAddress(e.target.value)}/>
                                </span>
                                <span>
                                    Внеси број на лична карта:
                                    <input onChange={(e) => setIdCard(e.target.value)}/>
                                </span>
                                <span>
                                    Внеси износ на залог во зборови:
                                    <input placeholder={pawn.price_pawned}
                                           onChange={(e) => setMoneyStr(e.target.value)}/>
                                </span>
                                <span>
                                    Внеси денови на валидност на залогот во зборови:
                                    <input placeholder={pawn.total_days}
                                           onChange={(e) => setDaysPawnStr(e.target.value)}/>
                                </span>
                                <div>
                                    <button onClick={() => setModalPrintDocument(false)} style={{background: "#444"}}>
                                        <ArrowLeft size={32} /> Врати се назад </button>
                                    <button
                                        type="button"
                                        onClick={() => {
                                            try {
                                                handlePrintDoc(hiddenDocRefLoan, true);
                                                handlePrintDoc(hiddenDocRefPawn, false);
                                            } catch (error) {
                                                console.error("Error downloading pdf: " + error);
                                            }
                                        }}
                                    ><ArrowDownToLine size={32}/> {isDownloading ? "Се Симнува..." : "Симни"}</button>
                                </div>
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
                <p>Шифра на залог:</p>
                <p>{pawn.id}</p>
            </span>
            <span>
                <p>Бренд:</p>
                <p>{pawn.brand}</p>
            </span>
            <span>
                <p>Година:</p>
                <p>{pawn.year}</p>
            </span>
            <span>
                <p>Опис:</p>
                {
                    isEditing ?
                        <input type="text" defaultValue={pawn.description}
                               onChange={e => editPawn.current.description = e.target.value}/>
                        :
                        <p>{pawn.description}</p>
                }
            </span>
            <span>
                <p>Месечна исплата:</p>
                <p>{(Number(pawn.price_to_redeem) - Number(pawn.price_pawned)).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Вредност на залогот:</p>
                {
                    isEditing ?
                        <input type="number" defaultValue={pawn.price_pawned}
                               onChange={e => editPawn.current.pricePawned = e.target.value}/>
                        :
                        <p>{Number(pawn.price_pawned).toLocaleString("de-DE")}</p>
                }
            </span>
            <span>
                <p>Цена за подигање:</p>
                <p>{Number(pawn.price_to_redeem).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Провизија:</p>
                {
                    isEditing ?
                        <input type="number" defaultValue={pawn.provision}
                               onChange={e => editPawn.current.provision = e.target.value}/>
                        :
                        <p>{pawn.provision}%</p>
                }
            </span>
            <span>
                <p>Дневна провизија:</p>
                <p>{Math.round(pawn.provision / pawn.total_days * 100) / 100}%</p>
            </span>
            <span>
                <p>Денови валидно:</p>
                <p>{pawn.total_days}</p>
            </span>
            <span>
                <p>Валидно од:</p>
                <p>{pawn.date_from.substring(0, 10)}</p>
            </span>
            <span>
                <p>Валидно до:</p>
                <p>{pawn.date_to.substring(0, 10)}</p>
            </span>
            <span>
                <p>Преостанато:</p>
                <p>{pawn["Days Left"]}</p>
            </span>
        </PawnDetailsWrapper>
    )
}
const renderGold = (pawn, isEditing, editPawn) => {
    return (
        <PawnDetailsWrapper>
            <span>
                <p>Шифра на залог:</p>
                <p>{pawn.id}</p>
            </span>
            <span>
                <p>Тип:</p>
                <p>{pawn.type}</p>
            </span>
            <span>
                <p>Тежина во грам:</p>
                {
                    isEditing ?
                        <input type="text" defaultValue={pawn.weight}
                               onChange={e => editPawn.current.goldGramsDiff = parseInt(e.target.value) - pawn.weight}/>
                        :
                        <p>{pawn.weight}</p>
                }
            </span>
            <span>
                <p>Каратажа:</p>
                <p>{pawn.carats}</p>
            </span>
            <span>
                <p>Опис:</p>
                {
                    isEditing ?
                        <input type="text" defaultValue={pawn.description}
                               onChange={e => editPawn.current.description = e.target.value}/>
                        :
                        <p>{pawn.description}</p>
                }
            </span>
            <span>
                <p>Цена по грам:</p>
                <p>{Math.round( Number(pawn.price_pawned) / Number(pawn.weight)).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Месечна исплата:</p>
                <p>{(Number(pawn.price_to_redeem) - Number(pawn.price_pawned)).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Вредност на залогот:</p>
                {
                    isEditing ?
                        <input type="number" defaultValue={pawn.price_pawned}
                               onChange={e => editPawn.current.pricePawned = e.target.value}/>
                        :
                        <p>{Number(pawn.price_pawned).toLocaleString("de-DE")}</p>
                }
            </span>
            <span>
                <p>Цена за подигање:</p>
                <p>{Number(pawn.price_to_redeem).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Провизија:</p>
                {
                    isEditing ?
                        <input type="number" defaultValue={pawn.provision}
                               onChange={e => editPawn.current.provision = e.target.value}/>
                        :
                        <p>{pawn.provision}%</p>
                }
            </span>
            <span>
                <p>Дневна провизија:</p>
                <p>{Math.round(pawn.provision / pawn.total_days * 100) / 100}%</p>
            </span>
            <span>
                <p>Денови валидно:</p>
                <p>{pawn.total_days}</p>
            </span>
            <span>
                <p>Валидно од:</p>
                <p>{pawn.date_from.substring(0, 10)}</p>
            </span>
            <span>
                <p>Валидно до:</p>
                <p>{pawn.date_to.substring(0, 10)}</p>
            </span>
            <span>
                <p>Преостанато:</p>
                <p>{pawn["Days Left"]}</p>
            </span>
        </PawnDetailsWrapper>
    )
}
const renderVehicle = (pawn, isEditing, editPawn) => {
    return (
        <PawnDetailsWrapper>
            <span>
                <p>Шифра на залог:</p>
                <p>{pawn.id}</p>
            </span>
            <span>
                <p>Бренд:</p>
                <p>{pawn.brand}</p>
            </span>
            <span>
                <p>Модел:</p>
                <p>{pawn.model}</p>
            </span>
            <span>
                <p>Година:</p>
                <p>{pawn.year}</p>
            </span>
            <span>
                <p>Опис:</p>
                {
                    isEditing ?
                        <input type="text" defaultValue={pawn.description}
                               onChange={e => editPawn.current.description = e.target.value}/>
                        :
                        <p>{pawn.description}</p>
                }
            </span>
            <span>
                <p>Месечна исплата:</p>
                <p>{(Number(pawn.price_to_redeem) - Number(pawn.price_pawned)).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Вредност на залогот:</p>
                {
                    isEditing ?
                        <input type="number" defaultValue={pawn.price_pawned}
                               onChange={e => editPawn.current.pricePawned = e.target.value}/>
                        :
                        <p>{Number(pawn.price_pawned).toLocaleString("de-DE")}</p>
                }
            </span>
            <span>
                <p>Цена за подигање:</p>
                <p>{Number(pawn.price_to_redeem).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Провизија:</p>
                {
                    isEditing ?
                        <input type="number" defaultValue={pawn.provision}
                               onChange={e => editPawn.current.provision = e.target.value}/>
                        :
                        <p>{pawn.provision}%</p>
                }
            </span>
            <span>
                <p>Дневна провизија:</p>
                <p>{Math.round(pawn.provision / pawn.total_days * 100) / 100}%</p>
            </span>
            <span>
                <p>Денови валидно:</p>
                <p>{pawn.total_days}</p>
            </span>
            <span>
                <p>Валидно од:</p>
                <p>{pawn.date_from.substring(0, 10)}</p>
            </span>
            <span>
                <p>Валидно до:</p>
                <p>{pawn.date_to.substring(0, 10)}</p>
            </span>
            <span>
                <p>Преостанато:</p>
                <p>{pawn["Days Left"]}</p>
            </span>
        </PawnDetailsWrapper>
    )
}
const renderOther = (pawn, isEditing, editPawn) => {
    return (
        <PawnDetailsWrapper>
            <span>
                <p>Шифра на залог:</p>
                <p>{pawn.id}</p>
            </span>
            <span>
                <p>Опис:</p>
                {
                    isEditing ?
                        <input type="text" defaultValue={pawn.description}
                               onChange={e => editPawn.current.description = e.target.value}/>
                        :
                        <p>{pawn.description}</p>
                }
            </span>
            <span>
                <p>Месечна исплата:</p>
                <p>{(Number(pawn.price_to_redeem) - Number(pawn.price_pawned)).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Вредност на залогот:</p>
                {
                    isEditing ?
                        <input type="number" defaultValue={pawn.price_pawned}
                               onChange={e => editPawn.current.pricePawned = e.target.value}/>
                        :
                        <p>{Number(pawn.price_pawned).toLocaleString("de-DE")}</p>
                }
            </span>
            <span>
                <p>Цена за подигање:</p>
                <p>{Number(pawn.price_to_redeem).toLocaleString("de-DE")}</p>
            </span>
            <span>
                <p>Провизија:</p>
                {
                    isEditing ?
                        <input type="number" defaultValue={pawn.provision}
                               onChange={e => editPawn.current.provision = e.target.value}/>
                        :
                        <p>{pawn.provision}%</p>
                }
            </span>
            <span>
                <p>Дневна провизија:</p>
                <p>{Math.round(pawn.provision / pawn.total_days * 100) / 100}%</p>
            </span>
            <span>
                <p>Денови валидно:</p>
                <p>{pawn.total_days}</p>
            </span>
            <span>
                <p>Валидно од:</p>
                <p>{pawn.date_from.substring(0, 10)}</p>
            </span>
            <span>
                <p>Валидно до:</p>
                <p>{pawn.date_to.substring(0, 10)}</p>
            </span>
            <span>
                <p>Преостанато:</p>
                <p>{pawn["Days Left"]}</p>
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
            gap: .2rem;
            font-size: 1.2rem;
        }
        
        & input {
            font-size: 1rem;
            padding: .4rem;
            //border: 1px solid rgba(0,0,0,.8);
            border: none;
            border-radius: 2px;
        }
        
        & > div {
            display: flex;
            gap: 2rem;
        }

        & button {
            width: max-content;
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 1rem;
            background: var(--green);
            color: white;
            padding: .5rem 2rem;
            border-radius: 4px;
            font-size: 1.2rem;
            margin-top: 1rem;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
            transition: scale 400ms ease-in-out;
        }

        & button:hover {
            scale: 1.05;
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
    gap: 1rem;

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
    gap: 1rem;

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
    width: max-content;

    p {
        min-width: fit-content;
    }
    
    & input {
        width: 100%;
        font-size: 1rem;
        padding: .2rem;
        border: 2px solid var(--green);
        border-radius: 4px;
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