
package com.prati.backend.security;

import com.prati.backend.entity.Usuario;
import com.prati.backend.enums.Perfil;
import com.prati.backend.repository.UsuarioRepository;
import com.prati.backend.service.ClienteService;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mail.javamail.JavaMailSender;
import static org.mockito.ArgumentMatchers.any;
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

    @MockitoBean
    private ClienteService clienteService;

    @MockitoBean
    private JavaMailSender mailSender;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRetornar401QuandoRequisicaoProtegidaNaoPossuiToken() throws Exception {
        mockMvc.perform(post("/api/clientes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void devePermitirRequisicaoQuandoTokenPertenceAUsuarioAtivo() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@autogestao.com");
        usuario.setSenha("senha-de-teste");
        usuario.setPerfil(Perfil.CLIENTE);
        usuario.setAtivo(true);

        usuarioRepository.save(usuario);

        String token = jwtService.gerarToken(usuario.getEmail());

        when(clienteService.cadastrarCliente(any())).thenAnswer(invocation -> {
            var request = invocation.getArgument(
                    0, com.prati.backend.dto.ClienteRequest.class);

            var cliente = new com.prati.backend.entity.Cliente();
            cliente.setNome(request.getNome());
            cliente.setCpf(request.getCpf());
            cliente.setTelefone(request.getTelefone());
            cliente.setEmail(request.getEmail());
            cliente.setEndereco(request.getEndereco());
            return cliente;
        });

        mockMvc.perform(post("/api/clientes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content("""
                            {
                              "nome": "Cliente Teste",
                              "cpf": "12345678909",
                              "telefone": "51999999999",
                              "email": "cliente@teste.com",
                              "endereco": "Rua de Teste"
                            }
                            """))
                .andExpect(status().isCreated());
    }

    @Test
    void deveRetornar401QuandoTokenForInvalido() throws Exception {
        mockMvc.perform(post("/api/clientes")
                        .header("Authorization", "Bearer token-invalido")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content("""
                            {
                              "nome": "Cliente Teste",
                              "cpf": "12345678909",
                              "telefone": "51999999999",
                              "email": "cliente@teste.com",
                              "endereco": "Rua de Teste"
                            }
                            """))
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
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content("""
                            {
                              "nome": "Cliente Teste",
                              "cpf": "12345678909",
                              "telefone": "51999999999",
                              "email": "cliente@teste.com",
                              "endereco": "Rua de Teste"
                            }
                            """))
                .andExpect(status().isUnauthorized());
    }
}
