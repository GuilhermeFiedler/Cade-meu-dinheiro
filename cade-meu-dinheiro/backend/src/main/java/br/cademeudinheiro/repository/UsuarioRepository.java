package br.cademeudinheiro.repository;

import br.cademeudinheiro.model.Usuario;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioRepository {
    private static final RowMapper<Usuario> MAPPER = (rs, i) -> new Usuario(
        rs.getObject("id", UUID.class), rs.getString("nome"), rs.getString("email"), rs.getString("senha_hash"));

    private final JdbcTemplate jdbc;

    public UsuarioRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void criar(Usuario u) {
        jdbc.update("INSERT INTO usuario(id, nome, email, senha_hash) VALUES (?, ?, ?, ?)",
            u.id(), u.nome(), u.email(), u.senhaHash());
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return jdbc.query("SELECT id, nome, email, senha_hash FROM usuario WHERE email = ?", MAPPER, email)
            .stream().findFirst();
    }

    public int contar() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM usuario", Integer.class);
    }
}
