import test from "node:test";
import assert from "node:assert/strict";

globalThis.localStorage = { getItem: () => null, setItem() {}, removeItem() {} };
const { listarGastos, criarGasto, atualizarGasto, excluirGasto } = await import("./gastoService.js");
const { obterResumo } = await import("./resumoService.js");
const { entrar, registrar } = await import("./authService.js");

function fingeFetch() {
  const chamadas = [];
  globalThis.fetch = async (url, opcoes) => {
    chamadas.push({ url, metodo: opcoes.method, corpo: opcoes.body ? JSON.parse(opcoes.body) : undefined });
    return { status: 200, ok: true, json: async () => ({}) };
  };
  return chamadas;
}

test("os serviços chamam os endpoints documentados", async () => {
  const c = fingeFetch();
  await listarGastos("2026-10");
  await criarGasto({ valor: "10", categoria_id: 1 });
  await atualizarGasto("abc", { valor: "20", categoria_id: 2 });
  await excluirGasto("abc");
  await obterResumo("2026-10");
  await entrar("a@x.com", "12345678");
  await registrar("Ana", "a@x.com", "12345678");
  assert.deepEqual(c.map((x) => x.metodo + " " + x.url), [
    "GET /api/gastos?mes=2026-10",
    "POST /api/gastos",
    "PUT /api/gastos/abc",
    "DELETE /api/gastos/abc",
    "GET /api/resumo?mes=2026-10",
    "POST /api/auth/entrar",
    "POST /api/auth/registrar",
  ]);
  assert.deepEqual(c[1].corpo, { valor: "10", categoria_id: 1 });
  assert.deepEqual(c[6].corpo, { nome: "Ana", email: "a@x.com", senha: "12345678" });
});
