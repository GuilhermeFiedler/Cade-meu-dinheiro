/** Formata número como moeda brasileira (RN10). */
export const brl = (n) => Number(n).toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
