package com.prati.backend.repository;

import com.prati.backend.entity.CodigoVerificacao;
import com.prati.backend.enums.FinalidadeCodigoVerificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CodigoVerificacaoRepository
        extends JpaRepository<CodigoVerificacao, Long> {

    List<CodigoVerificacao> findAllByEmailAndFinalidadeAndUtilizadoFalse(
            String email,
            FinalidadeCodigoVerificacao finalidade
    );

    Optional<CodigoVerificacao> findFirstByEmailAndFinalidadeAndUtilizadoFalseOrderByCriadoEmDesc(
            String email,
            FinalidadeCodigoVerificacao finalidade
    );
}