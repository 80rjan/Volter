import ReactDom from "react-dom";
import { CircleCheckBig } from 'lucide-react';
import jsPDF from "jspdf";
import html2canvas from "html2canvas";
import { useRef } from "react";
import PotvrdaZaVratenPredmet from "./documents/PotvrdaZaVratenPredmet.tsx";

interface Props {
    closeModal: () => void;
    successMsg: string;
    infoMsg: string;
    clientName: string;
}

export default function ModalShowMessagePawn({ closeModal, successMsg, infoMsg, clientName }: Props) {
    const hiddenDocForClosingPawn = useRef<HTMLDivElement>(null);

    const handlePrintDoc = async (ref: React.RefObject<HTMLDivElement | null>) => {
        const element = ref.current;
        if (!element) return;
        element.style.width = "1000px";

        const pdf = new jsPDF({ orientation: "portrait", unit: "mm", format: "a4" });

        try {
            const canvas = await html2canvas(element, { scale: 2, useCORS: true });
            const imgData = canvas.toDataURL("image/png");
            const imgWidth = 210;
            const pageHeight = 297;
            const imgHeight = (canvas.height * imgWidth) / canvas.width;
            let heightLeft = imgHeight;
            let position = 0;

            pdf.addImage(imgData, "PNG", 0, position, imgWidth, imgHeight);
            while (heightLeft > pageHeight) {
                position -= pageHeight;
                pdf.addPage();
                pdf.addImage(imgData, "PNG", 0, position, imgWidth, imgHeight);
                heightLeft -= pageHeight;
            }
            pdf.save(`Potvrda_za_vrakanje_na_zalozhen_predmet.pdf`);
        } catch (error) {
            console.error(error);
        }
    };

    // Closing a pawn prints the return receipt on the way out.
    const handleClose = () => {
        if (successMsg.includes("затворен залог")) {
            handlePrintDoc(hiddenDocForClosingPawn).catch(console.error);
        }
        closeModal();
    };

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" onClick={handleClose} />
            <div className="flex flex-col items-center gap-4 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-white z-[1000] px-8 py-9 rounded-2xl shadow-[0_10px_40px_rgba(0,0,0,0.3)] w-[min(420px,92%)] text-center">
                <span className="flex h-16 w-16 items-center justify-center rounded-full bg-green/15">
                    <CircleCheckBig size={38} className="text-green" />
                </span>
                <h1 className="text-xl font-semibold leading-snug">{successMsg}</h1>
                {infoMsg && <p className="text-sm text-[#666]">{infoMsg}</p>}
                <button
                    onClick={handleClose}
                    className="mt-2 w-full flex justify-center items-center px-6 py-2.5 rounded-lg bg-green text-white font-medium shadow-[0_4px_10px_rgba(0,0,0,0.15)] transition-all duration-200 hover:scale-[1.02] active:scale-[0.98]"
                >
                    Во ред
                </button>
            </div>
            <div style={{ height: "0px", width: "0px", overflow: "hidden" }}>
                <PotvrdaZaVratenPredmet ref={hiddenDocForClosingPawn} fullName={clientName} />
            </div>
        </>,
        document.getElementById("portal")!
    );
}
