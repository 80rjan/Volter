import styled from "styled-components";
import Nav from "../components/Nav.jsx";
import CashRegister from "./CashRegister.jsx";
import {DollarSign, Handshake, Landmark, Plus, Repeat, Tag} from "lucide-react";
import React, {useState} from "react";
import axios from "axios";
import Loading from "../components/Loading.jsx";

export default function PeriodReport() {
    const [dateFrom, setDateFrom] = useState(new Date().toISOString().split("T")[0]);
    const [dateTo, setDateTo] = useState(new Date().toISOString().split("T")[0]);
    const [showDate, setShowDate] = useState(null);
    const [report, setReport] = useState(null);
    const [loading, setLoading] = useState(false);

    const getCat = {
        "Electronics": "Електроника",
        "Watch": "Часовници",
        "Vehicle": "Возила",
        "Gold": "Злато",
        "Other": "Останато",
        "Sale": "Продажба",
        "Insert": "Внес Каса",
        "Remove": "Излез Каса",
        "Expense": "Расходи"
    }


    const getReport = (dateFrom, dateTo) => {
        setShowDate(dateFrom + " / " + dateTo)
        setLoading(true)
        axios.get(`http://localhost:3000/periodReport?dateFrom=${dateFrom}&dateTo=${dateTo}`)
            .then(res => {
                setReport(res.data)
                console.log(res.data)
            })
            .catch(error => {
                console.error('Error fetching period report:', error)
            })
            .finally(() => setLoading(false))
    }

    return (
        <ReportPage>
            <Nav/>
            <Container>

                <HeaderWrapper>
                    <h1>Периодичен Извештај <span>{showDate}</span></h1>
                    <div>
                        <DateInput
                            type="date"
                            value={dateFrom}
                            onChange={(e) => setDateFrom(e.target.value)}
                        />
                        <DateInput
                            type="date"
                            value={dateTo}
                            onChange={(e) => setDateTo(e.target.value)}
                        />
                        <ButtonGetReport
                            onClick={() => getReport(dateFrom, dateTo)}
                        >
                            <Plus size={22} color="white" strokeWidth={3}/>
                            Генерирај Извештај
                        </ButtonGetReport>
                    </div>
                </HeaderWrapper>

                {loading ? <Loading/> : report === null ? <div style={{height: "100%"}}/> :
                    <ContentWrapper>
                        <MainReports>
                            <MainReport>
                                <DollarSign color="var(--grey)" size={40}/>
                                <div>
                                    <h1>{Number(report.total.profit).toLocaleString("de-DE")}</h1>
                                    <p>Бруто профит</p>
                                </div>
                            </MainReport>
                            <MainReport>
                                <DollarSign color="var(--green)" size={40}/>
                                <div>
                                    <h1>
                                        {(
                                            Number(report?.total?.profit || 0) -
                                            Number(
                                                report?.cashFlow?.find(c => c.category === "Expense")?.moneyGiven || 0
                                            )
                                        ).toLocaleString("de-DE")}
                                    </h1>
                                    <p>Нето профит</p>
                                </div>
                            </MainReport>
                            <MainReport>
                                <Landmark color="var(--grey)" size={40}/>
                                <div>
                                    <h1>{Number(report.total.moneyGiven).toLocaleString("de-DE")}</h1>
                                    <p>Исплатени средства</p>
                                </div>
                            </MainReport>
                            <MainReport>
                                <Handshake color="var(--grey)" size={40}/>
                                <div>
                                    <h1>{Number(report.numPawns.numTransactions).toLocaleString("de-DE")}</h1>
                                    <p>Нови залози</p>
                                </div>
                            </MainReport>
                            <MainReport>
                                <Tag color="var(--grey)" size={40}/>
                                <div>
                                    <h1>{Number(report.numSales.numTransactions).toLocaleString("de-DE")}</h1>
                                    <p>Нови продажби</p>
                                </div>
                            </MainReport>
                        </MainReports>

                        <DetailedReports>
                            <PawnSaleReports>
                                {
                                    report.categories.map(category => (
                                        <PawnSaleReport>
                                            <h2>{getCat[category.category]}</h2>
                                            <div>
                                                <div>
                                                    <span>Нови:</span>
                                                    {Number(category.numNew).toLocaleString("de-DE")}
                                                </div>
                                                <div>
                                                    <span>Исплата:</span>
                                                    {Number(category.moneyGiven).toLocaleString("de-DE")}
                                                </div>
                                                <div>
                                                    <span>Профит:</span>
                                                    {Number(category.profit).toLocaleString("de-DE")}
                                                </div>
                                            </div>
                                        </PawnSaleReport>
                                    ))
                                }
                            </PawnSaleReports>

                            <TransactionCashFlowReports>
                                <CashFlowReports>
                                    <CashFlowReport>
                                        <div>
                                            <span>Бр. трансакции:</span>
                                            {Number(report.transactions.length).toLocaleString("de-DE")}
                                        </div>
                                    </CashFlowReport>
                                    {
                                        report.cashFlow.map(item => (
                                            <CashFlowReport>
                                                <div>
                                                    <span>{getCat[item.category]}:</span>
                                                    {Number(item.category === "Insert" ? item.moneyGot : item.moneyGiven).toLocaleString("de-DE")}
                                                </div>
                                            </CashFlowReport>
                                        ))
                                    }
                                </CashFlowReports>

                                <TransactionReports>
                                    <TableHeader>
                                        <Text>Име</Text>
                                        <Text>Категорија</Text>
                                        <Text>Дадено</Text>
                                        <Text>Земено</Text>
                                        <Text>Профит</Text>
                                        <Text>Отстапување</Text>
                                    </TableHeader>

                                    <ScrollableTransactions>
                                        {
                                            report.transactions.map((transaction, index) => (
                                                <TransactionShort
                                                    style={index % 2 === 1 ? {background: "#f0f0f0"} : {background: "#ffffff"}}>
                                                    <span>{transaction.Name}</span>
                                                    <span>{getCat[transaction.Category]}</span>
                                                    {
                                                        +transaction.Given + +transaction.Got + +transaction.Profit + +transaction.Diff === 0 ? (
                                                                <span
                                                                    style={{ gridColumn: "span 4" }}>{transaction.Description}</span>
                                                            ) :
                                                            (
                                                                <>
                                                                    <span
                                                                        className="bold color">{Number(transaction.Given).toLocaleString("de-DE")}</span>
                                                                    <span
                                                                        className="bold color">{Number(transaction.Got).toLocaleString("de-DE")}</span>
                                                                    <span
                                                                        className="bold color">{Number(transaction.Profit).toLocaleString("de-DE")}</span>
                                                                    <span
                                                                        className={`bold color ${Number(transaction.Diff) === 0 ? "" : Number(transaction.Diff) < 0 ? "red" : "green"}`}>{transaction.Diff}</span>
                                                                </>
                                                            )
                                                    }
                                                </TransactionShort>
                                            ))
                                        }
                                    </ScrollableTransactions>
                                </TransactionReports>
                            </TransactionCashFlowReports>
                        </DetailedReports>

                    </ContentWrapper>
                }

                <CashRegister refreshDependency={true}/>
            </Container>
        </ReportPage>
    )
}

const ReportPage = styled.div`
    height: 100vh;
    display: grid;
    grid-template-columns: max(15%, 240px) auto;
`

const Container = styled.div`
    display: flex;
    flex-direction: column;
    padding: 2rem 2rem 0 2rem;
    gap: 1rem;
    flex-grow: 1;
    overflow: hidden;
`

const HeaderWrapper = styled.div`
    display: flex;
    justify-content: space-between;
    width: 100%;

    & > h1 {
        font-size: 1.8rem;
    }

    & span {
        margin-left: 1rem;
        font-weight: 400;
        font-size: 1rem;
    }

    & > div {
        display: flex;
        flex-direction: row;
        gap: 1rem;
        align-items: center;
    }
`

const DateInput = styled.input`
    padding: .4rem 1rem;
    height: fit-content;
    border: 2px solid rgba(0, 96, 64, 0.4);
    border-radius: .4rem;
    box-shadow: 4px 2px 6px rgba(0, 0, 0, 0.2);
    font-size: 1rem;

    &:focus {
        border-color: var(--green);
    }
`

const ButtonGetReport = styled.button`
    background: var(--green);
    height: fit-content;
    color: white;
    border-radius: .4rem;
    display: flex;
    align-items: center;
    padding: .6rem 1.6rem;
    font-size: 1rem;
    box-shadow: 4px 2px 6px rgba(0, 0, 0, 0.2);
    gap: .5rem;
    transition: all 250ms ease-in-out;

    svg {
        transition: all 500ms ease-in-out;
    }

    &:hover {
        scale: 1.05;

        svg {
            transform: rotate(90deg);
        }
    }
`

const ContentWrapper = styled.div`
    display: flex;
    flex-direction: column;
    background: white;
    border-radius: .5rem;
    box-shadow: 0 0 8px rgba(0, 0, 0, 0.2);
    overflow: hidden;
    flex-grow: 1;
    min-height: 0;
    height: 100%;
`

const MainReports = styled.div`
    display: flex;
    padding: 2rem 2rem 0 2rem;
    justify-content: space-evenly;
    align-items: center;
    gap: 2rem;
`

const MainReport = styled.div`
    display: flex;
    align-items: center;
    line-height: 1;
    gap: 1rem;

    & > div {
        display: flex;
        flex-direction: column;
        gap: .4rem;

        & > h1 {
            font-size: 1.8rem;
        }
    }

    & > p {
        font-weight: 400;
        font-size: 1rem;
    }
`

const DetailedReports = styled.div`
    display: grid;
    grid-template-columns: 1fr 2fr;
    overflow: hidden;
    height: 100%;
`

const PawnSaleReports = styled.div`
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    align-content: space-evenly;
    padding: 2rem;
    gap: 0 1rem; /* only horizontal spacing */
`

const PawnSaleReport = styled.div`
    display: flex;
    flex-direction: column;
    gap: .2rem;

    & > h2 {
        font-size: 1.2rem;
        text-align: left;
        font-weight: 600;
    }

    & > div {
        display: flex;
        flex-direction: column;
        align-items: start;
        gap: .2rem;

        & > div {
            display: flex;
            gap: .4rem;
            font-size: 1rem;
            font-weight: 600;

            & > span {
                font-weight: 400;
                color: var(--grey);
            }
        }
    }
`

const TransactionCashFlowReports = styled.div`
    display: flex;
    flex-direction: column;
    padding: 2rem;
    gap: 1rem;
    overflow: hidden;
`

const CashFlowReports = styled.div`
    display: flex;
    width: 100%;
    justify-content: space-between;
`

const CashFlowReport = styled.div`
    display: flex;
    flex-direction: column;
    gap: .2rem;

    & > h2 {
        font-size: 1.2rem;
        text-align: left;
        font-weight: 600;
    }

    & > div {
        display: flex;
        gap: .4rem;
        font-size: 1rem;
        font-weight: 600;

        & > span {
            font-weight: 400;
            color: var(--grey);
        }
    }
`

const TransactionReports = styled.div`
    display: flex;
    flex-direction: column;
    background: white;
    border-radius: .5rem;
    box-shadow: 0 0 8px rgba(0, 0, 0, 0.2);
    overflow: hidden;
`

const TableHeader = styled.div`
    display: grid;
    place-items: center;
    grid-template-columns: 2fr 1.5fr repeat(4, 1.5fr);
    column-gap: 1rem;
    padding: .6rem .5rem;
    border-bottom: rgba(0, 0, 0, 0.2) 2px solid;
    color: #eee;
    background: #666;
`

const Text = styled.div`
    font-weight: 500;
    font-size: .7rem;
    display: flex;
    align-items: center;
`

const ScrollableTransactions = styled.div`
    overflow-y: auto;
    display: flex;
    flex-direction: column;

    &::-webkit-scrollbar {
        width: 4px;
    }

    &::-webkit-scrollbar-thumb {
        background: #888;
        border-radius: 8px;

        &:hover {
            background: #aaa;
        }
    }
`

const TransactionShort = styled.div`
    font-weight: 400;
    display: grid;
    place-items: center;
    //padding: .2rem;
    grid-template-columns: 2fr 1.5fr repeat(4, 1.5fr);
    column-gap: 1rem;

    & > span {
        font-weight: inherit;
        font-size: .8rem;

        &.bold {
            font-weight: 400;
        }

        &.color {
            font-style: italic;
        }

        &.red {
            color: red;
        }

        &.green {
            color: green;
        }
    }
`
