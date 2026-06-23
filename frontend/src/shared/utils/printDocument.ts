import jsPDF from "jspdf";
import html2canvas from "html2canvas";

// The documents were authored against a 1000px-wide layout. We always capture at
// that fixed width (never a content/viewport-driven size) so line wrapping — and
// therefore the whole layout — is identical on every machine.
const PAGE_W = 1000;
const A4_RATIO = 297 / 210; // portrait height / width
const PAGE_H = Math.round(PAGE_W * A4_RATIO); // 1414 — one A4 page in capture px

// Render with the app-bundled font (see @font-face "VolterDoc" in index.css), so
// wrapping never falls back to each OS's own Cyrillic font — the cause of the
// "looks different per laptop" bug. It's Liberation Sans (Arial-metric), so the
// layout is identical to the Arial fallbacks listed after it.
const DOC_FONT = '"VolterDoc", Arial, "Liberation Sans", "Helvetica Neue", Helvetica, sans-serif';

interface Opts {
    // Paginated documents (multi-article contracts) flow across as many A4 pages
    // as needed, with page breaks snapped to whitespace. Non-paginated documents
    // (short receipts) are locked to exactly one A4 page.
    paginated?: boolean;
}

/**
 * Render a document element into an A4 PDF and download it.
 *
 * Fixed capture width + pinned font => the output is byte-for-byte consistent
 * across machines. Single-page mode clamps to one A4 page; paginated mode slices
 * the tall render into A4-height pages, snapping each break to a blank row so a
 * line of text is never cut in half.
 */
export async function printElementToPdf(
    element: HTMLElement | null,
    filename: string,
    opts: Opts = {},
): Promise<void> {
    if (!element) return;

    const prev = {
        width: element.style.width,
        height: element.style.height,
        overflow: element.style.overflow,
        boxSizing: element.style.boxSizing,
        fontFamily: element.style.fontFamily,
        background: element.style.background,
    };

    element.style.width = `${PAGE_W}px`;
    element.style.boxSizing = "border-box";
    element.style.fontFamily = DOC_FONT;
    element.style.background = "#ffffff";
    if (!opts.paginated) {
        element.style.height = `${PAGE_H}px`;
        element.style.overflow = "hidden";
    }

    try {
        const canvas = await html2canvas(element, {
            scale: 2, // ~192dpi — crisp text, reasonable file size
            useCORS: true,
            backgroundColor: "#ffffff",
            width: PAGE_W,
            windowWidth: PAGE_W,
            ...(opts.paginated ? {} : { height: PAGE_H, windowHeight: PAGE_H }),
        });

        const pdf = new jsPDF({ orientation: "portrait", unit: "mm", format: "a4" });
        if (opts.paginated) {
            addPaginated(pdf, canvas);
        } else {
            // One image, one full A4 page — never a stray second page.
            pdf.addImage(canvas.toDataURL("image/png"), "PNG", 0, 0, 210, 297);
        }
        pdf.save(filename);
    } finally {
        element.style.width = prev.width;
        element.style.height = prev.height;
        element.style.overflow = prev.overflow;
        element.style.boxSizing = prev.boxSizing;
        element.style.fontFamily = prev.fontFamily;
        element.style.background = prev.background;
    }
}

/**
 * Slice a tall capture into A4 pages. Each page boundary is nudged up to the
 * nearest blank row (within ~20% of a page) so breaks land in the gaps between
 * articles/lines rather than through them.
 */
function addPaginated(pdf: jsPDF, canvas: HTMLCanvasElement): void {
    const pageHeightPx = Math.floor(canvas.width * A4_RATIO);
    const ctx = canvas.getContext("2d", { willReadFrequently: true });

    let top = 0;
    let first = true;
    while (top < canvas.height) {
        let sliceH = Math.min(pageHeightPx, canvas.height - top);

        // For every page except the last, try to break on whitespace.
        if (ctx && top + sliceH < canvas.height) {
            const snapped = findWhitespaceBreak(ctx, canvas.width, top + sliceH, Math.floor(pageHeightPx * 0.2));
            // Only accept a snap that still leaves a sensibly tall page.
            if (snapped - top > pageHeightPx * 0.5) sliceH = snapped - top;
        }

        const slice = document.createElement("canvas");
        slice.width = canvas.width;
        slice.height = sliceH;
        const sctx = slice.getContext("2d")!;
        sctx.fillStyle = "#ffffff";
        sctx.fillRect(0, 0, slice.width, sliceH);
        sctx.drawImage(canvas, 0, top, canvas.width, sliceH, 0, 0, canvas.width, sliceH);

        const imgH = (sliceH / canvas.width) * 210; // mm, preserving aspect
        if (!first) pdf.addPage();
        pdf.addImage(slice.toDataURL("image/png"), "PNG", 0, 0, 210, imgH);

        first = false;
        top += sliceH;
    }
}

/**
 * Scan upward from `target` for the first fully-white row, up to `searchUp` px.
 * Returns that row, or `target` if none is found (or pixels can't be read).
 */
function findWhitespaceBreak(
    ctx: CanvasRenderingContext2D,
    width: number,
    target: number,
    searchUp: number,
): number {
    const top = Math.max(0, target - searchUp);
    let block: Uint8ClampedArray;
    try {
        block = ctx.getImageData(0, top, width, target - top).data;
    } catch {
        return target; // tainted canvas (shouldn't happen — text only) → hard cut
    }

    for (let y = target - top - 1; y >= 0; y--) {
        const base = y * width * 4;
        let white = true;
        // Sample every 4th pixel across the row — enough to detect any glyph.
        for (let x = 0; x < width; x += 4) {
            const i = base + x * 4;
            if (block[i] < 245 || block[i + 1] < 245 || block[i + 2] < 245) {
                white = false;
                break;
            }
        }
        if (white) return top + y;
    }
    return target;
}
