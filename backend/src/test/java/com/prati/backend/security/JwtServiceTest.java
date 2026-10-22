
package com.prati.backend.security;

import io.jsonwebtoken.Claims;
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
}

