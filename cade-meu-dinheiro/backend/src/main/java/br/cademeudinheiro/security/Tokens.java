package br.cademeudinheiro.security;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Token de sessão assinado com HMAC-SHA256, válido por 7 dias (RNF12). */
@Component
public class Tokens {
    private static final long SESSAO_SEGUNDOS = 7L * 24 * 3600;
    private final byte[] segredo;

    public Tokens(@Value("${app.secret:}") String segredo) {
        String s = segredo.isBlank() ? UUID.randomUUID() + "" + UUID.randomUUID() : segredo;
        this.segredo = s.getBytes(UTF_8);
    }

    private String assina(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(segredo, "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public String gerar(UUID usuarioId) {
        long exp = System.currentTimeMillis() / 1000 + SESSAO_SEGUNDOS;
        String payload = Base64.getUrlEncoder().withoutPadding().encodeToString((usuarioId + "." + exp).getBytes(UTF_8));
        return payload + "." + assina(payload);
    }

    /** Devolve o id do usuário, ou null se o token for inválido ou estiver expirado. */
    public UUID ler(String token) {
        try {
            int i = token.lastIndexOf('.');
            String payload = token.substring(0, i);
            if (!MessageDigest.isEqual(assina(payload).getBytes(UTF_8), token.substring(i + 1).getBytes(UTF_8))) {
                return null;
            }
            String dados = new String(Base64.getUrlDecoder().decode(payload), UTF_8);
            int j = dados.lastIndexOf('.');
            if (Long.parseLong(dados.substring(j + 1)) <= System.currentTimeMillis() / 1000) {
                return null;
            }
            return UUID.fromString(dados.substring(0, j));
        } catch (Exception e) {
            return null;
        }
    }
}
