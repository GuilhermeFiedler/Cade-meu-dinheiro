import { hoje, nomeMes, somaMes } from "../utils/datas.js";

// RF10 – trocar o mês exibido
export default function SeletorMes({ mes, onMudar }) {
  return (
    <div className="mes">
      <button onClick={() => onMudar(somaMes(mes, -1))} aria-label="Mês anterior">‹</button>
      <b>{nomeMes(mes)}</b>
      <button onClick={() => onMudar(somaMes(mes, 1))} aria-label="Próximo mês" disabled={mes >= hoje().slice(0, 7)}>›</button>
    </div>
  );
}
