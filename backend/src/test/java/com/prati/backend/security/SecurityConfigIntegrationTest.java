package com.prati.backend.security;

import com.prati.backend.dto.ClienteRequest;
import com.prati.backend.entity.Cliente;
import com.prati.backend.entity.Oficina;
import com.prati.backend.entity.Usuario;
import com.prati.backend.enums.Perfil;
import com.prati.backend.repository.OficinaRepository;
import com.prati.backend.repository.UsuarioRepository;
import com.prati.backend.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
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

    @MockitoBean
    private ClienteService clienteService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRetornar401QuandoRequisicaoProtegidaNaoPossuiToken()
            throws Exception {

        mockMvc.perform(post("/api/clientes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar403QuandoClienteTentarCadastrarOutroCliente()
            throws Exception {

        Usuario usuario = new Usuario();
        usuario.setEmail("teste@autogestao.com");
        usuario.setSenha("senha-de-teste");
        usuario.setPerfil(Perfil.CLIENTE);
        usuario.setAtivo(true);

        usuarioRepository.save(usuario);

        String token = jwtService.gerarToken(usuario.getEmail());

        mockMvc.perform(post("/api/clientes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Cliente Teste",
                                  "cpf": "12345678909",
                                  "telefone": "51999999999",
                                  "email": "cliente@teste.com",
                                  "endereco": "Rua de Teste"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornar401QuandoTokenForInvalido() throws Exception {

        mockMvc.perform(post("/api/clientes")
                        .header("Authorization", "Bearer token-invalido")
                        .contentType(MediaType.APPLICATION_JSON)
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
                        .contentType(MediaType.APPLICATION_JSON)
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
    void devePermitirCadastroQuandoAdministradorDaOficinaEstiverAutenticado()
            throws Exception {

        Oficina oficina = new Oficina();
        oficina.setNome("Oficina Teste");
        oficina.setCnpj("12345678000195");
        oficina.setTelefone("51999999999");
        oficina.setEmail("oficina@teste.com");

        oficina = oficinaRepository.save(oficina);

        Usuario usuario = new Usuario();
        usuario.setEmail("admin.oficina@autogestao.com");
        usuario.setSenha("senha-de-teste");
        usuario.setPerfil(Perfil.ADM_OFICINA);
        usuario.setAtivo(true);
        usuario.setOficina(oficina);

        usuarioRepository.save(usuario);

        String token = jwtService.gerarToken(usuario.getEmail());

        when(clienteService.cadastrarCliente(any(ClienteRequest.class), anyString()))
                .thenAnswer(invocacao -> {
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
}