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
import org.springframework.test.web.servlet.MockMvc;

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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private OficinaRepository oficinaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClienteOficinaRepository clienteOficinaRepository;

    private String token;

    @BeforeEach
    void prepararAdministradorDaOficina() {
        usuarioRepository.deleteAll();
        clienteOficinaRepository.deleteAll();
        clienteRepository.deleteAll();
        oficinaRepository.deleteAll();

        Oficina oficina = new Oficina();
        oficina.setNome("Oficina Teste");
        oficina.setCnpj("12345678000195");
        oficina.setTelefone("51988888888");
        oficina.setEmail("oficina@teste.com");

        oficina = oficinaRepository.save(oficina);

        Usuario administrador = new Usuario();
        administrador.setEmail("admin@oficina.com");
        administrador.setSenha("senha-de-teste");
        administrador.setPerfil(Perfil.ADM_OFICINA);
        administrador.setAtivo(true);
        administrador.setOficina(oficina);

        usuarioRepository.save(administrador);

        token = jwtService.gerarToken(administrador.getEmail());
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
                        .header("Authorization", "Bearer " + token)
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
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro")
                        .value("O CPF informado é inválido."));
    }

    @Test
    void deveRetornar409QuandoCpfJaEstiverCadastrado() throws Exception {
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
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(primeiroCadastro))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/clientes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(segundoCadastro))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro")
                        .value("Já existe um cliente cadastrado com este CPF."));
    }
}