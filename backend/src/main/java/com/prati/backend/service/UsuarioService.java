
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

    public void validarPerfil(
            Perfil perfil,
            Long oficinaId,
            Long clienteId
    ) {
        switch (perfil) {
            case ADM_SISTEMA -> {
                if (oficinaId != null || clienteId != null) {
                    throw new IllegalArgumentException(
                            "Administrador do sistema não pode estar vinculado a uma oficina nem a um cliente."
                    );
                }
            }

            case ADM_OFICINA -> {
                if (oficinaId == null || clienteId != null) {
                    throw new IllegalArgumentException(
                            "Administrador da oficina deve estar vinculado a uma oficina e não pode estar vinculado a um cliente."
                    );
                }
            }

            case CLIENTE -> {
                if (oficinaId != null || clienteId == null) {
                    throw new IllegalArgumentException(
                            "Cliente não deve estar vinculado diretamente a uma oficina e deve estar vinculado a um cliente."
                    );
                }
            }
        }
    }

    public Usuario cadastrarAdministradorOficina(
            UsuarioRequest request,
            Long oficinaId
    ) {
        return cadastrarUsuario(
                request,
                Perfil.ADM_OFICINA,
                oficinaId,
                null
        );
    }

    public Usuario cadastrarUsuarioCliente(
            UsuarioRequest request,
            Long clienteId
    ) {
        return cadastrarUsuario(
                request,
                Perfil.CLIENTE,
                null,
                clienteId
        );
    }

    private Usuario cadastrarUsuario(
            UsuarioRequest request,
            Perfil perfil,
            Long oficinaId,
            Long clienteId
    ) {
        validarEmail(request.getEmail());
        validarPerfil(perfil, oficinaId, clienteId);

        Usuario novoUsuario = new Usuario();
        novoUsuario.setEmail(request.getEmail());
        novoUsuario.setSenha(request.getSenha());
        novoUsuario.setPerfil(perfil);

        if (oficinaId != null) {
            Oficina oficina = oficinaRepository.findById(oficinaId)
                    .orElseThrow(() -> new RecursoNaoEncontradoException(
                            "Oficina não encontrada."
                    ));

            novoUsuario.setOficina(oficina);
        }

        if (clienteId != null) {
            Cliente cliente = clienteRepository.findById(clienteId)
                    .orElseThrow(() -> new RecursoNaoEncontradoException(
                            "Cliente não encontrado."
                    ));

            novoUsuario.setCliente(cliente);
        }

        return usuarioRepository.save(novoUsuario);
    }
}
