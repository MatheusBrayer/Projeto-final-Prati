package com.prati.backend.config;

import com.prati.backend.entity.Usuario;
import com.prati.backend.enums.Perfil;
import com.prati.backend.repository.UsuarioRepository;
import com.prati.backend.service.SenhaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminInitializer {

    @Bean
    @ConditionalOnProperty(
            name = "app.admin.bootstrap.enabled",
            havingValue = "true"
    )
    CommandLineRunner inicializarAdmin(
            UsuarioRepository usuarioRepository,
            SenhaService senhaService
    ) {
        return args -> {
            String email = System.getProperty("ADMIN_EMAIL");
            String senha = System.getProperty("ADMIN_PASSWORD");

            if (email == null || email.isBlank()
                    || senha == null || senha.isBlank()) {
                throw new IllegalStateException(
                        "ADMIN_EMAIL e ADMIN_PASSWORD precisam estar configurados."
                );
            }

            if (usuarioRepository.findByEmail(email).isPresent()) {
                return;
            }

            Usuario admin = new Usuario();
            admin.setEmail(email);
            admin.setSenha(senhaService.criptografar(senha));
            admin.setPerfil(Perfil.ADM_SISTEMA);
            admin.setAtivo(true);

            usuarioRepository.save(admin);

            System.out.println("Administrador inicial criado com sucesso.");
        };
    }
}