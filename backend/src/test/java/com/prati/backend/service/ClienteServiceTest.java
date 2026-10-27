package com.prati.backend.service;

import com.prati.backend.dto.ClienteRequest;
import com.prati.backend.entity.Cliente;
import com.prati.backend.entity.Oficina;
import com.prati.backend.entity.Usuario;
import com.prati.backend.enums.Perfil;
import com.prati.backend.exception.ConflitoCadastroException;
import com.prati.backend.exception.DadosInvalidosException;
import com.prati.backend.repository.ClienteRepository;
import com.prati.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    private static final String EMAIL_ADMIN = "admin@oficina.com";

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ClienteOficinaService clienteOficinaService;

    @InjectMocks
    private ClienteService clienteService;

    private void prepararAdministrador(String email) {
        Oficina oficina = new Oficina();
        oficina.setId(1L);

        Usuario administrador = new Usuario();
        administrador.setEmail(email);
        administrador.setPerfil(Perfil.ADM_OFICINA);
        administrador.setAtivo(true);
        administrador.setOficina(oficina);

        when(usuarioRepository.findByEmail(email))
                .thenReturn(Optional.of(administrador));
    }

    @Test
    void deveCadastrarClienteQuandoCpfNaoExiste() {
        prepararAdministrador(EMAIL_ADMIN);

        ClienteRequest request = new ClienteRequest();
        request.setNome("João da Silva");
        request.setCpf("52998224725");
        request.setTelefone("51999999999");
        request.setEmail("joao@email.com");
        request.setEndereco("Rua das Flores");

        when(clienteRepository.existsByCpf(request.getCpf()))
                .thenReturn(false);

        when(clienteRepository.save(any(Cliente.class)))
                .thenAnswer(invocacao -> {
                    Cliente cliente = invocacao.getArgument(0);
                    cliente.setId(1L);
                    return cliente;
                });

        Cliente resultado = clienteService.cadastrarCliente(
                request,
                EMAIL_ADMIN
        );

        assertNotNull(resultado);
        assertEquals("João da Silva", resultado.getNome());
        assertEquals("52998224725", resultado.getCpf());

        verify(clienteRepository).save(any(Cliente.class));
        verify(clienteOficinaService).vincularClienteOficina(1L, 1L);
    }

    @Test
    void deveLancarExcecaoQuandoCpfJaExiste() {
        prepararAdministrador(EMAIL_ADMIN);

        ClienteRequest request = new ClienteRequest();
        request.setNome("João da Silva");
        request.setCpf("52998224725");
        request.setTelefone("51999999999");

        when(clienteRepository.existsByCpf(request.getCpf()))
                .thenReturn(true);

        assertThrows(
                ConflitoCadastroException.class,
                () -> clienteService.cadastrarCliente(request, EMAIL_ADMIN)
        );

        verify(clienteRepository, never()).save(any(Cliente.class));
        verifyNoInteractions(clienteOficinaService);
    }

    @Test
    void deveSalvarTodosOsDadosRecebidosNoRequest() {
        prepararAdministrador(EMAIL_ADMIN);

        ClienteRequest request = new ClienteRequest();
        request.setNome("Maria Souza");
        request.setCpf("11144477735");
        request.setTelefone("51988888888");
        request.setEmail("maria@email.com");
        request.setEndereco("Avenida Central");

        when(clienteRepository.existsByCpf(request.getCpf()))
                .thenReturn(false);

        when(clienteRepository.save(any(Cliente.class)))
                .thenAnswer(invocacao -> {
                    Cliente cliente = invocacao.getArgument(0);
                    cliente.setId(2L);
                    return cliente;
                });

        Cliente resultado = clienteService.cadastrarCliente(
                request,
                EMAIL_ADMIN
        );

        assertAll(
                () -> assertEquals("Maria Souza", resultado.getNome()),
                () -> assertEquals("11144477735", resultado.getCpf()),
                () -> assertEquals("51988888888", resultado.getTelefone()),
                () -> assertEquals("maria@email.com", resultado.getEmail()),
                () -> assertEquals("Avenida Central", resultado.getEndereco())
        );

        verify(clienteOficinaService).vincularClienteOficina(2L, 1L);
    }

    @Test
    void deveLancarExcecaoQuandoCpfForInvalido() {
        prepararAdministrador(EMAIL_ADMIN);

        ClienteRequest request = new ClienteRequest();
        request.setNome("Maria");
        request.setCpf("12345678900");
        request.setTelefone("51988888888");

        assertThrows(
                DadosInvalidosException.class,
                () -> clienteService.cadastrarCliente(request, EMAIL_ADMIN)
        );

        verify(clienteRepository, never()).save(any(Cliente.class));
        verifyNoInteractions(clienteOficinaService);
    }
}