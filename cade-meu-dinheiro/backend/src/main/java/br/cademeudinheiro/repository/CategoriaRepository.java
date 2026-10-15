package br.cademeudinheiro.repository;

import br.cademeudinheiro.model.Categoria;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class CategoriaRepository {
    private static final RowMapper<Categoria> MAPPER = (rs, i) -> new Categoria(
        rs.getInt("id"), rs.getString("nome"), rs.getString("cor"), rs.getString("icone"));

    private final JdbcTemplate jdbc;

    public CategoriaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Categoria> listar() {
        return jdbc.query("SELECT id, nome, cor, icone FROM categoria ORDER BY ordem", MAPPER);
    }

    public boolean existe(int id) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM categoria WHERE id = ?", Integer.class, id) > 0;
    }

    public int contar() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM categoria", Integer.class);
    }

    public void criar(Categoria c, int ordem) {
        jdbc.update("INSERT INTO categoria(id, nome, cor, icone, ordem) VALUES (?, ?, ?, ?, ?)",
            c.id(), c.nome(), c.cor(), c.icone(), ordem);
    }
}
