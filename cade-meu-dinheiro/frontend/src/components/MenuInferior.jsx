const ABAS = [["novo", "Novo"], ["hist", "Histórico"], ["resumo", "Resumo"]];

export default function MenuInferior({ atual, onIr }) {
  return (
    <nav>
      {ABAS.map(([chave, nome]) => (
        <button key={chave} className={atual === chave ? "on" : ""} onClick={() => onIr(chave)}>{nome}</button>
      ))}
    </nav>
  );
}
