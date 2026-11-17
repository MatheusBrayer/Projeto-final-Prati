package com.prati.backend.repository;

import com.prati.backend.entity.Usuario;
import com.prati.backend.enums.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Boolean existsByEmail(String email);

    long deleteByOficina_IdAndPerfil(Long oficinaId, Perfil perfil);
}
