import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import path from 'path'

export default defineConfig({
  plugins: [react(), tailwindcss()],
  base: './',
  server: {
    host: true,            // bind 0.0.0.0 so the container is reachable from the host
    port: 5173,
    watch: {
      usePolling: true,    // detect file changes across the Docker bind mount
    },
  },
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
  },
})
