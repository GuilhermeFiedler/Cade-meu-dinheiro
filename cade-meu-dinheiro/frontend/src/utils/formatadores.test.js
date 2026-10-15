import test from "node:test";
import assert from "node:assert/strict";
import { brl } from "./formatadores.js";

const normaliza = (s) => s.replace(/\s/g, " ");

test("brl formata em reais no padrão brasileiro (RN10)", () => {
  assert.equal(normaliza(brl(1234.5)), "R$ 1.234,50");
  assert.equal(normaliza(brl("32.50")), "R$ 32,50");
  assert.equal(normaliza(brl(0)), "R$ 0,00");
});
