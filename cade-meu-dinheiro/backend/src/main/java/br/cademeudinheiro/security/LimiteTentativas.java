package br.cademeudinheiro.security;

import br.cademeudinheiro.exception.ApiException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Limita tentativas de login e criação de contas (proteção contra força bruta e abuso do cadastro).
 * Guarda os eventos em memória, numa janela de tempo deslizante. Serve para uma única instância do backend;
 * com várias instâncias, o contador deve ir para um armazenamento compartilhado (ex.: Redis).
 */
@Component
public class LimiteTentativas {
    private static final int MAX_CHAVES = 10_000;
    private static final String MENSAGEM = "Muitas tentativas. Tente novamente em alguns minutos.";

    private final int maxFalhasLogin;
    private final int maxRegistros;
    private final long janelaMs;
    private final Map<String, Deque<Long>> eventos = new ConcurrentHashMap<>();

    public LimiteTentativas(@Value("${app.limite.login-falhas:5}") int maxFalhasLogin,
                            @Value("${app.limite.registros:20}") int maxRegistros,
                            @Value("${app.limite.janela-minutos:15}") int janelaMinutos) {
        this.maxFalhasLogin = maxFalhasLogin;
        this.maxRegistros = maxRegistros;
        this.janelaMs = janelaMinutos * 60_000L;
    }

    /** Recusa (429) se este e-mail ou este IP já falhou demais no login dentro da janela. */
    public void exigirLoginLiberado(String ip, String email) {
        if (contar(chaveEmail(email)) >= maxFalhasLogin || contar(chaveIp(ip)) >= maxFalhasLogin * 4) {
            throw new ApiException(429, MENSAGEM, null);
        }
    }

    public void registrarFalhaLogin(String ip, String email) {
        somar(chaveEmail(email));
        somar(chaveIp(ip));
    }

    /** Login correto: zera as falhas desse e-mail (as do IP continuam valendo). */
    public void limparLogin(String email) {
        eventos.remove(chaveEmail(email));
    }

    /** Cada tentativa de cadastro conta para o IP; passou do limite, recusa (429). */
    public void contarRegistro(String ip) {
        String chave = "reg:" + normalizaIp(ip);
        if (contar(chave) >= maxRegistros) {
            throw new ApiException(429, MENSAGEM, null);
        }
        somar(chave);
    }

    private static String normalizaIp(String ip) {
        return ip == null || ip.isBlank() ? "desconhecido" : ip;
    }

    private static String chaveIp(String ip) {
        return "ip:" + normalizaIp(ip);
    }

    private static String chaveEmail(String email) {
        String e = email == null ? "" : email;
        return "email:" + (e.length() > 254 ? e.substring(0, 254) : e); // limita o tamanho da chave
    }

    private int contar(String chave) {
        Deque<Long> d = eventos.get(chave);
        if (d == null) {
            return 0;
        }
        long limite = System.currentTimeMillis() - janelaMs;
        synchronized (d) {
            while (!d.isEmpty() && d.peekFirst() < limite) {
                d.pollFirst();
            }
            return d.size();
        }
    }

    private void somar(String chave) {
        Deque<Long> d = eventos.computeIfAbsent(chave, k -> new ArrayDeque<>());
        synchronized (d) {
            d.addLast(System.currentTimeMillis());
        }
        if (eventos.size() > MAX_CHAVES) {
            eventos.keySet().removeIf(k -> contar(k) == 0); // evita crescer sem limite
        }
    }
}
