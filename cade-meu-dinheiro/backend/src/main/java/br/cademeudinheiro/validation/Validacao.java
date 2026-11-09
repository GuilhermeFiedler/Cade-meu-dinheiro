package br.cademeudinheiro.validation;

import br.cademeudinheiro.exception.ApiException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Set;
import java.util.regex.Pattern;

/** Validações de entrada e regras RN02 (valor), RN04 (data) e RN05 (descrição). */
public final class Validacao {
    private static final BigDecimal MAX = new BigDecimal("9999999.99");
    private static final Pattern MES = Pattern.compile("20\\d\\d-(0[1-9]|1[0-2])");

    /** Senhas muito comuns, recusadas no cadastro (lista curta de exemplo; ver docs/06-seguranca.md). */
    private static final Set<String> SENHAS_COMUNS = Set.of(
        "12345678", "123456789", "1234567890", "11111111", "00000000", "87654321", "12341234",
        "senha123", "senha1234", "senha12345", "password", "password1", "password123",
        "qwerty123", "qwertyui", "abc12345", "abcd1234", "iloveyou", "admin123",
        "minhasenha", "brasil123", "mudar123");

    private Validacao() {}

    /** RN06 (reforço): recusa senha previsível: da lista de comuns, um único caractere repetido ou igual ao e-mail. */
    public static void senhaForte(String senha, String email) {
        String s = senha.toLowerCase();
        String parteLocal = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
        boolean repetida = senha.chars().distinct().count() == 1;
        if (SENHAS_COMUNS.contains(s) || repetida || s.equals(email) || (parteLocal.length() >= 4 && s.equals(parteLocal))) {
            throw new ApiException(422, "Escolha uma senha menos previsível", "senha");
        }
    }

    public static String texto(Object o) {
        return o instanceof String s ? s.trim() : "";
    }

    /** RN02: maior que zero, até 2 casas decimais, máximo R$ 9.999.999,99. Aceita "32,50". */
    public static BigDecimal valor(Object v) {
        BigDecimal d = null;
        if (v != null && !v.toString().isBlank()) {
            try {
                d = new BigDecimal(v.toString().trim().replace(",", "."));
            } catch (NumberFormatException e) {
                d = null;
            }
        }
        if (d == null || d.signum() <= 0) {
            throw new ApiException(422, "Informe um valor maior que zero", "valor");
        }
        if (d.stripTrailingZeros().scale() > 2) {
            throw new ApiException(422, "O valor deve ter no máximo duas casas decimais", "valor");
        }
        if (d.compareTo(MAX) > 0) {
            throw new ApiException(422, "O valor máximo é R$ 9.999.999,99", "valor");
        }
        return d.setScale(2);
    }

    /** RN04: a data não pode ser futura; se omitida, assume hoje. */
    public static LocalDate data(Object v) {
        if (v == null || v.toString().isBlank()) {
            return LocalDate.now();
        }
        LocalDate d;
        try {
            d = LocalDate.parse(v.toString());
        } catch (DateTimeParseException e) {
            throw new ApiException(422, "Data inválida. Use AAAA-MM-DD", "data");
        }
        if (d.isAfter(LocalDate.now())) {
            throw new ApiException(422, "A data não pode ser futura", "data");
        }
        return d;
    }

    /** RN05: opcional, até 100 caracteres. */
    public static String descricao(Object v) {
        String s = texto(v);
        if (s.length() > 100) {
            throw new ApiException(422, "A descrição deve ter no máximo 100 caracteres", "descricao");
        }
        return s.isEmpty() ? null : s;
    }

    /** Mês no formato AAAA-MM; se omitido, assume o mês atual. */
    public static YearMonth mes(String m) {
        if (m == null || m.isBlank()) {
            return YearMonth.now();
        }
        if (!MES.matcher(m).matches()) {
            throw new ApiException(400, "Mês inválido. Use AAAA-MM", "mes");
        }
        return YearMonth.parse(m);
    }
}
