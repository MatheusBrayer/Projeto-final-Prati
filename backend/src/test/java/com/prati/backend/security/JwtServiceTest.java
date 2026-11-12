
package com.prati.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "jwtSecret",
                "VGhpc0lzQVRlc3RTZWNyZXRLZXlGb3JBdXRvR2VzdGFvUHJv"
        );

        ReflectionTestUtils.setField(
                jwtService,
                "jwtExpiration",
                3600000L
        );
    }

    @Test
    void deveGerarTokenComEmailEExpiracao() {
        String email = "usuario@teste.com";

        String token = jwtService.gerarToken(email);

        assertNotNull(token);
        assertFalse(token.isBlank());

        SecretKey chave = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(
                        "VGhpc0lzQVRlc3RTZWNyZXRLZXlGb3JBdXRvR2VzdGFvUHJv"
                )
        );

        Claims claims = Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(email, claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(new Date()));
    }


    @Test
    void deveExtrairEmailDeTokenValido() {
        // Arrange: prepara um token válido para um usuário
        String email = "teste@autogestao.com";
        String token = jwtService.gerarToken(email);

        // Act: extrai o e-mail do token
        String emailExtraido = jwtService.extrairEmailDoToken(token);

        // Assert: verifica se o e-mail foi recuperado corretamente
        assertEquals(email, emailExtraido);
    }

    @Test
    void deveRejeitarTokenComAssinaturaAdulterada() {
        // Arrange: gera um token válido
        String token = jwtService.gerarToken("teste@autogestao.com");
        String[] partes = token.split("\\.");

        // Altera a assinatura sem alterar o restante do token
        String primeiraLetra = partes[2].charAt(0) == 'A' ? "B" : "A";
        String assinaturaAdulterada =
                primeiraLetra + partes[2].substring(1);

        String tokenAdulterado =
                partes[0] + "." + partes[1] + "." + assinaturaAdulterada;

        // Act + Assert: espera uma exceção ao validar a assinatura
        assertThrows(
                JwtException.class,
                () -> jwtService.extrairEmailDoToken(tokenAdulterado)
        );
    }

    @Test
    void deveRejeitarTokenMalformado() {
        // Arrange: texto que não representa um JWT válido
        String tokenMalformado = "isto-nao-e-um-jwt";

        // Act + Assert: espera uma exceção ao tentar interpretar o token
        assertThrows(
                JwtException.class,
                () -> jwtService.extrairEmailDoToken(tokenMalformado)
        );
    }

}

