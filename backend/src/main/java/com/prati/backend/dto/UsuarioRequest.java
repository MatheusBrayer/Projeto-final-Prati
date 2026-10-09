package com.prati.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.prati.backend.enums.Perfil;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRequest {

    @NotBlank(message = "Favor informe seu email.")
    @Email(message = "Informe um email valido.")
    @Size(max = 100, message = "O email deve ter no maximo 100 caracteres.")
    private String email;

    @NotBlank(message = "Favor informe sua senha.")
    private String senha;

    @NotNull(message = "O perfil é obrigatório")
    private Perfil perfil;

    private Long oficinaId;

    private Long clienteId;
}
