
package com.prati.backend.entity;

import com.prati.backend.enums.FinalidadeCodigoVerificacao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "codigos_verificacao")
public class CodigoVerificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FinalidadeCodigoVerificacao finalidade;

    @Column(nullable = false, length = 100)
    private String codigoHash;

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    @Column(nullable = false)
    private LocalDateTime expiraEm;

    @Column(nullable = false)
    private Integer tentativas = 0;

    @Column(nullable = false)
    private Boolean utilizado = false;
}
