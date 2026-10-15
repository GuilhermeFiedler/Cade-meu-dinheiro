package br.cademeudinheiro.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Corpo de POST/PUT /api/gastos. Os campos são validados em Validacao, para devolver mensagens específicas. */
public record GastoRequest(
    Object valor,
    @JsonProperty("categoria_id") Object categoriaId,
    Object data,
    Object descricao) {}
