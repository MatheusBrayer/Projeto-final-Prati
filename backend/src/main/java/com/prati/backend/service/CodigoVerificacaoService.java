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

    private static final int MAX_TENTATIVAS = 5;

    private static final SecureRandom RANDOM = new SecureRandom();

    private final CodigoVerificacaoRepository codigoVerificacaoRepository;
    private final SenhaService senhaService;
    private final EmailService emailService;

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

        emailService.enviarEmail(
                email,
                "Código de verificação - AutoGestão Pro",
                """
                Olá!

                Seu código de verificação é: %s

                Ele é válido por %d minutos.

                Se você não solicitou este código, ignore este e-mail.

                Atenciosamente,
                Equipe AutoGestão Pro
                """.formatted(codigo, VALIDADE_MINUTOS)
        );

        return codigo;
    }

    @Transactional
    public boolean validarCodigo(
            String email,
            String codigoInformado,
            FinalidadeCodigoVerificacao finalidade
    ) {
        CodigoVerificacao codigoVerificacao =
                codigoVerificacaoRepository
                        .findFirstByEmailAndFinalidadeAndUtilizadoFalseOrderByCriadoEmDesc(
                                email,
                                finalidade
                        )
                        .orElse(null);

        if (codigoVerificacao == null) {
            return false;
        }

        LocalDateTime agora = LocalDateTime.now();

        if (!agora.isBefore(codigoVerificacao.getExpiraEm())) {
            codigoVerificacao.setUtilizado(true);
            codigoVerificacaoRepository.save(codigoVerificacao);

            return false;
        }

        if (codigoVerificacao.getTentativas() >= MAX_TENTATIVAS) {
            codigoVerificacao.setUtilizado(true);
            codigoVerificacaoRepository.save(codigoVerificacao);

            return false;
        }

        boolean codigoCorreto = senhaService.comparar(
                codigoInformado,
                codigoVerificacao.getCodigoHash()
        );

        if (!codigoCorreto) {
            codigoVerificacao.setTentativas(
                    codigoVerificacao.getTentativas() + 1
            );

            if (codigoVerificacao.getTentativas() >= MAX_TENTATIVAS) {
                codigoVerificacao.setUtilizado(true);
            }

            codigoVerificacaoRepository.save(codigoVerificacao);

            return false;
        }

        codigoVerificacao.setUtilizado(true);
        codigoVerificacaoRepository.save(codigoVerificacao);

        return true;
    }


    @Transactional
    public boolean verificarCodigo(
            String email,
            String codigoInformado,
            FinalidadeCodigoVerificacao finalidade
    ) {
        CodigoVerificacao registro = codigoVerificacaoRepository
                .findFirstByEmailAndFinalidadeAndUtilizadoFalseOrderByCriadoEmDesc(
                        email,
                        finalidade
                )
                .orElse(null);

        if (registro == null) {
            return false;
        }

        LocalDateTime agora = LocalDateTime.now();

        // Verifica se o código expirou ou excedeu as tentativas.
        if (!agora.isBefore(registro.getExpiraEm())
                || registro.getTentativas() >= MAX_TENTATIVAS) {

            registro.setUtilizado(true);
            codigoVerificacaoRepository.save(registro);
            return false;
        }

        // Compara o código informado com o hash armazenado.
        boolean codigoCorreto = senhaService.comparar(
                codigoInformado,
                registro.getCodigoHash()
        );

        if (!codigoCorreto) {
            registro.setTentativas(registro.getTentativas() + 1);

            if (registro.getTentativas() >= MAX_TENTATIVAS) {
                registro.setUtilizado(true);
            }

            codigoVerificacaoRepository.save(registro);
            return false;
        }

        // Código válido, mas NÃO é consumido nesta etapa.
        return true;
    }

}
