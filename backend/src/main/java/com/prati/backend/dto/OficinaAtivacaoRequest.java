
package com.prati.backend.dto;

import jakarta.validation.constraints.NotNull;

public record OficinaAtivacaoRequest(
        @NotNull Boolean ativo
) {
}
