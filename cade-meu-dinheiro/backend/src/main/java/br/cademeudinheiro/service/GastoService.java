package br.cademeudinheiro.service;

import br.cademeudinheiro.dto.GastoRequest;
import br.cademeudinheiro.dto.GastosDoMes;
import br.cademeudinheiro.exception.ApiException;
import br.cademeudinheiro.model.Gasto;
import br.cademeudinheiro.repository.GastoRepository;
import br.cademeudinheiro.validation.Validacao;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;
import org.springframework.stereotype.Service;

/** US02, US03 e US04 – registrar, listar, editar e excluir gastos (RF03, RF05, RF06, RF07, RF10). */
@Service
public class GastoService {
    private final GastoRepository repo;
    private final CategoriaService categorias;

    public GastoService(GastoRepository repo, CategoriaService categorias) {
        this.repo = repo;
        this.categorias = categorias;
    }

    public GastosDoMes listar(UUID usuarioId, String mes) {
        YearMonth ym = Validacao.mes(mes);
        return new GastosDoMes(ym.toString(), repo.listarDoMes(usuarioId, ym.atDay(1), ym.plusMonths(1).atDay(1)));
    }

    public Gasto criar(UUID usuarioId, GastoRequest r) {
        BigDecimal valor = Validacao.valor(r.valor());
        int categoria = categorias.validarId(r.categoriaId());
        LocalDate data = Validacao.data(r.data());
        String descricao = Validacao.descricao(r.descricao());
        UUID id = UUID.randomUUID();
        repo.criar(id, usuarioId, categoria, valor, data, descricao);
        return repo.buscar(id);
    }

    public Gasto editar(UUID usuarioId, UUID id, GastoRequest r) {
        if (!repo.existeDoUsuario(id, usuarioId)) { // RN01: só o dono acessa
            throw new ApiException(404, "Gasto não encontrado", null);
        }
        BigDecimal valor = Validacao.valor(r.valor());
        int categoria = categorias.validarId(r.categoriaId());
        LocalDate data = Validacao.data(r.data());
        String descricao = Validacao.descricao(r.descricao());
        repo.atualizar(id, usuarioId, categoria, valor, data, descricao);
        return repo.buscar(id);
    }

    public void excluir(UUID usuarioId, UUID id) {
        if (repo.excluir(id, usuarioId) == 0) {
            throw new ApiException(404, "Gasto não encontrado", null);
        }
    }
}
