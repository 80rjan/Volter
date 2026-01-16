import React, { useEffect } from "react";
import ReactDom from "react-dom";
import styled from "styled-components";
import { UserRound, X, DollarSign, Sigma, History } from "lucide-react";
import axios from "axios";

export default function ModalReadMoreClient({ client, closeModal }) {
  const [pawns, setPawns] = React.useState([]);
  const [transactions, setTransactions] = React.useState([]);
  const [loading, setLoading] = React.useState(false);

  const fetchClientPawnsAndTransactions = () => {
    setLoading(true);
    axios
      .get("http://localhost:3000/clients/details?clientId=" + client.Id)
      .then((res) => {
        setPawns(res.data.pawns);
        setTransactions(res.data.transactions);
      })
      .catch((err) => {
        console.error("Error fetching client details:", err);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchClientPawnsAndTransactions();
  }, []);

  const getCat = {
    Electronics: "Електроника",
    Watch: "Часовници",
    Vehicle: "Возила",
    Gold: "Злато",
    Other: "Останато",
    Sale: "Продажба",
    Insert: "Внес Каса",
    Remove: "Излез Каса",
    Expense: "Расходи",
  };

console.log(client)

  return ReactDom.createPortal(
    <>
      <Overlay />
      <Wrapper>
        <X size={32} onClick={closeModal} />
        <ClientInfo>
          <div>
            <UserRound size={32} />
            {client.Name} <div>({client["Date Joined"].split("T")[0]})</div>
          </div>
          <div>
            <span>
              <p>Град:</p>
              <p>{client.City}</p>
            </span>
            <span>
              <p>Ембг:</p>
              <p>{client.Embg}</p>
            </span>
            <span>
              <p>Телефон:</p>
              <p>
                {client["Telephone 1"]}
                {client["Telephone 2"].trim() !== ""
                  ? ` / ${client["Telephone 2"]}`
                  : ""}
              </p>
            </span>
          </div>
        </ClientInfo>

        <ClientStatistics>
          <Statistic>
            <Sigma color="var(--grey)" size={40} />
            <div>
              <h1>{Number(client["Total Pawns"]).toLocaleString("de-DE")}</h1>
              <p>Вкупно залози</p>
            </div>
          </Statistic>
          <Statistic>
            <History color="var(--grey)" size={40} />
            <div>
              <h1>{Number(client["Active Pawns"]).toLocaleString("de-DE")}</h1>
              <p>Активни залози</p>
            </div>
          </Statistic>
          <Statistic>
            <DollarSign color="var(--grey)" size={40} />
            <div>
              <h1>{Number(client["Money Pawns"]).toLocaleString("de-DE")}</h1>
              <p>Вредност на залози</p>
            </div>
          </Statistic>
          <Statistic>
            <DollarSign color="var(--green)" size={40} />
            <div>
              <h1>
                {Number(client["Money Provision"]).toLocaleString("de-DE")}
              </h1>
              <p>Очекуван приход</p>
            </div>
          </Statistic>
        </ClientStatistics>

        <Tables>
          <div>
            <h2>Залози</h2>
            <Table>
              <TableHeader>
                <Text>Категорија</Text>
                <Text>Опис</Text>
                <Text>Вредност</Text>
                <Text>Провизија</Text>
                <Text>Рок</Text>
                {/* <Text>Вкупно денови</Text> */}
                <Text>Валидно до</Text>
              </TableHeader>

              <ScrollableData>
                {loading
                  ? Array.from({ length: 50 }).map((_, index) => (
                      <SkeletonRow
                        key={index}
                        style={
                          index % 2 === 1
                            ? { background: "#f0f0f0" }
                            : { background: "#ffffff" }
                        }
                      />
                    ))
                  : pawns.map((pawn, index) => (
                      <DataShort
                        key={index}
                        style={
                          index % 2 === 1
                            ? { background: "#f0f0f0" }
                            : { background: "#ffffff" }
                        }
                      >
                        <span>{getCat[pawn.category]}</span>
                        <span>{pawn.description}</span>
                        <span>
                          {Number(pawn.price_pawned).toLocaleString("de-DE")}
                        </span>
                        <span>
                          {Number(pawn.provision).toLocaleString("de-DE")}
                        </span>
                        <span
                          className={`bold ${
                            pawn.days_left < 0 ? "red" : "green"
                          }`}
                        >
                          {pawn.days_left}
                        </span>
                        <span style={{ whiteSpace: "nowrap" }}>
                          {pawn.date_to.split("T")[0]}
                        </span>
                      </DataShort>
                    ))}
              </ScrollableData>
            </Table>
          </div>

          <div>
            <h2>Трансакции</h2>
            <Table>
              <TableHeader>
                <Text>Категорија</Text>
                <Text>Опис</Text>
                <Text>Дадено</Text>
                <Text>Земено</Text>
                <Text>Профит</Text>
                <Text>Отстапување</Text>
                <Text>Датум</Text>
              </TableHeader>

              <ScrollableData>
                {loading
                  ? Array.from({ length: 50 }).map((_, index) => (
                      <SkeletonRow
                        key={index}
                        style={
                          index % 2 === 1
                            ? { background: "#f0f0f0" }
                            : { background: "#ffffff" }
                        }
                      />
                    ))
                  : transactions.map((transaction, index) => (
                      <DataShort
                        key={index}
                        style={
                          index % 2 === 1
                            ? { background: "#f0f0f0" }
                            : { background: "#ffffff" }
                        }
                      >
                        <span>{getCat[transaction.category]}</span>
                        <span>{transaction.description}</span>
                        <span>
                          {Number(transaction.money_given).toLocaleString(
                            "de-DE"
                          )}
                        </span>
                        <span>
                          {Number(transaction.money_got).toLocaleString(
                            "de-DE"
                          )}
                        </span>
                        <span>
                          {Number(transaction.profit).toLocaleString("de-DE")}
                        </span>
                        <span
                          className={`bold color ${
                            Number(transaction.money_diff) === 0
                              ? ""
                              : Number(transaction.money_diff) < 0
                              ? "red"
                              : "green"
                          }`}
                        >
                          {Number(transaction.money_diff).toLocaleString(
                            "de-DE"
                          )}
                        </span>
                        <span style={{ whiteSpace: "nowrap" }}>
                          {transaction.date.split("T")[0]}
                        </span>
                      </DataShort>
                    ))}
              </ScrollableData>
            </Table>
          </div>
        </Tables>
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
  background: rgba(0, 0, 0, 0.7);
  z-index: 1000;
`;

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
  padding: 1rem 1rem 1rem 1rem;
  border-radius: 8px;
  min-width: 95%;
  overflow: hidden;
  max-height: 90%;

  & > svg {
    margin-left: auto;
    transition: all 400ms ease-in-out;
    cursor: pointer;
    flex-shrink: 0;
  }

  & > svg:hover {
    transform: rotate(90deg);
  }
`;

const ClientInfo = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  font-size: 1.8rem;
  font-weight: 600;
  width: 100%;

  & > div:first-child {
    display: flex;
    align-items: center;
    gap: 0.6rem;
    white-space: nowrap;

    & > div {
      font-size: 1.2rem;
      font-weight: 400;
      margin-top: 0.4rem;
    }
  }

  & > div:last-child {
    display: flex;
    align-items: center;
    gap: 1rem;
    white-space: nowrap;
    height: 100%;

    & > span {
      display: flex;
      align-items: flex-start;
      gap: 0.4rem;
      font-size: 1rem;
      font-weight: 400;
    }
  }
`;

const ClientStatistics = styled.div`
  display: flex;
  justify-content: space-evenly;
  align-items: center;
  gap: 2rem;
  margin: 2rem 0;
`;

const Statistic = styled.div`
  display: flex;
  align-items: center;
  line-height: 1;
  gap: 1rem;
  white-space: nowrap;

  & svg {
    flex-shrink: 0;
  }

  & > div {
    display: flex;
    flex-direction: column;
    gap: 0.4rem;

    & > h1 {
      font-size: 2rem;
    }

    & > p {
      font-weight: 400;
      font-size: 1rem;
    }
  }
`;

const Tables = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  overflow: hidden;

  & h2 {
    font-weight: 600;
    font-size: 1.2rem;
  }

  & > div {
    display: flex;
    flex-direction: column;
    overflow: hidden;
  }
`;

const Table = styled.div`
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 0.5rem;
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.2);
  overflow: hidden;
`;

const TableHeader = styled.div`
  display: grid;
  place-items: center;
  text-align: center;
  grid-template-columns: 1.5fr 3fr repeat(3, 1fr) 1.5fr;
  /* column-gap: 1rem; */
  padding: 0.6rem 0.5rem;
  border-bottom: rgba(0, 0, 0, 0.2) 2px solid;
  color: #eee;
  background: #666;
  white-space: nowrap;

  ${Tables}>div:last-child & {
    grid-template-columns: 1.5fr 3fr repeat(4, 1fr) 1.5fr;
  }
`;

const Text = styled.div`
  font-weight: 500;
  font-size: 0.7rem;
  display: flex;
  align-items: center;
`;

const ScrollableData = styled.div`
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
`;

const DataShort = styled.div`
  font-weight: 400;
  display: grid;
  place-items: center;
  //padding: .2rem;
  grid-template-columns: 1.5fr 3fr repeat(3, 1fr) 1.5fr;
  /* column-gap: 1rem; */

  ${Tables}>div:last-child & {
    grid-template-columns: 1.5fr 3fr repeat(4, 1fr) 1.5fr;
  }

  & > span {
    font-weight: inherit;
    font-size: 0.8rem;
    text-align: center;

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
`;

const SkeletonRow = styled.div`
  display: grid;
  place-items: center;
  grid-template-columns: 1.5fr 3fr repeat(3, 1fr) 1.5fr;
  padding: 0.1rem;
  animation: pulse 1.5s ease-in-out infinite;

  &::before {
    content: "";
    grid-column: 1 / -1;
    height: 1rem;
    background: linear-gradient(90deg, #e0e0e0 25%, #f0f0f0 50%, #e0e0e0 75%);
    background-size: 200% 100%;
    border-radius: 4px;
    animation: shimmer 1.5s ease-in-out infinite;
  }

  @keyframes pulse {
    0%,
    100% {
      opacity: 1;
    }
    50% {
      opacity: 0.5;
    }
  }

  @keyframes shimmer {
    0% {
      background-position: -200% 0;
    }
    100% {
      background-position: 200% 0;
    }
  }
`;
