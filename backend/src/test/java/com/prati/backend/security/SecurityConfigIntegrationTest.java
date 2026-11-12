
package com.prati.backend.security;

import com.prati.backend.dto.ClienteRequest;
import com.prati.backend.entity.Cliente;
import com.prati.backend.entity.Oficina;
import com.prati.backend.entity.Usuario;
import com.prati.backend.enums.Perfil;
import com.prati.backend.repository.OficinaRepository;
import com.prati.backend.repository.UsuarioRepository;
import com.prati.backend.service.AuthService;
import com.prati.backend.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:security_test;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "jwt.secret=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=",
        "jwt.expiration=3600000"
})
@AutoConfigureMockMvc
class SecurityConfigIntegrationTest {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private OficinaRepository oficinaRepository;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService clienteService;

    @MockitoBean
    private JavaMailSender mailSender;

    @MockitoBean
    private AuthService authService;

    private static final String CLIENTE_JSON = """
            {
                "nome": "Cliente Teste",
                "cpf": "52998224725",
                "telefone": "51999999999",
                "email": "cliente@teste.com",
                "endereco": "Rua de Teste"
            }
            """;

    private String criarToken(String email, Perfil perfil) {
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenha("senha-de-teste");
        usuario.setPerfil(perfil);
        usuario.setAtivo(true);

        usuarioRepository.save(usuario);

        return jwtService.gerarToken(usuario.getEmail());
    }

    @Test
    void deveRetornar401QuandoRequisicaoProtegidaNaoPossuiToken()
            throws Exception {

        mockMvc.perform(post("/api/clientes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar401QuandoTokenForInvalido() throws Exception {

        mockMvc.perform(post("/api/clientes")
                        .header("Authorization", "Bearer token-invalido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CLIENTE_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar401QuandoUsuarioEstiverInativo() throws Exception {

        Usuario usuario = new Usuario();
        usuario.setEmail("inativo@autogestao.com");
        usuario.setSenha("senha-de-teste");
        usuario.setPerfil(Perfil.CLIENTE);
        usuario.setAtivo(false);

        usuarioRepository.save(usuario);

        String token = jwtService.gerarToken(usuario.getEmail());

        mockMvc.perform(post("/api/clientes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CLIENTE_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void devePermitirSolicitarPrimeiroAcessoSemToken() throws Exception {

        mockMvc.perform(post("/api/auth/primeiro-acesso/solicitar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "cliente@email.com"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void devePermitirConfirmarPrimeiroAcessoSemToken() throws Exception {

        mockMvc.perform(post("/api/auth/primeiro-acesso/confirmar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "cliente@email.com",
                                    "codigo": "123456",
                                    "senha": "Senha123!"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornar403QuandoClienteTentarCadastrarOutroCliente()
            throws Exception {

        String token = criarToken(
                "cliente@autogestao.com",
                Perfil.CLIENTE
        );

        mockMvc.perform(post("/api/clientes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CLIENTE_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornar403QuandoAdministradorSistemaTentarCadastrarCliente()
            throws Exception {

        String token = criarToken(
                "admin@autogestao.com",
                Perfil.ADM_SISTEMA
        );

        mockMvc.perform(post("/api/clientes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CLIENTE_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirCadastroQuandoAdministradorDaOficinaEstiverAutenticado()
            throws Exception {

        Oficina oficina = new Oficina();
        oficina.setNome("Oficina Teste");
        oficina.setCnpj("12345678000195");
        oficina.setTelefone("51999999999");
        oficina.setEmail("oficina@teste.com");

        oficina = oficinaRepository.save(oficina);

        Usuario administrador = new Usuario();
        administrador.setEmail("admin.oficina@autogestao.com");
        administrador.setSenha("senha-de-teste");
        administrador.setPerfil(Perfil.ADM_OFICINA);
        administrador.setAtivo(true);
        administrador.setOficina(oficina);

        usuarioRepository.save(administrador);

        String token = jwtService.gerarToken(administrador.getEmail());

        when(clienteService.cadastrarCliente(
                any(ClienteRequest.class),
                anyString()
        )).thenAnswer(invocacao -> {

            ClienteRequest request = invocacao.getArgument(
                    0, ClienteRequest.class
            );

            Cliente cliente = new Cliente();
            cliente.setNome(request.getNome());
            cliente.setCpf(request.getCpf());
            cliente.setTelefone(request.getTelefone());
            cliente.setEmail(request.getEmail());
            cliente.setEndereco(request.getEndereco());

            return cliente;
        });

        mockMvc.perform(post("/api/clientes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CLIENTE_JSON))
                .andExpect(status().isCreated());
    }
}
