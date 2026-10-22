package com.prati.backend.service;

import com.prati.backend.dto.ClienteRequest;
import com.prati.backend.entity.Cliente;
import com.prati.backend.exception.ConflitoCadastroException;
import com.prati.backend.exception.DadosInvalidosException;
import com.prati.backend.repository.ClienteRepository;
import com.prati.backend.validation.CpfValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public Cliente cadastrarCliente (ClienteRequest request) {

        if (!CpfValidator.isValid(request.getCpf())) {
            throw new DadosInvalidosException("O CPF informado é inválido.");
        }

        if(clienteRepository.existsByCpf(request.getCpf())) {
            throw new ConflitoCadastroException(
                    "Já existe um cliente cadastrado com este CPF."
            );
        }

        Cliente novoCliente = new Cliente();
        novoCliente.setNome(request.getNome());
        novoCliente.setCpf(request.getCpf());
        novoCliente.setTelefone(request.getTelefone());
        novoCliente.setEmail(request.getEmail());
        novoCliente.setEndereco(request.getEndereco());

        return clienteRepository.save(novoCliente);
    }


}
