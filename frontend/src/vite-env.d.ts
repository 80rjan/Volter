/// <reference types="vite/client" />

interface ImportMetaEnv {
    // Public base URL of the backend API. Baked into the bundle at build time.
    // Defaults to http://localhost:8080 when unset (see shared/api/config.ts).
    readonly VITE_API_BASE?: string;
}

interface ImportMeta {
    readonly env: ImportMetaEnv;
}
