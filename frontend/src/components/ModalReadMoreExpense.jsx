import ReactDom from "react-dom";
import styled from "styled-components";
import {
    X,
    Calendar1,
    House,
    Users,
    ReceiptText,
    HandCoins
} from 'lucide-react'
import React from "react";

export default function ModalReadMoreExpense({expenseInfo, closeModal}) {
    const expense = expenseInfo.expense;
    const transactions = expenseInfo.transactions;

    function extractExpenses(transactions) {
        const types = ['Плати', 'Кирија', 'Сметки', 'Останато'];

        const allExpenses = [];

        for (const tx of transactions) {
            let description = '';
            const descriptionMatch = tx.Description.match(/Опис:\s*(.*)/);
            if (descriptionMatch) {
                description = descriptionMatch[1].trim();
            }
            for (const type of types) {
                if (tx.Description.includes(type)) {
                    const expenseAmount = tx["Money Given"];
                    const dateOnly = new Date(tx.Date).toISOString().split('T')[0];
                    allExpenses.push({
                        date: dateOnly,
                        typeOfExpense: type,
                        expenseMoney: expenseAmount,
                        description: description
                    });
                }
            }
        }

        return allExpenses;
    }

    return ReactDom.createPortal(
        <>
            <Overlay/>
            <Wrapper>
                <X size={32} onClick={closeModal}/>
                <Header>
                    <Calendar1 size={44}/>
                    Расходи за {expense.Month}-{expense.Year}
                </Header>
                <ReportWrapper>
                    <MainReports>
                        <Report>
                            <House color="var(--grey)" size={36}/>
                            <div>
                                <h1>{Number(expense.Rent).toLocaleString("de-DE")}</h1>
                                <p>Ќирија</p>
                            </div>
                        </Report>
                        <Report>
                            <Users color="var(--grey)" size={36}/>
                            <div>
                                <h1>{Number(expense.Salaries).toLocaleString("de-DE")}</h1>
                                <p>Плати</p>
                            </div>
                        </Report>
                        <Report>
                            <ReceiptText color="var(--grey)" size={36}/>
                            <div>
                                <h1>{Number(expense.Bills).toLocaleString("de-DE")}</h1>
                                <p>Сметки</p>
                            </div>
                        </Report>
                        <Report>
                            <HandCoins color="var(--grey)" size={36}/>
                            <div>
                                <h1>{Number(expense.Other).toLocaleString("de-DE")}</h1>
                                <p>Останато</p>
                            </div>
                        </Report>
                    </MainReports>

                    <Separator/>

                    <ExpensesWrapper>
                        <TableHeader >
                            <Text>Датум</Text>
                            <Text>Тип на разход</Text>
                            <Text>Износ</Text>
                            <Text>Опис</Text>
                        </TableHeader>

                        <ScrollableExpenses>
                            {
                                extractExpenses(transactions).map((expense, index) => (
                                    <ExpenseShort style={index % 2 === 1 ? { background: "#f0f0f0" } : { background: "#ffffff" }}>
                                        <span>{expense.date}</span>
                                        <span>{expense.typeOfExpense}</span>
                                        <span>{Number(expense.expenseMoney).toLocaleString("de-DE")}</span>
                                        <span>{expense.description || "/"}</span>
                                    </ExpenseShort>
                                ))
                            }
                        </ScrollableExpenses>
                    </ExpensesWrapper>

                </ReportWrapper>

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
    height: 320px;
    background: rgba(0, 0, 0, 0.2);
    border-radius: 100px;
`

const ExpensesWrapper = styled.div`
    display: flex;
    flex-direction: column;
    background: white;
    border-radius: .5rem;
    box-shadow: 0 0 8px rgba(0,0,0,0.2);
    overflow: hidden;
    flex-grow: 1;
    min-height: 0;
    width: 800px;
`

const TableHeader = styled.div`
    display: grid;
    place-items: center;
    grid-template-columns: 1fr 1fr 1fr 2fr;
    column-gap: 4rem;
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

const ScrollableExpenses = styled.div`
    overflow-y: auto;
    overflow-x: hidden;
    flex-grow: 1;
    max-height: 280px;
    display: flex;
    flex-direction: column;
    
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

const ExpenseShort = styled.div`
    font-weight: 400;
    display: grid;
    place-items: center;
    padding: .6rem;
    grid-template-columns: 1fr 1fr 1fr 2fr;
    column-gap: 4rem;
    
    & > span {
        font-weight: inherit;
    }
`