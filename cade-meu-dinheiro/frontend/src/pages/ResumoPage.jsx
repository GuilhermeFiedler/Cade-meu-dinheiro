import BarraCategoria from "../components/BarraCategoria.jsx";
import EstadoCarga from "../components/EstadoCarga.jsx";
import SeletorMes from "../components/SeletorMes.jsx";
import useCarregar from "../hooks/useCarregar.js";
import { obterResumo } from "../services/resumoService.js";
import { brl } from "../utils/formatadores.js";

// US05 – Gastos por categoria (RF09) e US06 – Total do mês (RF08)
export default function ResumoPage({ mes, onMudarMes }) {
  const { carregando, dados, erro, recarregar } = useCarregar(() => obterResumo(mes), [mes]);
  const maior = dados ? Math.max(...dados.por_categoria.map((r) => Number(r.total)), 1) : 1;

  return (
    <>
      <SeletorMes mes={mes} onMudar={onMudarMes} />
      <EstadoCarga carregando={carregando} erro={erro} onTentarDeNovo={recarregar} />
      {dados && (<>
        <div className="mut">Total do mês</div>
        <div className="tot">{brl(dados.total)}</div>
        {dados.por_categoria.length === 0 && <div className="info">Nenhum gasto neste mês</div>}
        {dados.por_categoria.map((r) => <BarraCategoria key={r.categoria_id} item={r} maior={maior} />)}
      </>)}
    </>
  );
}
