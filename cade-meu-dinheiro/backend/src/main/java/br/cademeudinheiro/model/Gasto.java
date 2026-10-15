package br.cademeudinheiro.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Tabela GASTO do DER, já com nome e cor da categoria (JOIN) para exibição. */
public record Gasto(
    UUID id,
    @JsonProperty("categoria_id") int categoriaId,
    String categoria,
    String cor,
    BigDecimal valor,
    LocalDate data,
    String descricao) {}
