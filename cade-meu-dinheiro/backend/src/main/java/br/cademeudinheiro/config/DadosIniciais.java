package br.cademeudinheiro.config;

import br.cademeudinheiro.model.Categoria;
import br.cademeudinheiro.model.Usuario;
import br.cademeudinheiro.repository.CategoriaRepository;
import br.cademeudinheiro.repository.GastoRepository;
import br.cademeudinheiro.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/** Dados iniciais: categorias (RN03) e, opcionalmente, um usuário de teste com alguns gastos. */
@Component
public class DadosIniciais implements CommandLineRunner {
    private final CategoriaRepository categorias;
    private final UsuarioRepository usuarios;
    private final GastoRepository gastos;
    private final BCryptPasswordEncoder encoder;
    private final boolean dadosTeste;

    public DadosIniciais(CategoriaRepository categorias, UsuarioRepository usuarios, GastoRepository gastos,
                         BCryptPasswordEncoder encoder, @Value("${app.dados-teste:true}") boolean dadosTeste) {
        this.categorias = categorias;
        this.usuarios = usuarios;
        this.gastos = gastos;
        this.encoder = encoder;
        this.dadosTeste = dadosTeste;
    }

    @Override
    public void run(String... args) {
        if (categorias.contar() == 0) {
            categorias.criar(new Categoria(1, "Alimentação", "#d9731f", "utensils"), 1);
            categorias.criar(new Categoria(2, "Transporte", "#2f7d5b", "bus"), 2);
            categorias.criar(new Categoria(3, "Lazer", "#7b5ea7", "film"), 3);
            categorias.criar(new Categoria(4, "Moradia", "#3b7ea1", "home"), 4);
            categorias.criar(new Categoria(5, "Saúde", "#c24d5a", "heart"), 5);
            categorias.criar(new Categoria(6, "Outros", "#8a948d", "tag"), 6);
        }
        if (dadosTeste && usuarios.contar() == 0) {
            UUID uid = UUID.randomUUID();
            usuarios.criar(new Usuario(uid, "Usuário Demo", "demo@exemplo.com", encoder.encode("senha12345")));
            Object[][] g = {{1, "32.50", "Almoço"}, {2, "4.90", "Ônibus"}, {3, "38.00", "Cinema"},
                {1, "210.30", "Mercado"}, {4, "150.00", "Conta de luz"}, {5, "45.90", "Farmácia"}};
            for (int i = 0; i < g.length; i++) {
                gastos.criar(UUID.randomUUID(), uid, (Integer) g[i][0], new BigDecimal((String) g[i][1]),
                    LocalDate.now().minusDays(i), (String) g[i][2]);
            }
        }
    }
}
