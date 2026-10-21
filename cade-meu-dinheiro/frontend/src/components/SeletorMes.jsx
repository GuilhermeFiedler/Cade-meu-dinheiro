import { hoje, nomeMes, somaMes } from "../utils/datas.js";
import Icone from "./Icone.jsx";

// RF10 – trocar o mês exibido
export default function SeletorMes({ mes, onMudar }) {
  return (
    <div className="mes">
      <button onClick={() => onMudar(somaMes(mes, -1))} aria-label="Mês anterior"><Icone nome="esquerda" /></button>
      <b>{nomeMes(mes)}</b>
      <button onClick={() => onMudar(somaMes(mes, 1))} aria-label="Próximo mês" disabled={mes >= hoje().slice(0, 7)}><Icone nome="direita" /></button>
    </div>
  );
}
