import React, {useState} from "react";
import ReactDom from "react-dom";
import axios from "axios";
import styled from "styled-components";
import {
    Euro,
    RotateCcw,
    X,
    Printer,
    UserPen,
    Check,
    ArrowLeft,
    Calendar1,
    ChartNoAxesCombined,
    HandCoins,
    Handshake,
    Laptop,
    Coins,
    Car,
    Watch,
    Tag,
    CircleDollarSign,
    DollarSign, Landmark, Repeat, Banknote, Sigma, Receipt, Vault
} from 'lucide-react';

export default function ModalReadMoreMonthReport({report, closeModal}) {

    return ReactDom.createPortal(
        <>
            <Overlay/>
            <Wrapper>
                <X size={40} onClick={closeModal}/>
                <Header>
                    <Calendar1 size={44}/>
                    Месечен Извештај за {report.Month}-{report.Year}
                </Header>
                <ReportWrapper>
                    <MainReports>
                        <Report>
                            <Handshake size={36} />
                            <div>
                                <h1>{Number(report["Money Given"]).toLocaleString("de-DE")}</h1>
                                <p>Исплатено</p>
                            </div>
                        </Report>
                        <Report>
                            <Landmark size={36} />
                            <div>
                                <h1>{Number(report["Money Got"]).toLocaleString("de-DE")}</h1>
                                <p>Примено</p>
                            </div>
                        </Report>
                        <Report>
                            <Repeat size={36} />
                            <div>
                                <h1>{Number(report["Total Turnover"]).toLocaleString("de-DE")}</h1>
                                <p>Обрт</p>
                            </div>
                        </Report>
                        <Report>
                            <Sigma size={36} />
                            <div>
                                <h1>{(Number(report["Money Got"]) + Number(report["Gross Profit"])).toLocaleString("de-DE")}</h1>
                                <p>Промет</p>
                            </div>
                        </Report>
                        <Report>
                            <Receipt size={36} />
                            <div>
                                <h1>{Number(report["Gross Profit"]).toLocaleString("de-DE")}</h1>
                                <p>Бруто Профит</p>
                            </div>
                        </Report>
                        <Report>
                            <Receipt size={36} />
                            <div>
                                <h1>-{Math.abs(report["Gross Profit"] - report["Net Profit"]).toLocaleString("de-DE")}</h1>
                                <p>Расходи</p>
                            </div>
                        </Report>
                        <Report>
                            <Banknote size={36} />
                            <div>
                                <h1>{Number(report["Net Profit"]).toLocaleString("de-DE")}</h1>
                                <p>Нето Профит</p>
                            </div>
                        </Report>
                    </MainReports>

                    <Separator/>

                    <ReportDetailsWrapper>
                        <div>
                            <h3><Vault color="var(--grey)" size={36}/>Залози</h3>
                            <span>
                                    <p>Вкупно нови:</p>
                                    <p>{Number(report["Total Pawns"]).toLocaleString("de-DE")}</p>
                            </span>
                            <span>
                                    <p>Пари добиени:</p>
                                    <p>{Number(report["Money Pawns"]).toLocaleString("de-DE")}</p>
                            </span>
                            <span>
                                    <p>Профит остварен:</p>
                                    <p>{Number(report["Profit Pawns"]).toLocaleString("de-DE")}</p>
                            </span>
                        </div>

                        <div>
                            <h3><Laptop color="var(--blue)" size={36}/>Електроника</h3>
                            <span>
                                    <p>Вкупно нови:</p>
                                    <p>{Number(report["Num Electronics Pawns"]).toLocaleString("de-DE")}</p>
                            </span>
                            <span>
                                    <p>Пари добиени:</p>
                                    <p>{Number(report["Money Electronics Pawns"]).toLocaleString("de-DE")}</p>
                            </span>
                            <span>
                                    <p>Профит остварен:</p>
                                    <p>{Number(report["Profit Electronics Pawns"]).toLocaleString("de-DE")}</p>
                            </span>
                        </div>

                        <div>
                            <h3><Coins color="var(--gold)" size={36}/>Злато</h3>
                            <span>
                                    <p>Вкупно нови:</p>
                                    <p>{Number(report["Num Gold Pawns"]).toLocaleString("de-DE")}</p>
                            </span>
                            <span>
                                    <p>Пари добиени:</p>
                                    <p>{Number(report["Money Gold Pawns"]).toLocaleString("de-DE")}</p>
                            </span>
                            <span>
                                    <p>Профит остварен:</p>
                                    <p>{Number(report["Profit Gold Pawns"]).toLocaleString("de-DE")}</p>
                            </span>
                        </div>

                        <div>
                            <h3><Watch color="var(--gold)" size={36}/>Часовници</h3>
                            <span>
                                    <p>Вкупно нови:</p>
                                    <p>{Number(report["Num Watch Pawns"]).toLocaleString("de-DE")}</p>
                            </span>
                            <span>
                                    <p>Пари добиени:</p>
                                    <p>{Number(report["Money Watch Pawns"]).toLocaleString("de-DE")}</p>
                            </span>
                            <span>
                                    <p>Профит остварен:</p>
                                    <p>{Number(report["Profit Watch Pawns"]).toLocaleString("de-DE")}</p>
                            </span>
                        </div>

                        <div>
                            <h3><Car color="var(--dark-red)" size={36}/>Возила</h3>
                            <span>
                                    <p>Вкупно нови:</p>
                                    <p>{Number(report["Num Vehicle Pawns"]).toLocaleString("de-DE")}</p>
                            </span>
                            <span>
                                    <p>Пари добиени:</p>
                                    <p>{Number(report["Money Vehicle Pawns"]).toLocaleString("de-DE")}</p>
                            </span>
                            <span>
                                    <p>Профит остварен:</p>
                                    <p>{Number(report["Profit Vehicle Pawns"]).toLocaleString("de-DE")}</p>
                            </span>
                        </div>

                        <div>
                            <h3><CircleDollarSign color="var(--green)" size={36}/> Останато</h3>
                            <span>
                                    <p>Вкупно нови:</p>
                                    <p>{Number(report["Num Other Pawns"]).toLocaleString("de-DE")}</p>
                            </span>
                            <span>
                                    <p>Пари добиени:</p>
                                    <p>{Number(report["Money Other Pawns"]).toLocaleString("de-DE")}</p>
                            </span>
                            <span>
                                    <p>Профит остварен:</p>
                                    <p>{Number(report["Profit Other Pawns"]).toLocaleString("de-DE")}</p>
                            </span>
                        </div>

                        <div>
                            <h3><Tag color="var(--green)" size={36}/>Продажби</h3>
                            <span>
                                    <p>Вкупно нови:</p>
                                    <p>{Number(report["Total Sales"]).toLocaleString("de-DE")}</p>
                            </span>
                            <span>
                                    <p>Пари добиени:</p>
                                    <p>{Number(report["Money Sales"]).toLocaleString("de-DE")}</p>
                            </span>
                            <span>
                                    <p>Профит остварен:</p>
                                    <p>{Number(report["Profit Sales"]).toLocaleString("de-DE")}</p>
                            </span>
                        </div>
                    </ReportDetailsWrapper>
                </ReportWrapper>
            </Wrapper>
        </>,
        document.getElementById("portal")
    );
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

const Header = styled.div`
    display: flex;
    align-items: center;
    gap: .4rem;
    font-size: 1.8rem;
    font-weight: 600;
    margin-bottom: 2rem;
    width: 100%;
`;

const ReportWrapper = styled.div`
    display: flex;
    align-items: center;
    gap: 4rem;

    & span {
        display: flex;
        flex-direction: column;
        gap: 0;
        font-weight: 500;

    }

    & span > p:first-child {
        font-weight: 400;
        color: #666;
        margin-left: -.4rem;
    }
`

const MainReports = styled.div`
    display: flex;
    flex-direction: column;
    justify-content: space-evenly;
    align-items: flex-start;
    gap: 2rem;
    
`

const Report = styled.div`
    display: flex;
    line-height: 1;
    gap: 1rem;
    min-width: max-content;
    
    & > div {
        display: flex;
        flex-direction: column;
        gap: .4rem;
        
        & > h1 {
            font-size: 1.4rem;
        }
    }
    
    & p {
        font-weight: 400;
        font-size: 1rem;
    }
`

const Separator = styled.div`
    width: 2px;
    height: 500px;
    background: rgba(0, 0, 0, 0.2);
    border-radius: 100px;
`

const ReportDetailsWrapper = styled.div`
    display: grid;
    grid-template-rows: repeat(2 , 1fr);
    grid-auto-flow: column;
    gap: 2rem 4rem;

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
    
    & > div {
        display: flex;
        flex-direction: column;
        min-width: max-content;
        
        & > h3 {
            display: flex;
            align-items: center;
            gap: .4rem;
            margin-bottom: .4rem;
        }
        
        & > span {
            margin-left: 1rem;
        }
    }
`
