
package com.prati.backend.service;

import com.prati.backend.entity.Cliente;
import com.prati.backend.entity.ClienteOficina;
import com.prati.backend.entity.Oficina;
import com.prati.backend.exception.ConflitoCadastroException;
import com.prati.backend.exception.RecursoNaoEncontradoException;
import com.prati.backend.repository.ClienteOficinaRepository;
import com.prati.backend.repository.ClienteRepository;
import com.prati.backend.repository.OficinaRepository;
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
class ClienteOficinaServiceTest {

    @Mock
    private ClienteOficinaRepository clienteOficinaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private OficinaRepository oficinaRepository;

    @InjectMocks
    private ClienteOficinaService clienteOficinaService;

    @Test
    void deveVincularClienteAOficinaQuandoVinculoNaoExiste() {
        // Arrange: preparar os dados do teste
        Long clienteId = 1L;
        Long oficinaId = 2L;

        Cliente cliente = new Cliente();
        Oficina oficina = new Oficina();

        when(clienteRepository.findById(clienteId))
                .thenReturn(Optional.of(cliente));

        when(oficinaRepository.findById(oficinaId))
                .thenReturn(Optional.of(oficina));

        when(clienteOficinaRepository
                .existsByClienteIdAndOficinaId(clienteId, oficinaId))
                .thenReturn(false);

        when(clienteOficinaRepository.save(any(ClienteOficina.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act: executar o método que queremos testar
        ClienteOficina resultado = clienteOficinaService
                .vincularClienteOficina(clienteId, oficinaId);

        // Assert: verificar o resultado
        assertNotNull(resultado);
        assertSame(cliente, resultado.getCliente());
        assertSame(oficina, resultado.getOficina());

        verify(clienteOficinaRepository).save(any(ClienteOficina.class));
    }


    @Test
    void deveLancarExcecaoQuandoVinculoJaExiste() {
        // Arrange: preparar os dados
        Long clienteId = 1L;
        Long oficinaId = 2L;

        Cliente cliente = new Cliente();
        Oficina oficina = new Oficina();

        when(clienteRepository.findById(clienteId))
                .thenReturn(Optional.of(cliente));

        when(oficinaRepository.findById(oficinaId))
                .thenReturn(Optional.of(oficina));

        when(clienteOficinaRepository
                .existsByClienteIdAndOficinaId(clienteId, oficinaId))
                .thenReturn(true);

        // Act + Assert: executar e verificar a exceção
        assertThrows(
                ConflitoCadastroException.class,
                () -> clienteOficinaService
                        .vincularClienteOficina(clienteId, oficinaId)
        );

        // Garantir que nenhum vínculo foi salvo
        verify(clienteOficinaRepository, never())
                .save(any(ClienteOficina.class));
    }


    @Test
    void deveLancarExcecaoQuandoClienteNaoExiste() {
        // Arrange
        Long clienteId = 1L;
        Long oficinaId = 2L;

        when(clienteRepository.findById(clienteId))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> clienteOficinaService
                        .vincularClienteOficina(clienteId, oficinaId)
        );

        // O serviço não deve tentar buscar a oficina
        verifyNoInteractions(oficinaRepository);

        // O serviço não deve salvar nenhum vínculo
        verify(clienteOficinaRepository, never())
                .save(any(ClienteOficina.class));
    }


    @Test
    void deveLancarExcecaoQuandoOficinaNaoExiste() {
        // Arrange
        Long clienteId = 1L;
        Long oficinaId = 2L;

        Cliente cliente = new Cliente();

        when(clienteRepository.findById(clienteId))
                .thenReturn(Optional.of(cliente));

        when(oficinaRepository.findById(oficinaId))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> clienteOficinaService
                        .vincularClienteOficina(clienteId, oficinaId)
        );

        // Não deve tentar verificar ou salvar o vínculo
        verify(clienteOficinaRepository, never())
                .existsByClienteIdAndOficinaId(clienteId, oficinaId);

        verify(clienteOficinaRepository, never())
                .save(any(ClienteOficina.class));
    }



}
