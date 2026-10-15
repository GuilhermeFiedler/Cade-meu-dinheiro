// Teste de ponta a ponta: navegador (React) -> API (Java) -> banco de dados.
// Pré-requisito: backend e frontend rodando (npm run dev). Use BASE_URL para mudar o endereço.
import test, { after, before } from "node:test";
import assert from "node:assert/strict";
import { chromium } from "playwright";

const BASE = process.env.BASE_URL || "http://localhost:5173";
const SENHA = "senha12345";
const email = (n) => `e2e.${n}.${Date.now()}@exemplo.com`;
const EMAIL_A = email("a");

let browser, page;

before(async () => {
  browser = await chromium.launch();
  page = await novaPagina();
});
after(async () => { await browser?.close(); });

async function novaPagina() {
  const ctx = await browser.newContext({ viewport: { width: 390, height: 780 } });
  const p = await ctx.newPage();
  p.setDefaultTimeout(8000);
  return p;
}
const verTexto = (p, texto) =>
  p.waitForFunction((t) => document.body.innerText.replace(/\u00a0/g, " ").includes(t), texto);
const naoVerTexto = (p, texto) =>
  p.waitForFunction((t) => !document.body.innerText.replace(/\u00a0/g, " ").includes(t), texto);
const abaCriarConta = (p) => p.locator(".tabs button", { hasText: "Criar conta" });
const enviar = (p) => p.locator("form .btn").first();

async function criarConta(p, nome, mail) {
  await p.goto(BASE);
  await abaCriarConta(p).click();
  await p.fill("input[placeholder=Nome]", nome);
  await p.fill("input[type=email]", mail);
  await p.fill("input[type=password]", SENHA);
  await enviar(p).click();
  await p.waitForSelector("input.big");
}
async function registrarGasto(p, valor, categoria, descricao) {
  await p.locator("nav button", { hasText: "Novo" }).click();
  await p.fill("input.big", valor);
  await p.locator(".chip", { hasText: categoria }).click();
  await p.fill("input[placeholder^=Descri]", descricao);
  await enviar(p).click();
  await p.waitForSelector(".row");
}

test("US01 / RF01 – validações do cadastro e criação de conta (CA01.1)", async () => {
  await page.goto(BASE);
  await abaCriarConta(page).click();
  await enviar(page).click();
  await verTexto(page, "Informe seu nome");
  await verTexto(page, "Informe um e-mail válido");
  await verTexto(page, "A senha deve ter pelo menos 8 caracteres");
  await criarConta(page, "Ana E2E", EMAIL_A);
  await verTexto(page, "Conta criada!");
});

test("US01 / RF02 – sair, senha incorreta (CA01.4) e login correto (CA01.3)", async () => {
  await page.getByRole("button", { name: "Sair" }).click();
  await page.waitForSelector("input[type=email]");
  await page.fill("input[type=email]", EMAIL_A);
  await page.fill("input[type=password]", "senha-errada");
  await enviar(page).click();
  await verTexto(page, "E-mail ou senha incorretos");
  await page.fill("input[type=password]", SENHA);
  await enviar(page).click();
  await page.waitForSelector("input.big");
});

test("US01 – e-mail duplicado é recusado (CA01.2, RN06)", async () => {
  const outra = await novaPagina();
  await outra.goto(BASE);
  await abaCriarConta(outra).click();
  await outra.fill("input[placeholder=Nome]", "Outra");
  await outra.fill("input[type=email]", EMAIL_A);
  await outra.fill("input[type=password]", SENHA);
  await enviar(outra).click();
  await verTexto(outra, "Este e-mail já está em uso");
  await outra.context().close();
});

test("US02 / RF03 – validações do gasto e registro (CA02.1, CA02.2, CA02.3)", async () => {
  await enviar(page).click();
  await verTexto(page, "Informe um valor maior que zero");
  await page.fill("input.big", "12,90");
  await enviar(page).click();
  await verTexto(page, "Escolha uma categoria");
  await page.locator(".chip", { hasText: "Lazer" }).click();
  await page.fill("input[placeholder^=Descri]", "Sorvete E2E");
  await enviar(page).click();
  await verTexto(page, "Gasto salvo");
  await page.waitForSelector(".row");
  await verTexto(page, "Sorvete E2E");
  await verTexto(page, "R$ 12,90");
});

test("Integração – o gasto foi gravado no banco e continua após recarregar a página", async () => {
  const gastos = await page.evaluate(async () => {
    const r = await fetch("/api/gastos", { headers: { Authorization: "Bearer " + localStorage.getItem("t") } });
    return (await r.json()).gastos;
  });
  assert.equal(gastos.length, 1);
  assert.equal(gastos[0].descricao, "Sorvete E2E");
  assert.equal(Number(gastos[0].valor), 12.9);
  assert.equal(gastos[0].categoria, "Lazer");
  await page.reload();
  await page.locator("nav button", { hasText: "Histórico" }).click();
  await verTexto(page, "Sorvete E2E");
});

test("US03 / RF10 – trocar de mês mostra a lista vazia (CA03.2, CA03.3)", async () => {
  await page.getByRole("button", { name: "Mês anterior" }).click();
  await verTexto(page, "Nenhum gasto neste mês");
  await page.getByRole("button", { name: "Próximo mês" }).click();
  await verTexto(page, "Sorvete E2E");
});

test("US04 / RF06 – editar gasto (CA04.1)", async () => {
  await page.locator(".row", { hasText: "Sorvete E2E" }).getByRole("button", { name: "Editar" }).click();
  await verTexto(page, "Salvar alterações");
  await page.fill("input.big", "15,00");
  await enviar(page).click();
  await verTexto(page, "Gasto atualizado");
  await verTexto(page, "R$ 15,00");
});

test("US04 / RF07 / RN08 – excluir pede confirmação (CA04.3) e é definitivo (CA04.2)", async () => {
  const linha = page.locator(".row", { hasText: "Sorvete E2E" });
  page.once("dialog", (d) => d.dismiss());
  await linha.getByRole("button", { name: "Excluir" }).click();
  await page.waitForTimeout(400);
  assert.equal(await linha.count(), 1, "o gasto deve continuar quando a exclusão é cancelada");
  page.once("dialog", (d) => d.accept());
  await linha.getByRole("button", { name: "Excluir" }).click();
  await verTexto(page, "Gasto excluído");
  await naoVerTexto(page, "Sorvete E2E");
});

test("US05 / US06 – total do mês e gastos por categoria, do maior para o menor (CA05.1, CA06.1)", async () => {
  await page.locator("nav button", { hasText: "Novo" }).click();
  await registrarGasto(page, "5,50", "Transporte", "Ônibus E2E");
  await registrarGasto(page, "20,00", "Alimentação", "Pão E2E");
  await page.locator("nav button", { hasText: "Resumo" }).click();
  await page.waitForSelector(".bar");
  await verTexto(page, "R$ 25,50");
  const nomes = await page.locator(".bar span:first-child").allInnerTexts();
  assert.deepEqual(nomes, ["Alimentação", "Transporte"]);
});

test("RN01 – outro usuário não vê os gastos da Ana", async () => {
  const bia = await novaPagina();
  await criarConta(bia, "Bia E2E", email("b"));
  await bia.locator("nav button", { hasText: "Histórico" }).click();
  await verTexto(bia, "Nenhum gasto neste mês");
  await bia.locator("nav button", { hasText: "Resumo" }).click();
  await verTexto(bia, "R$ 0,00");
  await bia.context().close();
});

test("Tratamento de erro – falha de conexão mostra mensagem e permite tentar de novo", async () => {
  await page.locator("nav button", { hasText: "Resumo" }).click();
  await page.route("**/api/gastos?*", (rota) => rota.abort());
  await page.locator("nav button", { hasText: "Histórico" }).click();
  await verTexto(page, "Não foi possível conectar ao servidor");
  await page.unroute("**/api/gastos?*");
  await page.getByRole("button", { name: "Tentar de novo" }).click();
  await verTexto(page, "Pão E2E");
});
