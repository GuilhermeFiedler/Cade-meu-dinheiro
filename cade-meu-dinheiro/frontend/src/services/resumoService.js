import { api } from "./api.js";

export const obterResumo = (mes) => api("/resumo?mes=" + mes);
