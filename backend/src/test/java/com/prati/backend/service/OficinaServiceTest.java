
package com.prati.backend.service;

import com.prati.backend.dto.OficinaCadastroRequest;
import com.prati.backend.entity.Oficina;
import com.prati.backend.entity.Usuario;
import com.prati.backend.enums.Perfil;
import com.prati.backend.exception.ConflitoCadastroException;
import com.prati.backend.repository.OficinaRepository;
import com.prati.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OficinaServiceTest {

    @Mock
    private OficinaRepository oficinaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private OficinaService oficinaService;

    private OficinaCadastroRequest request;

    @BeforeEach
    void setUp() {
        request = new OficinaCadastroRequest();
        request.setNome("Auto Center Silva");
        request.setCnpj("12345678000199");
        request.setTelefone("51999999999");
        request.setEmail("contato@autocenter.com");
        request.setEndereco("Rua Central, 100");
        request.setEmailAdministrador("ADMIN@AUTOCENTER.COM");
    }

    @Test
    void deveCadastrarOficinaComAdministradorInativo() {
        when(oficinaRepository.save(any(Oficina.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Oficina oficina = oficinaService.cadastrar(request);

        assertEquals("Auto Center Silva", oficina.getNome());
        assertEquals("12345678000199", oficina.getCnpj());

        ArgumentCaptor<Usuario> captor =
                ArgumentCaptor.forClass(Usuario.class);

        verify(usuarioRepository).save(captor.capture());

        Usuario administrador = captor.getValue();

        assertEquals("admin@autocenter.com", administrador.getEmail());
        assertEquals(Perfil.ADM_OFICINA, administrador.getPerfil());
        assertFalse(administrador.getAtivo());
        assertNull(administrador.getSenha());
        assertSame(oficina, administrador.getOficina());
    }

    @Test
    void naoDeveCadastrarOficinaComCnpjDuplicado() {
        when(oficinaRepository.existsByCnpj("12345678000199"))
                .thenReturn(true);

        assertThrows(
                ConflitoCadastroException.class,
                () -> oficinaService.cadastrar(request)
        );

        verify(oficinaRepository, never()).save(any());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void naoDeveCadastrarOficinaComEmailAdministradorDuplicado() {
        when(usuarioRepository.existsByEmail("admin@autocenter.com"))
                .thenReturn(true);

        assertThrows(
                ConflitoCadastroException.class,
                () -> oficinaService.cadastrar(request)
        );

        verify(oficinaRepository, never()).save(any());
        verify(usuarioRepository, never()).save(any());
    }
}
