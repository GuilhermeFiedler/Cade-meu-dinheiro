import { brl } from "../utils/formatadores.js";

/** Cartão verde com o total do mês. */
export default function CartaoTotal({ rotulo, valor, detalhe }) {
  return (
    <section className="cartao-total">
      <span className="rotulo">{rotulo}</span>
      <strong className="tot">{brl(valor)}</strong>
      {detalhe && <span className="detalhe">{detalhe}</span>}
    </section>
  );
}
