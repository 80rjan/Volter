import { Ellipsis } from "lucide-react";
import { useState } from "react";
import ModalReadMoreClient from "./ModalReadMoreClient.tsx";
import { ClientRow } from "../types.ts";

interface Props {
    client: ClientRow;
    index: number;
    updateTelephones: (tel1: string, tel2: string) => void;
}

export default function Client({ client, index, updateTelephones }: Props) {
    const [modalReadMore, setModalReadMore] = useState(false);

    return (
        <div
            style={{ background: index % 2 === 1 ? "#f0f0f0" : "#ffffff" }}
            className="grid place-items-center text-center grid-cols-[3rem_repeat(8,1fr)] py-1 px-2 border-b border-black/20 svg-hover"
        >
            <p className="text-xs">{client.Id}</p>
            <p className="text-xs font-bold">{client.Name}</p>
            <p className="text-xs">{client["Telephone 1"]}{client["Telephone 2"].trim() !== "" ? ` / ${client["Telephone 2"]}` : ""}</p>
            <p className="text-xs">{client.City}</p>
            <p className="text-xs">{Number(client["Total Pawns"]).toLocaleString("de-DE")}</p>
            <p className="text-xs">{Number(client["Active Pawns"]).toLocaleString("de-DE")}</p>
            <p className="text-xs">{Number(client["Money Pawns"]).toLocaleString("de-DE")}</p>
            <p className="text-xs font-bold italic">{Number(client["Money Provision"]).toLocaleString("de-DE")}</p>
            <Ellipsis size={20} color="#888" className="cursor-pointer" onClick={() => setModalReadMore(true)} />

            {modalReadMore && (
                <ModalReadMoreClient
                    client={client}
                    closeModal={() => setModalReadMore(false)}
                    updateTelephones={updateTelephones}
                />
            )}
        </div>
    );
}
