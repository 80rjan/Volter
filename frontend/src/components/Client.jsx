import styled from "styled-components";

export default function Client({ client, index }) {

    return (
        <Wrapper style={index % 2 === 1 ? {background: "#f0f0f0"} : {background: "#ffffff"}}>

            <Text>{client.Id}</Text>
            <Text>{client.Name}</Text>
            <Text>
                {client["Telephone 1"]}
                {client["Telephone 2"].trim() !== "" ? ` / ${client["Telephone 2"]}` : ""}
            </Text>
            <Text>{client.City}</Text>
            <Text>{Number(client["Total Pawns"]).toLocaleString("de-DE")}</Text>
            <Text>{Number(client["Active Pawns"]).toLocaleString("de-DE")}</Text>
            <Text>{Number(client["Money Pawns"]).toLocaleString("de-DE")}</Text>
            <Text>{Number(client["Money Provision"]).toLocaleString("de-DE")}</Text>

        </Wrapper>
    )
}


const Wrapper = styled.div`
    display: grid;
    place-items: center;
    grid-template-columns: 2rem repeat(7, 1fr);
    padding: .5rem;
    border-bottom: rgba(0,0,0,0.2) 2px solid;
    svg {
        cursor: pointer;
        transition: all 300ms ease-in-out;
    }
    svg:hover {
        scale: 1.2;
    }
`;

const Text = styled.p`
    font-weight: 500;
    font-size: .8rem;
    
    &.bold {
        font-weight: bold;
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
    
`
