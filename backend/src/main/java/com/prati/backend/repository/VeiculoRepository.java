package com.prati.backend.repository;

import com.prati.backend.entity.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    long deleteByOficina_Id(Long oficinaId);

}
