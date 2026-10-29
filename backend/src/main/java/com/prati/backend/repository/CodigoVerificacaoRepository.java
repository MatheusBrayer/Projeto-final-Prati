package com.prati.backend.repository;

import com.prati.backend.entity.CodigoVerificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CodigoVerificacaoRepository
        extends JpaRepository<CodigoVerificacao, Long> {

    List<CodigoVerificacao> findAllByEmailAndUtilizadoFalse(String email);
}
