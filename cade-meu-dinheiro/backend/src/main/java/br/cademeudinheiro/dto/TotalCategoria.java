package br.cademeudinheiro.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record TotalCategoria(
    @JsonProperty("categoria_id") int categoriaId,
    String categoria,
    String cor,
    BigDecimal total) {}
