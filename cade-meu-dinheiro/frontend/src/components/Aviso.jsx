import Icone from "./Icone.jsx";

export default function Aviso({ aviso }) {
  if (!aviso) return null;
  return (
    <div id="toast" className={aviso.erro ? "bad" : ""} role="status">
      <Icone nome={aviso.erro ? "alerta" : "ok"} tamanho={18} />{aviso.msg}
    </div>
  );
}
