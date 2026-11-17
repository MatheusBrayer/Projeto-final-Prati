
package com.prati.backend.security;

import com.prati.backend.entity.Oficina;
import com.prati.backend.entity.Usuario;
import com.prati.backend.enums.Perfil;
import com.prati.backend.repository.UsuarioRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
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

                if (usuarioOptional.isPresent()) {

                    Usuario usuario = usuarioOptional.get();

                    // Verifica se a conta do usuário está ativa.
                    boolean usuarioAtivo =
                            Boolean.TRUE.equals(usuario.getAtivo());

                    // Verifica se a oficina permite acesso.
                    boolean oficinaPermitida = true;

                    if (usuario.getPerfil() == Perfil.ADM_OFICINA) {
                        Oficina oficina = usuario.getOficina();

                        oficinaPermitida =
                                oficina != null && oficina.isAtivo();
                    }

                    // Só autentica se o usuário e sua oficina
                    // estiverem habilitados.
                    if (usuarioAtivo && oficinaPermitida) {

                        List<GrantedAuthority> authorities =
                                new ArrayList<>();

                        authorities.add(
                                new SimpleGrantedAuthority(
                                        "ROLE_" + usuario.getPerfil().name()
                                )
                        );

                        if (usuario.getPerfil() == Perfil.ADM_OFICINA) {
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
            }

        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
