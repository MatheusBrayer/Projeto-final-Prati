
package com.prati.backend.controller;

import com.prati.backend.dto.ClienteRequest;
import com.prati.backend.entity.Cliente;
import com.prati.backend.exception.DadosInvalidosException;
import com.prati.backend.repository.UsuarioRepository;
import com.prati.backend.security.JwtService;
import com.prati.backend.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClienteController.class)
class ClienteControllerHttpTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService clienteService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    void deveRetornar201QuandoClienteForCadastrado() throws Exception {
        Cliente clienteSalvo = new Cliente();
        clienteSalvo.setId(1L);
        clienteSalvo.setNome("Maria da Silva");
        clienteSalvo.setCpf("52998224725");
        clienteSalvo.setTelefone("51988888888");
        clienteSalvo.setEmail("maria@email.com");
        clienteSalvo.setEndereco("Rua das Flores, 123");

        when(clienteService.cadastrarCliente(any(ClienteRequest.class), anyString()))
                .thenReturn(clienteSalvo);

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
                        .with(request -> {
                            var authentication =
                                    new org.springframework.security.authentication
                                            .UsernamePasswordAuthenticationToken(
                                            "admin@oficina.com", null);
                            var context =
                                    org.springframework.security.core.context
                                            .SecurityContextHolder.createEmptyContext();
                            context.setAuthentication(authentication);
                            org.springframework.security.core.context
                                    .SecurityContextHolder.setContext(context);
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Maria da Silva"))
                .andExpect(jsonPath("$.cpf").value("52998224725"));
    }

    @Test
    void deveRetornar400QuandoCpfForInvalido() throws Exception {
        when(clienteService.cadastrarCliente(any(ClienteRequest.class), anyString()))
                .thenThrow(new DadosInvalidosException(
                        "O CPF informado é inválido."));

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
                        .with(request -> {
                            var authentication =
                                    new org.springframework.security.authentication
                                            .UsernamePasswordAuthenticationToken(
                                            "admin@oficina.com", null);
                            var context =
                                    org.springframework.security.core.context
                                            .SecurityContextHolder.createEmptyContext();
                            context.setAuthentication(authentication);
                            org.springframework.security.core.context
                                    .SecurityContextHolder.setContext(context);
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro")
                        .value("O CPF informado é inválido."));
    }

    @Test
    void deveRetornar400QuandoNomeForVazio() throws Exception {
        String json = """
                {
                    "nome": "",
                    "cpf": "52998224725",
                    "telefone": "51988888888",
                    "email": "maria@email.com",
                    "endereco": "Rua das Flores, 123"
                }
                """;

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400QuandoCpfTiverMenosDe11Caracteres() throws Exception {
        String json = """
                {
                    "nome": "Maria da Silva",
                    "cpf": "12345",
                    "telefone": "51988888888",
                    "email": "maria@email.com",
                    "endereco": "Rua das Flores, 123"
                }
                """;

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

}
