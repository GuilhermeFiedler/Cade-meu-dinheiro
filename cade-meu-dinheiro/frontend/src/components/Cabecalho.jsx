export default function Cabecalho({ titulo, onSair }) {
  return (
    <header>
      <b>{titulo}</b>
      {onSair && <button className="link" onClick={onSair}>Sair</button>}
    </header>
  );
}
