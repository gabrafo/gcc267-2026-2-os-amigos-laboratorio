import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    // Em desenvolvimento, as chamadas para /api vao para o back-end, evitando problema de CORS.
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})
