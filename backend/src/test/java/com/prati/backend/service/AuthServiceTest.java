
package com.prati.backend.service;

import com.prati.backend.dto.LoginRequest;
import com.prati.backend.dto.LoginResponse;
import com.prati.backend.entity.Usuario;
import com.prati.backend.enums.Perfil;
import com.prati.backend.repository.UsuarioRepository;
import com.prati.backend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.prati.backend.exception.AutenticacaoException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SenhaService senhaService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("usuario@teste.com");
        usuario.setSenha("hash-bcrypt");
        usuario.setPerfil(Perfil.ADM_SISTEMA);
        usuario.setAtivo(true);
    }

    @Test
    void deveRealizarLoginComCredenciaisValidas() {
        LoginRequest request = new LoginRequest();
        request.setEmail("usuario@teste.com");
        request.setSenha("senha-correta");

        when(usuarioRepository.findByEmail("usuario@teste.com"))
                .thenReturn(Optional.of(usuario));

        when(senhaService.comparar("senha-correta", "hash-bcrypt"))
                .thenReturn(true);

        when(jwtService.gerarToken("usuario@teste.com"))
                .thenReturn("token-de-teste");

        LoginResponse response = authService.login(request);

        assertEquals("token-de-teste", response.getToken());
        assertEquals("Bearer", response.getTipo());
        assertEquals(1L, response.getUsuarioId());
        assertEquals("usuario@teste.com", response.getEmail());
        assertEquals(Perfil.ADM_SISTEMA, response.getPerfil());

        verify(jwtService).gerarToken("usuario@teste.com");
    }

    @Test
    void naoDeveRealizarLoginComEmailInexistente() {
        LoginRequest request = new LoginRequest();
        request.setEmail("inexistente@teste.com");
        request.setSenha("qualquer-senha");

        when(usuarioRepository.findByEmail("inexistente@teste.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                AutenticacaoException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never()).gerarToken(anyString());
    }

    @Test
    void naoDeveRealizarLoginComSenhaIncorreta() {
        LoginRequest request = new LoginRequest();
        request.setEmail("usuario@teste.com");
        request.setSenha("senha-errada");

        when(usuarioRepository.findByEmail("usuario@teste.com"))
                .thenReturn(Optional.of(usuario));

        when(senhaService.comparar("senha-errada", "hash-bcrypt"))
                .thenReturn(false);

        assertThrows(
                AutenticacaoException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never()).gerarToken(anyString());
    }

    @Test
    void naoDeveRealizarLoginComUsuarioInativo() {
        usuario.setAtivo(false);

        LoginRequest request = new LoginRequest();
        request.setEmail("usuario@teste.com");
        request.setSenha("senha-correta");

        when(usuarioRepository.findByEmail("usuario@teste.com"))
                .thenReturn(Optional.of(usuario));

        when(senhaService.comparar("senha-correta", "hash-bcrypt"))
                .thenReturn(true);

        assertThrows(
                AutenticacaoException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never()).gerarToken(anyString());
    }
}

