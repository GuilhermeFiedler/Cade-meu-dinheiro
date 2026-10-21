import { brl } from "../utils/formatadores.js";

/** Gráfico de rosca (SVG) com uma fatia por categoria. */
export default function Rosca({ itens, total }) {
  const r = 42;
  const c = 2 * Math.PI * r;
  const soma = itens.reduce((s, i) => s + Number(i.total), 0) || 1;
  let acumulado = 0;
  return (
    <svg className="rosca" viewBox="0 0 120 120" role="img" aria-label={"Gastos por categoria, total " + brl(total)}>
      <circle cx="60" cy="60" r={r} fill="none" stroke="var(--line)" strokeWidth="14" />
      {itens.map((i) => {
        const fracao = Number(i.total) / soma;
        const fatia = (
          <circle key={i.categoria_id} cx="60" cy="60" r={r} fill="none" stroke={i.cor} strokeWidth="14"
                  strokeDasharray={`${Math.max(fracao * c - 1.5, 0)} ${c}`} strokeDashoffset={-acumulado * c}
                  transform="rotate(-90 60 60)" />
        );
        acumulado += fracao;
        return fatia;
      })}
    </svg>
  );
}
