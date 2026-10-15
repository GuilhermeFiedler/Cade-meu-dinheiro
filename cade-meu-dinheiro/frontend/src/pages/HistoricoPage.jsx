import EstadoCarga from "../components/EstadoCarga.jsx";
import ItemGasto from "../components/ItemGasto.jsx";
import SeletorMes from "../components/SeletorMes.jsx";
import useCarregar from "../hooks/useCarregar.js";
import { excluirGasto, listarGastos } from "../services/gastoService.js";

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

  return (
    <>
      <SeletorMes mes={mes} onMudar={onMudarMes} />
      <EstadoCarga carregando={carregando} erro={erro} onTentarDeNovo={recarregar} />
      {dados && dados.gastos.length === 0 && (
        <div className="info">Nenhum gasto neste mês<br /><br /><button className="btn" onClick={onNovo}>Registrar gasto</button></div>
      )}
      {dados && dados.gastos.map((g) => <ItemGasto key={g.id} gasto={g} onEditar={onEditar} onExcluir={excluir} />)}
    </>
  );
}
