package br.cademeudinheiro.controller;

import static br.cademeudinheiro.controller.TestSupport.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import br.cademeudinheiro.TesteApi;
import com.jayway.jsonpath.JsonPath;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

/** US02, US03 e US04 – registrar, listar, editar e excluir gastos (RF03 a RF07, RF10, RN01 a RN05, RN07, RN08). */
@TesteApi
class GastoApiTest {
    @Autowired MockMvc mvc;

    private String postGasto(String token, String corpo, int esperado) throws Exception {
        return enviar(mvc, post("/api/gastos"), token, corpo, esperado);
    }

    // ---------- US02 ----------
    @Test
    void ca02_1_salvaGastoComDataDeHojeQuandoNaoInformada() throws Exception {
        String t = conta(mvc);
        String r = postGasto(t, "{\"valor\":\"32,50\",\"categoria_id\":1,\"descricao\":\"Almoço\"}", 201);
        assertEquals(32.5, numero(r, "$.valor"));
        assertEquals(LocalDate.now().toString(), JsonPath.read(r, "$.data"));
        assertEquals("Almoço", JsonPath.read(r, "$.descricao"));
        assertEquals("Alimentação", JsonPath.read(r, "$.categoria"));
    }

    @Test
    void ca02_2_valorAusenteOuZeroEhRecusado() throws Exception {
        String t = conta(mvc);
        for (String corpo : List.of("{\"categoria_id\":1}", "{\"valor\":0,\"categoria_id\":1}",
                "{\"valor\":-5,\"categoria_id\":1}", "{\"valor\":\"abc\",\"categoria_id\":1}")) {
            String r = postGasto(t, corpo, 422);
            assertEquals("Informe um valor maior que zero", JsonPath.read(r, "$.erro.mensagem"), corpo);
            assertEquals("valor", JsonPath.read(r, "$.erro.campo"));
        }
    }

    @Test
    void ca02_3_categoriaObrigatoriaEExistente() throws Exception {
        String t = conta(mvc);
        for (String corpo : List.of("{\"valor\":10}", "{\"valor\":10,\"categoria_id\":99}", "{\"valor\":10,\"categoria_id\":\"x\"}")) {
            String r = postGasto(t, corpo, 422);
            assertEquals("Escolha uma categoria", JsonPath.read(r, "$.erro.mensagem"), corpo);
            assertEquals("categoria_id", JsonPath.read(r, "$.erro.campo"));
        }
    }

    @Test
    void ca02_4_dataAnteriorEhRespeitada() throws Exception {
        String r = postGasto(conta(mvc), corpoGasto("10", 1, "2024-05-02", "x"), 201);
        assertEquals("2024-05-02", JsonPath.read(r, "$.data"));
    }

    @Test
    void rn02_limitesDoValor() throws Exception {
        String t = conta(mvc);
        String casas = postGasto(t, "{\"valor\":\"1.234\",\"categoria_id\":1}", 422);
        assertEquals("O valor deve ter no máximo duas casas decimais", JsonPath.read(casas, "$.erro.mensagem"));
        String grande = postGasto(t, "{\"valor\":\"10000000\",\"categoria_id\":1}", 422);
        assertEquals("O valor máximo é R$ 9.999.999,99", JsonPath.read(grande, "$.erro.mensagem"));
        postGasto(t, "{\"valor\":\"9999999.99\",\"categoria_id\":1}", 201);
        postGasto(t, "{\"valor\":\"0.01\",\"categoria_id\":1}", 201);
    }

    @Test
    void rn03_categoriasSaoFixas() throws Exception {
        String r = enviar(mvc, get("/api/categorias"), conta(mvc), null, 200);
        List<String> nomes = JsonPath.read(r, "$[*].nome");
        assertEquals(List.of("Alimentação", "Transporte", "Lazer", "Moradia", "Saúde", "Outros"), nomes);
    }

    @Test
    void rn04_dataNaoPodeSerFutura() throws Exception {
        String t = conta(mvc);
        String amanha = LocalDate.now().plusDays(1).toString();
        String r = postGasto(t, corpoGasto("10", 1, amanha, "x"), 422);
        assertEquals("A data não pode ser futura", JsonPath.read(r, "$.erro.mensagem"));
        String formato = postGasto(t, corpoGasto("10", 1, "31/12/2024", "x"), 422);
        assertEquals("data", JsonPath.read(formato, "$.erro.campo"));
    }

    @Test
    void rn05_descricaoOpcionalComAte100Caracteres() throws Exception {
        String t = conta(mvc);
        String r = postGasto(t, corpoGasto("10", 1, "2024-05-02", "x".repeat(101)), 422);
        assertEquals("A descrição deve ter no máximo 100 caracteres", JsonPath.read(r, "$.erro.mensagem"));
        postGasto(t, corpoGasto("10", 1, "2024-05-02", "x".repeat(100)), 201);
        String vazia = postGasto(t, "{\"valor\":10,\"categoria_id\":1}", 201);
        assertNull(JsonPath.read(vazia, "$.descricao"));
    }

    // ---------- US03 ----------
    @Test
    void ca03_1_listaDoMaisRecenteParaOMaisAntigoComTodosOsCampos() throws Exception {
        String t = conta(mvc);
        gasto(mvc, t, "10", 1, "2024-05-02", "Primeiro");
        gasto(mvc, t, "20.50", 2, "2024-05-20", "Terceiro");
        gasto(mvc, t, "30", 3, "2024-05-10", "Segundo");
        String r = enviar(mvc, get("/api/gastos?mes=2024-05"), t, null, 200);
        List<String> datas = JsonPath.read(r, "$.gastos[*].data");
        assertEquals(List.of("2024-05-20", "2024-05-10", "2024-05-02"), datas);
        assertEquals("Terceiro", JsonPath.read(r, "$.gastos[0].descricao"));
        assertEquals("Transporte", JsonPath.read(r, "$.gastos[0].categoria"));
        assertEquals(20.5, numero(r, "$.gastos[0].valor"));
    }

    @Test
    void ca03_2_mesSemGastosDevolveListaVazia() throws Exception {
        String r = enviar(mvc, get("/api/gastos?mes=2020-01"), conta(mvc), null, 200);
        assertTrue(((List<?>) JsonPath.read(r, "$.gastos")).isEmpty());
    }

    @Test
    void ca03_3_rn07_listaSoTemGastosDoMesPelaDataDoGasto() throws Exception {
        String t = conta(mvc);
        gasto(mvc, t, "10", 1, "2024-05-31", "Maio");
        gasto(mvc, t, "20", 1, "2024-06-01", "Junho");
        List<String> maio = JsonPath.read(enviar(mvc, get("/api/gastos?mes=2024-05"), t, null, 200), "$.gastos[*].descricao");
        List<String> junho = JsonPath.read(enviar(mvc, get("/api/gastos?mes=2024-06"), t, null, 200), "$.gastos[*].descricao");
        assertEquals(List.of("Maio"), maio);
        assertEquals(List.of("Junho"), junho);
    }

    // ---------- US04 ----------
    @Test
    void ca04_1_editarAlteraOsDadosERefleteNosTotais() throws Exception {
        String t = conta(mvc);
        String id = gasto(mvc, t, "10", 1, "2024-05-02", "Antes");
        String r = enviar(mvc, put("/api/gastos/" + id), t, corpoGasto("50", 2, "2024-05-02", "Depois"), 200);
        assertEquals(50.0, numero(r, "$.valor"));
        assertEquals("Transporte", JsonPath.read(r, "$.categoria"));
        assertEquals("Depois", JsonPath.read(r, "$.descricao"));
        String resumo = enviar(mvc, get("/api/resumo?mes=2024-05"), t, null, 200);
        assertEquals(50.0, numero(resumo, "$.total"));
    }

    @Test
    void ca04_1_editarAplicaAsMesmasValidacoesDoCadastro() throws Exception {
        String t = conta(mvc);
        String id = gasto(mvc, t, "10", 1, "2024-05-02", "x");
        enviar(mvc, put("/api/gastos/" + id), t, corpoGasto("0", 1, "2024-05-02", "x"), 422);
        enviar(mvc, put("/api/gastos/" + id), t, corpoGasto("10", 99, "2024-05-02", "x"), 422);
    }

    @Test
    void ca04_2_rn08_excluirRemoveDefinitivamente() throws Exception {
        String t = conta(mvc);
        String id = gasto(mvc, t, "10", 1, "2024-05-02", "Apagar");
        String r = enviar(mvc, delete("/api/gastos/" + id), t, null, 200);
        assertEquals("Gasto excluído", JsonPath.read(r, "$.mensagem"));
        List<?> lista = JsonPath.read(enviar(mvc, get("/api/gastos?mes=2024-05"), t, null, 200), "$.gastos");
        assertTrue(lista.isEmpty());
        enviar(mvc, delete("/api/gastos/" + id), t, null, 404); // já não existe
    }

    @Test
    void ca04_4_rn01_outroUsuarioNaoVeNemAlteraNemExclui() throws Exception {
        String dono = conta(mvc), intruso = conta(mvc);
        String id = gasto(mvc, dono, "10", 1, "2024-05-02", "Meu");
        List<?> lista = JsonPath.read(enviar(mvc, get("/api/gastos?mes=2024-05"), intruso, null, 200), "$.gastos");
        assertTrue(lista.isEmpty());
        enviar(mvc, put("/api/gastos/" + id), intruso, corpoGasto("99", 1, "2024-05-02", "Hackeado"), 404);
        enviar(mvc, delete("/api/gastos/" + id), intruso, null, 404);
        String r = enviar(mvc, get("/api/gastos?mes=2024-05"), dono, null, 200);
        assertEquals("Meu", JsonPath.read(r, "$.gastos[0].descricao")); // continua intacto
        assertEquals(10.0, numero(r, "$.gastos[0].valor"));
    }
}
