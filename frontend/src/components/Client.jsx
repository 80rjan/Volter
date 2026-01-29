import styled from "styled-components";
import { Ellipsis } from "lucide-react";
import { useState } from "react";
import ModalReadMoreClient from "./ModalReadMoreClient.jsx";

export default function Client({ client, index, updateTelephones }) {
  const [modalReadMore, setModalReadMore] = useState(false);
  
  return (
    <Wrapper
      style={
        index % 2 === 1 ? { background: "#f0f0f0" } : { background: "#ffffff" }
      }
    >
      <Text>{client.Id}</Text>
      <Text className="bold">{client.Name}</Text>
      <Text>
        {client["Telephone 1"]}
        {client["Telephone 2"].trim() !== ""
          ? ` / ${client["Telephone 2"]}`
          : ""}
      </Text>
      <Text>{client.City}</Text>
      <Text>{Number(client["Total Pawns"]).toLocaleString("de-DE")}</Text>
      <Text>{Number(client["Active Pawns"]).toLocaleString("de-DE")}</Text>
      <Text>{Number(client["Money Pawns"]).toLocaleString("de-DE")}</Text>
      <Text className="bold color">
        {Number(client["Money Provision"]).toLocaleString("de-DE")}
      </Text>
      <Text>
        <Ellipsis size={20} color="#888" onClick={() => {setModalReadMore(true)}} />
      </Text>

      {modalReadMore && (
        <ModalReadMoreClient
          client={client}
          closeModal={() => setModalReadMore(false)}
          updateTelephones={updateTelephones}
        />
      )}
    </Wrapper>
  );
}

const Wrapper = styled.div`
  display: grid;
  place-items: center;
  grid-template-columns: 3rem repeat(8, 1fr);
  padding: 0.2rem;
  border-bottom: rgba(0, 0, 0, 0.2) 2px solid;
  svg {
    cursor: pointer;
    transition: all 300ms ease-in-out;
  }
  svg:hover {
    scale: 1.2;
  }
`;

const Text = styled.p`
  font-weight: 400;
  font-size: 0.7rem;

  &.bold {
    font-weight: 700;
  }
  &.color {
    font-style: italic;
  }
  &.red {
    color: red;
  }
  &.orange {
    color: orangered;
  }
  &.green {
    color: var(--green);
  }
`;
