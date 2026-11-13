package com.prati.backend.dto;

import com.prati.backend.entity.Oficina;

public record OficinaResumoResponse(
        Long id,
        String nome,
        String cnpj,
        String telefone,
        String email,
        String slug,
        boolean ativo
) {
    public static OficinaResumoResponse fromEntity(Oficina oficina) {
        return new OficinaResumoResponse(
                oficina.getId(),
                oficina.getNome(),
                oficina.getCnpj(),
                oficina.getTelefone(),
                oficina.getEmail(),
                oficina.getSlug(),
                oficina.isAtivo()
        );
    }
}