
package com.prati.backend.controller;

import com.prati.backend.entity.Oficina;
import com.prati.backend.entity.Usuario;
import com.prati.backend.enums.Perfil;
import com.prati.backend.repository.ClienteOficinaRepository;
import com.prati.backend.repository.ClienteRepository;
import com.prati.backend.repository.OficinaRepository;
import com.prati.backend.repository.UsuarioRepository;
import com.prati.backend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:autogestao_test;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "jwt.secret=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=",
        "jwt.expiration=3600000"
})
@AutoConfigureMockMvc
class ClienteControllerIntegrationTest {

    private static final String EMAIL_ADMIN = "admin@oficina.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private OficinaRepository oficinaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClienteOficinaRepository clienteOficinaRepository;

    @MockitoBean
    private JavaMailSender mailSender;

    private String tokenAdministrador;

    @BeforeEach
    void prepararBancoDeDados() {

        clienteOficinaRepository.deleteAll();
        usuarioRepository.deleteAll();
        clienteRepository.deleteAll();
        oficinaRepository.deleteAll();

        Oficina oficina = new Oficina();
        oficina.setNome("Oficina Teste");
        oficina.setCnpj("12345678000199");
        oficina.setTelefone("51999999999");
        oficina.setEmail("oficina@email.com");
        oficina.setEndereco("Rua de Teste");

        oficina = oficinaRepository.save(oficina);

        Usuario administrador = new Usuario();
        administrador.setEmail(EMAIL_ADMIN);
        administrador.setSenha("senha-teste");
        administrador.setPerfil(Perfil.ADM_OFICINA);
        administrador.setAtivo(true);
        administrador.setOficina(oficina);

        usuarioRepository.save(administrador);

        tokenAdministrador = jwtService.gerarToken(administrador.getEmail());
    }

    private String criarTokenParaPerfil(Perfil perfil) {

        Usuario usuario = new Usuario();
        usuario.setEmail(perfil.name().toLowerCase() + "@teste.com");
        usuario.setSenha("senha-teste");
        usuario.setPerfil(perfil);
        usuario.setAtivo(true);

        usuarioRepository.save(usuario);

        return jwtService.gerarToken(usuario.getEmail());
    }

    @Test
    void deveCadastrarClienteComSucesso() throws Exception {

        String json = """
                {
                    "nome": "Maria da Silva",
                    "cpf": "52998224725",
                    "telefone": "51988888888",
                    "email": "maria@email.com",
                    "endereco": "Rua das Flores, 123"
                }
                """;

        mockMvc.perform(post("/api/clientes")
                        .header("Authorization", "Bearer " + tokenAdministrador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Maria da Silva"))
                .andExpect(jsonPath("$.cpf").value("52998224725"));
    }

    @Test
    void deveRetornar400QuandoCpfForInvalido() throws Exception {

        String json = """
                {
                    "nome": "Maria da Silva",
                    "cpf": "12345678900",
                    "telefone": "51988888888",
                    "email": "maria@email.com",
                    "endereco": "Rua das Flores, 123"
                }
                """;

        mockMvc.perform(post("/api/clientes")
                        .header("Authorization", "Bearer " + tokenAdministrador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro")
                        .value("O CPF informado é inválido."));
    }

    @Test
    void devePreservarDadosDoClienteQuandoCpfJaEstiverCadastrado() throws Exception {

        String primeiroCadastro = """
            {
                "nome": "Maria da Silva",
                "cpf": "52998224725",
                "telefone": "51988888888",
                "email": "maria@email.com",
                "endereco": "Rua das Flores, 123"
            }
            """;

        String segundoCadastro = """
            {
                "nome": "Maria Souza",
                "cpf": "52998224725",
                "telefone": "51999999999",
                "email": "maria.souza@email.com",
                "endereco": "Rua das Palmeiras, 456"
            }
            """;

        mockMvc.perform(post("/api/clientes")
                        .header("Authorization", "Bearer " + tokenAdministrador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(primeiroCadastro))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/clientes")
                        .header("Authorization", "Bearer " + tokenAdministrador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(segundoCadastro))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Maria da Silva"))
                .andExpect(jsonPath("$.email").value("maria@email.com"));
    }

    @Test
    void deveRetornar400QuandoEmailNaoForInformado() throws Exception {

        String json = """
                {
                    "nome": "Maria da Silva",
                    "cpf": "52998224725",
                    "telefone": "51988888888",
                    "endereco": "Rua das Flores, 123"
                }
                """;

        mockMvc.perform(post("/api/clientes")
                        .header("Authorization", "Bearer " + tokenAdministrador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveBuscarClientePorCpfComSucesso() throws Exception {

        String json = """
                {
                    "nome": "Maria da Silva",
                    "cpf": "52998224725",
                    "telefone": "51988888888",
                    "email": "maria@email.com",
                    "endereco": "Rua das Flores, 123"
                }
                """;

        mockMvc.perform(post("/api/clientes")
                        .header("Authorization", "Bearer " + tokenAdministrador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/clientes/buscar-por-cpf/{cpf}", "52998224725")
                        .header("Authorization", "Bearer " + tokenAdministrador))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria da Silva"))
                .andExpect(jsonPath("$.cpf").value("52998224725"))
                .andExpect(jsonPath("$.email").value("maria@email.com"));
    }

    @Test
    void deveRetornar404QuandoClienteNaoForEncontradoPeloCpf() throws Exception {

        mockMvc.perform(get("/api/clientes/buscar-por-cpf/{cpf}", "52998224725")
                        .header("Authorization", "Bearer " + tokenAdministrador))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar400QuandoCpfForInvalidoNaBusca() throws Exception {

        mockMvc.perform(get("/api/clientes/buscar-por-cpf/{cpf}", "12345678900")
                        .header("Authorization", "Bearer " + tokenAdministrador))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro")
                        .value("O CPF informado é inválido."));
    }

    @Test
    void deveRetornar403QuandoClienteTentarBuscarPorCpf() throws Exception {

        String tokenCliente = criarTokenParaPerfil(Perfil.CLIENTE);

        mockMvc.perform(get("/api/clientes/buscar-por-cpf/{cpf}", "52998224725")
                        .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornar403QuandoAdministradorSistemaTentarBuscarPorCpf() throws Exception {

        String tokenSistema = criarTokenParaPerfil(Perfil.ADM_SISTEMA);

        mockMvc.perform(get("/api/clientes/buscar-por-cpf/{cpf}", "52998224725")
                        .header("Authorization", "Bearer " + tokenSistema))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornar401QuandoBuscarPorCpfSemAutenticacao() throws Exception {

        mockMvc.perform(get("/api/clientes/buscar-por-cpf/{cpf}", "52998224725"))
                .andExpect(status().isUnauthorized());
    }
}
