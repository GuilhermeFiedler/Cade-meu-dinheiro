package br.cademeudinheiro.model;

import java.util.UUID;

/** Tabela USUARIO do DER. */
public record Usuario(UUID id, String nome, String email, String senhaHash) {}
