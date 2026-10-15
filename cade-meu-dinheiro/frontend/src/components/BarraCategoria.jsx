import { brl } from "../utils/formatadores.js";

export default function BarraCategoria({ item, maior }) {
  return (
    <div className="bar">
      <span>{item.categoria}</span>
      <i><b style={{ width: (Number(item.total) / maior) * 100 + "%", background: item.cor }} /></i>
      <span>{brl(item.total)}</span>
    </div>
  );
}
