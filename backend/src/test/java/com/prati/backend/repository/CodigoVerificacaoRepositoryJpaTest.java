
package com.prati.backend.repository;

import com.prati.backend.entity.CodigoVerificacao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
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

        Optional<CodigoVerificacao> resultado =
                codigoVerificacaoRepository
                        .findByEmailAndUtilizadoFalse("cliente@email.com");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getCodigoHash()).isEqualTo("hash-de-teste");
        assertThat(resultado.get().getUtilizado()).isFalse();
    }

    @Test
    void deveRetornarVazioQuandoNaoExistirCodigoNaoUtilizado() {
        Optional<CodigoVerificacao> resultado =
                codigoVerificacaoRepository
                        .findByEmailAndUtilizadoFalse("inexistente@email.com");

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

        Optional<CodigoVerificacao> resultado =
                codigoVerificacaoRepository
                        .findByEmailAndUtilizadoFalse("cliente@email.com");

        assertThat(resultado).isEmpty();
    }
}
