/** Mostra "Carregando…" ou a mensagem de erro com botão para tentar de novo. Não mostra nada quando está pronto. */
export default function EstadoCarga({ carregando, erro, onTentarDeNovo }) {
  if (carregando) return <div className="info">Carregando…</div>;
  if (erro) {
    return (
      <div className="info">
        <span className="err">{erro}</span><br /><br />
        <button className="btn sec" onClick={onTentarDeNovo}>Tentar de novo</button>
      </div>
    );
  }
  return null;
}
