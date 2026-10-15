import { sessao } from "./sessao.js";

// Função chamada quando o backend responde 401 com o usuário logado (sessão expirada).
let aoExpirar = () => {};
export const definirAoExpirar = (fn) => (aoExpirar = fn);

/** Cliente HTTP da API. Lança Error com a mensagem do backend (e `campo`, quando houver). */
export async function api(caminho, { method = "GET", body } = {}) {
  const token = sessao.token();
  let resp;
  try {
    resp = await fetch("/api" + caminho, {
      method,
      headers: { "Content-Type": "application/json", ...(token ? { Authorization: "Bearer " + token } : {}) },
      body: body ? JSON.stringify(body) : undefined,
    });
  } catch {
    throw new Error("Não foi possível conectar ao servidor. O backend está rodando?");
  }
  let json = {};
  try { json = await resp.json(); } catch { /* resposta sem corpo */ }
  if (resp.status === 401 && token) {
    aoExpirar();
    throw new Error("Sessão expirada. Entre novamente.");
  }
  if (!resp.ok) {
    throw Object.assign(new Error(json.erro?.mensagem || "Erro inesperado. Tente novamente."), { campo: json.erro?.campo });
  }
  return json;
}
