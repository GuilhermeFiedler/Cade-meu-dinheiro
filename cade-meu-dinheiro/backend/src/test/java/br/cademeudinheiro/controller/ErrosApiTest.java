package br.cademeudinheiro.controller;

import static br.cademeudinheiro.controller.TestSupport.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import br.cademeudinheiro.TesteApi;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

/** Tratamento de erros: todo erro volta como {"erro":{"mensagem":...,"campo":...}} com o status correto. */
@TesteApi
class ErrosApiTest {
    @Autowired MockMvc mvc;

    @Test
    void jsonInvalidoDevolve400() throws Exception {
        String t = conta(mvc);
        String r = enviar(mvc, post("/api/gastos"), t, "{isso não é json", 400);
        assertEquals("Corpo da requisição inválido (JSON esperado)", JsonPath.read(r, "$.erro.mensagem"));
        enviar(mvc, post("/api/auth/entrar"), null, "texto solto", 400);
    }

    @Test
    void mesInvalidoDevolve400() throws Exception {
        String t = conta(mvc);
        for (String mes : new String[] {"2024-13", "05/2024", "abc"}) {
            String r = enviar(mvc, get("/api/gastos?mes=" + mes), t, null, 400);
            assertEquals("mes", JsonPath.read(r, "$.erro.campo"));
            enviar(mvc, get("/api/resumo?mes=" + mes), t, null, 400);
        }
    }

    @Test
    void rotaInexistenteDevolve404EmJson() throws Exception {
        String r = enviar(mvc, get("/api/nao-existe"), null, null, 404);
        assertEquals("Rota não encontrada", JsonPath.read(r, "$.erro.mensagem"));
    }

    @Test
    void idQueNaoEhUuidDevolve404() throws Exception {
        String t = conta(mvc);
        enviar(mvc, delete("/api/gastos/abc"), t, null, 404);
        enviar(mvc, put("/api/gastos/abc"), t, corpoGasto("10", 1, "2024-05-02", "x"), 404);
    }

    @Test
    void metodoNaoPermitidoDevolve405() throws Exception {
        String r = enviar(mvc, delete("/api/gastos"), null, null, 405);
        assertEquals("Método não permitido", JsonPath.read(r, "$.erro.mensagem"));
    }

    @Test
    void erroDeValidacaoTemMensagemECampo() throws Exception {
        String r = enviar(mvc, post("/api/gastos"), conta(mvc), "{\"valor\":0,\"categoria_id\":1}", 422);
        assertNotNull(JsonPath.read(r, "$.erro.mensagem"));
        assertEquals("valor", JsonPath.read(r, "$.erro.campo"));
    }

    @Test
    void docsListaOsEndpoints() throws Exception {
        String r = enviar(mvc, get("/api/docs"), null, null, 200);
        assertEquals(8, ((java.util.List<?>) JsonPath.read(r, "$")).size());
    }
}
