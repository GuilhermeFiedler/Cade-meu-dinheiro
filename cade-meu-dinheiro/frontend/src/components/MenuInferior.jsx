import Icone from "./Icone.jsx";

const ABAS = [["novo", "Novo"], ["hist", "Histórico"], ["resumo", "Resumo"]];

export default function MenuInferior({ atual, onIr }) {
  return (
    <nav aria-label="Navegação principal">
      {ABAS.map(([chave, nome]) => (
        <button key={chave} className={atual === chave ? "on" : ""} onClick={() => onIr(chave)}
                aria-current={atual === chave ? "page" : undefined}>
          <Icone nome={chave} /><span>{nome}</span>
        </button>
      ))}
    </nav>
  );
}
