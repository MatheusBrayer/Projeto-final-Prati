
package com.prati.backend.config;

import com.prati.backend.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import jakarta.servlet.DispatcherType;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter)
            throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED)))

                .authorizeHttpRequests(auth -> auth

                        // Endpoints públicos de autenticação.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/login",
                                "/api/auth/primeiro-acesso/solicitar",
                                "/api/auth/primeiro-acesso/confirmar"
                        ).permitAll()

                        // Somente o administrador do sistema pode cadastrar oficinas.
                        .requestMatchers(HttpMethod.POST, "/api/oficinas")
                        .hasRole("ADM_SISTEMA")

                        // Somente administradores de oficina podem cadastrar clientes.
                        .requestMatchers(HttpMethod.POST, "/api/clientes")
                        .hasRole("ADM_OFICINA")

                        // Somente administradores de oficina podem consultar clientes por CPF.
                        .requestMatchers(HttpMethod.GET, "/api/clientes/buscar-por-cpf/**")
                        .hasRole("ADM_OFICINA")

                        // Permite o tratamento de erros pelo Spring.
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()

                        /*
                         * Novos endpoints devem receber regras específicas por perfil.
                         *
                         * ADM_SISTEMA: recursos administrativos do sistema.
                         * ADM_OFICINA: recursos limitados à própria oficina.
                         * CLIENTE: recursos limitados aos próprios dados.
                         *
                         * A autorização por perfil não substitui a validação
                         * de propriedade dos dados na camada de serviço.
                         */

                        // Demais endpoints exigem autenticação.
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        // Origem permitida: frontend React com Vite.
        configuration.setAllowedOrigins(
                List.of("http://localhost:5174","http://localhost:5173")
        );

        // Métodos HTTP permitidos.
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );

        // Cabeçalhos permitidos.
        configuration.setAllowedHeaders(
                List.of("Authorization", "Content-Type")
        );

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}
