import BarraCategoria from "../components/BarraCategoria.jsx";
import CartaoTotal from "../components/CartaoTotal.jsx";
import EstadoCarga from "../components/EstadoCarga.jsx";
import Icone from "../components/Icone.jsx";
import Rosca from "../components/Rosca.jsx";
import SeletorMes from "../components/SeletorMes.jsx";
import useCarregar from "../hooks/useCarregar.js";
import { obterResumo } from "../services/resumoService.js";

// US05 – Gastos por categoria (RF09) e US06 – Total do mês (RF08)
export default function ResumoPage({ mes, onMudarMes }) {
  const { carregando, dados, erro, recarregar } = useCarregar(() => obterResumo(mes), [mes]);
  const itens = dados ? dados.por_categoria : [];
  const maior = Math.max(...itens.map((r) => Number(r.total)), 1);
  const principal = itens[0];

  return (
    <>
      <SeletorMes mes={mes} onMudar={onMudarMes} />
      <EstadoCarga carregando={carregando} erro={erro} onTentarDeNovo={recarregar} />
      {dados && (<>
        <CartaoTotal rotulo="Total do mês" valor={dados.total}
                     detalhe={itens.length === 1 ? "1 categoria" : itens.length + " categorias"} />
        {itens.length === 0 && (
          <div className="info"><Icone nome="carteira" tamanho={40} />Nenhum gasto neste mês</div>
        )}
        {itens.length > 0 && (
          <section className="card">
            <div className="rosca-box">
              <Rosca itens={itens} total={dados.total} />
              <div className="rosca-centro">
                <strong>{Math.round((Number(principal.total) / (Number(dados.total) || 1)) * 100)}%</strong>
                <small>{principal.categoria}</small>
              </div>
            </div>
            {itens.map((r) => <BarraCategoria key={r.categoria_id} item={r} maior={maior} />)}
          </section>
        )}
      </>)}
    </>
  );
}
