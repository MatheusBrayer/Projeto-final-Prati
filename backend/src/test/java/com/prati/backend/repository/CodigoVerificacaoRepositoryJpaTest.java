
package com.prati.backend.repository;

import com.prati.backend.entity.CodigoVerificacao;
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
        CodigoVerificacao codigo = new CodigoVerificacao();
        codigo.setEmail("cliente@email.com");
        codigo.setCodigoHash("hash-de-teste");
        codigo.setCriadoEm(LocalDateTime.now());
        codigo.setExpiraEm(LocalDateTime.now().plusMinutes(5));
        codigo.setTentativas(0);
        codigo.setUtilizado(false);

        codigoVerificacaoRepository.save(codigo);

        List<CodigoVerificacao> resultado =
                codigoVerificacaoRepository
                        .findAllByEmailAndUtilizadoFalse("cliente@email.com");

        assertThat(resultado).hasSize(1);

        assertThat(resultado.get(0).getCodigoHash())
                .isEqualTo("hash-de-teste");

        assertThat(resultado.get(0).getUtilizado())
                .isFalse();
    }

    @Test
    void deveRetornarVazioQuandoNaoExistirCodigoNaoUtilizado() {
        List<CodigoVerificacao> resultado =
                codigoVerificacaoRepository
                        .findAllByEmailAndUtilizadoFalse("cliente@email.com");

        assertThat(resultado).isEmpty();
    }

    @Test
    void naoDeveEncontrarCodigoJaUtilizado() {
        CodigoVerificacao codigo = new CodigoVerificacao();
        codigo.setEmail("cliente@email.com");
        codigo.setCodigoHash("hash-de-teste");
        codigo.setCriadoEm(LocalDateTime.now());
        codigo.setExpiraEm(LocalDateTime.now().plusMinutes(5));
        codigo.setTentativas(0);
        codigo.setUtilizado(true);

        codigoVerificacaoRepository.save(codigo);

        List<CodigoVerificacao> resultado =
                codigoVerificacaoRepository
                        .findAllByEmailAndUtilizadoFalse("cliente@email.com");

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveRetornarTodosOsCodigosNaoUtilizadosDoEmail() {
        CodigoVerificacao primeiro = new CodigoVerificacao();
        primeiro.setEmail("cliente@email.com");
        primeiro.setCodigoHash("hash-1");
        primeiro.setCriadoEm(LocalDateTime.now());
        primeiro.setExpiraEm(LocalDateTime.now().plusMinutes(5));
        primeiro.setTentativas(0);
        primeiro.setUtilizado(false);

        CodigoVerificacao segundo = new CodigoVerificacao();
        segundo.setEmail("cliente@email.com");
        segundo.setCodigoHash("hash-2");
        segundo.setCriadoEm(LocalDateTime.now());
        segundo.setExpiraEm(LocalDateTime.now().plusMinutes(5));
        segundo.setTentativas(0);
        segundo.setUtilizado(false);

        codigoVerificacaoRepository.save(primeiro);
        codigoVerificacaoRepository.save(segundo);

        List<CodigoVerificacao> resultado =
                codigoVerificacaoRepository
                        .findAllByEmailAndUtilizadoFalse("cliente@email.com");

        assertThat(resultado).hasSize(2);
        assertThat(resultado)
                .extracting(CodigoVerificacao::getCodigoHash)
                .containsExactlyInAnyOrder("hash-1", "hash-2");
    }
}
