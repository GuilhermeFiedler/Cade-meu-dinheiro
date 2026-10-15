package br.cademeudinheiro.service;

import br.cademeudinheiro.dto.ResumoResponse;
import br.cademeudinheiro.dto.TotalCategoria;
import br.cademeudinheiro.repository.GastoRepository;
import br.cademeudinheiro.validation.Validacao;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

/** US05 e US06 – total do mês (RF08) e total por categoria (RF09). */
@Service
public class ResumoService {
    private final GastoRepository repo;

    public ResumoService(GastoRepository repo) {
        this.repo = repo;
    }

    public ResumoResponse resumo(UUID usuarioId, String mes) {
        YearMonth ym = Validacao.mes(mes);
        List<TotalCategoria> porCategoria = repo.totalPorCategoria(usuarioId, ym.atDay(1), ym.plusMonths(1).atDay(1));
        BigDecimal total = porCategoria.stream().map(TotalCategoria::total).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ResumoResponse(ym.toString(), total, porCategoria);
    }
}
