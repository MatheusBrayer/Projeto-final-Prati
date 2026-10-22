
package com.prati.backend.dto;

import com.prati.backend.enums.Perfil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String token;

    private String tipo;

    private Long usuarioId;

    private String email;

    private Perfil perfil;
}