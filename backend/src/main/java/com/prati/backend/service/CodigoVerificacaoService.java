package com.prati.backend.service;

import com.prati.backend.entity.CodigoVerificacao;
import com.prati.backend.enums.FinalidadeCodigoVerificacao;
import com.prati.backend.repository.CodigoVerificacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CodigoVerificacaoService {

    private static final int VALIDADE_MINUTOS = 5;

    private static final SecureRandom RANDOM = new SecureRandom();

    private final CodigoVerificacaoRepository codigoVerificacaoRepository;
    private final SenhaService senhaService;

    @Transactional
    public String gerarCodigo(
            String email,
            FinalidadeCodigoVerificacao finalidade
    ) {
        List<CodigoVerificacao> codigosAnteriores =
                codigoVerificacaoRepository
                        .findAllByEmailAndFinalidadeAndUtilizadoFalse(
                                email,
                                finalidade
                        );

        codigosAnteriores.forEach(codigo ->
                codigo.setUtilizado(true)
        );

        codigoVerificacaoRepository.saveAll(codigosAnteriores);

        String codigo = String.format(
                Locale.ROOT,
                "%06d",
                RANDOM.nextInt(1_000_000)
        );

        LocalDateTime agora = LocalDateTime.now();

        CodigoVerificacao novoCodigo = new CodigoVerificacao();
        novoCodigo.setEmail(email);
        novoCodigo.setFinalidade(finalidade);
        novoCodigo.setCodigoHash(senhaService.criptografar(codigo));
        novoCodigo.setCriadoEm(agora);
        novoCodigo.setExpiraEm(agora.plusMinutes(VALIDADE_MINUTOS));
        novoCodigo.setTentativas(0);
        novoCodigo.setUtilizado(false);

        codigoVerificacaoRepository.save(novoCodigo);

        return codigo;
    }
}