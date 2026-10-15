package br.cademeudinheiro.controller;

import static br.cademeudinheiro.controller.TestSupport.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import br.cademeudinheiro.TesteApi;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

/** US01 – Criar conta e entrar (RF01, RF02, RN06). */
@TesteApi
class AuthApiTest {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;

    @Test
    void ca01_1_criaContaEDevolveTokenEUsuario() throws Exception {
        String email = emailNovo();
        String r = enviar(mvc, post("/api/auth/registrar"), null, corpoConta("Ana", email, SENHA), 201);
        assertFalse(((String) JsonPath.read(r, "$.token")).isBlank());
        assertEquals(email, JsonPath.read(r, "$.usuario.email"));
        assertEquals("Ana", JsonPath.read(r, "$.usuario.nome"));
    }

    @Test
    void ca01_2_emailJaCadastradoEhRecusado() throws Exception {
        String email = emailNovo();
        enviar(mvc, post("/api/auth/registrar"), null, corpoConta("Ana", email, SENHA), 201);
        String r = enviar(mvc, post("/api/auth/registrar"), null, corpoConta("Outra", email.toUpperCase(), SENHA), 409);
        assertEquals("Este e-mail já está em uso", JsonPath.read(r, "$.erro.mensagem"));
        assertEquals("email", JsonPath.read(r, "$.erro.campo"));
    }

    @Test
    void ca01_3_loginComCredenciaisCorretas() throws Exception {
        String email = emailNovo();
        enviar(mvc, post("/api/auth/registrar"), null, corpoConta("Ana", email, SENHA), 201);
        String r = enviar(mvc, post("/api/auth/entrar"), null, "{\"email\":\"%s\",\"senha\":\"%s\"}".formatted(email, SENHA), 200);
        assertFalse(((String) JsonPath.read(r, "$.token")).isBlank());
    }

    @Test
    void ca01_4_senhaIncorretaNaoRevelaQualCampoErrou() throws Exception {
        String email = emailNovo();
        enviar(mvc, post("/api/auth/registrar"), null, corpoConta("Ana", email, SENHA), 201);
        String senhaErrada = enviar(mvc, post("/api/auth/entrar"), null, "{\"email\":\"%s\",\"senha\":\"errada123\"}".formatted(email), 401);
        String emailInexistente = enviar(mvc, post("/api/auth/entrar"), null, "{\"email\":\"%s\",\"senha\":\"%s\"}".formatted(emailNovo(), SENHA), 401);
        assertEquals("E-mail ou senha incorretos", JsonPath.read(senhaErrada, "$.erro.mensagem"));
        assertEquals("E-mail ou senha incorretos", JsonPath.read(emailInexistente, "$.erro.mensagem"));
    }

    @Test
    void rn06_validacoesDoCadastro() throws Exception {
        String curta = enviar(mvc, post("/api/auth/registrar"), null, corpoConta("Ana", emailNovo(), "123"), 422);
        assertEquals("senha", JsonPath.read(curta, "$.erro.campo"));
        String email = enviar(mvc, post("/api/auth/registrar"), null, corpoConta("Ana", "sem-arroba", SENHA), 422);
        assertEquals("Informe um e-mail válido", JsonPath.read(email, "$.erro.mensagem"));
        String nome = enviar(mvc, post("/api/auth/registrar"), null, corpoConta("", emailNovo(), SENHA), 422);
        assertEquals("nome", JsonPath.read(nome, "$.erro.campo"));
    }

    @Test
    void rnf10_senhaFicaGuardadaComoHashBcrypt() throws Exception {
        String email = emailNovo();
        enviar(mvc, post("/api/auth/registrar"), null, corpoConta("Ana", email, SENHA), 201);
        String hash = jdbc.queryForObject("SELECT senha_hash FROM usuario WHERE email = ?", String.class, email);
        assertNotEquals(SENHA, hash);
        assertTrue(hash.startsWith("$2"), "hash BCrypt começa com $2");
    }

    @Test
    void rotasProtegidasExigemTokenValido() throws Exception {
        String r = enviar(mvc, get("/api/gastos"), null, null, 401);
        assertEquals("Sessão expirada. Entre novamente.", JsonPath.read(r, "$.erro.mensagem"));
        enviar(mvc, get("/api/gastos"), conta(mvc) + "x", null, 401); // token adulterado
        enviar(mvc, get("/api/resumo"), null, null, 401);
        enviar(mvc, get("/api/categorias"), null, null, 401);
    }
}
