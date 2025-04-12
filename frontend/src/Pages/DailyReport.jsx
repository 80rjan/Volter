import styled from "styled-components";
import Nav from "../Components/Nav.jsx";
import CashRegister from "./CashRegister.jsx";
import {DollarSign, Handshake, Landmark, Plus, Repeat, Tag} from "lucide-react";
import {useState} from "react";
import axios from "axios";
import Loading from "../Components/Loading.jsx";

export default function DailyReport() {
    const [date, setDate] = useState(new Date().toISOString().split("T")[0]);
    const [showDate, setShowDate] = useState(null);
    const [report, setReport] = useState(null);
    const [loading, setLoading] = useState(false);

    const getReport = (date) => {
        setShowDate(date)
        setLoading(true)
        axios.get(`http://localhost:3000/dailyReport?date=${date}`)
            .then(res => {
                setReport(res.data)
            })
            .catch(error => {
                console.error('Error fetching daily report:', error)
            })
            .finally(() => setLoading(false))
    }

    return (
        <ReportPage >
            <Nav />
            <Container >

                <HeaderWrapper >
                    <h1>Дневен Извештај <span>{showDate}</span></h1>
                    <div>
                        <DateInput
                            type="date"
                            value={date}
                            max={new Date().toISOString().split("T")[0]}
                            onChange={(e) => setDate(e.target.value)}
                        />
                        <ButtonGetReport
                            onClick={() => getReport(date)}
                        >
                            <Plus size={22} color="white" strokeWidth={3} />
                            Генерирај Извештај
                        </ButtonGetReport>
                    </div>
                </HeaderWrapper>

                {loading ? <Loading /> : report === null ? <div style={{height: "100%"}}/> :
                    <ReportWrapper>
                        <MainReports>
                            <Report>
                                <DollarSign size={44} />
                                <div>
                                    <h1>{Number(report.total.profit).toLocaleString("de-DE")}</h1>
                                    <p>Профит</p>
                                </div>
                            </Report>
                            <Report>
                                <Landmark size={44} />
                                <div>
                                    <h1>{Number(report.total.moneyGiven).toLocaleString("de-DE")}</h1>
                                    <p>Исплатени средства</p>
                                </div>
                            </Report>
                            <Report>
                                <Repeat size={44} />
                                <div>
                                    <h1>{Number(report.total.turnover).toLocaleString("de-DE")}</h1>
                                    <p>Обрт</p>
                                </div>
                            </Report>
                            <Report>
                                <Handshake size={44} />
                                <div>
                                    <h1>{Number(report.numPawns.numTransactions).toLocaleString("de-DE")}</h1>
                                    <p>Нови залози</p>
                                </div>
                            </Report>
                            <Report>
                                <Tag size={44} />
                                <div>
                                    <h1>{Number(report.numSales.numTransactions).toLocaleString("de-DE")}</h1>
                                    <p>Нови продажби</p>
                                </div>
                            </Report>
                        </MainReports>
                        <PawnReports >
                            {
                                report.categories.map(category => {
                                    const getCat = {
                                        "Electronics": "Електроника",
                                        "Watch": "Часовници",
                                        "Vehicle": "Возила",
                                        "Gold": "Злато",
                                        "Other": "Останато",
                                        "Sale": "Продажба"
                                    }
                                    return <div >
                                        <h2>{getCat[category.category]}</h2>
                                        <span style={{display: "flex", height: "100%", gap: "2rem", alignItems: "center"}}>
                                            <VerticalLine />
                                            <div>
                                                <Report>
                                                    <div>
                                                        <h3>{Number(category.numNewPawns).toLocaleString("de-DE")}</h3>
                                                        <p>Нови Предмети</p>
                                                    </div>
                                                </Report>
                                                <Report>
                                                    <div>
                                                        <h3>{Number(category.moneyGiven).toLocaleString("de-DE")}</h3>
                                                        <p>Исплата</p>
                                                    </div>
                                                </Report>
                                                <Report>
                                                    <div>
                                                        <h3>{Number(category.profit).toLocaleString("de-DE")}</h3>
                                                        <p>Профит</p>
                                                    </div>
                                                </Report>
                                            </div>
                                        </span>
                                    </div>
                                })
                            }
                        </PawnReports>

                    </ReportWrapper>
                }

                <CashRegister refreshDependancy={true} />
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
    
    & span {
        margin-left: 2rem;
        font-weight: 400;
        font-size: 1.8rem;
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
    box-shadow: 4px 2px 6px rgba(0,0,0,0.2);
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
    font-size: 1.2rem;
    box-shadow: 4px 2px 6px rgba(0,0,0,0.2);
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

const ReportWrapper = styled.div`
    display: flex;
    flex-direction: column;
    background: white;
    border-radius: .5rem;
    box-shadow: 0 0 8px rgba(0,0,0,0.2);
    overflow: hidden;
    flex-grow: 1;
    min-height: 0;
    height: 100%;
`

const MainReports = styled.div`
    display: flex;
    justify-content: space-evenly;
    padding: 2rem 0;
    align-items: center;
    gap: 2rem;
`

const PawnReports = styled.div`
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    padding: 2rem;
    gap: 1rem 2rem;

    & > div {
        display: flex;
        background: rgba(0, 96, 64, 0.1);
        border: 2px solid rgba(0, 0, 0, 0.2);
        border-radius: .4rem;
        padding: 1rem 2rem;
        //gap: 2rem;
        align-items: center;
        justify-content: space-between;
        

        & > span > div {
            display: flex;
            flex-direction: column;
            justify-content: space-evenly;
            align-items: center;
            gap: 1rem;

            & p {
                font-size: .8rem;
            }
        }
    }
`

const VerticalLine = styled.div`
    width: 2px;
    height: 90%;
    background: rgba(0,0,0,0.2);
    border-radius: .4rem;
`

const Report = styled.div`
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
    
    & p {
        font-weight: 400;
        font-size: 1rem;
    }
`
