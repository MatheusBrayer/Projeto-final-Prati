package com.prati.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "clientes_oficinas",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"cliente_id", "oficina_id"})})
public class ClienteOficina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "oficina_id", nullable = false)
    private Oficina oficina;
}
