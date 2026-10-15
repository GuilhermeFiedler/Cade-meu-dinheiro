package br.cademeudinheiro.exception;

import br.cademeudinheiro.dto.ErroResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/** Tratamento central de erros: toda resposta de erro tem o formato {"erro":{"mensagem":...,"campo":...}}. */
@RestControllerAdvice
public class ErroHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ErroHandler.class);

    private static ResponseEntity<ErroResponse> erro(int status, String mensagem, String campo) {
        return ResponseEntity.status(status).body(new ErroResponse(new ErroResponse.Detalhe(mensagem, campo)));
    }

    @ExceptionHandler(ApiException.class)
    ResponseEntity<ErroResponse> api(ApiException e) {
        return erro(e.status, e.getMessage(), e.campo);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, HttpMediaTypeNotSupportedException.class})
    ResponseEntity<ErroResponse> corpoInvalido(Exception e) {
        return erro(400, "Corpo da requisição inválido (JSON esperado)", null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ErroResponse> idInvalido(Exception e) {
        return erro(404, "Gasto não encontrado", null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ErroResponse> naoEncontrado(Exception e) {
        return erro(404, "Rota não encontrada", null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ErroResponse> metodoNaoPermitido(Exception e) {
        return erro(405, "Método não permitido", null);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErroResponse> inesperado(Exception e) {
        LOG.error("Erro inesperado", e);
        return erro(500, "Erro interno. Tente novamente em instantes.", null);
    }
}
