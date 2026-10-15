package br.cademeudinheiro.security;

import br.cademeudinheiro.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/** Exige token válido nas rotas protegidas e guarda o id do usuário no atributo "uid" da requisição. */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final Tokens tokens;

    public AuthInterceptor(Tokens tokens) {
        this.tokens = tokens;
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        String h = req.getHeader("Authorization");
        UUID uid = h != null && h.startsWith("Bearer ") ? tokens.ler(h.substring(7)) : null;
        if (uid == null) {
            throw new ApiException(401, "Sessão expirada. Entre novamente.", null);
        }
        req.setAttribute("uid", uid);
        return true;
    }
}
