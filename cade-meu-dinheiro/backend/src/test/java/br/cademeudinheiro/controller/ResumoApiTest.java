package br.cademeudinheiro.controller;

import static br.cademeudinheiro.controller.TestSupport.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import br.cademeudinheiro.TesteApi;
import com.jayway.jsonpath.JsonPath;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

/** US05 e US06 – total do mês (RF08) e gastos por categoria (RF09), com RN07. */
@TesteApi
class ResumoApiTest {
    @Autowired MockMvc mvc;

    private String resumo(String token, String mes) throws Exception {
        return enviar(mvc, get("/api/resumo?mes=" + mes), token, null, 200);
    }

    @Test
    void ca05_1_totaisPorCategoriaDoMaiorParaOMenor() throws Exception {
        String t = conta(mvc);
        gasto(mvc, t, "10", 1, "2024-05-02", "a");
        gasto(mvc, t, "5", 1, "2024-05-03", "b");
        gasto(mvc, t, "80", 2, "2024-05-04", "c");
        gasto(mvc, t, "20", 3, "2024-05-05", "d");
        String r = resumo(t, "2024-05");
        List<String> nomes = JsonPath.read(r, "$.por_categoria[*].categoria");
        assertEquals(List.of("Transporte", "Lazer", "Alimentação"), nomes);
        assertEquals(15.0, numero(r, "$.por_categoria[2].total"));
    }

    @Test
    void ca05_2_categoriaSemGastosNaoAparece() throws Exception {
        String t = conta(mvc);
        gasto(mvc, t, "10", 1, "2024-05-02", "a");
        List<String> nomes = JsonPath.read(resumo(t, "2024-05"), "$.por_categoria[*].categoria");
        assertEquals(List.of("Alimentação"), nomes);
    }

    @Test
    void ca05_3_resumoAcompanhaEdicaoEExclusao() throws Exception {
        String t = conta(mvc);
        String id = gasto(mvc, t, "10", 1, "2024-05-02", "a");
        String outro = gasto(mvc, t, "20", 2, "2024-05-03", "b");
        enviar(mvc, put("/api/gastos/" + id), t, corpoGasto("40", 1, "2024-05-02", "a"), 200);
        assertEquals(60.0, numero(resumo(t, "2024-05"), "$.total"));
        enviar(mvc, delete("/api/gastos/" + outro), t, null, 200);
        assertEquals(40.0, numero(resumo(t, "2024-05"), "$.total"));
    }

    @Test
    void ca06_1_totalDoMesEhASomaDeTodosOsGastos() throws Exception {
        String t = conta(mvc);
        gasto(mvc, t, "10.10", 1, "2024-05-02", "a");
        gasto(mvc, t, "20.20", 2, "2024-05-03", "b");
        gasto(mvc, t, "99", 1, "2024-06-01", "outro mês"); // RN07: não entra
        assertEquals(30.3, numero(resumo(t, "2024-05"), "$.total"), 0.001);
    }

    @Test
    void ca06_2_totalZeradoQuandoNaoHaGastos() throws Exception {
        String r = resumo(conta(mvc), "2020-01");
        assertEquals(0.0, numero(r, "$.total"));
        assertTrue(((List<?>) JsonPath.read(r, "$.por_categoria")).isEmpty());
    }

    @Test
    void ca06_3_somaDasCategoriasIgualAoTotal() throws Exception {
        String t = conta(mvc);
        gasto(mvc, t, "10.10", 1, "2024-05-02", "a");
        gasto(mvc, t, "20.20", 2, "2024-05-03", "b");
        gasto(mvc, t, "0.30", 3, "2024-05-04", "c");
        String r = resumo(t, "2024-05");
        List<Number> totais = JsonPath.read(r, "$.por_categoria[*].total");
        double soma = totais.stream().mapToDouble(Number::doubleValue).sum();
        assertEquals(numero(r, "$.total"), soma, 0.001);
    }

    @Test
    void rn01_resumoSoConsideraOsGastosDoProprioUsuario() throws Exception {
        String a = conta(mvc), b = conta(mvc);
        gasto(mvc, a, "10", 1, "2024-05-02", "a");
        assertEquals(0.0, numero(resumo(b, "2024-05"), "$.total"));
    }
}
