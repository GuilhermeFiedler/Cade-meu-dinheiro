import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// O frontend chama "/api/..." e o Vite repassa para o backend Java em localhost:8080.
export default defineConfig({
  plugins: [react()],
  server: { port: 5173, open: true, proxy: { "/api": "http://localhost:8080" } },
});
