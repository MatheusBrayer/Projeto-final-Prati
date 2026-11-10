
package com.prati.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OficinaCadastroRequest {

    @NotBlank(message = "Informe o nome da oficina.")
    @Size(max = 150)
    private String nome;

    @NotBlank(message = "Informe o CNPJ.")
    @Size(min = 14, max = 14)
    private String cnpj;

    @NotBlank(message = "Informe o telefone.")
    @Size(max = 20)
    private String telefone;

    private String endereco;

    @NotBlank(message = "Informe o e-mail da oficina.")
    @Email
    @Size(max = 150)
    private String email;

    @NotBlank(message = "Informe o e-mail do administrador.")
    @Email
    @Size(max = 150)
    private String emailAdministrador;
}
