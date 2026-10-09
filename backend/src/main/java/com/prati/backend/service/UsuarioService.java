package com.prati.backend.service;

import com.prati.backend.dto.UsuarioRequest;
import com.prati.backend.entity.Cliente;
import com.prati.backend.entity.Oficina;
import com.prati.backend.entity.Usuario;
import com.prati.backend.enums.Perfil;
import com.prati.backend.exception.ConflitoCadastroException;
import com.prati.backend.exception.RecursoNaoEncontradoException;
import com.prati.backend.repository.ClienteRepository;
import com.prati.backend.repository.OficinaRepository;
import com.prati.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final OficinaRepository oficinaRepository;
    private final ClienteRepository clienteRepository;

    public void validarEmail(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflitoCadastroException(
                    "Já existe um usuário cadastrado com este e-mail."
            );
        }
    }

    public void validarPerfil (
            Perfil perfil,
            Long oficinaID,
            Long clienteID
    ) {
        switch (perfil) {
            case ADM_SISTEMA -> {
                if (oficinaID != null || clienteID != null) {
                    throw new IllegalArgumentException("Administrador do sistema não pode estar vinculado a uma oficina nem a um cliente");
                }
            }

            case ADM_OFICINA -> {
                if (oficinaID == null || clienteID != null) {
                    throw new IllegalArgumentException("Administrador da oficina deve estar vinculado a uma oficina e não pode estar vinculado a um cliente.");
                }
            }

            case CLIENTE -> {
                if (oficinaID != null || clienteID == null) {
                    throw new IllegalArgumentException("Cliente não deve estar vinculado a uma oficina e deve estar vinculado a um cliente.");
                }
            }
        }
    }

    public Usuario cadastrarUsuario (UsuarioRequest request) {

        validarEmail(request.getEmail());

        validarPerfil(
                request.getPerfil(),
                request.getOficinaId(),
                request.getClienteId()
        );

        Usuario novoUsuario = new Usuario();
        novoUsuario.setEmail(request.getEmail());
        novoUsuario.setSenha(request.getSenha());
        novoUsuario.setPerfil(request.getPerfil());

        if (request.getOficinaId() != null) {
            Oficina oficina = oficinaRepository.findById(request.getOficinaId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Oficina não encontrada!"));

            novoUsuario.setOficina(oficina);
        }

        if (request.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado!"));

            novoUsuario.setCliente(cliente);
        }

        return usuarioRepository.save(novoUsuario);
    }

}
