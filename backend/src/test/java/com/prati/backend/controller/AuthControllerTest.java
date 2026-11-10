
package com.prati.backend.controller;

import com.prati.backend.exception.AutenticacaoException;
import com.prati.backend.repository.UsuarioRepository;
import com.prati.backend.security.JwtService;
import com.prati.backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    void deveRetornar200QuandoSolicitarPrimeiroAcesso() throws Exception {

        String json = """
        {
            "email": "cliente@email.com"
        }
        """;

        mockMvc.perform(post("/api/auth/primeiro-acesso/solicitar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").exists());

        verify(authService).solicitarPrimeiroAcesso(any());
    }

    @Test
    void deveRetornar400QuandoEmailForInvalidoNaSolicitacao() throws Exception {

        String json = """
        {
            "email": "email-invalido"
        }
        """;

        mockMvc.perform(post("/api/auth/primeiro-acesso/solicitar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void deveRetornar200QuandoConfirmarPrimeiroAcesso() throws Exception {

        String json = """
                {
                    "email": "cliente@email.com",
                    "codigo": "123456",
                    "senha": "Senha123!"
                }
                """;

        mockMvc.perform(post("/api/auth/primeiro-acesso/confirmar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem")
                        .value("Primeiro acesso realizado com sucesso."));

        verify(authService).confirmarPrimeiroAcesso(any());
    }

    @Test
    void deveRetornar401QuandoCodigoForInvalido() throws Exception {

        doThrow(new AutenticacaoException(
                "Não foi possível concluir o primeiro acesso. Verifique os dados e tente novamente."
        )).when(authService).confirmarPrimeiroAcesso(any());

        String json = """
                {
                    "email": "cliente@email.com",
                    "codigo": "123456",
                    "senha": "Senha123!"
                }
                """;

        mockMvc.perform(post("/api/auth/primeiro-acesso/confirmar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    void deveRetornar400QuandoCodigoTiverFormatoInvalido() throws Exception {

        String json = """
                {
                    "email": "cliente@email.com",
                    "codigo": "123",
                    "senha": "Senha123!"
                }
                """;

        mockMvc.perform(post("/api/auth/primeiro-acesso/confirmar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }
}