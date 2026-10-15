package br.cademeudinheiro.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

public record ResumoResponse(
    String mes,
    BigDecimal total,
    @JsonProperty("por_categoria") List<TotalCategoria> porCategoria) {}
