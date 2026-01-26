import React, { useEffect, useRef, useState } from "react";
import axios from "axios";
import styled from "styled-components";
import Nav from "../components/Nav.jsx";
import {
  Plus,
  X,
  Euro,
  RotateCcw,
  ChevronUp,
  ChevronDown,
  Minus,
} from "lucide-react";
import CashRegister from "./CashRegister.jsx";
import Loading from "../components/Loading.jsx";

export default function Transactions() {
  const [dateFrom, setDateFrom] = useState("");
  const [dateTo, setDateTo] = useState("");
  const [allTransactions, setAllTransactions] = useState([]);
  const [orderBy, setOrderBy] = useState("Date");
  const orderDirectionArr = useRef([0, 0, 0, 0, 0, 0, 0, 0, -1]);
  const [orderDirection, setOrderDirection] = useState("DESC");
  const [searchByName, setSearchByName] = useState("");
  const [searchByEmbg, setSearchByEmbg] = useState("");
  const [searchByCategory, setSearchByCategory] = useState("");
  const [refresh, setRefresh] = useState(false);
  const offset = useRef(0);
  const limit = 60;
  const [isLastPage, setIsLastPage] = useState(false);
  const scrollableTransactionsRef = useRef(null);
  const [loading, setLoading] = useState(false);
  const isFetchingRef = useRef(false);
  const [isFetching, setIsFetching] = useState(false);

  const getCat = {
    Electronics: "Електроника",
    Watch: "Часовници",
    Vehicle: "Возила",
    Gold: "Злато",
    Other: "Останато",
    Sale: "Продажба",
    Insert: "Внес Каса",
    Remove: "Излез Каса",
    Expense: "Расход",
  };

  const getDesc = {
    "Added new pawn": "Додаден нов залог",
    "Continued pawn": "Продолжен залог",
    "Closed pawn": "Затворен залог",
    "Added new sale": "Додадена нова продажба",
    "Closed sale": "Затворена продажба",
    "Transferred pawn to sale": "Префрлен залог во продажба",
  };

  const filterSelectOptions = [
    { value: "Gold", label: "Залог злато" },
    { value: "Electronics", label: "Залог електроника" },
    { value: "Watch", label: "Залог часовници" },
    { value: "Vehicle", label: "Залог возила" },
    { value: "Other", label: "Залог останато" },
    { value: "Sale", label: "Продажби" },
    { value: "Change", label: "Промени во залози" },
    { value: "Insert", label: "Внес каса" },
    { value: "Remove", label: "Излез каса" },
    { value: "Expense", label: "Расходи" },
  ];

  const fetchedTransactionIds = useRef(new Set());

  const fetchTransactions = (
    limit,
    offset,
    order,
    direction,
    searchByName,
    searchByEmbg,
    dateFrom,
    dateTo,
    searchByCategory,
    isLoading
  ) => {
    if (isFetching) return;
    setIsFetching(true);
    setLoading(isLoading);
    isFetchingRef.current = true;

    axios
      .get(
        `http://localhost:3000/transactions?limit=${limit}&offset=${offset}&orderBy=${order}&orderDirection=${direction}&searchByName=${searchByName}&searchByEmbg=${searchByEmbg}&dateFrom=${dateFrom}&dateTo=${dateTo}&searchByCategory=${searchByCategory}`
      )
      .then((res) => {
        const newUnique = res.data.filter(
          (t) => !fetchedTransactionIds.current.has(t.Id)
        );
        newUnique.forEach((t) => fetchedTransactionIds.current.add(t.Id));

        setAllTransactions((prev) => [...prev, ...newUnique]);
        setIsLastPage(res.data.length < limit);
      })
      .catch((error) => {
        console.error("Error fetching all transactions:", error);
      })
      .finally(() => {
        setLoading(false);
        isFetchingRef.current = false;
        setIsFetching(false);
      });
  };

  useEffect(() => {
    const handleScroll = () => {
      const scrollDiv = scrollableTransactionsRef.current;
      const scrollHeight = scrollDiv.scrollHeight;
      const scrollTop = scrollDiv.scrollTop;
      const clientHeight = scrollDiv.clientHeight;

      if (
        scrollHeight - scrollTop - clientHeight <= scrollHeight * 0.3 &&
        !isLastPage &&
        !isFetchingRef.current
      ) {
        offset.current += limit;
        fetchTransactions(
          limit,
          offset.current,
          orderBy,
          orderDirection,
          searchByName,
          searchByEmbg,
          dateFrom,
          dateTo,
          searchByCategory,
          false
        );
      }
    };

    const scrollableDiv = scrollableTransactionsRef.current;
    scrollableDiv.addEventListener("scroll", handleScroll);

    return () => {
      scrollableDiv.removeEventListener("scroll", handleScroll);
    };
  }, [
    isLastPage,
    orderBy,
    orderDirection,
    searchByName,
    searchByEmbg,
    dateFrom,
    dateTo,
    searchByCategory,
  ]);

  useEffect(() => {
    fetchedTransactionIds.current.clear();
    setAllTransactions([]);
    offset.current = 0;
    fetchTransactions(
      limit,
      offset.current,
      orderBy,
      orderDirection,
      searchByName,
      searchByEmbg,
      dateFrom,
      dateTo,
      searchByCategory,
      true
    );
  }, [
    refresh,
    orderBy,
    orderDirection,
    searchByName,
    searchByEmbg,
    dateFrom,
    dateTo,
    searchByCategory,
  ]);

  const handleOrder = (orderBy, index) => {
    const oldDirection = [...orderDirectionArr.current];
    const newDirection = new Array(oldDirection.length).fill(0);
    newDirection[index] =
      oldDirection[index] === 0 ? 1 : oldDirection[index] === 1 ? -1 : 1;
    orderDirectionArr.current = newDirection;
    setOrderDirection(newDirection.includes(-1) ? "DESC" : "ASC");
    setOrderBy(orderBy);
  };

  return (
    <TransactionsPage>
      <Nav />
      <Container>
        {/*<HeaderWrapper>*/}
        {/*    <h1>Трансакции</h1>*/}
        {/*</HeaderWrapper>*/}
        <FilterWrapper>
          <StyledSelect
            style={{
              color: searchByCategory === "" ? "#888" : "#000",
            }}
            onChange={(e) => setSearchByCategory(e.target.value)}
          >
            <option style={{ color: "#888" }} value="">
              Пребарубај по
            </option>
            {filterSelectOptions.map((option, index) => (
              <option
                style={{ color: "#111" }}
                key={index}
                value={option.value}
              >
                {option.label}
              </option>
            ))}
          </StyledSelect>
          <StyledInput
            type={"search"}
            placeholder="Пребарувај по име"
            onChange={(e) => setSearchByName(e.target.value)}
          />
          <StyledInput
            type={"search"}
            placeholder="Пребарувај по ембг"
            onChange={(e) => setSearchByEmbg(e.target.value)}
          />
          <DateBox>
            <DateInput
              type="date"
              value={dateFrom}
              onChange={(e) => setDateFrom(e.target.value)}
              style={{ opacity: dateFrom ? 1 : 0.6 }}
            />
            <DateInput
              type="date"
              value={dateTo}
              onChange={(e) => setDateTo(e.target.value)}
              style={{ opacity: dateTo ? 1 : 0.6 }}
            />
          </DateBox>
        </FilterWrapper>
        <TransactionsWrapper>
          <TableHeader>
            <Text onClick={() => handleOrder("Client Id", 0)}>
              Ид{" "}
              {orderDirectionArr.current[0] === 0 ? (
                <Minus size={14} />
              ) : orderDirectionArr.current[0] === -1 ? (
                <ChevronDown size={14} />
              ) : (
                <ChevronUp size={14} />
              )}
            </Text>
            <Text onClick={() => handleOrder("Name", 1)}>
              Име{" "}
              {orderDirectionArr.current[1] === 0 ? (
                <Minus size={14} />
              ) : orderDirectionArr.current[1] === -1 ? (
                <ChevronDown size={14} />
              ) : (
                <ChevronUp size={14} />
              )}
            </Text>
            <Text style={{ cursor: "default" }}>Ембг</Text>
            <Text onClick={() => handleOrder("Category", 2)}>
              Категорија{" "}
              {orderDirectionArr.current[2] === 0 ? (
                <Minus size={14} />
              ) : orderDirectionArr.current[2] === -1 ? (
                <ChevronDown size={14} />
              ) : (
                <ChevronUp size={14} />
              )}
            </Text>
            <Text onClick={() => handleOrder("Description", 3)}>
              Опис{" "}
              {orderDirectionArr.current[3] === 0 ? (
                <Minus size={14} />
              ) : orderDirectionArr.current[3] === -1 ? (
                <ChevronDown size={14} />
              ) : (
                <ChevronUp size={14} />
              )}
            </Text>
            <Text onClick={() => handleOrder("Given", 4)}>
              Дадено{" "}
              {orderDirectionArr.current[4] === 0 ? (
                <Minus size={14} />
              ) : orderDirectionArr.current[4] === -1 ? (
                <ChevronDown size={14} />
              ) : (
                <ChevronUp size={14} />
              )}
            </Text>
            <Text onClick={() => handleOrder("Got", 5)}>
              Земено{" "}
              {orderDirectionArr.current[5] === 0 ? (
                <Minus size={14} />
              ) : orderDirectionArr.current[5] === -1 ? (
                <ChevronDown size={14} />
              ) : (
                <ChevronUp size={14} />
              )}
            </Text>
            <Text onClick={() => handleOrder("Profit", 6)}>
              Профит{" "}
              {orderDirectionArr.current[6] === 0 ? (
                <Minus size={14} />
              ) : orderDirectionArr.current[6] === -1 ? (
                <ChevronDown size={14} />
              ) : (
                <ChevronUp size={14} />
              )}
            </Text>
            <Text onClick={() => handleOrder("Diff", 7)}>
              Отстапување{" "}
              {orderDirectionArr.current[7] === 0 ? (
                <Minus size={14} />
              ) : orderDirectionArr.current[7] === -1 ? (
                <ChevronDown size={14} />
              ) : (
                <ChevronUp size={14} />
              )}
            </Text>
            <Text onClick={() => handleOrder("Date", 8)}>
              Датум{" "}
              {orderDirectionArr.current[8] === 0 ? (
                <Minus size={14} />
              ) : orderDirectionArr.current[8] === -1 ? (
                <ChevronDown size={14} />
              ) : (
                <ChevronUp size={14} />
              )}
            </Text>
          </TableHeader>
          <ScrollableTransactions ref={scrollableTransactionsRef}>
            {loading ? (
              <Loading />
            ) : (
              allTransactions.map((transaction, index) => (
                <Transaction
                  key={index}
                  style={
                    index % 2 === 1
                      ? { background: "#f0f0f0" }
                      : { background: "#ffffff" }
                  }
                >
                  <TextTransaction>{transaction["Client Id"]}</TextTransaction>
                  <TextTransaction className="bold">
                    {transaction.Name}
                  </TextTransaction>
                  <TextTransaction>{transaction.Embg}</TextTransaction>
                  <TextTransaction>
                    {getCat[transaction.Category] || transaction.Category}
                  </TextTransaction>
                  <TextTransaction>
                    {getDesc[transaction.Description] ||
                      transaction.Description}
                  </TextTransaction>
                  <TextTransaction className="bold color">
                    {Number(transaction.Given).toLocaleString("de-DE")}
                  </TextTransaction>
                  <TextTransaction className="bold color">
                    {Number(transaction.Got).toLocaleString("de-DE")}
                  </TextTransaction>
                  <TextTransaction className="bold color">
                    {Number(transaction.Profit).toLocaleString("de-DE")}
                  </TextTransaction>
                  <TextTransaction
                    className={`bold color ${
                      Number(transaction.Diff) === 0
                        ? ""
                        : Number(transaction.Diff) < 0
                        ? "red"
                        : "green"
                    }`}
                  >
                    {Number(transaction.Diff).toLocaleString("de-DE")}
                  </TextTransaction>
                  <TextTransaction>
                    {transaction.Date.split(".")[0].split("T").join(" ")}
                  </TextTransaction>
                </Transaction>
              ))
            )}
          </ScrollableTransactions>
        </TransactionsWrapper>
        <CashRegister
          refreshDependency={refresh}
          refreshTransactions={() => setRefresh((prev) => !prev)}
        />
      </Container>
    </TransactionsPage>
  );
}

const TransactionsPage = styled.div`
  height: 100vh;
  display: grid;
  grid-template-columns: max(15%, 240px) auto;
`;

const Container = styled.div`
  display: flex;
  flex-direction: column;
  padding: 0.4rem 2rem 0 2rem;
  //padding: 2rem 2rem 0 2rem;
  gap: 0.8rem;
  flex-grow: 1;
  overflow: hidden;
`;

const HeaderWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;

  & > h1 {
    font-size: 1.2rem;
  }
`;

const FilterWrapper = styled.div`
  display: flex;
  justify-content: space-between;
  width: 100%;
`;

const StyledSelect = styled.select`
  border: none;
  border-radius: 0.2rem;
  font-size: 0.8rem;
  width: 20%;
  padding: 0.4rem;
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.2);
`;

const StyledInput = styled.input`
  border: none;
  border-radius: 0.2rem;
  font-size: 0.8rem;
  width: 20%;
  padding: 0.4rem;
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.2);
`;

const DateBox = styled.div`
  display: flex;
  gap: 4px;
  width: fit-content;
`;

const DateInput = styled.input`
  padding: 0.4rem;
  height: fit-content;
  border: none;
  border-radius: 0.2rem;
  box-shadow: 4px 2px 6px rgba(0, 0, 0, 0.2);
  font-size: 0.8rem;

  &:focus {
    border-color: var(--green);
  }
`;

const TransactionsWrapper = styled.div`
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 0.5rem;
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.2);
  overflow: hidden;
  flex-grow: 1;
  min-height: 0;
`;

const TableHeader = styled.div`
  display: grid;
  place-items: center;
  grid-template-columns: 2rem 1.5fr 1.5fr 1fr 2fr repeat(4, 1fr) 2fr;
  gap: 1rem;
  padding: 0.6rem 0.5rem;
  border-bottom: rgba(0, 0, 0, 0.2) 2px solid;
  color: #eee;
  background: #666;
`;

const Text = styled.div`
  font-weight: 500;
  font-size: 0.7rem;
  cursor: pointer;
  display: flex;
  align-items: center;
`;

const ScrollableTransactions = styled.div`
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
`;

const Transaction = styled.div`
  display: grid;
  place-items: center;
  grid-template-columns: 2rem 1.5fr 1.5fr 1fr 2fr repeat(4, 1fr) 2fr;
  gap: 0.4rem;
  padding: 0.2rem;
  border-bottom: rgba(0, 0, 0, 0.2) 2px solid;
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

const TextTransaction = styled.p`
  font-size: 0.7rem;
  font-weight: 400;

  &.bold {
    font-weight: 700;
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
`;
