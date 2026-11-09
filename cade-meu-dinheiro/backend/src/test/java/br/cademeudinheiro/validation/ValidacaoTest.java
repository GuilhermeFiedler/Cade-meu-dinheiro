package br.cademeudinheiro.validation;

import static org.junit.jupiter.api.Assertions.*;

import br.cademeudinheiro.exception.ApiException;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ValidacaoTest {
    @Test
    void valorAceitaVirgulaERejeitaInvalidos() { // RN02
        assertEquals(new BigDecimal("32.50"), Validacao.valor("32,50"));
        for (Object ruim : new Object[] {null, "", "abc", 0, -1, "1.234", 100000000}) {
            assertThrows(ApiException.class, () -> Validacao.valor(ruim), String.valueOf(ruim));
        }
    }

    @Test
    void dataNaoPodeSerFutura() { // RN04
        assertEquals(LocalDate.now(), Validacao.data(null));
        assertThrows(ApiException.class, () -> Validacao.data(LocalDate.now().plusDays(1).toString()));
        assertThrows(ApiException.class, () -> Validacao.data("31/12/2024"));
    }

    @Test
    void descricaoTemNoMaximo100Caracteres() { // RN05
        assertNull(Validacao.descricao("  "));
        assertThrows(ApiException.class, () -> Validacao.descricao("x".repeat(101)));
    }

    @Test
    void senhaPrevisivelEhRecusada() { // RN06 (reforço)
        for (String fraca : new String[] {"12345678", "PASSWORD", "zzzzzzzz", "ana.silva@x.com"}) {
            assertThrows(ApiException.class, () -> Validacao.senhaForte(fraca, "ana.silva@x.com"), fraca);
        }
        assertThrows(ApiException.class, () -> Validacao.senhaForte("anasilva", "anasilva@x.com"));
        assertDoesNotThrow(() -> Validacao.senhaForte("Gasto#Teste2024", "ana@x.com"));
    }

    @Test
    void mesUsaFormatoAnoMes() {
        assertEquals("2024-05", Validacao.mes("2024-05").toString());
        assertThrows(ApiException.class, () -> Validacao.mes("05/2024"));
    }
}
