import { brl } from "../utils/formatadores.js";
import { diaMes } from "../utils/datas.js";

export default function ItemGasto({ gasto, onEditar, onExcluir }) {
  return (
    <div className="row">
      <div>
        {gasto.descricao || gasto.categoria}
        <small>{gasto.categoria} · {diaMes(gasto.data)}</small>
      </div>
      <div className="dir">
        {brl(gasto.valor)}<br />
        <button className="link" onClick={() => onEditar(gasto)}>Editar</button>
        <button className="link" onClick={() => onExcluir(gasto)}>Excluir</button>
      </div>
    </div>
  );
}
