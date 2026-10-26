
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

@Configuration
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter)
            throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED)))

                .authorizeHttpRequests(auth -> auth
                        // Login público.
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                        // Cadastro de clientes exclusivo para administradores de oficina.
                        .requestMatchers(HttpMethod.POST, "/api/clientes")
                        .hasRole("ADM_OFICINA")

                        /*
                         * TODO(Card 7): Ao adicionar novos endpoints, definir regras
                         * específicas por perfil antes da regra geral.
                         *
                         * ADM_SISTEMA: recursos administrativos do sistema.
                         * ADM_OFICINA: recursos limitados à própria oficina.
                         * CLIENTE: recursos limitados aos próprios dados.
                         *
                         * A autorização por perfil não substitui a validação de
                         * propriedade dos dados no serviço.
                         */

                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

}
