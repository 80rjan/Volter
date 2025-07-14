import {Ellipsis, Euro} from "lucide-react";
import styled from "styled-components";
import {useEffect, useState} from "react";
import axios from "axios";
import ModalReadMoreExpense from "./ModalReadMoreExpense.jsx";
import Loading from "./Loading.jsx";

export default function Expense({ expense, index }) {
    const [modalReadMore, setModalReadMore] = useState(false);
    const [expenseInfo, setExpenseInfo] = useState(null);
    const [loading, setLoading] = useState(false);

    useEffect(() =>{
        if (expenseInfo != null)
            setModalReadMore(true);
    }, [expenseInfo])

    const fetchExpense = () => {
        setLoading(true);
        axios.get(`http://localhost:3000/expenses/getExpense?month=${expense.Month}&year=${expense.Year}`)
            .then(res => {
                setExpenseInfo(res.data);
            })
            .catch(error => {
                console.error('Error fetching expense:', error);
            })
            .finally(() => setLoading(false))
    }

    return (
        <Wrapper style={index % 2 === 1 ? { background: "#f0f0f0" } : { background: "#ffffff" }}>
            <TextExpense>{expense.Year}</TextExpense>
            <TextExpense>{expense.Month}</TextExpense>
            <TextExpense>{Number(expense.Rent).toLocaleString("de-DE")}</TextExpense>
            <TextExpense>{Number(expense.Salaries).toLocaleString("de-DE")}</TextExpense>
            <TextExpense>{Number(expense.Bills).toLocaleString("de-DE")}</TextExpense>
            <TextExpense>{Number(expense.Other).toLocaleString("de-DE")}</TextExpense>
            {
                loading ? <Loading width={30} height={30} /> :
                    <TextExpense>
                        <Ellipsis size={28} color="#888" onClick={() => fetchExpense()} />
                    </TextExpense>
            }

            {modalReadMore &&
                <ModalReadMoreExpense
                    expenseInfo={expenseInfo}
                    closeModal={() => setModalReadMore(false)}
                />
            }
        </Wrapper>
    )
}


const Wrapper = styled.div`
    display: grid;
    place-items: center;
    grid-template-columns: repeat(7, 1fr);
    gap: .4rem;
    padding: .5rem;
    border-bottom: rgba(0,0,0,0.2) 2px solid;
    transition: all 200ms ease-in-out;
    z-index: 1;

    //&:hover {
    //    padding: 1rem;
    //    box-shadow: 0 0 8px rgba(0,0,0,0.6);
    //    z-index: 10;
    //    scale: 1.001;
    //    //border: none;
    //}
`;

const TextExpense = styled.p`
    font-weight: 500;
    font-size: .8rem;

    &.bold {
        font-weight: bold;
    }
    &.color {
        font-style: italic;
    }

    svg {
        cursor: pointer;
        transition: all 300ms ease-in-out;
    }
    svg:hover {
        scale: 1.2;
    }
`;