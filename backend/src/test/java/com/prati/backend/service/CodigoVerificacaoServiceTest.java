package com.prati.backend.service;

import com.prati.backend.entity.CodigoVerificacao;
import com.prati.backend.enums.FinalidadeCodigoVerificacao;
import com.prati.backend.repository.CodigoVerificacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CodigoVerificacaoServiceTest {

    @Mock
    private CodigoVerificacaoRepository codigoVerificacaoRepository;

    @Mock
    private SenhaService senhaService;

    @InjectMocks
    private CodigoVerificacaoService codigoVerificacaoService;

    @Test
    void deveGerarCodigoDeSeisDigitosESalvarSeuHash() {
        // Arrange
        String email = "cliente@exemplo.com";

        when(codigoVerificacaoRepository
                .findAllByEmailAndFinalidadeAndUtilizadoFalse(
                        email,
                        FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO
                ))
                .thenReturn(List.of());

        when(senhaService.criptografar(anyString()))
                .thenReturn("hash-do-codigo");

        // Act
        String codigo = codigoVerificacaoService.gerarCodigo(
                email,
                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO
        );

        // Assert
        assertNotNull(codigo);
        assertTrue(codigo.matches("\\d{6}"));

        ArgumentCaptor<CodigoVerificacao> captor =
                ArgumentCaptor.forClass(CodigoVerificacao.class);

        verify(codigoVerificacaoRepository).save(captor.capture());

        CodigoVerificacao salvo = captor.getValue();

        assertEquals(email, salvo.getEmail());
        assertEquals(
                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO,
                salvo.getFinalidade()
        );
        assertEquals("hash-do-codigo", salvo.getCodigoHash());
        assertNotEquals(codigo, salvo.getCodigoHash());
        assertEquals(0, salvo.getTentativas());
        assertFalse(salvo.getUtilizado());

        verify(senhaService).criptografar(codigo);
    }

    @Test
    void deveDefinirValidadeDeCincoMinutosParaOCodigo() {
        // Arrange
        String email = "cliente@exemplo.com";

        when(codigoVerificacaoRepository
                .findAllByEmailAndFinalidadeAndUtilizadoFalse(
                        email,
                        FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO
                ))
                .thenReturn(List.of());

        when(senhaService.criptografar(anyString()))
                .thenReturn("hash-do-codigo");

        LocalDateTime inicioDoTeste = LocalDateTime.now();

        // Act
        codigoVerificacaoService.gerarCodigo(
                email,
                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO
        );

        LocalDateTime fimDoTeste = LocalDateTime.now();

        // Assert
        ArgumentCaptor<CodigoVerificacao> captor =
                ArgumentCaptor.forClass(CodigoVerificacao.class);

        verify(codigoVerificacaoRepository).save(captor.capture());

        CodigoVerificacao salvo = captor.getValue();

        assertNotNull(salvo.getCriadoEm());
        assertNotNull(salvo.getExpiraEm());

        assertFalse(salvo.getCriadoEm().isBefore(inicioDoTeste));
        assertFalse(salvo.getCriadoEm().isAfter(fimDoTeste));

        assertEquals(
                5,
                Duration.between(
                        salvo.getCriadoEm(),
                        salvo.getExpiraEm()
                ).toMinutes()
        );

        assertEquals(
                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO,
                salvo.getFinalidade()
        );
    }

    @Test
    void deveInvalidarCodigosAnterioresAoGerarNovoCodigo() {
        // Arrange
        String email = "cliente@exemplo.com";

        CodigoVerificacao codigoAnterior1 = new CodigoVerificacao();
        codigoAnterior1.setEmail(email);
        codigoAnterior1.setFinalidade(
                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO
        );
        codigoAnterior1.setUtilizado(false);

        CodigoVerificacao codigoAnterior2 = new CodigoVerificacao();
        codigoAnterior2.setEmail(email);
        codigoAnterior2.setFinalidade(
                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO
        );
        codigoAnterior2.setUtilizado(false);

        when(codigoVerificacaoRepository
                .findAllByEmailAndFinalidadeAndUtilizadoFalse(
                        email,
                        FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO
                ))
                .thenReturn(List.of(codigoAnterior1, codigoAnterior2));

        when(senhaService.criptografar(anyString()))
                .thenReturn("hash-do-novo-codigo");

        // Act
        codigoVerificacaoService.gerarCodigo(
                email,
                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO
        );

        // Assert
        assertTrue(codigoAnterior1.getUtilizado());
        assertTrue(codigoAnterior2.getUtilizado());

        verify(codigoVerificacaoRepository)
                .saveAll(List.of(codigoAnterior1, codigoAnterior2));

        verify(codigoVerificacaoRepository)
                .save(any(CodigoVerificacao.class));
    }
}
