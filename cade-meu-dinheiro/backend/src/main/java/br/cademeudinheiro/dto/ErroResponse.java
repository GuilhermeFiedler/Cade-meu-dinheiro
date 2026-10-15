package br.cademeudinheiro.dto;

/** Formato padrão de erro: {"erro":{"mensagem":"...","campo":"..."}}. */
public record ErroResponse(Detalhe erro) {
    public record Detalhe(String mensagem, String campo) {}
}
