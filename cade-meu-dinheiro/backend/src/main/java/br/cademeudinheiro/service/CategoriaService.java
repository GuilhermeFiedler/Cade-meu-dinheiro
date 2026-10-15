package br.cademeudinheiro.service;

import br.cademeudinheiro.exception.ApiException;
import br.cademeudinheiro.model.Categoria;
import br.cademeudinheiro.repository.CategoriaRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/** US02 – categorias predefinidas (RF04, RN03). */
@Service
public class CategoriaService {
    private final CategoriaRepository repo;

    public CategoriaService(CategoriaRepository repo) {
        this.repo = repo;
    }

    public List<Categoria> listar() {
        return repo.listar();
    }

    /** RN03: o gasto precisa ter uma categoria existente. */
    public int validarId(Object v) {
        if (!(v instanceof Integer id) || !repo.existe(id)) {
            throw new ApiException(422, "Escolha uma categoria", "categoria_id");
        }
        return id;
    }
}
