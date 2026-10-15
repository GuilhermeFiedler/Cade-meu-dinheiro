package br.cademeudinheiro.repository;

import static org.junit.jupiter.api.Assertions.*;

import br.cademeudinheiro.TesteApi;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

/** Banco de dados: confere que as restrições do DER (PK, FK, UNIQUE, CHECK, CASCADE) realmente valem. */
@TesteApi
class BancoDadosTest {
    @Autowired JdbcTemplate jdbc;

    private UUID usuario() {
        UUID id = UUID.randomUUID();
        jdbc.update("INSERT INTO usuario(id, nome, email, senha_hash) VALUES (?, ?, ?, ?)",
            id, "Teste", "bd" + id + "@exemplo.com", "hash");
        return id;
    }

    private int inserirGasto(UUID usuario, int categoria, String valor) {
        return jdbc.update("INSERT INTO gasto(id, usuario_id, categoria_id, valor, data, descricao) VALUES (?, ?, ?, ?, ?, ?)",
            UUID.randomUUID(), usuario, categoria, new BigDecimal(valor), Date.valueOf(LocalDate.now()), null);
    }

    @Test
    void asTresTabelasDoDerExistemEAsCategoriasForamCriadas() {
        assertNotNull(jdbc.queryForObject("SELECT COUNT(*) FROM usuario", Integer.class));
        assertNotNull(jdbc.queryForObject("SELECT COUNT(*) FROM gasto", Integer.class));
        assertEquals(6, jdbc.queryForObject("SELECT COUNT(*) FROM categoria", Integer.class));
    }

    @Test
    void gastoValidoEhAceito() {
        assertEquals(1, inserirGasto(usuario(), 1, "10.00"));
    }

    @Test
    void rn02_checkRecusaValorZeroNegativoOuAcimaDoMaximo() {
        UUID u = usuario();
        assertThrows(DataIntegrityViolationException.class, () -> inserirGasto(u, 1, "0"));
        assertThrows(DataIntegrityViolationException.class, () -> inserirGasto(u, 1, "-1"));
        assertThrows(DataIntegrityViolationException.class, () -> inserirGasto(u, 1, "10000000.00"));
    }

    @Test
    void fkRecusaCategoriaOuUsuarioInexistente() {
        UUID u = usuario();
        assertThrows(DataIntegrityViolationException.class, () -> inserirGasto(u, 99, "10"));
        assertThrows(DataIntegrityViolationException.class, () -> inserirGasto(UUID.randomUUID(), 1, "10"));
    }

    @Test
    void rn06_emailEhUnico() {
        UUID u = usuario();
        String email = jdbc.queryForObject("SELECT email FROM usuario WHERE id = ?", String.class, u);
        assertThrows(DataIntegrityViolationException.class, () ->
            jdbc.update("INSERT INTO usuario(id, nome, email, senha_hash) VALUES (?, ?, ?, ?)",
                UUID.randomUUID(), "Outro", email, "hash"));
    }

    @Test
    void chavePrimariaImpedeIdRepetido() {
        UUID u = usuario();
        assertThrows(DataIntegrityViolationException.class, () ->
            jdbc.update("INSERT INTO usuario(id, nome, email, senha_hash) VALUES (?, ?, ?, ?)",
                u, "Outro", "outro" + u + "@exemplo.com", "hash"));
    }

    @Test
    void excluirUsuarioApagaOsGastosDeleEmCascata() {
        UUID u = usuario();
        inserirGasto(u, 1, "10");
        inserirGasto(u, 2, "20");
        assertEquals(2, jdbc.queryForObject("SELECT COUNT(*) FROM gasto WHERE usuario_id = ?", Integer.class, u));
        jdbc.update("DELETE FROM usuario WHERE id = ?", u);
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM gasto WHERE usuario_id = ?", Integer.class, u));
    }

    @Test
    void naoEhPossivelExcluirCategoriaQueTemGastos() {
        UUID u = usuario();
        inserirGasto(u, 6, "10");
        assertThrows(DataIntegrityViolationException.class, () -> jdbc.update("DELETE FROM categoria WHERE id = 6"));
    }
}
