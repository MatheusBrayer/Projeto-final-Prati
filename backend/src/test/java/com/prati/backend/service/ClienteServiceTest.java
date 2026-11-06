package com.prati.backend.service;

import com.prati.backend.dto.ClienteRequest;
import com.prati.backend.entity.Cliente;
import com.prati.backend.entity.Oficina;
import com.prati.backend.entity.Usuario;
import com.prati.backend.enums.Perfil;
import com.prati.backend.exception.ConflitoCadastroException;
import com.prati.backend.exception.DadosInvalidosException;
import com.prati.backend.exception.RecursoNaoEncontradoException;
import com.prati.backend.repository.ClienteOficinaRepository;
import com.prati.backend.repository.ClienteRepository;
import com.prati.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import com.prati.backend.dto.ClienteConsultaResponse;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteOficinaRepository clienteOficinaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ClienteService clienteService;

    private static final String EMAIL_ADMIN = "admin@oficina.com";

    private void autenticarAdministrador() {
        Oficina oficina = new Oficina();
        oficina.setId(1L);

        Usuario administrador = new Usuario();
        administrador.setEmail(EMAIL_ADMIN);
        administrador.setPerfil(Perfil.ADM_OFICINA);
        administrador.setAtivo(true);
        administrador.setOficina(oficina);

        when(usuarioRepository.findByEmail(EMAIL_ADMIN))
                .thenReturn(Optional.of(administrador));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        EMAIL_ADMIN,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADM_OFICINA"))
                )
        );
    }

    @AfterEach
    void limparContextoSeguranca() {
        SecurityContextHolder.clearContext();
    }

    private ClienteRequest criarRequest() {
        ClienteRequest request = new ClienteRequest();
        request.setNome("João da Silva");
        request.setCpf("52998224725");
        request.setTelefone("51999999999");
        request.setEmail("joao@email.com");
        request.setEndereco("Rua das Flores");

        return request;
    }

    @Test
    void deveCadastrarClienteQuandoCpfNaoExiste() {
        autenticarAdministrador();

        ClienteRequest request = criarRequest();

        when(clienteRepository.findByCpf(request.getCpf()))
                .thenReturn(Optional.empty());

        when(clienteRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(clienteRepository.save(any(Cliente.class)))
                .thenAnswer(invocacao -> {
                    Cliente cliente = invocacao.getArgument(0);
                    cliente.setId(1L);
                    return cliente;
                });

        when(clienteOficinaRepository
                .existsByClienteIdAndOficinaId(1L, 1L))
                .thenReturn(false);

        Cliente resultado = clienteService.cadastrarCliente(request);

        assertNotNull(resultado);
        assertEquals("João da Silva", resultado.getNome());
        assertEquals("52998224725", resultado.getCpf());

        verify(clienteRepository).save(any(Cliente.class));
        verify(clienteOficinaRepository).save(any());
    }

    @Test
    void deveAtualizarClienteQuandoCpfJaExiste() {
        autenticarAdministrador();

        ClienteRequest request = criarRequest();

        Cliente clienteExistente = new Cliente();
        clienteExistente.setId(1L);
        clienteExistente.setNome("João antigo");
        clienteExistente.setCpf(request.getCpf());
        clienteExistente.setEmail(request.getEmail());

        when(clienteRepository.findByCpf(request.getCpf()))
                .thenReturn(Optional.of(clienteExistente));

        when(clienteRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(clienteExistente));

        when(clienteRepository.save(any(Cliente.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        when(clienteOficinaRepository
                .existsByClienteIdAndOficinaId(1L, 1L))
                .thenReturn(true);

        Cliente resultado = clienteService.cadastrarCliente(request);

        assertEquals("João da Silva", resultado.getNome());
        assertEquals("51999999999", resultado.getTelefone());

        verify(clienteRepository).save(clienteExistente);
        verify(clienteOficinaRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoEmailPertencerAOutroCliente() {
        autenticarAdministrador();

        ClienteRequest request = criarRequest();

        Cliente clienteExistente = new Cliente();
        clienteExistente.setId(1L);
        clienteExistente.setCpf(request.getCpf());

        Cliente outroCliente = new Cliente();
        outroCliente.setId(2L);
        outroCliente.setEmail(request.getEmail());

        when(clienteRepository.findByCpf(request.getCpf()))
                .thenReturn(Optional.of(clienteExistente));

        when(clienteRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(outroCliente));

        assertThrows(
                ConflitoCadastroException.class,
                () -> clienteService.cadastrarCliente(request)
        );

        verify(clienteRepository, never()).save(any());
        verifyNoInteractions(clienteOficinaRepository);
    }

    @Test
    void deveSalvarTodosOsDadosRecebidosNoRequest() {
        autenticarAdministrador();

        ClienteRequest request = criarRequest();

        when(clienteRepository.findByCpf(request.getCpf()))
                .thenReturn(Optional.empty());

        when(clienteRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(clienteRepository.save(any(Cliente.class)))
                .thenAnswer(invocacao -> {
                    Cliente cliente = invocacao.getArgument(0);
                    cliente.setId(1L);
                    return cliente;
                });

        when(clienteOficinaRepository
                .existsByClienteIdAndOficinaId(1L, 1L))
                .thenReturn(false);

        Cliente resultado = clienteService.cadastrarCliente(request);

        assertAll(
                () -> assertEquals("João da Silva", resultado.getNome()),
                () -> assertEquals("52998224725", resultado.getCpf()),
                () -> assertEquals("51999999999", resultado.getTelefone()),
                () -> assertEquals("joao@email.com", resultado.getEmail()),
                () -> assertEquals("Rua das Flores", resultado.getEndereco())
        );
    }

    @Test
    void deveLancarExcecaoQuandoCpfForInvalido() {
        ClienteRequest request = criarRequest();
        request.setCpf("12345678900");

        assertThrows(
                DadosInvalidosException.class,
                () -> clienteService.cadastrarCliente(request)
        );

        verifyNoInteractions(
                clienteRepository,
                clienteOficinaRepository,
                usuarioRepository
        );
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
                () -> clienteService.buscarClientePorEmail(
                        "inexistente@email.com"
                )
        );
    }

    @Test
    void deveBuscarClientePeloCpf() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João da Silva");
        cliente.setCpf("52998224725");
        cliente.setTelefone("51999999999");
        cliente.setEmail("joao@email.com");
        cliente.setEndereco("Rua das Flores");

        when(clienteRepository.findByCpf("52998224725"))
                .thenReturn(Optional.of(cliente));

        ClienteConsultaResponse resultado =
                clienteService.buscarPorCpf("52998224725");

        assertAll(
                () -> assertEquals(1L, resultado.id()),
                () -> assertEquals("João da Silva", resultado.nome()),
                () -> assertEquals("52998224725", resultado.cpf()),
                () -> assertEquals("51999999999", resultado.telefone()),
                () -> assertEquals("joao@email.com", resultado.email()),
                () -> assertEquals("Rua das Flores", resultado.endereco())
        );

        verify(clienteRepository).findByCpf("52998224725");
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoForEncontradoPeloCpf() {
        when(clienteRepository.findByCpf("52998224725"))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> clienteService.buscarPorCpf("52998224725")
        );

        verify(clienteRepository).findByCpf("52998224725");
    }

    @Test
    void deveLancarExcecaoQuandoCpfForInvalidoNaBusca() {
        assertThrows(
                DadosInvalidosException.class,
                () -> clienteService.buscarPorCpf("12345678900")
        );

        verifyNoInteractions(clienteRepository);
    }
}