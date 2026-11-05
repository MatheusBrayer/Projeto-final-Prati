
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
import com.prati.backend.repository.ClienteRepository;
import com.prati.backend.entity.Cliente;
import com.prati.backend.enums.FinalidadeCodigoVerificacao;
import com.prati.backend.dto.PrimeiroAcessoRequest;
import com.prati.backend.dto.ConfirmarPrimeiroAcessoRequest;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SenhaService senhaService;

    @Mock
    private JwtService jwtService;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private CodigoVerificacaoService codigoVerificacaoService;

    @Mock
    private UsuarioService usuarioService;

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

    @Test
    void deveSolicitarPrimeiroAcessoParaClienteCadastrado() {

        PrimeiroAcessoRequest request = new PrimeiroAcessoRequest();
        request.setEmail("  CLIENTE@TESTE.COM  ");

        Cliente cliente = new Cliente();
        cliente.setId(10L);
        cliente.setEmail("cliente@teste.com");

        when(usuarioRepository.existsByEmail("cliente@teste.com"))
                .thenReturn(false);

        when(clienteRepository.findByEmail("cliente@teste.com"))
                .thenReturn(Optional.of(cliente));

        authService.solicitarPrimeiroAcesso(request);

        verify(codigoVerificacaoService).gerarCodigo(
                "cliente@teste.com",
                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO
        );
    }

    @Test
    void naoDeveEnviarCodigoQuandoUsuarioJaExiste() {

        PrimeiroAcessoRequest request = new PrimeiroAcessoRequest();
        request.setEmail("usuario@teste.com");

        when(usuarioRepository.existsByEmail("usuario@teste.com"))
                .thenReturn(true);

        authService.solicitarPrimeiroAcesso(request);

        verify(codigoVerificacaoService, never())
                .gerarCodigo(anyString(), any(FinalidadeCodigoVerificacao.class));

        verify(clienteRepository, never()).findByEmail(anyString());
    }

    @Test
    void naoDeveEnviarCodigoQuandoClienteNaoExiste() {

        PrimeiroAcessoRequest request = new PrimeiroAcessoRequest();
        request.setEmail("desconhecido@teste.com");

        when(usuarioRepository.existsByEmail("desconhecido@teste.com"))
                .thenReturn(false);

        when(clienteRepository.findByEmail("desconhecido@teste.com"))
                .thenReturn(Optional.empty());

        authService.solicitarPrimeiroAcesso(request);

        verify(codigoVerificacaoService, never())
                .gerarCodigo(anyString(), any(FinalidadeCodigoVerificacao.class));
    }

    @Test
    void deveConfirmarPrimeiroAcessoComCodigoValido() {

        ConfirmarPrimeiroAcessoRequest request =
                new ConfirmarPrimeiroAcessoRequest();

        request.setEmail("  CLIENTE@TESTE.COM  ");
        request.setCodigo("123456");
        request.setSenha("senha123");

        Cliente cliente = new Cliente();
        cliente.setId(10L);
        cliente.setEmail("cliente@teste.com");

        when(usuarioRepository.existsByEmail("cliente@teste.com"))
                .thenReturn(false);

        when(clienteRepository.findByEmail("cliente@teste.com"))
                .thenReturn(Optional.of(cliente));

        when(codigoVerificacaoService.validarCodigo(
                "cliente@teste.com",
                "123456",
                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO
        )).thenReturn(true);

        authService.confirmarPrimeiroAcesso(request);

        verify(usuarioService).cadastrarUsuarioCliente(
                "cliente@teste.com",
                "senha123",
                10L
        );
    }

    @Test
    void naoDeveCriarUsuarioQuandoCodigoForInvalido() {

        ConfirmarPrimeiroAcessoRequest request =
                new ConfirmarPrimeiroAcessoRequest();

        request.setEmail("cliente@teste.com");
        request.setCodigo("999999");
        request.setSenha("senha123");

        Cliente cliente = new Cliente();
        cliente.setId(10L);
        cliente.setEmail("cliente@teste.com");

        when(usuarioRepository.existsByEmail("cliente@teste.com"))
                .thenReturn(false);

        when(clienteRepository.findByEmail("cliente@teste.com"))
                .thenReturn(Optional.of(cliente));

        when(codigoVerificacaoService.validarCodigo(
                "cliente@teste.com",
                "999999",
                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO
        )).thenReturn(false);

        assertThrows(
                AutenticacaoException.class,
                () -> authService.confirmarPrimeiroAcesso(request)
        );

        verify(usuarioService, never())
                .cadastrarUsuarioCliente(anyString(), anyString(), anyLong());
    }
}

