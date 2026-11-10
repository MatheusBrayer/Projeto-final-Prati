package com.prati.backend.dto;

import com.prati.backend.entity.Cliente;

public record ClienteConsultaResponse(
        Long id,
        String nome,
        String cpf,
        String telefone,
        String email,
        String endereco
) {

    public static ClienteConsultaResponse fromEntity(Cliente cliente) {
        return new ClienteConsultaResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getTelefone(),
                cliente.getEmail(),
                cliente.getEndereco()
        );
    }
}
