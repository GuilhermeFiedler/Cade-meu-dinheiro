import { api } from "./api.js";

export const entrar = (email, senha) => api("/auth/entrar", { method: "POST", body: { email, senha } });
export const registrar = (nome, email, senha) => api("/auth/registrar", { method: "POST", body: { nome, email, senha } });
