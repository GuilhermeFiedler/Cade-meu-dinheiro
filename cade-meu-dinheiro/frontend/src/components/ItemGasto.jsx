import { brl } from "../utils/formatadores.js";
import Icone from "./Icone.jsx";

export default function ItemGasto({ gasto, onEditar, onExcluir }) {
  return (
    <div className="row" style={{ "--cor": gasto.cor }}>
      <span className="avatar" aria-hidden="true">{gasto.categoria.charAt(0)}</span>
      <div className="row-info">
        <strong>{gasto.descricao || gasto.categoria}</strong>
        {gasto.descricao && <small>{gasto.categoria}</small>}
      </div>
      <div className="row-dir">
        <span className="valor">{brl(gasto.valor)}</span>
        <span className="acoes">
          <button className="link" onClick={() => onEditar(gasto)}><Icone nome="editar" tamanho={15} />Editar</button>
          <button className="link" onClick={() => onExcluir(gasto)}><Icone nome="excluir" tamanho={15} />Excluir</button>
        </span>
      </div>
    </div>
  );
}
