import { api } from "./api.js";

export const listarGastos = (mes) => api("/gastos?mes=" + mes);
export const criarGasto = (dados) => api("/gastos", { method: "POST", body: dados });
export const atualizarGasto = (id, dados) => api("/gastos/" + id, { method: "PUT", body: dados });
export const excluirGasto = (id) => api("/gastos/" + id, { method: "DELETE" });
