import ReactDom from "react-dom";
import { X, CircleCheckBig } from 'lucide-react';
import jsPDF from "jspdf";
import html2canvas from "html2canvas";
import { useRef } from "react";
import PotvrdaZaVratenPredmet from "../documents/PotvrdaZaVratenPredmet.tsx";

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

    return ReactDom.createPortal(
        <>
            <div className="fixed inset-0 bg-black/70 z-[1000]" />
            <div className="flex flex-col items-center gap-3 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-[#eee] z-[1000] p-4 pb-8 px-8 rounded-lg min-w-fit max-w-[90%]">
                <X
                    size={32}
                    className="ml-auto close-x-btn"
                    onClick={() => {
                        if (successMsg.includes("затворен залог"))
                            handlePrintDoc(hiddenDocForClosingPawn).catch(console.error);
                        closeModal();
                    }}
                />
                <CircleCheckBig size={120} className="text-green mx-auto" />
                <h1 className="text-center">{successMsg}</h1>
                <p className="text-xl font-normal">{infoMsg}</p>
            </div>
            <div style={{ height: "0px", width: "0px", overflow: "hidden" }}>
                <PotvrdaZaVratenPredmet ref={hiddenDocForClosingPawn} fullName={clientName} />
            </div>
        </>,
        document.getElementById("portal")!
    );
}
