import test from "node:test";
import assert from "node:assert/strict";
import { createElement as h } from "react";
import { renderToStaticMarkup } from "react-dom/server";
import BarraCategoria from "./BarraCategoria.jsx";
import EstadoCarga from "./EstadoCarga.jsx";
import ItemGasto from "./ItemGasto.jsx";
import SeletorCategoria from "./SeletorCategoria.jsx";
import SeletorMes from "./SeletorMes.jsx";
import { hoje } from "../utils/datas.js";

const html = (el) => renderToStaticMarkup(el).replace(/\u00a0/g, " ");
const noop = () => {};

test("ItemGasto mostra descrição, categoria, dia/mês e valor em reais (US03)", () => {
  const g = { id: "1", descricao: "Almoço", categoria: "Alimentação", data: "2026-10-05", valor: 32.5 };
  const s = html(h(ItemGasto, { gasto: g, onEditar: noop, onExcluir: noop }));
  for (const t of ["Almoço", "Alimentação", "05/10", "R$ 32,50", "Editar", "Excluir"]) assert.ok(s.includes(t), t);
});

test("ItemGasto sem descrição mostra o nome da categoria", () => {
  const g = { id: "1", descricao: null, categoria: "Lazer", data: "2026-10-05", valor: 10 };
  assert.ok(html(h(ItemGasto, { gasto: g, onEditar: noop, onExcluir: noop })).includes("Lazer"));
});

test("BarraCategoria dimensiona a barra pelo maior valor (US05)", () => {
  const s = html(h(BarraCategoria, { item: { categoria: "Lazer", total: 50, cor: "#7b5ea7" }, maior: 200 }));
  assert.ok(s.includes("width:25%"));
  assert.ok(s.includes("R$ 50,00"));
});

test("EstadoCarga mostra carregando, erro com nova tentativa, e nada quando pronto", () => {
  assert.ok(html(h(EstadoCarga, { carregando: true })).includes("Carregando…"));
  const erro = html(h(EstadoCarga, { carregando: false, erro: "Falhou", onTentarDeNovo: noop }));
  assert.ok(erro.includes("Falhou") && erro.includes("Tentar de novo"));
  assert.equal(html(h(EstadoCarga, { carregando: false, erro: null })), "");
});

test("SeletorCategoria marca somente a categoria escolhida (RN03)", () => {
  const cats = [{ id: 1, nome: "Alimentação" }, { id: 2, nome: "Lazer" }];
  const s = html(h(SeletorCategoria, { categorias: cats, valor: 2, onMudar: noop }));
  assert.equal((s.match(/checked=""/g) || []).length, 1);
  assert.ok(s.includes("Alimentação") && s.includes("Lazer"));
});

test("SeletorMes não deixa avançar além do mês atual (RF10)", () => {
  const atual = html(h(SeletorMes, { mes: hoje().slice(0, 7), onMudar: noop }));
  assert.match(atual, /aria-label="Próximo mês" disabled=""/);
  const passado = html(h(SeletorMes, { mes: "2024-05", onMudar: noop }));
  assert.doesNotMatch(passado, /aria-label="Próximo mês" disabled=""/);
  assert.ok(passado.includes("Maio de 2024"));
});
