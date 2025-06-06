import {defineConfig} from 'vite'
import react from '@vitejs/plugin-react-swc'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
    plugins: [
        react(),
        tailwindcss(),
    ],
    server: {
        watch: {
            usePolling: true,
        },
        host: true,
        strictPort: true,
        port: 5173,
        allowedHosts: ['35.209.62.223', 'http://localhost:5173'],
        proxy: {
            '/api': {
                target: 'http://35.209.62.223',
                changeOrigin: true,
                secure: false
            }
        }
    }
})
