import { useState } from "react";
import SeletorCategoria from "../components/SeletorCategoria.jsx";
import { atualizarGasto, criarGasto } from "../services/gastoService.js";
import { diasAtras, hoje } from "../utils/datas.js";

// US02 – Registrar gasto (RF03, RF04) e US04 – Editar gasto (RF06)
export default function NovoGastoPage({ categorias, edit, avisar, onSalvo, onCancelar }) {
  const [valor, setValor] = useState(edit ? String(edit.valor).replace(".", ",") : "");
  const [categoria, setCategoria] = useState(edit ? edit.categoria_id : null);
  const [descricao, setDescricao] = useState(edit?.descricao || "");
  const [data, setData] = useState(edit ? edit.data : hoje());
  const [erros, setErros] = useState({});
  const [salvando, setSalvando] = useState(false);

  async function enviar(ev) {
    ev.preventDefault();
    const limpo = valor.trim().replace(",", ".");
    const e = {};
    if (!(Number(limpo) > 0)) e.valor = "Informe um valor maior que zero";
    else if (categoria == null) e.categoria_id = "Escolha uma categoria";
    else if (data > hoje()) e.data = "A data não pode ser futura";
    setErros(e);
    if (Object.keys(e).length) return;
    setSalvando(true);
    const dados = { valor: limpo, categoria_id: categoria, data, descricao };
    try {
      await (edit ? atualizarGasto(edit.id, dados) : criarGasto(dados));
      avisar(edit ? "Gasto atualizado" : "Gasto salvo");
      onSalvo(data.slice(0, 7));
    } catch (x) {
      setErros({ [x.campo || "geral"]: x.message });
      setSalvando(false);
    }
  }

  return (
    <form onSubmit={enviar} noValidate className="form">
      <div className="valor-campo">
        <span aria-hidden="true">R$</span>
        <input className="big" inputMode="decimal" autoComplete="off" placeholder="0,00" aria-label="Valor"
               value={valor} onChange={(e) => setValor(e.target.value)} autoFocus={!edit} />
      </div>
      <div className="err">{erros.valor}</div>
      <SeletorCategoria categorias={categorias} valor={categoria} onMudar={setCategoria} />
      <div className="err">{erros.categoria_id}</div>
      <input maxLength={100} placeholder="Descrição (opcional)" value={descricao} onChange={(e) => setDescricao(e.target.value)} />
      <div className="err">{erros.descricao}</div>
      <label className="campo">Data
        <input type="date" max={hoje()} value={data} onChange={(e) => setData(e.target.value)} />
      </label>
      <div className="atalhos">
        <button type="button" className="atalho" onClick={() => setData(hoje())}>Hoje</button>
        <button type="button" className="atalho" onClick={() => setData(diasAtras(1))}>Ontem</button>
      </div>
      <div className="err">{erros.data}</div>
      <button className="btn" disabled={salvando}>{salvando ? "Salvando…" : edit ? "Salvar alterações" : "Salvar gasto"}</button>
      {edit && <button type="button" className="btn sec" onClick={onCancelar}>Cancelar</button>}
      <div className="err" role="alert">{erros.geral}</div>
    </form>
  );
}
