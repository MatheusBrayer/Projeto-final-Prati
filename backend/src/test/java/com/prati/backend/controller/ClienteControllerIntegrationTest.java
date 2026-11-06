package com.prati.backend.controller;

import com.prati.backend.entity.Oficina;
import com.prati.backend.entity.Usuario;
import com.prati.backend.enums.Perfil;
import com.prati.backend.repository.OficinaRepository;
import com.prati.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
        properties = {
                "spring.datasource.url=jdbc:h2:mem:autogestao_test;DB_CLOSE_DELAY=-1",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        }
)
@AutoConfigureMockMvc
class ClienteControllerIntegrationTest {

    private static final String EMAIL_ADMIN = "admin@oficina.com";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OficinaRepository oficinaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private JavaMailSender mailSender;

    private void autenticarAdministrador() throws Exception {
        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/api/clientes")
                        .with(user(EMAIL_ADMIN).roles("ADM_OFICINA"))
        );
    }

    @BeforeEach
    void prepararBancoDeDados() {

        jdbcTemplate.execute("DELETE FROM clientes_oficinas");
        jdbcTemplate.execute("DELETE FROM usuarios");
        jdbcTemplate.execute("DELETE FROM clientes");
        jdbcTemplate.execute("DELETE FROM oficinas");

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
    }

    private SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor administradorAutenticado() {
        return user(EMAIL_ADMIN).roles("ADM_OFICINA");
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
                        .with(administradorAutenticado())
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
                        .with(administradorAutenticado())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro")
                        .value("O CPF informado é inválido."));
    }

    @Test
    void deveAtualizarClienteQuandoCpfJaEstiverCadastrado() throws Exception {

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
                        .with(administradorAutenticado())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(primeiroCadastro))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/clientes")
                        .with(administradorAutenticado())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(segundoCadastro))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Maria Souza"))
                .andExpect(jsonPath("$.email")
                        .value("maria.souza@email.com"));
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
                        .with(administradorAutenticado())
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
                        .with(administradorAutenticado())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/clientes/buscar-por-cpf/{cpf}", "52998224725")
                        .with(administradorAutenticado()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria da Silva"))
                .andExpect(jsonPath("$.cpf").value("52998224725"))
                .andExpect(jsonPath("$.email").value("maria@email.com"));
    }

    @Test
    void deveRetornar404QuandoClienteNaoForEncontradoPeloCpf() throws Exception {

        mockMvc.perform(get("/api/clientes/buscar-por-cpf/{cpf}", "52998224725")
                        .with(administradorAutenticado()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar400QuandoCpfForInvalidoNaBusca() throws Exception {

        mockMvc.perform(get("/api/clientes/buscar-por-cpf/{cpf}", "12345678900")
                        .with(administradorAutenticado()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro")
                        .value("O CPF informado é inválido."));
    }

    @Test
    void deveRetornar403QuandoClienteTentarBuscarPorCpf() throws Exception {

        mockMvc.perform(get("/api/clientes/buscar-por-cpf/{cpf}", "52998224725")
                        .with(user("cliente@email.com").roles("CLIENTE")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornar403QuandoAdministradorSistemaTentarBuscarPorCpf() throws Exception {

        mockMvc.perform(get("/api/clientes/buscar-por-cpf/{cpf}", "52998224725")
                        .with(user("sistema@email.com").roles("ADM_SISTEMA")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornar401QuandoBuscarPorCpfSemAutenticacao() throws Exception {

        mockMvc.perform(get("/api/clientes/buscar-por-cpf/{cpf}", "52998224725"))
                .andExpect(status().isUnauthorized());
    }
}