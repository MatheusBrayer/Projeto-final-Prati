
package com.prati.backend.security;

import com.prati.backend.entity.Usuario;
import com.prati.backend.repository.UsuarioRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.prati.backend.enums.Perfil;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        try {
            String email = jwtService.extrairEmailDoToken(token);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                Optional<Usuario> usuarioOptional =
                        usuarioRepository.findByEmail(email);

                if (usuarioOptional.isPresent()
                        && Boolean.TRUE.equals(usuarioOptional.get().getAtivo())) {

                    Usuario usuario = usuarioOptional.get();

                    // Autoridades do usuário.
                    List<GrantedAuthority> authorities = new ArrayList<>();

                    // Adiciona a autoridade correspondente ao perfil.
                    authorities.add(
                            new SimpleGrantedAuthority(
                                    "ROLE_" + usuario.getPerfil().name()
                            )
                    );

                    // Adiciona a autoridade quando a oficina estiver ativa.
                    if (usuario.getPerfil() == Perfil.ADM_OFICINA
                            && usuario.getOficina() != null
                            && usuario.getOficina().isAtivo()) {

                        authorities.add(
                                new SimpleGrantedAuthority("OFICINA_ATIVA")
                        );
                    }

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    null,
                                    authorities
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                }
            }

        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

}
