
package com.prati.backend.service;

import com.prati.backend.dto.ClienteRequest;
import com.prati.backend.entity.Cliente;
import com.prati.backend.exception.ConflitoCadastroException;
import com.prati.backend.exception.DadosInvalidosException;
import com.prati.backend.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.prati.backend.exception.RecursoNaoEncontradoException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void deveCadastrarClienteQuandoCpfNaoExiste() {
        ClienteRequest request = new ClienteRequest();
        request.setNome("João da Silva");
        request.setCpf("52998224725");
        request.setTelefone("51999999999");
        request.setEmail("joao@email.com");
        request.setEndereco("Rua das Flores");

        when(clienteRepository.existsByCpf(request.getCpf()))
                .thenReturn(false);

        when(clienteRepository.save(any(Cliente.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        Cliente resultado = clienteService.cadastrarCliente(request);

        assertNotNull(resultado);
        assertEquals("João da Silva", resultado.getNome());
        assertEquals("52998224725", resultado.getCpf());

        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoQuandoCpfJaExiste() {
        ClienteRequest request = new ClienteRequest();
        request.setNome("João da Silva");
        request.setCpf("52998224725");
        request.setTelefone("51999999999");

        when(clienteRepository.existsByCpf(request.getCpf()))
                .thenReturn(true);

        assertThrows(
                ConflitoCadastroException.class,
                () -> clienteService.cadastrarCliente(request)
        );

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveSalvarTodosOsDadosRecebidosNoRequest() {
        ClienteRequest request = new ClienteRequest();
        request.setNome("Maria Souza");
        request.setCpf("11144477735");
        request.setTelefone("51988888888");
        request.setEmail("maria@email.com");
        request.setEndereco("Avenida Central");

        when(clienteRepository.existsByCpf(request.getCpf()))
                .thenReturn(false);

        when(clienteRepository.save(any(Cliente.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        Cliente resultado = clienteService.cadastrarCliente(request);

        assertAll(
                () -> assertEquals("Maria Souza", resultado.getNome()),
                () -> assertEquals("11144477735", resultado.getCpf()),
                () -> assertEquals("51988888888", resultado.getTelefone()),
                () -> assertEquals("maria@email.com", resultado.getEmail()),
                () -> assertEquals("Avenida Central", resultado.getEndereco())
        );
    }


    @Test
    void deveLancarExcecaoQuandoCpfForInvalido() {
        ClienteRequest request = new ClienteRequest();
        request.setNome("Maria");
        request.setCpf("12345678900");
        request.setTelefone("51988888888");

        assertThrows(
                DadosInvalidosException.class,
                () -> clienteService.cadastrarCliente(request)
        );

        verifyNoInteractions(clienteRepository);
    }

    @Test
    void deveBuscarClientePeloEmail() {
        Cliente cliente = new Cliente();
        cliente.setEmail("cliente@email.com");

        when(clienteRepository.findByEmail("cliente@email.com"))
                .thenReturn(Optional.of(cliente));

        Cliente resultado =
                clienteService.buscarClientePorEmail("cliente@email.com");

        assertEquals("cliente@email.com", resultado.getEmail());
        verify(clienteRepository).findByEmail("cliente@email.com");
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoForEncontradoPeloEmail() {
        when(clienteRepository.findByEmail("inexistente@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> clienteService.buscarClientePorEmail("inexistente@email.com")
        );
    }

}
