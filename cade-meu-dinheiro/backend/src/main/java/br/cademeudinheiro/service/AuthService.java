package br.cademeudinheiro.service;

import br.cademeudinheiro.dto.LoginRequest;
import br.cademeudinheiro.dto.RegistroRequest;
import br.cademeudinheiro.dto.SessaoResponse;
import br.cademeudinheiro.dto.UsuarioResponse;
import br.cademeudinheiro.exception.ApiException;
import br.cademeudinheiro.model.Usuario;
import br.cademeudinheiro.repository.UsuarioRepository;
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
    private final UsuarioRepository usuarios;
    private final Tokens tokens;
    private final BCryptPasswordEncoder encoder;

    public AuthService(UsuarioRepository usuarios, Tokens tokens, BCryptPasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.tokens = tokens;
        this.encoder = encoder;
    }

    public SessaoResponse registrar(RegistroRequest r) {
        String nome = Validacao.texto(r.nome());
        String email = Validacao.texto(r.email()).toLowerCase();
        String senha = r.senha() == null ? "" : r.senha();
        if (nome.isEmpty() || nome.length() > 80) {
            throw new ApiException(422, "Informe seu nome (até 80 caracteres)", "nome");
        }
        if (email.length() > 254 || !EMAIL.matcher(email).matches()) {
            throw new ApiException(422, "Informe um e-mail válido", "email");
        }
        if (senha.length() < 8 || senha.length() > 72) {
            throw new ApiException(422, "A senha deve ter entre 8 e 72 caracteres", "senha");
        }
        Usuario u = new Usuario(UUID.randomUUID(), nome, email, encoder.encode(senha)); // RN06
        try {
            usuarios.criar(u);
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(409, "Este e-mail já está em uso", "email");
        }
        return sessao(u);
    }

    public SessaoResponse entrar(LoginRequest r) {
        String senha = r.senha() == null ? "" : r.senha();
        Usuario u = usuarios.buscarPorEmail(Validacao.texto(r.email()).toLowerCase()).orElse(null);
        if (u == null || !encoder.matches(senha, u.senhaHash())) {
            throw new ApiException(401, "E-mail ou senha incorretos", null);
        }
        return sessao(u);
    }

    private SessaoResponse sessao(Usuario u) {
        return new SessaoResponse(tokens.gerar(u.id()), new UsuarioResponse(u.id(), u.nome(), u.email()));
    }
}
