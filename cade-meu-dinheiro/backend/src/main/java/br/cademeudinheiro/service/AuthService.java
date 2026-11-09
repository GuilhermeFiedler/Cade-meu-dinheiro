package br.cademeudinheiro.service;

import br.cademeudinheiro.dto.LoginRequest;
import br.cademeudinheiro.dto.RegistroRequest;
import br.cademeudinheiro.dto.SessaoResponse;
import br.cademeudinheiro.dto.UsuarioResponse;
import br.cademeudinheiro.exception.ApiException;
import br.cademeudinheiro.model.Usuario;
import br.cademeudinheiro.repository.UsuarioRepository;
import br.cademeudinheiro.security.LimiteTentativas;
import br.cademeudinheiro.security.Tokens;
import br.cademeudinheiro.validation.Validacao;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/** US01 – criar conta (RF01) e entrar (RF02). */
@Service
public class AuthService {
    private static final Pattern EMAIL = Pattern.compile("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");
    private static final int SENHA_MAX = 72; // limite do BCrypt
    private final UsuarioRepository usuarios;
    private final Tokens tokens;
    private final BCryptPasswordEncoder encoder;
    private final LimiteTentativas limite;
    /** Hash descartável: o login de e-mail inexistente também gasta o tempo do BCrypt (evita revelar quem tem conta). */
    private final String hashFalso;

    public AuthService(UsuarioRepository usuarios, Tokens tokens, BCryptPasswordEncoder encoder, LimiteTentativas limite) {
        this.usuarios = usuarios;
        this.tokens = tokens;
        this.encoder = encoder;
        this.limite = limite;
        this.hashFalso = encoder.encode(UUID.randomUUID().toString());
    }

    public SessaoResponse registrar(RegistroRequest r, String ip) {
        limite.contarRegistro(ip);
        String nome = Validacao.texto(r.nome());
        String email = Validacao.texto(r.email()).toLowerCase();
        String senha = r.senha() == null ? "" : r.senha();
        if (nome.isEmpty() || nome.length() > 80) {
            throw new ApiException(422, "Informe seu nome (até 80 caracteres)", "nome");
        }
        if (email.length() > 254 || !EMAIL.matcher(email).matches()) {
            throw new ApiException(422, "Informe um e-mail válido", "email");
        }
        if (senha.length() < 8 || senha.length() > SENHA_MAX) {
            throw new ApiException(422, "A senha deve ter entre 8 e 72 caracteres", "senha");
        }
        Validacao.senhaForte(senha, email);
        Usuario u = new Usuario(UUID.randomUUID(), nome, email, encoder.encode(senha)); // RN06
        try {
            usuarios.criar(u);
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(409, "Este e-mail já está em uso", "email");
        }
        return sessao(u);
    }

    public SessaoResponse entrar(LoginRequest r, String ip) {
        String email = Validacao.texto(r.email()).toLowerCase();
        limite.exigirLoginLiberado(ip, email);
        String senha = r.senha() == null ? "" : r.senha();
        Usuario u = usuarios.buscarPorEmail(email).orElse(null);
        // Sempre roda o BCrypt, exista o usuário ou não. Senha acima do limite nunca confere (e não é processada).
        String hash = u != null ? u.senhaHash() : hashFalso;
        String tentativa = senha.length() <= SENHA_MAX ? senha : "";
        boolean confere = encoder.matches(tentativa, hash);
        if (u == null || !confere || senha.length() > SENHA_MAX) {
            limite.registrarFalhaLogin(ip, email);
            throw new ApiException(401, "E-mail ou senha incorretos", null);
        }
        limite.limparLogin(email);
        return sessao(u);
    }

    private SessaoResponse sessao(Usuario u) {
        return new SessaoResponse(tokens.gerar(u.id()), new UsuarioResponse(u.id(), u.nome(), u.email()));
    }
}
