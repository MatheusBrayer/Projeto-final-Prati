package com.prati.backend.repository;

import com.prati.backend.entity.ClienteOficina;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteOficinaRepository extends JpaRepository <ClienteOficina, Long> {

    boolean existsByClienteIdAndOficinaId(Long clienteId, Long oficinaId);
}
