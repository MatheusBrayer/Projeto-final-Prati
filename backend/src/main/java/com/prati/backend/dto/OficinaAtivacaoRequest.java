
package com.prati.backend.enums;

import jakarta.validation.constraints.NotNull;

public record OficinaAtivacaoRequest(
        @NotNull Boolean ativo
) {
}
