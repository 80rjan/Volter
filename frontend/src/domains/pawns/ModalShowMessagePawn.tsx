import ReactDom from "react-dom";
import { CircleCheckBig } from 'lucide-react';

interface Props {
    closeModal: () => void;
    successMsg: string;
    infoMsg: string;
    // Kept for call-site compatibility; documents are now generated at the action
    // site (create/extend/redeem), not from this modal.
    clientName?: string;
}

export default function ModalShowMessagePawn({ closeModal, successMsg, infoMsg }: Props) {
    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" onClick={closeModal} />
            <div className="flex flex-col items-center gap-4 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-white z-[1000] px-8 py-9 rounded-2xl shadow-[0_10px_40px_rgba(0,0,0,0.3)] w-[min(420px,92%)] text-center">
                <span className="flex h-16 w-16 items-center justify-center rounded-full bg-green/15">
                    <CircleCheckBig size={38} className="text-green" />
                </span>
                <h1 className="text-xl font-semibold leading-snug">{successMsg}</h1>
                {infoMsg && <p className="text-sm text-[#666]">{infoMsg}</p>}
                <button
                    onClick={closeModal}
                    className="mt-2 w-full flex justify-center items-center px-6 py-2.5 rounded-lg bg-green text-white font-medium shadow-[0_4px_10px_rgba(0,0,0,0.15)] transition-all duration-200 hover:scale-[1.02] active:scale-[0.98]"
                >
                    Во ред
                </button>
            </div>
        </>,
        document.getElementById("portal")!
    );
}
