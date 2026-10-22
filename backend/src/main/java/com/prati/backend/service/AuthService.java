
package com.prati.backend.service;

import com.prati.backend.dto.LoginRequest;
import com.prati.backend.dto.LoginResponse;
import com.prati.backend.entity.Usuario;
import com.prati.backend.exception.AutenticacaoException;
import com.prati.backend.repository.UsuarioRepository;
import com.prati.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final SenhaService senhaService;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {

        Usuario usuario = usuarioRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new AutenticacaoException(
                        "E-mail ou senha inválidos."
                ));

        if (!senhaService.comparar(
                request.getSenha(),
                usuario.getSenha()
        )) {
            throw new AutenticacaoException(
                    "E-mail ou senha inválidos."
            );
        }

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new AutenticacaoException(
                    "Usuário inativo."
            );
        }

        String token = jwtService.gerarToken(usuario.getEmail());

        return new LoginResponse(
                token,
                "Bearer",
                usuario.getId(),
                usuario.getEmail(),
                usuario.getPerfil()
        );
    }
}
