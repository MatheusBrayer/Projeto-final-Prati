
package com.prati.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SenhaServiceTest {

    private SenhaService senhaService;

    @BeforeEach
    void setUp() {
        senhaService = new SenhaService();
    }

    @Test
    void deveGerarHashDiferenteDaSenhaOriginal() {
        String senha = "MinhaSenha123";

        String hash = senhaService.criptografar(senha);

        assertNotEquals(senha, hash);
        assertTrue(hash.startsWith("$2"));
    }

    @Test
    void deveRetornarTrueQuandoSenhaInformadaEstiverCorreta() {
        String senha = "MinhaSenha123";
        String hash = senhaService.criptografar(senha);

        boolean resultado = senhaService.comparar(senha, hash);

        assertTrue(resultado);
    }

    @Test
    void deveRetornarFalseQuandoSenhaInformadaEstiverIncorreta() {
        String senha = "MinhaSenha123";
        String hash = senhaService.criptografar(senha);

        boolean resultado = senhaService.comparar("SenhaErrada456", hash);

        assertFalse(resultado);
    }


    @Test
    void deveGerarHashesDiferentesParaAMesmaSenha() {
        String senha = "MinhaSenha123";

        String hash1 = senhaService.criptografar(senha);
        String hash2 = senhaService.criptografar(senha);

        assertNotEquals(hash1, hash2);
        assertTrue(senhaService.comparar(senha, hash1));
        assertTrue(senhaService.comparar(senha, hash2));
    }

}
