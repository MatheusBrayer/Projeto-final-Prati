package com.prati.backend.repository;

import com.prati.backend.entity.CodigoVerificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodigoVerificacaoRepository
        extends JpaRepository<CodigoVerificacao, Long> {

    Optional<CodigoVerificacao> findByEmailAndUtilizadoFalse(String email);
}
