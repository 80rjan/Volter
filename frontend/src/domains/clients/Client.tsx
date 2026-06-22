import { Ellipsis } from "lucide-react";
import { useState } from "react";
import ModalReadMoreClient from "./ModalReadMoreClient.tsx";
import { Customer } from "./types.ts";

interface Props {
    client: Customer;
    isOdd: boolean;
    cols: string;
    onUpdated: (updated: Customer) => void;
}

const phone = (c: Customer) =>
    c.phoneSecondary && c.phoneSecondary.trim() !== "" ? `${c.phonePrimary} / ${c.phoneSecondary}` : c.phonePrimary;

export default function Client({ client, isOdd, cols, onUpdated }: Props) {
    const [modalReadMore, setModalReadMore] = useState(false);

    return (
        <div
            style={{ background: isOdd ? "#f0f0f0" : "#ffffff" }}
            className={`grid place-items-center text-center ${cols} py-1 px-2 border-b border-black/20 svg-hover`}
        >
            <p className="text-xs">{client.id}</p>
            <p className="text-xs font-bold">{client.fullName}</p>
            <p className="text-xs">{client.nationalId}</p>
            <p className="text-xs">{phone(client)}</p>
            <p className="text-xs">{client.city}</p>
            <p className="text-xs">{String(client.createdAt).substring(0, 10)}</p>
            <Ellipsis size={20} color="#888" className="cursor-pointer" onClick={() => setModalReadMore(true)} />

            {modalReadMore && (
                <ModalReadMoreClient
                    client={client}
                    closeModal={() => setModalReadMore(false)}
                    onUpdated={onUpdated}
                />
            )}
        </div>
    );
}
