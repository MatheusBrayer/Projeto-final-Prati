package com.prati.backend.entity;

import com.prati.backend.enums.Perfil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Perfil perfil;

    @ManyToOne
    @JoinColumn(name = "oficina_id")
    private Oficina oficina;

    @OneToOne
    @JoinColumn(name = "cliente_id", unique = true)
    private Cliente cliente;

}
