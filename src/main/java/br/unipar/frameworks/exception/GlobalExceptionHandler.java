package br.unipar.frameworks.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> semPermissao() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "mensagem", "Acesso negado"
        ));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> registroNaoEncontrado() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "mensagem", "Registro não encontrado"
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> erroInterno(Exception excecao) {
        String codigoErro = gerarCodigoErro();
        registrarErroNoLog(codigoErro, excecao);

        return ResponseEntity.internalServerError().body(Map.of(
                "mensagem", "Erro interno no servidor. Informe o código: " + codigoErro
        ));
    }

    private String gerarCodigoErro() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private void registrarErroNoLog(String codigoErro, Exception excecao) {
        log.error("Erro [{}] - {}", codigoErro, excecao.getMessage(), excecao);
    }
}
