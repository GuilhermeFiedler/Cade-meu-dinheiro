import Icone from "./Icone.jsx";

export default function Cabecalho({ titulo, subtitulo, onSair }) {
  return (
    <header className="appbar">
      <span className="logo" aria-hidden="true"><Icone nome="carteira" tamanho={20} /></span>
      <div className="appbar-txt">
        <b>{titulo}</b>
        {subtitulo && <small>{subtitulo}</small>}
      </div>
      {onSair && <button className="link" onClick={onSair}><Icone nome="sair" tamanho={16} />Sair</button>}
    </header>
  );
}
