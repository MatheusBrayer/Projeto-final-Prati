package com.prati.backend.repository;

import com.prati.backend.entity.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    long deleteByOficina_Id(Long oficinaId);

}

