import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],

  // En producción el build se copia dentro del JAR de Spring Boot
  build: {
    outDir: '../src/main/resources/static',
    emptyOutDir: true,
  },

  server: {
    port: 5173,
    // Solo activo en desarrollo (npm run dev)
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
