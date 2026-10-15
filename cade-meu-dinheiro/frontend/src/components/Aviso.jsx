export default function Aviso({ aviso }) {
  if (!aviso) return null;
  return <div id="toast" className={aviso.erro ? "bad" : ""} role="status">{aviso.msg}</div>;
}
