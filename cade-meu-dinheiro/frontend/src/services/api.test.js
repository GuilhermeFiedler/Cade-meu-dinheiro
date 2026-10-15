import test, { beforeEach } from "node:test";
import assert from "node:assert/strict";

const memoria = new Map();
globalThis.localStorage = {
  getItem: (k) => (memoria.has(k) ? memoria.get(k) : null),
  setItem: (k, v) => memoria.set(k, String(v)),
  removeItem: (k) => memoria.delete(k),
};
const { api, definirAoExpirar } = await import("./api.js");
const { sessao } = await import("./sessao.js");

/** Troca o fetch por um falso que responde `status` e `corpo` e registra as chamadas. */
function fingeFetch(status, corpo) {
  const chamadas = [];
  globalThis.fetch = async (url, opcoes) => {
    chamadas.push({ url, opcoes });
    return { status, ok: status >= 200 && status < 300, json: async () => { if (corpo === undefined) throw new Error("sem corpo"); return corpo; } };
  };
  return chamadas;
}

beforeEach(() => { memoria.clear(); definirAoExpirar(() => {}); });

test("devolve o JSON quando a resposta é de sucesso", async () => {
  fingeFetch(200, { ok: true });
  assert.deepEqual(await api("/categorias"), { ok: true });
});

test("envia o token no cabeçalho Authorization quando há sessão", async () => {
  sessao.salvar("abc123", { nome: "Ana" });
  const chamadas = fingeFetch(200, {});
  await api("/gastos");
  assert.equal(chamadas[0].opcoes.headers.Authorization, "Bearer abc123");
  assert.equal(chamadas[0].url, "/api/gastos");
});

test("lança a mensagem e o campo devolvidos pelo backend (tratamento de erros)", async () => {
  fingeFetch(422, { erro: { mensagem: "Informe um valor maior que zero", campo: "valor" } });
  await assert.rejects(api("/gastos", { method: "POST", body: {} }), (e) => {
    assert.equal(e.message, "Informe um valor maior que zero");
    assert.equal(e.campo, "valor");
    return true;
  });
});

test("usa mensagem padrão quando o erro não tem corpo", async () => {
  fingeFetch(500, undefined);
  await assert.rejects(api("/gastos"), /Erro inesperado/);
});

test("avisa que não conseguiu conectar quando o servidor está fora do ar", async () => {
  globalThis.fetch = async () => { throw new TypeError("fetch failed"); };
  await assert.rejects(api("/gastos"), /Não foi possível conectar ao servidor/);
});

test("401 com usuário logado dispara o aviso de sessão expirada", async () => {
  sessao.salvar("velho", { nome: "Ana" });
  let expirou = false;
  definirAoExpirar(() => { expirou = true; });
  fingeFetch(401, { erro: { mensagem: "Sessão expirada. Entre novamente." } });
  await assert.rejects(api("/gastos"), /Sessão expirada/);
  assert.equal(expirou, true);
});

test("401 no login (sem sessão) mostra a mensagem do backend e não expira nada", async () => {
  let expirou = false;
  definirAoExpirar(() => { expirou = true; });
  fingeFetch(401, { erro: { mensagem: "E-mail ou senha incorretos" } });
  await assert.rejects(api("/auth/entrar", { method: "POST", body: {} }), /E-mail ou senha incorretos/);
  assert.equal(expirou, false);
});
