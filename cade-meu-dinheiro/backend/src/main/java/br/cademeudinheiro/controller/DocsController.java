package br.cademeudinheiro.controller;

import br.cademeudinheiro.dto.EndpointDoc;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Lista resumida dos endpoints (a documentação completa está no README). */
@RestController
@RequestMapping("/api/docs")
public class DocsController {
    @GetMapping
    public List<EndpointDoc> docs() {
        return List.of(
            new EndpointDoc("POST", "/api/auth/registrar", "Cria conta (nome, email, senha)", false),
            new EndpointDoc("POST", "/api/auth/entrar", "Login (email, senha)", false),
            new EndpointDoc("GET", "/api/categorias", "Lista categorias", true),
            new EndpointDoc("GET", "/api/gastos?mes=AAAA-MM", "Gastos do mês", true),
            new EndpointDoc("POST", "/api/gastos", "Registra gasto (valor, categoria_id, data?, descricao?)", true),
            new EndpointDoc("PUT", "/api/gastos/{id}", "Altera gasto", true),
            new EndpointDoc("DELETE", "/api/gastos/{id}", "Exclui gasto", true),
            new EndpointDoc("GET", "/api/resumo?mes=AAAA-MM", "Total do mês e por categoria", true));
    }
}
