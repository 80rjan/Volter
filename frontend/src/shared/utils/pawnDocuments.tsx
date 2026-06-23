import { ReactElement } from "react";
import { createRoot } from "react-dom/client";
import axios from "axios";
import { API_BASE } from "../api/config.ts";
import { printElementToPdf } from "./printDocument.ts";
import DogovorZaZaem from "../../domains/pawns/documents/DogovorZaZaem.tsx";
import DogovorZaRacenZalog from "../../domains/pawns/documents/DogovorZaRacenZalog.tsx";
import AneksDogovorZaZaem from "../../domains/pawns/documents/AneksDogovorZaZaem.tsx";
import PotvrdaZaVratenPredmet from "../../domains/pawns/documents/PotvrdaZaVratenPredmet.tsx";

// Data needed by the loan/pawn agreement documents. `idCard` (л.к.бр) isn't
// stored on the customer, so it's optional and defaults to blank.
export interface PawnDocData {
    fullName: string;
    city: string;
    address: string;
    embg: string;
    idCard?: string;
    telephone: string;
    moneyGiven: number;
    pawnDays: number;
    dateFrom: string;
    dateTo: string;
}

const isoDay = (s: string) => s.slice(0, 10);

function addDays(iso: string, n: number): string {
    const [y, m, d] = isoDay(iso).split("-").map(Number);
    return new Date(Date.UTC(y, m - 1, d + n)).toISOString().slice(0, 10);
}

function daysBetween(aIso: string, bIso: string): number {
    const a = Date.parse(isoDay(aIso) + "T00:00:00Z");
    const b = Date.parse(isoDay(bIso) + "T00:00:00Z");
    return Math.round((b - a) / 86_400_000);
}

/**
 * Mount a document component into a throwaway off-screen React root, render it to
 * a PDF, then tear the root down. Lets us generate documents straight from data
 * without keeping hidden components mounted in every modal/row.
 */
async function renderToPdf(node: ReactElement, filename: string, paginated: boolean): Promise<void> {
    const host = document.createElement("div");
    host.style.cssText = "position:fixed;left:-99999px;top:0;width:1000px;";
    document.body.appendChild(host);
    const root = createRoot(host);
    try {
        root.render(node);
        // Force the bundled document font (both weights) to load before capturing,
        // otherwise html2canvas may snapshot with a fallback font and shift the layout.
        if (document.fonts?.load) {
            await Promise.all([
                document.fonts.load('400 12px "VolterDoc"'),
                document.fonts.load('700 12px "VolterDoc"'),
            ]).catch(() => {});
        }
        // Then let layout settle (two frames) and any remaining fonts finish.
        await new Promise<void>(r => requestAnimationFrame(() => requestAnimationFrame(() => r())));
        await document.fonts?.ready?.catch?.(() => {});
        await printElementToPdf(host.firstElementChild as HTMLElement, filename, { paginated });
    } finally {
        root.unmount();
        host.remove();
    }
}

/** On pawn creation: the loan agreement + the pawn (rachen zalog) agreement. */
export async function downloadPawnCreationDocs(data: PawnDocData, pawnInfo: string): Promise<void> {
    const props = { ...data, idCard: data.idCard ?? "" };
    await renderToPdf(<DogovorZaZaem {...props} />, "Dogovor_za_zaem.pdf", true);
    await renderToPdf(<DogovorZaRacenZalog {...props} pawnInfo={pawnInfo} />, "Dogovor_za_racen_zalog.pdf", true);
}

/** Compute the day count + due date from issue date and term, then download. */
export function pawnDocDataFromTerms(
    customer: { fullName: string; city: string; address: string; nationalId: string; phonePrimary: string },
    principalAmount: number,
    termDays: number,
    issueDate: string,
): PawnDocData {
    return {
        fullName: customer.fullName,
        city: customer.city,
        address: customer.address,
        embg: customer.nationalId,
        telephone: customer.phonePrimary,
        moneyGiven: principalAmount,
        pawnDays: termDays,
        dateFrom: isoDay(issueDate),
        dateTo: addDays(issueDate, termDays),
    };
}

/**
 * On pawn extension: the annex. Fetches the pawn so the new period (from/to and
 * day count) comes from the freshly created extension rather than stale UI state.
 */
export async function downloadExtensionDocById(pawnId: number): Promise<void> {
    const det = (await axios.get(`${API_BASE}/pawns/${pawnId}`)).data;
    const exts = det.extensions ?? [];
    if (exts.length === 0) return;
    const ext = [...exts].sort((a, b) => a.id - b.id).pop();
    const c = det.customer;

    await renderToPdf(
        <AneksDogovorZaZaem
            fullName={c.fullName}
            city={c.city}
            address={c.address}
            embg={c.nationalId}
            idCard=""
            telephone={c.phonePrimary}
            moneyGiven={det.principalAmount}
            pawnDays={daysBetween(ext.previousDueDate, ext.newDueDate)}
            dateFrom={isoDay(ext.previousDueDate)}
            dateTo={isoDay(ext.newDueDate)}
        />,
        "Aneks_dogovor_za_zaem.pdf",
        true,
    );
}

/** On pawn redemption: the return-of-pledged-item receipt. */
export async function downloadRedemptionDoc(fullName: string): Promise<void> {
    await renderToPdf(
        <PotvrdaZaVratenPredmet fullName={fullName} />,
        "Potvrda_za_vrakanje_na_zalozhen_predmet.pdf",
        false,
    );
}
