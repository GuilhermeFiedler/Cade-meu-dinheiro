package br.cademeudinheiro.controller;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/** Funções de apoio compartilhadas pelos testes de API. */
final class TestSupport {
    static final String SENHA = "12345678";

    private TestSupport() {}

    /** Envia a requisição, confere o status HTTP esperado e devolve o corpo da resposta. */
    static String enviar(MockMvc mvc, MockHttpServletRequestBuilder req, String token, String corpo, int esperado)
            throws Exception {
        req.contentType(MediaType.APPLICATION_JSON);
        if (corpo != null) {
            req.content(corpo);
        }
        if (token != null) {
            req.header("Authorization", "Bearer " + token);
        }
        return mvc.perform(req).andExpect(status().is(esperado)).andReturn().getResponse().getContentAsString(UTF_8);
    }

    static String emailNovo() {
        return "t" + UUID.randomUUID().toString().substring(0, 8) + "@exemplo.com";
    }

    static String corpoConta(String nome, String email, String senha) {
        return "{\"nome\":\"%s\",\"email\":\"%s\",\"senha\":\"%s\"}".formatted(nome, email, senha);
    }

    /** Cria uma conta nova e devolve o token. */
    static String conta(MockMvc mvc) throws Exception {
        String r = enviar(mvc, post("/api/auth/registrar"), null, corpoConta("Ana", emailNovo(), SENHA), 201);
        return JsonPath.read(r, "$.token");
    }

    static String corpoGasto(String valor, int categoria, String data, String descricao) {
        return "{\"valor\":\"%s\",\"categoria_id\":%d,\"data\":\"%s\",\"descricao\":\"%s\"}"
            .formatted(valor, categoria, data, descricao);
    }

    /** Registra um gasto e devolve o id. */
    static String gasto(MockMvc mvc, String token, String valor, int categoria, String data, String descricao)
            throws Exception {
        String r = enviar(mvc, post("/api/gastos"), token, corpoGasto(valor, categoria, data, descricao), 201);
        return JsonPath.read(r, "$.id");
    }

    static double numero(String json, String caminho) {
        return ((Number) JsonPath.read(json, caminho)).doubleValue();
    }
}
