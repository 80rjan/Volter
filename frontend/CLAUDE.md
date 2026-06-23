# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# From this directory (frontend/):
npm run dev               # Vite dev server on all interfaces (--host)
npm run build             # Vite production build → dist/
npm run preview           # Serve the production build locally
npm run lint              # ESLint
```

There is no test suite in the frontend. Lint is the only static check.

## What this app is

Volter A&B is a **web app** (Vite + React SPA) for a Macedonian pawn shop. The UI is entirely in Macedonian. `npm run build` produces a static `dist/` bundle that can be served by any static host. In development the Vite dev server runs on `http://localhost:5173`.

The backend is a separate **Spring Boot (Java) server** (in `../backend/`). All API calls go to `http://localhost:8080` (configured in `src/shared/api/config.ts`).

## Architecture

**Routing**: `HashRouter` is used (not `BrowserRouter`), so routes resolve under any static host without server-side rewrite rules. Routes are defined in `src/App.tsx`.

**State**: No global state manager. Each page component owns its own state via `useState`/`useRef`. `GlobalContext.tsx` exists but is currently empty — not in use.

**Styling**: Tailwind CSS v4 (`@tailwindcss/vite` plugin). Custom color tokens and utility classes (`.scrollbar-thin`, `.svg-hover`, `.close-x-btn`, `.skeleton-shimmer`) are defined in `src/index.css` using `@theme {}` and `@layer utilities`. MUI (`@mui/material`) is used only for the `Autocomplete` component in the pawn creation form. `lucide-react` provides all icons. Document components use inline `React.CSSProperties` styles (not Tailwind) for html2canvas/jsPDF compatibility.

**Folder structure**: Domain-driven. Each business domain owns its page, row component, modals, documents, and a `types.ts`:
```
src/
  domains/
    pawns/        Pawns.tsx, Pawn.tsx, ModalAddNewPawn.tsx, ModalReadMorePawn.tsx, ModalShowMessagePawn.tsx, documents/, types.ts
    sales/        Sales.tsx, Sale.tsx, ModalAddNewSale.tsx, ModalReadMoreSale.tsx, types.ts
    clients/      Clients.tsx, Client.tsx, ModalReadMoreClient.tsx, types.ts
    expenses/     Expenses.tsx, Expense.tsx, ModalAddNewExpense.tsx, ModalReadMoreExpense.tsx, types.ts
    transactions/ Transactions.tsx, types.ts
    reports/      PeriodReport.tsx, MonthlyReport.tsx, YearlyReport.tsx, ModalReadMoreMonthReport.tsx, types.ts
  shared/
    components/   Nav.tsx, Loading.tsx, GoldPriceLive.tsx, CashRegister.tsx, ModalActions.tsx, ModalAdjustCashRegister.tsx
    utils/        numberInWordsMkd.ts, useDebounce.ts
    api/          CashRegister.ts
    types.ts      (CashRegisterData, ClientRecord — used across domains)
```

**API layer**: All data fetching is done with `axios` directly inside domain files, hitting `http://localhost:8080` (configured in `src/shared/api/config.ts`). `src/shared/api/CashRegister.ts` is a thin wrapper around the cash-register endpoint.

**Document generation**: `src/domains/pawns/documents/` contains print-ready React components (loan agreements, pawn receipts) rendered with `jspdf` + `html2canvas`. `src/shared/utils/numberInWordsMkd.ts` converts numbers to Macedonian words for use in legal documents.

**Pagination**: The Pawns page uses scroll-based infinite loading (offset/limit with a `Set` to deduplicate). A `useRef` guard (`isFetchingRef`) prevents concurrent fetches. The trigger fires when the user scrolls within 30% of the bottom.

**Pawn categories**: `Gold`, `Electronics`, `Watch`, `Vehicle`, `Other`. The add-pawn modal (`ModalAddNewPawn.tsx`) renders different form fields per category via a `renderCategoryInputs()` switch.

**Gold price**: `GoldPriceLive.tsx` fetches `GET /gold/price` once per app session (caching the result in `sessionStorage`) and shows the per-gram price by karat in the nav sidebar. It does **not** poll. The backend proxies goldapi.io and caches the price with a **1-hour TTL**, so the upstream API is hit at most once an hour (no restart needed to refresh) and each new app session gets a price that's at most an hour old, held stable for that session.

## CSS variables

Global CSS variables are defined in `src/index.css`. `--green` is the primary brand color used for active nav links and primary buttons.

## Review

Codex will review your output once you are done.
