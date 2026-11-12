package com.prati.backend.controller;

import com.prati.backend.dto.ClienteConsultaResponse;
import com.prati.backend.dto.ClienteRequest;
import com.prati.backend.entity.Cliente;
import com.prati.backend.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente cadastrarCliente(
            @Valid @RequestBody ClienteRequest request) {

        String emailUsuario = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return clienteService.cadastrarCliente(request, emailUsuario);
    }

    @GetMapping("/buscar-por-cpf/{cpf}")
    public ClienteConsultaResponse buscarPorCpf(@PathVariable String cpf) {

        String emailUsuario = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return clienteService.buscarPorCpf(cpf, emailUsuario);
    }
}