package com.prati.backend.service;

import com.prati.backend.dto.UsuarioRequest;
import com.prati.backend.entity.Cliente;
import com.prati.backend.entity.Oficina;
import com.prati.backend.entity.Usuario;
import com.prati.backend.enums.Perfil;
import com.prati.backend.exception.ConflitoCadastroException;
import com.prati.backend.exception.RecursoNaoEncontradoException;
import com.prati.backend.repository.ClienteRepository;
import com.prati.backend.repository.OficinaRepository;
import com.prati.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private OficinaRepository oficinaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private SenhaService senhaService;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void configurarTeste() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveValidarEmailQuandoEmailNaoExiste() {
        when(usuarioRepository.existsByEmail("matheus@email.com"))
                .thenReturn(false);

        usuarioService.validarEmail("matheus@email.com");

        verify(usuarioRepository).existsByEmail("matheus@email.com");
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaExiste() {
        when(usuarioRepository.existsByEmail("matheus@email.com"))
                .thenReturn(true);

        assertThrows(
                ConflitoCadastroException.class,
                () -> usuarioService.validarEmail("matheus@email.com")
        );
    }

    @Test
    void deveValidarPerfilAdministradorSistemaSemVinculos() {
        usuarioService.validarPerfil(
                Perfil.ADM_SISTEMA,
                null,
                null
        );
    }

    @Test
    void deveLancarExcecaoQuandoAdministradorSistemaPossuiOficina() {
        assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.validarPerfil(
                        Perfil.ADM_SISTEMA,
                        1L,
                        null
                )
        );
    }

    @Test
    void deveLancarExcecaoQuandoAdministradorSistemaPossuiCliente() {
        assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.validarPerfil(
                        Perfil.ADM_SISTEMA,
                        null,
                        1L
                )
        );
    }

    @Test
    void deveLancarExcecaoQuandoAdministradorOficinaNaoPossuiOficina() {
        assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.validarPerfil(
                        Perfil.ADM_OFICINA,
                        null,
                        null
                )
        );
    }

    @Test
    void deveLancarExcecaoQuandoAdministradorOficinaPossuiCliente() {
        assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.validarPerfil(
                        Perfil.ADM_OFICINA,
                        1L,
                        1L
                )
        );
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoPossuiVinculoComCliente() {
        assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.validarPerfil(
                        Perfil.CLIENTE,
                        null,
                        null
                )
        );
    }

    @Test
    void deveLancarExcecaoQuandoClientePossuiVinculoDiretoComOficina() {
        assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.validarPerfil(
                        Perfil.CLIENTE,
                        1L,
                        1L
                )
        );
    }

    @Test
    void deveValidarPerfilAdministradorOficinaComOficina() {
        usuarioService.validarPerfil(
                Perfil.ADM_OFICINA,
                1L,
                null
        );
    }

    @Test
    void deveValidarPerfilClienteComClienteVinculado() {
        usuarioService.validarPerfil(
                Perfil.CLIENTE,
                null,
                1L
        );
    }

    @Test
    void deveImpedirCadastroQuandoEmailJaExiste() {
        UsuarioRequest request = new UsuarioRequest();
        request.setEmail("admin.oficina@autogestao.com");
        request.setSenha("SenhaTemporaria123");

        when(usuarioRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

        assertThrows(
                ConflitoCadastroException.class,
                () -> usuarioService.cadastrarAdministradorOficina(
                        request,
                        1L
                )
        );

        verify(usuarioRepository, never()).save(any(Usuario.class));
        verifyNoInteractions(oficinaRepository);
    }

    @Test
    void deveLancarExcecaoQuandoOficinaNaoExiste() {
        UsuarioRequest request = new UsuarioRequest();
        request.setEmail("admin.oficina@autogestao.com");
        request.setSenha("SenhaTemporaria123");

        when(usuarioRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(oficinaRepository.findById(1L))
                .thenReturn(java.util.Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> usuarioService.cadastrarAdministradorOficina(
                        request,
                        1L
                )
        );

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoExiste() {
        UsuarioRequest request = new UsuarioRequest();
        request.setEmail("cliente@autogestao.com");
        request.setSenha("SenhaTemporaria123");

        when(usuarioRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(clienteRepository.findById(1L))
                .thenReturn(java.util.Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> usuarioService.cadastrarUsuarioCliente(
                        request,
                        1L
                )
        );

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deveCadastrarAdministradorOficinaComVinculoCorreto() {
        UsuarioRequest request = new UsuarioRequest();
        request.setEmail("admin.oficina@autogestao.com");
        request.setSenha("SenhaTemporaria123");

        Oficina oficina = new Oficina();

        when(usuarioRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(oficinaRepository.findById(1L))
                .thenReturn(java.util.Optional.of(oficina));

        when(senhaService.criptografar(request.getSenha()))
                .thenReturn("hash-da-senha");

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        Usuario resultado =
                usuarioService.cadastrarAdministradorOficina(request, 1L);

        assertEquals(request.getEmail(), resultado.getEmail());
        assertEquals(Perfil.ADM_OFICINA, resultado.getPerfil());
        assertEquals(oficina, resultado.getOficina());
        assertNull(resultado.getCliente());
        assertEquals("hash-da-senha", resultado.getSenha());

        verify(senhaService).criptografar(request.getSenha());
        verify(oficinaRepository).findById(1L);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void deveCadastrarClienteComVinculoCorreto() {
        UsuarioRequest request = new UsuarioRequest();
        request.setEmail("cliente@autogestao.com");
        request.setSenha("SenhaTemporaria123");

        Cliente cliente = new Cliente();

        when(usuarioRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(clienteRepository.findById(1L))
                .thenReturn(java.util.Optional.of(cliente));

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        when(senhaService.criptografar(request.getSenha()))
                .thenReturn("hash-da-senha");

        Usuario resultado =
                usuarioService.cadastrarUsuarioCliente(request, 1L);

        assertEquals(request.getEmail(), resultado.getEmail());
        assertEquals(Perfil.CLIENTE, resultado.getPerfil());
        assertEquals(cliente, resultado.getCliente());
        assertNull(resultado.getOficina());
        assertEquals("hash-da-senha", resultado.getSenha());

        verify(senhaService).criptografar(request.getSenha());
        verify(clienteRepository).findById(1L);
        verify(usuarioRepository).save(any(Usuario.class));
    }
}
