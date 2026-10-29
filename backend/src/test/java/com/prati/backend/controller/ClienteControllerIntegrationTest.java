
package com.prati.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.security.core.context.SecurityContextHolder;

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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void limparBancoDeDados() {
        jdbcTemplate.execute("DELETE FROM clientes");
    }

    @Autowired
    private MockMvc mockMvc;

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
                        .with(SecurityMockMvcRequestPostProcessors.user("teste"))
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
                        .with(SecurityMockMvcRequestPostProcessors.user("teste"))
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
                        .with(SecurityMockMvcRequestPostProcessors.user("teste"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(primeiroCadastro))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/clientes")
                        .with(SecurityMockMvcRequestPostProcessors.user("teste"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(segundoCadastro))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro")
                        .value("Já existe um cliente cadastrado com este CPF."));
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
                        .with(SecurityMockMvcRequestPostProcessors.user("teste"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}
