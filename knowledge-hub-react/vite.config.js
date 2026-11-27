import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
    plugins: [react()],

    server: {
        proxy: {
            '/content': {
                target: 'http://localhost:4502',
                changeOrigin: true
            }
        }
    }
});
