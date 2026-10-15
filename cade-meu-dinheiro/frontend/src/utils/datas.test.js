import test from "node:test";
import assert from "node:assert/strict";
import { diaMes, hoje, nomeMes, somaMes } from "./datas.js";

test("hoje devolve a data no formato AAAA-MM-DD", () => {
  assert.match(hoje(), /^\d{4}-\d{2}-\d{2}$/);
});

test("somaMes avança e volta, inclusive na virada de ano (RF10)", () => {
  assert.equal(somaMes("2026-10", 1), "2026-11");
  assert.equal(somaMes("2026-12", 1), "2027-01");
  assert.equal(somaMes("2026-01", -1), "2025-12");
});

test("nomeMes escreve o mês por extenso", () => {
  assert.equal(nomeMes("2026-10"), "Outubro de 2026");
});

test("diaMes mostra dia/mês", () => {
  assert.equal(diaMes("2026-10-05"), "05/10");
});
