
package com.prati.backend.service;

import com.prati.backend.dto.OficinaCadastroRequest;
import com.prati.backend.entity.Oficina;
import com.prati.backend.entity.Usuario;
import com.prati.backend.enums.Perfil;
import com.prati.backend.exception.ConflitoCadastroException;
import com.prati.backend.repository.OficinaRepository;
import com.prati.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class OficinaService {

    private final OficinaRepository oficinaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Oficina cadastrar(OficinaCadastroRequest request) {

        String cnpj = request.getCnpj().trim();
        String emailAdministrador = request.getEmailAdministrador()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (oficinaRepository.existsByCnpj(cnpj)) {
            throw new ConflitoCadastroException(
                    "Já existe uma oficina cadastrada com este CNPJ."
            );
        }

        if (usuarioRepository.existsByEmail(emailAdministrador)) {
            throw new ConflitoCadastroException(
                    "Já existe um usuário cadastrado com este e-mail."
            );
        }

        Oficina oficina = new Oficina();
        oficina.setNome(request.getNome().trim());
        oficina.setCnpj(cnpj);
        oficina.setTelefone(request.getTelefone().trim());
        oficina.setEmail(request.getEmail().trim());
        oficina.setEndereco(request.getEndereco());

        Oficina oficinaSalva = oficinaRepository.save(oficina);

        Usuario administrador = new Usuario();
        administrador.setEmail(emailAdministrador);
        administrador.setSenha(null);
        administrador.setPerfil(Perfil.ADM_OFICINA);
        administrador.setAtivo(false);
        administrador.setOficina(oficinaSalva);

        usuarioRepository.save(administrador);

        return oficinaSalva;
    }
}
