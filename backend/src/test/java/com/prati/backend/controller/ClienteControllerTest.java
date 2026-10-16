
package com.prati.backend.controller;

import com.prati.backend.dto.ClienteRequest;
import com.prati.backend.entity.Cliente;
import com.prati.backend.exception.DadosInvalidosException;
import com.prati.backend.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteControllerTest {

    @Test
    void deveCadastrarClienteComSucesso() {
        // Arrange: prepara o cenário
        ClienteService clienteService = mock(ClienteService.class);
        ClienteController controller = new ClienteController(clienteService);

        ClienteRequest request = new ClienteRequest();
        request.setNome("Maria da Silva");
        request.setCpf("52998224725");
        request.setTelefone("51988888888");
        request.setEmail("maria@email.com");
        request.setEndereco("Rua das Flores, 123");

        Cliente clienteSalvo = new Cliente();
        clienteSalvo.setId(1L);
        clienteSalvo.setNome(request.getNome());
        clienteSalvo.setCpf(request.getCpf());
        clienteSalvo.setTelefone(request.getTelefone());
        clienteSalvo.setEmail(request.getEmail());
        clienteSalvo.setEndereco(request.getEndereco());

        when(clienteService.cadastrarCliente(request))
                .thenReturn(clienteSalvo);

        // Act: executa o método do controller
        Cliente resultado = controller.cadastrarCliente(request);

        // Assert: verifica o resultado
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Maria da Silva", resultado.getNome());

        verify(clienteService).cadastrarCliente(request);
    }


    @Test
    void devePropagarExcecaoQuandoCpfForInvalido() {
        ClienteService clienteService = mock(ClienteService.class);
        ClienteController controller = new ClienteController(clienteService);

        ClienteRequest request = new ClienteRequest();
        request.setNome("Maria da Silva");
        request.setCpf("12345678900");
        request.setTelefone("51988888888");

        when(clienteService.cadastrarCliente(request))
                .thenThrow(new DadosInvalidosException(
                        "O CPF informado é inválido."
                ));

        DadosInvalidosException excecao = assertThrows(
                DadosInvalidosException.class,
                () -> controller.cadastrarCliente(request)
        );

        assertEquals(
                "O CPF informado é inválido.",
                excecao.getMessage()
        );

        verify(clienteService).cadastrarCliente(request);
    }
}