import { useCallback, useEffect, useState } from "react";
import { definirAoExpirar } from "../services/api.js";
import { sessao } from "../services/sessao.js";

/** Estado do usuário logado: entrar, sair e expiração de sessão. */
export default function useSessao(avisar) {
  const [usuario, setUsuario] = useState(() => sessao.usuario());

  const sair = useCallback(() => {
    sessao.limpar();
    setUsuario(null);
  }, []);

  const entrou = useCallback((resposta, contaNova) => {
    sessao.salvar(resposta.token, resposta.usuario);
    setUsuario(resposta.usuario);
    avisar(contaNova ? "Conta criada!" : "Bem-vindo, " + resposta.usuario.nome);
  }, [avisar]);

  useEffect(() => {
    definirAoExpirar(() => { sair(); avisar("Sessão expirada. Entre novamente.", true); });
  }, [sair, avisar]);

  return { usuario, entrou, sair };
}
