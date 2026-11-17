
package com.prati.backend.service;

import com.prati.backend.entity.*;
import com.prati.backend.enums.Perfil;
import com.prati.backend.exception.RecursoNaoEncontradoException;
import com.prati.backend.repository.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OficinaExclusaoIntegrationTest {

    @Autowired
    private OficinaService oficinaService;

    @Autowired
    private OficinaRepository oficinaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClienteOficinaRepository clienteOficinaRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @MockitoBean
    private JavaMailSender mailSender;

    private Oficina criarOficina(String nome, String cnpj, String slug) {
        Oficina oficina = new Oficina();
        oficina.setNome(nome);
        oficina.setCnpj(cnpj);
        oficina.setSlug(slug);
        oficina.setTelefone("51999999999");
        oficina.setEmail(slug + "@teste.com");
        oficina.setAtivo(true);
        return oficinaRepository.saveAndFlush(oficina);
    }

    private Cliente criarCliente(String cpf, String email) {
        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Teste");
        cliente.setCpf(cpf);
        cliente.setTelefone("51988888888");
        cliente.setEmail(email);
        return clienteRepository.saveAndFlush(cliente);
    }

    private ClienteOficina vincularCliente(
            Cliente cliente, Oficina oficina) {

        ClienteOficina vinculo = new ClienteOficina();
        vinculo.setCliente(cliente);
        vinculo.setOficina(oficina);
        return clienteOficinaRepository.saveAndFlush(vinculo);
    }

    private Usuario criarAdministrador(Oficina oficina, String email) {
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenha("senha-teste");
        usuario.setPerfil(Perfil.ADM_OFICINA);
        usuario.setAtivo(true);
        usuario.setOficina(oficina);
        return usuarioRepository.saveAndFlush(usuario);
    }

    @Test
    void deveExcluirOficinaSemDependencias() {
        Oficina oficina = criarOficina(
                "Oficina Simples", "98765432000100", "oficina-simples");

        Long id = oficina.getId();

        oficinaService.excluirOficina(id);
        oficinaRepository.flush();

        assertFalse(oficinaRepository.existsById(id));
    }

    @Test
    void deveRetornarErroQuandoOficinaNaoExistir() {
        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> oficinaService.excluirOficina(-1L)
        );
    }

    @Test
    void deveExcluirAdministradorVinculado() {
        Oficina oficina = criarOficina(
                "Oficina Admin", "98765432000101", "oficina-admin");

        Usuario admin = criarAdministrador(
                oficina, "admin.exclusao@teste.com");

        Long oficinaId = oficina.getId();
        Long adminId = admin.getId();

        oficinaService.excluirOficina(oficinaId);
        oficinaRepository.flush();

        assertFalse(oficinaRepository.existsById(oficinaId));
        assertFalse(usuarioRepository.existsById(adminId));
    }

    @Test
    void devePreservarClienteGlobalEOutrasOficinas() {
        Oficina oficinaA = criarOficina(
                "Oficina A", "98765432000102", "oficina-a");

        Oficina oficinaB = criarOficina(
                "Oficina B", "98765432000103", "oficina-b");

        Cliente cliente = criarCliente(
                "12345678901", "cliente.global@teste.com");

        ClienteOficina vinculoA = vincularCliente(cliente, oficinaA);
        ClienteOficina vinculoB = vincularCliente(cliente, oficinaB);

        Long oficinaAId = oficinaA.getId();
        Long oficinaBId = oficinaB.getId();
        Long clienteId = cliente.getId();
        Long vinculoAId = vinculoA.getId();
        Long vinculoBId = vinculoB.getId();

        oficinaService.excluirOficina(oficinaAId);
        oficinaRepository.flush();

        assertFalse(oficinaRepository.existsById(oficinaAId));
        assertFalse(clienteOficinaRepository.existsById(vinculoAId));

        assertTrue(oficinaRepository.existsById(oficinaBId));
        assertTrue(clienteRepository.existsById(clienteId));
        assertTrue(clienteOficinaRepository.existsById(vinculoBId));
    }

    @Test
    void deveExcluirVeiculosEOrdensSemAfetarOutraOficina() {
        Oficina oficinaA = criarOficina(
                "Oficina Completa A",
                "98765432000104",
                "oficina-completa-a");

        Oficina oficinaB = criarOficina(
                "Oficina Completa B",
                "98765432000105",
                "oficina-completa-b");

        Cliente cliente = criarCliente(
                "12345678902", "cliente.completo@teste.com");

        vincularCliente(cliente, oficinaA);
        vincularCliente(cliente, oficinaB);

        Usuario adminA = criarAdministrador(
                oficinaA, "admin.completo.a@teste.com");

        Usuario adminB = criarAdministrador(
                oficinaB, "admin.completo.b@teste.com");

        Veiculo veiculoA = new Veiculo();
        veiculoA.setCliente(cliente);
        veiculoA.setOficina(oficinaA);
        veiculoA.setPlaca("ABC1D23");
        veiculoA.setMarca("Fiat");
        veiculoA.setModelo("Uno");
        veiculoA.setAno(2020);
        veiculoA = veiculoRepository.saveAndFlush(veiculoA);

        Veiculo veiculoB = new Veiculo();
        veiculoB.setCliente(cliente);
        veiculoB.setOficina(oficinaB);
        veiculoB.setPlaca("XYZ9A87");
        veiculoB.setMarca("Ford");
        veiculoB.setModelo("Ka");
        veiculoB.setAno(2022);
        veiculoB = veiculoRepository.saveAndFlush(veiculoB);

        OrdemServico ordemA = new OrdemServico();
        ordemA.setOficina(oficinaA);
        ordemA.setCliente(cliente);
        ordemA.setVeiculo(veiculoA);
        ordemA.setDescricao("Troca de oleo");
        ordemA.setStatus(
                com.prati.backend.enums.StatusOrdemServico.ABERTA);
        ordemA.setDataAbertura(java.time.LocalDateTime.now());
        ordemA = ordemServicoRepository.saveAndFlush(ordemA);

        OrdemServico ordemB = new OrdemServico();
        ordemB.setOficina(oficinaB);
        ordemB.setCliente(cliente);
        ordemB.setVeiculo(veiculoB);
        ordemB.setDescricao("Revisao");
        ordemB.setStatus(
                com.prati.backend.enums.StatusOrdemServico.ABERTA);
        ordemB.setDataAbertura(java.time.LocalDateTime.now());
        ordemB = ordemServicoRepository.saveAndFlush(ordemB);

        Long oficinaAId = oficinaA.getId();
        Long oficinaBId = oficinaB.getId();
        Long clienteId = cliente.getId();
        Long adminAId = adminA.getId();
        Long adminBId = adminB.getId();
        Long veiculoAId = veiculoA.getId();
        Long veiculoBId = veiculoB.getId();
        Long ordemAId = ordemA.getId();
        Long ordemBId = ordemB.getId();

        oficinaService.excluirOficina(oficinaAId);

        ordemServicoRepository.flush();
        veiculoRepository.flush();
        oficinaRepository.flush();

        // Dados da oficina excluida
        assertFalse(oficinaRepository.existsById(oficinaAId));
        assertFalse(usuarioRepository.existsById(adminAId));
        assertFalse(veiculoRepository.existsById(veiculoAId));
        assertFalse(ordemServicoRepository.existsById(ordemAId));

        // Dados que devem permanecer
        assertTrue(clienteRepository.existsById(clienteId));
        assertTrue(oficinaRepository.existsById(oficinaBId));
        assertTrue(usuarioRepository.existsById(adminBId));
        assertTrue(veiculoRepository.existsById(veiculoBId));
        assertTrue(ordemServicoRepository.existsById(ordemBId));
    }
}
