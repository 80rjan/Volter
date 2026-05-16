import { useRef, useState } from "react";
import type { RefObject } from "react";
import axios from "axios";
import html2canvas from "html2canvas";
import jsPDF from "jspdf";
import { AnyItemDetailed, GoldItemDetailed, PawnDetailed, PawnRow } from "./types.ts";
import { API_BASE } from "../../shared/api/config.ts";

export function useModalReadMorePawn(initialPawn: PawnDetailed, oldPawnRow: PawnRow, refreshCashReg: () => void) {
    const [pawn, setPawn] = useState(initialPawn);
    const [isEditing, setIsEditing] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [isDownloading, setIsDownloading] = useState(false);
    const [modalPrintDocument, setModalPrintDocument] = useState(false);
    const [whichDocToPrint, setWhichDocToPrint] = useState("insert");

    const editRef = useRef({
        amount: initialPawn.amount,
        interest: initialPawn.interest,
        description: initialPawn.item.description,
        goldGramsDiff: 0,
        defaultDurationDays: initialPawn.defaultDurationDays,
    });

    const hiddenDocRefLoan = useRef<HTMLDivElement>(null);
    const hiddenDocRefPawn = useRef<HTMLDivElement>(null);
    const hiddenDocRefAnnexLoan = useRef<HTMLDivElement>(null);

    const cancelEdit = () => {
        editRef.current = {
            amount: pawn.amount,
            interest: pawn.interest,
            description: pawn.item.description,
            goldGramsDiff: 0,
            defaultDurationDays: pawn.defaultDurationDays,
        };
        setIsEditing(false);
    };

    const handleUpdatePawn = () => {
        setIsLoading(true);
        const payload: any = {
            amount: editRef.current.amount,
            interest: editRef.current.interest,
            durationDays: editRef.current.defaultDurationDays,
            itemModificationRequest: { description: editRef.current.description },
            transactionDescription: editRef.current.description,
        };
        if (pawn.item.itemType === 'GOLD' && editRef.current.goldGramsDiff !== 0) {
            payload.itemModificationRequest.goldWeightGrams =
                Number((pawn.item as GoldItemDetailed).weightGrams) + editRef.current.goldGramsDiff;
        }
        axios.put(`${API_BASE}/pawns/${pawn.id}`, payload)
            .then(res => {
                const data = res.data;
                const newDaysLeft = Math.floor((new Date(data.maturityDate).getTime() - Date.now()) / 86400000);
                const updatedItem: AnyItemDetailed = { ...pawn.item, description: data.item?.description ?? pawn.item.description };
                if (pawn.item.itemType === 'GOLD' && data.item?.weightGrams !== undefined) {
                    (updatedItem as GoldItemDetailed).weightGrams = Number(data.item.weightGrams);
                }
                const updatedPawn: PawnDetailed = {
                    ...pawn,
                    amount: data.amount,
                    interest: data.interest,
                    defaultDurationDays: data.defaultDurationDays,
                    maturityDate: data.maturityDate,
                    daysLeft: newDaysLeft,
                    item: updatedItem,
                };
                oldPawnRow["About"] = updatedItem.description;
                oldPawnRow["Item Cost"] = data.amount;
                oldPawnRow["Provision"] = data.interest;
                oldPawnRow["Total Days"] = data.defaultDurationDays;
                oldPawnRow["Days Left"] = newDaysLeft;
                oldPawnRow["Valid Until"] = data.maturityDate;
                editRef.current.goldGramsDiff = 0;
                editRef.current.defaultDurationDays = data.defaultDurationDays;
                setPawn(updatedPawn);
            })
            .catch(err => console.error("Error updating pawn:", err))
            .finally(() => { setIsEditing(false); setIsLoading(false); refreshCashReg(); });
    };

    const handlePrintDoc = async (ref: RefObject<HTMLDivElement | null>, isLoan: boolean, isAnnex: boolean) => {
        setIsDownloading(true);
        const element = ref.current;
        if (!element) return;
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
            pdf.save(`${isAnnex ? "Aneks_za_dogovor_za_zaem" : isLoan ? "Dogovor_za_zaem" : "Dogovor_za_racen_zalog"}.pdf`);
        } catch (error) { console.error(error); }
        finally { setIsDownloading(false); }
    };

    return {
        pawn,
        isEditing, setIsEditing, cancelEdit,
        isLoading,
        isDownloading,
        modalPrintDocument, setModalPrintDocument,
        whichDocToPrint, setWhichDocToPrint,
        editRef,
        hiddenDocRefLoan, hiddenDocRefPawn, hiddenDocRefAnnexLoan,
        handleUpdatePawn, handlePrintDoc,
    };
}
