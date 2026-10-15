package br.cademeudinheiro.controller;

import br.cademeudinheiro.model.Categoria;
import br.cademeudinheiro.service.CategoriaService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {
    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    /** US02 / RF04 */
    @GetMapping
    public List<Categoria> listar() {
        return service.listar();
    }
}
