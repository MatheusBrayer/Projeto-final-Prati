
package com.prati.backend.service;

import com.prati.backend.dto.ClienteRequest;
import com.prati.backend.entity.Cliente;
import com.prati.backend.entity.Usuario;
import com.prati.backend.enums.Perfil;
import com.prati.backend.exception.ConflitoCadastroException;
import com.prati.backend.exception.DadosInvalidosException;
import com.prati.backend.exception.RecursoNaoEncontradoException;
import com.prati.backend.repository.ClienteRepository;
import com.prati.backend.repository.UsuarioRepository;
import com.prati.backend.validation.CpfValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteOficinaService clienteOficinaService;

    @Transactional
    public Cliente cadastrarCliente(ClienteRequest request, String emailUsuario) {

        Usuario administrador = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário autenticado não encontrado."
                        )
                );

        if (administrador.getPerfil() != Perfil.ADM_OFICINA
                || administrador.getOficina() == null) {
            throw new AccessDeniedException(
                    "Somente um administrador de oficina pode cadastrar clientes."
            );
        }

        if (!CpfValidator.isValid(request.getCpf())) {
            throw new DadosInvalidosException(
                    "O CPF informado é inválido."
            );
        }

        if (clienteRepository.existsByCpf(request.getCpf())) {
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

        Cliente clienteSalvo = clienteRepository.save(novoCliente);

        clienteOficinaService.vincularClienteOficina(
                clienteSalvo.getId(),
                administrador.getOficina().getId()
        );

        return clienteSalvo;
    }
}

