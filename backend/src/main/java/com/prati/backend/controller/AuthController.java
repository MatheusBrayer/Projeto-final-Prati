
package com.prati.backend.controller;

import com.prati.backend.dto.*;
import com.prati.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/primeiro-acesso/solicitar")
    public ResponseEntity<Map<String, String>> solicitarPrimeiroAcesso(
            @Valid @RequestBody PrimeiroAcessoRequest request
    ) {
        authService.solicitarPrimeiroAcesso(request);

        return ResponseEntity.ok(Map.of(
                "mensagem",
                "Se os dados corresponderem a um cadastro elegível, você receberá um código por e-mail."
        ));
    }

    @PostMapping("/primeiro-acesso/confirmar")
    public ResponseEntity<Map<String, String>> confirmarPrimeiroAcesso(
            @Valid @RequestBody ConfirmarPrimeiroAcessoRequest request
    ) {
        authService.confirmarPrimeiroAcesso(request);

        return ResponseEntity.ok(Map.of(
                "mensagem",
                "Primeiro acesso realizado com sucesso."
        ));
    }


    @PostMapping("/primeiro-acesso/verificar")
    public ResponseEntity<Map<String, String>> verificarCodigo(
            @Valid @RequestBody VerificarCodigoRequest request
    ) {
        authService.verificarCodigoPrimeiroAcesso(request);

        return ResponseEntity.ok(
                Map.of("mensagem", "Código verificado com sucesso.")
        );
    }

}

