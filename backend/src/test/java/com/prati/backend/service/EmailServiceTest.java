package com.prati.backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import static org.mockito.Mockito.verify;
import org.mockito.ArgumentCaptor;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void deveEnviarEmailComDadosCorretos() {
        String destinatario = "cliente@email.com";
        String assunto = "Código de verificação";
        String mensagem = "Seu código é 123456";

        emailService.enviarEmail(destinatario, assunto, mensagem);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender).send(captor.capture());

        SimpleMailMessage emailEnviado = captor.getValue();

        assertEquals(destinatario, emailEnviado.getTo()[0]);
        assertEquals(assunto, emailEnviado.getSubject());
        assertEquals(mensagem, emailEnviado.getText());
    }
}