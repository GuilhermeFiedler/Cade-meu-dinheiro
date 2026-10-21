/** Data de hoje (AAAA-MM-DD) no fuso local. */
export const hoje = () => {
  const d = new Date();
  return new Date(d - d.getTimezoneOffset() * 60000).toISOString().slice(0, 10);
};

/** Soma (ou subtrai) meses de um valor "AAAA-MM". */
export const somaMes = (mes, delta) => {
  const [a, m] = mes.split("-").map(Number);
  const d = new Date(a, m - 1 + delta, 1);
  return d.getFullYear() + "-" + String(d.getMonth() + 1).padStart(2, "0");
};

/** "2026-10" -> "Outubro de 2026". */
export const nomeMes = (mes) => {
  const [a, m] = mes.split("-").map(Number);
  const t = new Date(a, m - 1, 1).toLocaleDateString("pt-BR", { month: "long", year: "numeric" });
  return t.charAt(0).toUpperCase() + t.slice(1);
};

/** "2026-10-05" -> "05/10". */
export const diaMes = (data) => data.slice(8) + "/" + data.slice(5, 7);

/** Data de n dias atrás (AAAA-MM-DD) no fuso local. */
export const diasAtras = (n) => {
  const d = new Date();
  d.setDate(d.getDate() - n);
  return new Date(d - d.getTimezoneOffset() * 60000).toISOString().slice(0, 10);
};

/** "Hoje", "Ontem" ou dd/mm. */
export const rotuloDia = (data) => (data === hoje() ? "Hoje" : data === diasAtras(1) ? "Ontem" : diaMes(data));
