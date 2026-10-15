package br.cademeudinheiro.exception;

/** Erro de negócio ou de validação, devolvido ao cliente com o status HTTP indicado. */
public class ApiException extends RuntimeException {
    public final int status;
    public final String campo;

    public ApiException(int status, String mensagem, String campo) {
        super(mensagem);
        this.status = status;
        this.campo = campo;
    }
}
