package com.prati.backend.dto;

import com.prati.backend.entity.Oficina;

public record OficinaClienteResponse(
        Long id,
        String nome,
        String slug
) {
    public static OficinaClienteResponse fromEntity(Oficina oficina) {
        return new OficinaClienteResponse(
                oficina.getId(),
                oficina.getNome(),
                oficina.getSlug()
        );
    }
}