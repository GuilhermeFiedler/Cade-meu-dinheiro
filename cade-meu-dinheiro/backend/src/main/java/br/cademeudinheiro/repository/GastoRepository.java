package br.cademeudinheiro.repository;

import br.cademeudinheiro.dto.TotalCategoria;
import br.cademeudinheiro.model.Gasto;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class GastoRepository {
    private static final String SELECT =
        "SELECT g.id, g.categoria_id, c.nome AS categoria, c.cor, g.valor, g.data, g.descricao "
        + "FROM gasto g JOIN categoria c ON c.id = g.categoria_id ";

    private static final RowMapper<Gasto> MAPPER = (rs, i) -> new Gasto(
        rs.getObject("id", UUID.class), rs.getInt("categoria_id"), rs.getString("categoria"), rs.getString("cor"),
        rs.getBigDecimal("valor"), rs.getDate("data").toLocalDate(), rs.getString("descricao"));

    private static final RowMapper<TotalCategoria> TOTAL = (rs, i) -> new TotalCategoria(
        rs.getInt("categoria_id"), rs.getString("categoria"), rs.getString("cor"), rs.getBigDecimal("total"));

    private final JdbcTemplate jdbc;

    public GastoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void criar(UUID id, UUID usuarioId, int categoriaId, BigDecimal valor, LocalDate data, String descricao) {
        jdbc.update("INSERT INTO gasto(id, usuario_id, categoria_id, valor, data, descricao) VALUES (?, ?, ?, ?, ?, ?)",
            id, usuarioId, categoriaId, valor, Date.valueOf(data), descricao);
    }

    public int atualizar(UUID id, UUID usuarioId, int categoriaId, BigDecimal valor, LocalDate data, String descricao) {
        return jdbc.update("UPDATE gasto SET categoria_id = ?, valor = ?, data = ?, descricao = ?, "
            + "atualizado_em = CURRENT_TIMESTAMP WHERE id = ? AND usuario_id = ?",
            categoriaId, valor, Date.valueOf(data), descricao, id, usuarioId);
    }

    public int excluir(UUID id, UUID usuarioId) {
        return jdbc.update("DELETE FROM gasto WHERE id = ? AND usuario_id = ?", id, usuarioId);
    }

    public boolean existeDoUsuario(UUID id, UUID usuarioId) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM gasto WHERE id = ? AND usuario_id = ?",
            Integer.class, id, usuarioId) > 0;
    }

    public Gasto buscar(UUID id) {
        return jdbc.query(SELECT + "WHERE g.id = ?", MAPPER, id).get(0);
    }

    public List<Gasto> listarDoMes(UUID usuarioId, LocalDate inicio, LocalDate fimExclusivo) {
        return jdbc.query(SELECT + "WHERE g.usuario_id = ? AND g.data >= ? AND g.data < ? "
            + "ORDER BY g.data DESC, g.criado_em DESC", MAPPER, usuarioId, Date.valueOf(inicio), Date.valueOf(fimExclusivo));
    }

    public List<TotalCategoria> totalPorCategoria(UUID usuarioId, LocalDate inicio, LocalDate fimExclusivo) {
        return jdbc.query("SELECT c.id AS categoria_id, c.nome AS categoria, c.cor, SUM(g.valor) AS total "
            + "FROM gasto g JOIN categoria c ON c.id = g.categoria_id "
            + "WHERE g.usuario_id = ? AND g.data >= ? AND g.data < ? "
            + "GROUP BY c.id, c.nome, c.cor ORDER BY total DESC", TOTAL,
            usuarioId, Date.valueOf(inicio), Date.valueOf(fimExclusivo));
    }
}
