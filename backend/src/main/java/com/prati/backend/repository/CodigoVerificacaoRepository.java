package com.prati.backend.repository;

import com.prati.backend.entity.CodigoVerificacao;
import com.prati.backend.enums.FinalidadeCodigoVerificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CodigoVerificacaoRepository
        extends JpaRepository<CodigoVerificacao, Long> {

    List<CodigoVerificacao> findAllByEmailAndFinalidadeAndUtilizadoFalse(
            String email,
            FinalidadeCodigoVerificacao finalidade
    );
}