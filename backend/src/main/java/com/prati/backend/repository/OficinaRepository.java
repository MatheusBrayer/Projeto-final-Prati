
package com.prati.backend.repository;

import com.prati.backend.entity.Oficina;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OficinaRepository extends JpaRepository<Oficina, Long> {

    boolean existsByCnpj(String cnpj);

    boolean existsBySlug(String slug);

    Optional<Oficina> findBySlug(String slug);
}
