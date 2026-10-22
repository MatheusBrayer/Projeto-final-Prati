
package com.prati.backend.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    @Test
    void deveRetornar401QuandoAutenticacaoFalhar() {
        // Arrange: prepara a exceção que representa a falha
        AutenticacaoException exception =
                new AutenticacaoException("E-mail ou senha inválidos.");

        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        // Act: executa o método responsável por tratar a exceção
        ResponseEntity<Map<String, String>> resposta =
                handler.tratarFalhaAutenticacao(exception);

        // Assert: verifica a resposta produzida
        assertEquals(HttpStatus.UNAUTHORIZED, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(
                "E-mail ou senha inválidos.",
                resposta.getBody().get("erro")
        );
    }
}
