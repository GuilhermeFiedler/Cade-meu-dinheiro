import { useEffect, useState } from "react";
import Aviso from "./components/Aviso.jsx";
import Cabecalho from "./components/Cabecalho.jsx";
import MenuInferior from "./components/MenuInferior.jsx";
import useAviso from "./hooks/useAviso.js";
import useSessao from "./hooks/useSessao.js";
import HistoricoPage from "./pages/HistoricoPage.jsx";
import LoginPage from "./pages/LoginPage.jsx";
import NovoGastoPage from "./pages/NovoGastoPage.jsx";
import ResumoPage from "./pages/ResumoPage.jsx";
import { listarCategorias } from "./services/categoriaService.js";
import { hoje } from "./utils/datas.js";

const TITULOS = { novo: "Novo gasto", hist: "Histórico", resumo: "Resumo do mês" };

export default function App() {
  const { aviso, avisar } = useAviso();
  const { usuario, entrou, sair } = useSessao(avisar);
  const [tela, setTela] = useState("novo");
  const [mes, setMes] = useState(hoje().slice(0, 7));
  const [edit, setEdit] = useState(null);
  const [categorias, setCategorias] = useState([]);

  useEffect(() => {
    if (usuario) listarCategorias().then(setCategorias).catch((e) => avisar(e.message, true));
    else { setTela("novo"); setEdit(null); }
  }, [usuario, avisar]);

  const ir = (t) => { setTela(t); setEdit(null); };

  if (!usuario) {
    return <><LoginPage onEntrou={entrou} /><Aviso aviso={aviso} /></>;
  }
  return (
    <>
      <div id="app">
        <Cabecalho titulo={tela === "novo" && edit ? "Editar gasto" : TITULOS[tela]}
          subtitulo={"Olá, " + usuario.nome.split(" ")[0]} onSair={sair} />
        <main>
          {tela === "novo" && (
            <NovoGastoPage key={edit ? edit.id : "novo"} categorias={categorias} edit={edit} avisar={avisar}
              onSalvo={(m) => { setMes(m); setEdit(null); setTela("hist"); }}
              onCancelar={() => { setEdit(null); setTela("hist"); }} />
          )}
          {tela === "hist" && (
            <HistoricoPage mes={mes} onMudarMes={setMes} avisar={avisar} onNovo={() => ir("novo")}
              onEditar={(g) => { setEdit(g); setTela("novo"); }} />
          )}
          {tela === "resumo" && <ResumoPage mes={mes} onMudarMes={setMes} />}
        </main>
        <MenuInferior atual={tela} onIr={ir} />
      </div>
      <Aviso aviso={aviso} />
    </>
  );
}
