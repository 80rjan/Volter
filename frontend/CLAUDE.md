# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# From the project root (../):
npm run dev               # Start backend + frontend + Electron together (development)
npm run start-frontend    # Start only the Vite dev server
npm run build             # Build the Electron distributable (runs electron-builder)

# From this directory (frontend/):
npm run dev               # Vite dev server on all interfaces (--host)
npm run build             # Vite production build → dist/
npm run lint              # ESLint
```

There is no test suite in the frontend. Lint is the only static check.

## What this app is

Volter A&B is an **Electron desktop app** for a Macedonian pawn shop. The UI is entirely in Macedonian. The frontend is a Vite + React SPA embedded inside Electron; in production, Electron loads `frontend/dist/index.html` as a static file. In development, the Vite dev server runs separately and Electron points at `http://localhost:5173` (or the built file).

The backend is a separate Node.js Express server (in `../backend/`) that Electron spawns as a child process on startup. All API calls go to `http://localhost:3000`.

## Architecture

**Routing**: `HashRouter` is used (not `BrowserRouter`) because the app loads as a local file in production. Routes are defined in `src/App.jsx`.

**State**: No global state manager. Each page component owns its own state via `useState`/`useRef`. `GlobalContext.jsx` exists but is currently empty — not in use.

**Styling**: `styled-components` for all component styles, defined inline at the bottom of each file. MUI (`@mui/material`) is used only for the `Autocomplete` component in the pawn creation form. `lucide-react` provides all icons.

**API layer**: All data fetching is done with `axios` directly inside page/component files. The only abstraction is `src/api/CashRegister.js`, which has a special branch for `window.electronAPI` (an IPC bridge) vs. HTTP. The rest of the app always hits `http://localhost:3000` directly.

**Document generation**: `src/documents/` contains print-ready React components (loan agreements, pawn receipts) rendered with `jspdf` + `html2canvas`. `src/utils/numberInWordsMkd.js` converts numbers to Macedonian words for use in legal documents.

**Pagination**: The Pawns page uses scroll-based infinite loading (offset/limit with a `Set` to deduplicate). A `useRef` guard (`isFetchingRef`) prevents concurrent fetches. The trigger fires when the user scrolls within 30% of the bottom.

**Pawn categories**: `Gold`, `Electronics`, `Watch`, `Vehicle`, `Other`. The add-pawn modal (`ModalAddNewPawn.jsx`) renders different form fields per category via a `renderCategoryInputs()` switch.

**Live gold price**: `GoldPriceLive.jsx` polls `GET /goldPriceLive` every 5 minutes and displays the price per gram. The nav sidebar always shows this.

## CSS variables

Global CSS variables are defined in `src/index.css`. `--green` is the primary brand color used for active nav links and primary buttons.
