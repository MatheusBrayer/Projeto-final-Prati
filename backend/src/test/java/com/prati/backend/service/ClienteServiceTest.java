
package com.prati.backend.service;

import com.prati.backend.dto.ClienteConsultaResponse;
import com.prati.backend.dto.ClienteRequest;
import com.prati.backend.entity.Cliente;
import com.prati.backend.entity.ClienteOficina;
import com.prati.backend.entity.Oficina;
import com.prati.backend.entity.Usuario;
import com.prati.backend.enums.Perfil;
import com.prati.backend.exception.ConflitoCadastroException;
import com.prati.backend.exception.DadosInvalidosException;
import com.prati.backend.exception.RecursoNaoEncontradoException;
import com.prati.backend.repository.ClienteOficinaRepository;
import com.prati.backend.repository.ClienteRepository;
import com.prati.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

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
    private ClienteOficinaRepository clienteOficinaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ClienteService clienteService;

    private void prepararAdministrador() {

        Oficina oficina = new Oficina();
        oficina.setId(1L);

        Usuario administrador = new Usuario();
        administrador.setEmail(EMAIL_ADMIN);
        administrador.setPerfil(Perfil.ADM_OFICINA);
        administrador.setAtivo(true);
        administrador.setOficina(oficina);

        when(usuarioRepository.findByEmail(EMAIL_ADMIN))
                .thenReturn(Optional.of(administrador));
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

        prepararAdministrador();

        ClienteRequest request = criarRequest();

        when(clienteRepository.findByCpf(request.getCpf()))
                .thenReturn(Optional.empty());

        when(clienteRepository.save(any(Cliente.class)))
                .thenAnswer(invocacao -> {
                    Cliente cliente = invocacao.getArgument(0);
                    cliente.setId(1L);
                    return cliente;
                });

        Cliente resultado = clienteService.cadastrarCliente(
                request, EMAIL_ADMIN
        );

        assertNotNull(resultado);
        assertEquals("João da Silva", resultado.getNome());
        assertEquals("52998224725", resultado.getCpf());

        verify(clienteRepository).save(any(Cliente.class));
        verify(clienteOficinaRepository).save(any(ClienteOficina.class));
    }


    @Test
    void deveManterDadosGlobaisQuandoCpfJaExiste() {

        prepararAdministrador();

        ClienteRequest request = criarRequest();

        Cliente clienteExistente = new Cliente();
        clienteExistente.setId(1L);
        clienteExistente.setNome("João antigo");
        clienteExistente.setCpf(request.getCpf());
        clienteExistente.setEmail(request.getEmail());
        clienteExistente.setTelefone("51888888888");

        when(clienteRepository.findByCpf(request.getCpf()))
                .thenReturn(Optional.of(clienteExistente));

        when(clienteRepository.save(any(Cliente.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        when(clienteOficinaRepository
                .existsByClienteIdAndOficinaId(1L, 1L))
                .thenReturn(true);

        Cliente resultado = clienteService.cadastrarCliente(
                request, EMAIL_ADMIN
        );

        assertAll(
                () -> assertEquals(1L, resultado.getId()),
                () -> assertEquals("João antigo", resultado.getNome()),
                () -> assertEquals("51888888888", resultado.getTelefone())
        );

        verify(clienteRepository).save(clienteExistente);
        verify(clienteOficinaRepository, never()).save(any());
    }


    @Test
    void deveLancarExcecaoQuandoEmailPertencerAOutroCliente() {

        prepararAdministrador();

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
                () -> clienteService.cadastrarCliente(request, EMAIL_ADMIN)
        );

        verify(clienteRepository, never()).save(any());
        verifyNoInteractions(clienteOficinaRepository);
    }

    @Test
    void deveSalvarTodosOsDadosRecebidosNoRequest() {

        prepararAdministrador();

        ClienteRequest request = criarRequest();

        when(clienteRepository.findByCpf(request.getCpf()))
                .thenReturn(Optional.empty());

        when(clienteRepository.save(any(Cliente.class)))
                .thenAnswer(invocacao -> {
                    Cliente cliente = invocacao.getArgument(0);
                    cliente.setId(1L);
                    return cliente;
                });

        Cliente resultado = clienteService.cadastrarCliente(
                request, EMAIL_ADMIN
        );

        assertAll(
                () -> assertEquals("João da Silva", resultado.getNome()),
                () -> assertEquals("52998224725", resultado.getCpf()),
                () -> assertEquals("51999999999", resultado.getTelefone()),
                () -> assertEquals("joao@email.com", resultado.getEmail()),
                () -> assertEquals("Rua das Flores", resultado.getEndereco())
        );

        verify(clienteRepository).save(any(Cliente.class));
        verify(clienteOficinaRepository).save(any(ClienteOficina.class));
    }

    @Test
    void deveLancarExcecaoQuandoCpfForInvalido() {

        ClienteRequest request = criarRequest();
        request.setCpf("12345678900");

        assertThrows(
                DadosInvalidosException.class,
                () -> clienteService.cadastrarCliente(request, EMAIL_ADMIN)
        );

        verifyNoInteractions(
                clienteRepository,
                clienteOficinaRepository,
                usuarioRepository
        );
    }

    @Test
    void deveImpedirCadastroQuandoUsuarioNaoForAdministradorDaOficina() {

        Usuario usuario = new Usuario();
        usuario.setEmail(EMAIL_ADMIN);
        usuario.setPerfil(Perfil.CLIENTE);
        usuario.setAtivo(true);

        when(usuarioRepository.findByEmail(EMAIL_ADMIN))
                .thenReturn(Optional.of(usuario));

        assertThrows(
                AccessDeniedException.class,
                () -> clienteService.cadastrarCliente(
                        criarRequest(), EMAIL_ADMIN
                )
        );

        verifyNoInteractions(clienteRepository, clienteOficinaRepository);
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

        String emailUsuario = "admin@oficina.com";

        Oficina oficina = new Oficina();
        oficina.setId(1L);

        Usuario usuario = new Usuario();
        usuario.setPerfil(Perfil.ADM_OFICINA);
        usuario.setOficina(oficina);

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João da Silva");
        cliente.setCpf("52998224725");
        cliente.setTelefone("51999999999");
        cliente.setEmail("joao@email.com");
        cliente.setEndereco("Rua das Flores");

        when(usuarioRepository.findByEmail(emailUsuario))
                .thenReturn(Optional.of(usuario));

        when(clienteRepository.findByCpf("52998224725"))
                .thenReturn(Optional.of(cliente));

        when(clienteOficinaRepository.existsByClienteIdAndOficinaId(1L, 1L))
                .thenReturn(true);

        ClienteConsultaResponse resultado =
                clienteService.buscarPorCpf("52998224725", emailUsuario);

        assertAll(
                () -> assertEquals(1L, resultado.id()),
                () -> assertEquals("João da Silva", resultado.nome()),
                () -> assertEquals("52998224725", resultado.cpf()),
                () -> assertEquals("51999999999", resultado.telefone()),
                () -> assertEquals("joao@email.com", resultado.email()),
                () -> assertEquals("Rua das Flores", resultado.endereco())
        );

        verify(clienteRepository).findByCpf("52998224725");
        verify(clienteOficinaRepository)
                .existsByClienteIdAndOficinaId(1L, 1L);
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoForEncontradoPeloCpf() {

        String emailUsuario = "admin@oficina.com";

        Oficina oficina = new Oficina();
        oficina.setId(1L);

        Usuario usuario = new Usuario();
        usuario.setPerfil(Perfil.ADM_OFICINA);
        usuario.setOficina(oficina);

        when(usuarioRepository.findByEmail(emailUsuario))
                .thenReturn(Optional.of(usuario));

        when(clienteRepository.findByCpf("52998224725"))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> clienteService.buscarPorCpf(
                        "52998224725", emailUsuario)
        );

        verify(clienteRepository).findByCpf("52998224725");
    }

    @Test
    void deveLancarExcecaoQuandoCpfForInvalidoNaBusca() {

        assertThrows(
                DadosInvalidosException.class,
                () -> clienteService.buscarPorCpf(
                        "12345678900", "admin@oficina.com")
        );

        verifyNoInteractions(clienteRepository);
    }

}
