import CartaoTotal from "../components/CartaoTotal.jsx";
import EstadoCarga from "../components/EstadoCarga.jsx";
import Icone from "../components/Icone.jsx";
import ItemGasto from "../components/ItemGasto.jsx";
import SeletorMes from "../components/SeletorMes.jsx";
import useCarregar from "../hooks/useCarregar.js";
import { excluirGasto, listarGastos } from "../services/gastoService.js";
import { rotuloDia } from "../utils/datas.js";

/** Agrupa a lista (já ordenada por data) em [data, gastos[]]. */
const agruparPorDia = (gastos) =>
  gastos.reduce((grupos, g) => {
    const ultimo = grupos[grupos.length - 1];
    if (ultimo && ultimo[0] === g.data) ultimo[1].push(g);
    else grupos.push([g.data, [g]]);
    return grupos;
  }, []);

// US03 – Ver gastos do mês (RF05, RF10) e US04 – Excluir gasto (RF07)
export default function HistoricoPage({ mes, onMudarMes, onEditar, onNovo, avisar }) {
  const { carregando, dados, erro, recarregar } = useCarregar(() => listarGastos(mes), [mes]);

  async function excluir(gasto) {
    if (!window.confirm("Excluir este gasto? Essa ação não pode ser desfeita.")) return; // RN08
    try {
      await excluirGasto(gasto.id);
      avisar("Gasto excluído");
      recarregar();
    } catch (e) {
      avisar(e.message, true);
    }
  }

  const gastos = dados ? dados.gastos : [];
  const total = Math.round(gastos.reduce((s, g) => s + Number(g.valor), 0) * 100) / 100;

  return (
    <>
      <SeletorMes mes={mes} onMudar={onMudarMes} />
      {dados && <CartaoTotal rotulo="Total do mês" valor={total} detalhe={gastos.length === 1 ? "1 gasto" : gastos.length + " gastos"} />}
      <EstadoCarga carregando={carregando} erro={erro} onTentarDeNovo={recarregar} />
      {dados && gastos.length === 0 && (
        <div className="info">
          <Icone nome="carteira" tamanho={40} />
          Nenhum gasto neste mês
          <button className="btn" onClick={onNovo}>Registrar gasto</button>
        </div>
      )}
      {agruparPorDia(gastos).map(([data, itens]) => (
        <section key={data} style={{ display: "contents" }}>
          <h2 className="dia">{rotuloDia(data)}</h2>
          {itens.map((g) => <ItemGasto key={g.id} gasto={g} onEditar={onEditar} onExcluir={excluir} />)}
        </section>
      ))}
    </>
  );
}
