import { useState } from "react";
import Cabecalho from "../components/Cabecalho.jsx";
import { entrar, registrar } from "../services/authService.js";

// US01 – Criar conta e entrar (RF01, RF02)
export default function LoginPage({ onEntrou }) {
  const [criar, setCriar] = useState(false);
  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [erros, setErros] = useState({});
  const [carregando, setCarregando] = useState(false);

  const trocarModo = (novo) => { setCriar(novo); setErros({}); };

  async function enviar(ev) {
    ev.preventDefault();
    const e = {};
    if (criar && !nome.trim()) e.nome = "Informe seu nome";
    if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(email.trim())) e.email = "Informe um e-mail válido";
    if (criar && senha.length < 8) e.senha = "A senha deve ter pelo menos 8 caracteres";
    if (!criar && !senha) e.senha = "Informe a senha";
    setErros(e);
    if (Object.keys(e).length) return;
    setCarregando(true);
    try {
      const resposta = criar ? await registrar(nome.trim(), email.trim(), senha) : await entrar(email.trim(), senha);
      onEntrou(resposta, criar);
    } catch (x) {
      setErros({ [x.campo || "geral"]: x.message });
      setCarregando(false);
    }
  }

  return (
    <div id="app">
      <Cabecalho titulo="Cadê meu dinheiro?" />
      <main>
        <div className="tabs">
          <button className={"btn" + (criar ? " sec" : "")} onClick={() => trocarModo(false)}>Entrar</button>
          <button className={"btn" + (criar ? "" : " sec")} onClick={() => trocarModo(true)}>Criar conta</button>
        </div>
        <form onSubmit={enviar} noValidate className="form">
          {criar && (<>
            <input value={nome} onChange={(e) => setNome(e.target.value)} placeholder="Nome" maxLength={80} autoComplete="name" />
            <div className="err">{erros.nome}</div>
          </>)}
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="E-mail" autoComplete="email" />
          <div className="err">{erros.email}</div>
          <input type="password" value={senha} onChange={(e) => setSenha(e.target.value)} placeholder="Senha (mín. 8 caracteres)"
                 autoComplete={criar ? "new-password" : "current-password"} />
          <div className="err">{erros.senha}</div>
          <button className="btn" disabled={carregando}>{carregando ? "Aguarde…" : criar ? "Criar conta" : "Entrar"}</button>
          <div className="err" role="alert">{erros.geral}</div>
        </form>
        <small className="mut">Sem conexão com banco. Você digita só o que gastou.</small>
      </main>
    </div>
  );
}
