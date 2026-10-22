
package com.prati.backend.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SenhaService {

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public String criptografar(String senha) {
        return passwordEncoder.encode(senha);
    }

    public boolean comparar(
            String senhaInformada,
            String senhaCriptografada
    ) {
        return passwordEncoder.matches(
                senhaInformada,
                senhaCriptografada
        );
    }
}

