
package com.prati.backend.controller;

import com.prati.backend.dto.OficinaCadastroRequest;
import com.prati.backend.entity.Oficina;
import com.prati.backend.service.OficinaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/oficinas")
@RequiredArgsConstructor
public class OficinaController {

    private final OficinaService oficinaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Oficina cadastrar(
            @Valid @RequestBody OficinaCadastroRequest request) {

        return oficinaService.cadastrar(request);
    }
}
