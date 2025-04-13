import styled from "styled-components";
import Nav from "../Components/Nav.jsx";
import CashRegister from "./CashRegister.jsx";
import {ChevronDown, ChevronUp, Ellipsis, Minus, Plus} from "lucide-react";
import {useEffect, useRef, useState} from "react";
import axios from "axios";
import Loading from "../Components/Loading.jsx";
import ModalReadMoreMonthReport from "../Components/ModalReadMoreMonthReport.jsx";

export default function MonthlyReport() {
    const [allReports, setAllReports] = useState([]);
    const [orderBy, setOrderBy] = useState("Year");
    const orderDirectionArr = useRef([-1,0,0,0,0,0,0,0]); // -1=desc 0=normal 1=asc
    const [orderDirection, setOrderDirection] = useState("DESC");
    const [searchByMonth, setSearchByMonth] = useState("");
    const [searchByYear, setSearchByYear] = useState("");
    const [refresh, setRefresh] = useState(false);
    const offset = useRef(0);
    const limit = 20;
    const [isLastPage, setIsLastPage] = useState(false);
    const prevReports = useRef([]);
    const scrollableReportsRef = useRef(null);
    const [loading, setLoading] = useState(false);
    const isFetchingRef = useRef(false);
    const [isFetching, setIsFetching] = useState(false);
    const [modalReadMore, setModalReadMore] = useState(false);
    const [reportReadMore, setReportReadMore] = useState({});
    const [error, setError] = useState(false);
    const [reportGenerateInfo, setReportGenerateInfo] = useState({
        year: 0,
        month: 0,
    });

    const fetchReports = (limit, offset, order, direction, searchByMonth, searchByYear, isLoading) => {
        if (isFetching) return;
        setIsFetching(true);
        setLoading(isLoading);
        isFetchingRef.current = true;
        axios.get(`http://localhost:3000/monthlyReport?limit=${limit}&offset=${offset}&orderBy=${order}&orderDirection=${direction}&searchByMonth=${searchByMonth}&searchByYear=${searchByYear}`)
            .then(res => {
                if (JSON.stringify(prevReports.current) !== JSON.stringify(res.data)) {
                    setAllReports(prev => [...prev, ...res.data]);
                    prevReports.current = [...prevReports.current, ...res.data];
                    setIsLastPage(res.data.length < limit);
                }
            })
            .catch(error => {
                console.error('Error fetching all monthly reports:', error)
            })
            .finally(() => {
                setLoading(false)
                isFetchingRef.current = false;
                setIsFetching(false);
            })
    }

    const generateReport = (year, month) => {
        if (year <= 0 || month <= 0 || month >= 13) {
            setError("Внесете валиден месец и година");
            return;
        }
        setLoading(true);
        axios.post(`http://localhost:3000/monthlyReport/generate`, { year: year, month: month })
            .then(res => {
                const data = res.data
                if (!data.passed) {
                    setError(data.message)
                    throw new Error(data.message);
                }
            })
            .catch(error => {
                console.error(`Error generating monthly report for ${month}-${year}:`, error)
            })
            .finally(() => {
                setLoading(false)
                setRefresh(prev => !prev);
            })
    }

    useEffect(() => {
        const handleScroll = () => {
            const scrollDiv = scrollableReportsRef.current;
            const scrollHeight = scrollDiv.scrollHeight; // Total content height
            const scrollTop = scrollDiv.scrollTop; // Current scroll position
            const clientHeight = scrollDiv.clientHeight; // Visible height of the div

            // Check if the scrollbar is 30% up from the bottom
            if (scrollHeight - scrollTop - clientHeight <= scrollHeight * 0.3 && !isLastPage && !isFetchingRef.current) {
                offset.current += limit; // Increase offset for the next fetch
                fetchReports(limit, offset.current, orderBy, orderDirection, searchByMonth, searchByYear, false);
            }
        };

        const scrollableDiv = scrollableReportsRef.current;
        scrollableDiv.addEventListener("scroll", handleScroll);

        return () => {
            scrollableDiv.removeEventListener("scroll", handleScroll);
        };
    }, [isLastPage, refresh, orderBy, orderDirection, searchByMonth, searchByYear])

    useEffect(() => {
        setAllReports([]);
        prevReports.current = [];
        offset.current = 0;
        fetchReports(limit, offset.current, orderBy, orderDirection, searchByMonth, searchByYear, true);
    }, [refresh, orderBy, orderDirection, searchByMonth, searchByYear])

    const handleOrder = (orderBy, index) => {
        const oldDirection = [...orderDirectionArr.current];
        const newDirection = new Array(oldDirection.length).fill(0);
        newDirection[index] = oldDirection[index] === 0 ? 1 : oldDirection[index] === 1 ? -1 : 1;
        orderDirectionArr.current = newDirection;
        //0 -> 1 -> -1 -> 1
        setOrderDirection(newDirection.includes(-1) ? "DESC" : "ASC");
        setOrderBy(orderBy);
    }

    return (
        <ReportPage >
            <Nav />
            <Container >

                <HeaderWrapper >
                    <h1>Месечен Извештај</h1>
                    <div>
                        <Error>{error}</Error>
                        <ReportInput
                            placeholder="Месец"
                            type="number"
                            onChange={(e) => setReportGenerateInfo({...reportGenerateInfo, month: Number(e.target.value)})}
                        />
                        <ReportInput
                            placeholder="Година"
                            type="number"
                            onChange={(e) => setReportGenerateInfo({...reportGenerateInfo, year: Number(e.target.value)})}
                        />
                        <ButtonGetReport
                            onClick={() => generateReport(reportGenerateInfo.year, reportGenerateInfo.month)}
                        >
                            <Plus size={22} color="white" strokeWidth={3}/>
                            Генерирај Извештај
                        </ButtonGetReport>
                    </div>
                </HeaderWrapper>

                <FilterWrapper >
                    <StyledInput placeholder="Пребарувај по месец (број)"
                                 type="number"
                                 onKeyUp={(e) => {
                                     setSearchByMonth(e.target.value)
                                 }}
                    />
                    <StyledInput placeholder="Пребарувај по година (број)"
                                 type="number"
                                 onKeyUp={(e) => {
                                     setSearchByYear(e.target.value)
                                 }}
                    />
                </FilterWrapper>

                <ReportsWrapper>
                    <TableHeader >
                        <Text onClick={() => handleOrder("Year", 0)}>
                            Година {orderDirectionArr.current[0] === 0 ? <Minus size={14} /> : orderDirectionArr.current[0] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => handleOrder("Month", 1)}>
                            Месец {orderDirectionArr.current[1] === 0 ? <Minus size={14} /> : orderDirectionArr.current[1] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => handleOrder("Money Given", 2)}>
                            Исплатени Средства {orderDirectionArr.current[2] === 0 ? <Minus size={14} /> : orderDirectionArr.current[2] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => handleOrder("Net Profit", 3)}>
                            Нето Профит {orderDirectionArr.current[3] === 0 ? <Minus size={14} /> : orderDirectionArr.current[3] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => handleOrder("Total Pawns", 4)}>
                            Вкупно Залози {orderDirectionArr.current[4] === 0 ? <Minus size={14} /> : orderDirectionArr.current[4] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => handleOrder("Profit Pawns", 5)}>
                            Профит Залози {orderDirectionArr.current[5] === 0 ? <Minus size={14} /> : orderDirectionArr.current[5] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => handleOrder("Total Sales", 6)}>
                            Вкупно Продажби {orderDirectionArr.current[6] === 0 ? <Minus size={14} /> : orderDirectionArr.current[6] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text onClick={() => handleOrder("Profit Sales", 7)}>
                            Профит Продажби {orderDirectionArr.current[7] === 0 ? <Minus size={14} /> : orderDirectionArr.current[7] === -1 ? <ChevronDown size={14} /> : <ChevronUp size={14} />}
                        </Text>
                        <Text style={{cursor: "default"}}>Повеќе</Text>
                    </TableHeader>

                    <ScrollableReports ref={scrollableReportsRef}>
                        { loading ?
                            <Loading /> :
                            allReports.map((report, index) => (
                                <Report key={index} style={index % 2 === 1 ? { background: "#f0f0f0" } : { background: "#ffffff" }}>
                                    <TextReport>{report.Year}</TextReport>
                                    <TextReport>{report.Month}</TextReport>
                                    <TextReport>{Number(report["Money Given"]).toLocaleString("de-DE")}</TextReport>
                                    <TextReport>{Number(report["Net Profit"]).toLocaleString("de-DE")}</TextReport>
                                    <TextReport>{Number(report["Total Pawns"]).toLocaleString("de-DE")}</TextReport>
                                    <TextReport>{Number(report["Profit Pawns"]).toLocaleString("de-DE")}</TextReport>
                                    <TextReport>{Number(report["Total Sales"]).toLocaleString("de-DE")}</TextReport>
                                    <TextReport>{Number(report["Profit Sales"]).toLocaleString("de-DE")}</TextReport>
                                    <Ellipsis size={28} color="#888" onClick={() => {
                                        setReportReadMore(report);
                                        setModalReadMore(true);
                                    }}/>
                                </Report>
                            )) }
                    </ScrollableReports>
                </ReportsWrapper>

                {
                    modalReadMore && <ModalReadMoreMonthReport
                        report={reportReadMore}
                        closeModal={() => setModalReadMore(false)}
                    />
                }

                <CashRegister refreshDependancy={true}/>
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

    & > div {
        display: flex;
        flex-direction: row;
        gap: 1rem;
        align-items: center;
    }
`

const Error = styled.span`
    color: red;
    font-style: italic;
    font-size: 1rem;
    font-weight: 400;
`;

const FilterWrapper = styled.div`
    display: flex;
    justify-content: space-evenly;
    width: 100%;
`

const StyledInput = styled.input`
    border: none;
    border-radius: .2rem;
    font-size: 1rem;
    width: 25%;
    padding: .5rem;
    box-shadow: 0 0 8px rgba(0,0,0,0.2);
`

const ReportInput = styled.input`
    padding: .4rem 1rem;
    height: fit-content;
    border: 2px solid rgba(0, 96, 64, 0.4);
    border-radius: .4rem;
    box-shadow: 4px 2px 6px rgba(0,0,0,0.2);
    font-size: 1rem;
    max-width: 8rem;

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

const ReportsWrapper = styled.div`
    display: flex;
    flex-direction: column;
    background: white;
    border-radius: .5rem;
    box-shadow: 0 0 8px rgba(0,0,0,0.2);
    overflow: hidden;
    flex-grow: 1;
    min-height: 0;
`

const TableHeader = styled.div`
    display: grid;
    place-items: center;
    grid-template-columns: repeat(9, 1fr);
    padding: 1rem .5rem;
    border-bottom: rgba(0, 0, 0, 0.2) 2px solid;
    color: #eee;
    background: #666;
`

const Text = styled.div`
    font-weight: 600;
    font-size: .8rem;
    cursor: pointer;
    display: flex;
    align-items: center;
`

const ScrollableReports = styled.div`
    overflow-y: auto;
    overflow-x: hidden;
    flex-grow: 1;
    
    &::-webkit-scrollbar {
        width: 4px;
    }
    &::-webkit-scrollbar-track {
        
    }
    &::-webkit-scrollbar-thumb {
        background: #888;
        border-radius: 8px;
        
        &:hover {
            background: #aaa;
        }
    }
`

const Report = styled.div`
    display: grid;
    place-items: center;
    grid-template-columns: repeat(9, 1fr);
    gap: .4rem;
    padding: .8rem;
    border-bottom: rgba(0,0,0,0.2) 2px solid;
    transition: all 200ms ease-in-out;
    z-index: 1;

    svg {
        cursor: pointer;
        transition: all 300ms ease-in-out;
    }
    svg:hover {
        scale: 1.2;
    }

    //&:hover {
    //    padding: 1rem;
    //    box-shadow: 0 0 8px rgba(0,0,0,0.6);
    //    z-index: 10;
    //    scale: 1.001;
    //    //border: none;
    //}
`;

const TextReport = styled.p`
    font-weight: 500;
    font-size: .8rem;

    &.bold {
        font-weight: bold;
    }
    &.color {
        font-style: italic;
    }
`;