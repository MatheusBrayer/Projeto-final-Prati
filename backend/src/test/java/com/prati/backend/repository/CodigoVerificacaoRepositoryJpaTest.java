package com.prati.backend.repository;

import com.prati.backend.entity.CodigoVerificacao;
import com.prati.backend.enums.FinalidadeCodigoVerificacao;
import com.prati.backend.repository.CodigoVerificacaoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:codigo_verificacao_test;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class CodigoVerificacaoRepositoryJpaTest {

    @Autowired
    private CodigoVerificacaoRepository codigoVerificacaoRepository;

    @Test
    void deveSalvarEBuscarCodigoPorEmailQuandoNaoUtilizado() {
        CodigoVerificacao codigo = criarCodigo(
                "cliente@email.com",
                "hash-de-teste",
                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO,
                false
        );

        codigoVerificacaoRepository.save(codigo);

        List<CodigoVerificacao> resultado =
                codigoVerificacaoRepository
                        .findAllByEmailAndFinalidadeAndUtilizadoFalse(
                                "cliente@email.com",
                                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO
                        );

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCodigoHash())
                .isEqualTo("hash-de-teste");
        assertThat(resultado.get(0).getUtilizado()).isFalse();
        assertThat(resultado.get(0).getFinalidade())
                .isEqualTo(FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO);
    }

    @Test
    void deveRetornarVazioQuandoNaoExistirCodigoNaoUtilizado() {
        List<CodigoVerificacao> resultado =
                codigoVerificacaoRepository
                        .findAllByEmailAndFinalidadeAndUtilizadoFalse(
                                "cliente@email.com",
                                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO
                        );

        assertThat(resultado).isEmpty();
    }

    @Test
    void naoDeveEncontrarCodigoJaUtilizado() {
        CodigoVerificacao codigo = criarCodigo(
                "cliente@email.com",
                "hash-de-teste",
                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO,
                true
        );

        codigoVerificacaoRepository.save(codigo);

        List<CodigoVerificacao> resultado =
                codigoVerificacaoRepository
                        .findAllByEmailAndFinalidadeAndUtilizadoFalse(
                                "cliente@email.com",
                                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO
                        );

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveRetornarTodosOsCodigosNaoUtilizadosDoEmail() {
        CodigoVerificacao primeiro = criarCodigo(
                "cliente@email.com",
                "hash-1",
                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO,
                false
        );

        CodigoVerificacao segundo = criarCodigo(
                "cliente@email.com",
                "hash-2",
                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO,
                false
        );

        codigoVerificacaoRepository.save(primeiro);
        codigoVerificacaoRepository.save(segundo);

        List<CodigoVerificacao> resultado =
                codigoVerificacaoRepository
                        .findAllByEmailAndFinalidadeAndUtilizadoFalse(
                                "cliente@email.com",
                                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO
                        );

        assertThat(resultado).hasSize(2);
        assertThat(resultado)
                .extracting(CodigoVerificacao::getCodigoHash)
                .containsExactlyInAnyOrder("hash-1", "hash-2");
    }

    @Test
    void deveSepararCodigosPorFinalidade() {
        CodigoVerificacao primeiroAcesso = criarCodigo(
                "cliente@email.com",
                "hash-primeiro-acesso",
                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO,
                false
        );

        CodigoVerificacao redefinicaoSenha = criarCodigo(
                "cliente@email.com",
                "hash-redefinicao",
                FinalidadeCodigoVerificacao.REDEFINICAO_SENHA,
                false
        );

        codigoVerificacaoRepository.save(primeiroAcesso);
        codigoVerificacaoRepository.save(redefinicaoSenha);

        List<CodigoVerificacao> resultado =
                codigoVerificacaoRepository
                        .findAllByEmailAndFinalidadeAndUtilizadoFalse(
                                "cliente@email.com",
                                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO
                        );

        assertThat(resultado)
                .extracting(CodigoVerificacao::getCodigoHash)
                .containsExactly("hash-primeiro-acesso");
    }

    @Test
    void deveBuscarCodigoNaoUtilizadoMaisRecente() {
        CodigoVerificacao primeiro = criarCodigo(
                "cliente@email.com",
                "hash-antigo",
                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO,
                false
        );

        primeiro.setCriadoEm(LocalDateTime.now().minusMinutes(2));

        CodigoVerificacao segundo = criarCodigo(
                "cliente@email.com",
                "hash-recente",
                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO,
                false
        );

        segundo.setCriadoEm(LocalDateTime.now());

        codigoVerificacaoRepository.save(primeiro);
        codigoVerificacaoRepository.save(segundo);

        Optional<CodigoVerificacao> resultado =
                codigoVerificacaoRepository
                        .findFirstByEmailAndFinalidadeAndUtilizadoFalseOrderByCriadoEmDesc(
                                "cliente@email.com",
                                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO
                        );

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getCodigoHash())
                .isEqualTo("hash-recente");
    }

    @Test
    void deveRetornarVazioQuandoNaoExistirCodigoPendenteParaBuscaMaisRecente() {
        Optional<CodigoVerificacao> resultado =
                codigoVerificacaoRepository
                        .findFirstByEmailAndFinalidadeAndUtilizadoFalseOrderByCriadoEmDesc(
                                "cliente@email.com",
                                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO
                        );

        assertThat(resultado).isEmpty();
    }

    private CodigoVerificacao criarCodigo(
            String email,
            String codigoHash,
            FinalidadeCodigoVerificacao finalidade,
            boolean utilizado
    ) {
        LocalDateTime agora = LocalDateTime.now();

        CodigoVerificacao codigo = new CodigoVerificacao();
        codigo.setEmail(email);
        codigo.setCodigoHash(codigoHash);
        codigo.setFinalidade(finalidade);
        codigo.setCriadoEm(agora);
        codigo.setExpiraEm(agora.plusMinutes(5));
        codigo.setTentativas(0);
        codigo.setUtilizado(utilizado);

        return codigo;
    }
}
