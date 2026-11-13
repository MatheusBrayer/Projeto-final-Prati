
package com.prati.backend.service;

import com.prati.backend.dto.OficinaCadastroRequest;
import com.prati.backend.entity.Oficina;
import com.prati.backend.entity.Usuario;
import com.prati.backend.enums.Perfil;
import com.prati.backend.exception.ConflitoCadastroException;
import com.prati.backend.exception.RecursoNaoEncontradoException;
import com.prati.backend.repository.OficinaRepository;
import com.prati.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.prati.backend.dto.OficinaPublicaResponse;
import java.text.Normalizer;
import java.util.Locale;
import java.util.List;
import com.prati.backend.dto.OficinaResumoResponse;

@Service
@RequiredArgsConstructor
public class OficinaService {

    private final OficinaRepository oficinaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<OficinaResumoResponse> listarTodas() {

        return oficinaRepository.findAll()
                .stream()
                .map(OficinaResumoResponse::fromEntity)
                .toList();
    }

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

        oficina.setSlug(gerarSlugUnico(oficina.getNome()));
        oficina.setAtivo(false);

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

    private String gerarSlugUnico(String nome) {

        String slugBase = Normalizer.normalize(
                nome, Normalizer.Form.NFD
        );

        slugBase = slugBase
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");

        if (slugBase.isBlank()) {
            slugBase = "oficina";
        }

        if (slugBase.length() > 140) {
            slugBase = slugBase.substring(0, 140)
                    .replaceAll("-$", "");
        }

        String slug = slugBase;
        int contador = 2;

        while (oficinaRepository.existsBySlug(slug)) {
            slug = slugBase + "-" + contador;
            contador++;
        }

        return slug;
    }

    @Transactional
    public Oficina alterarAtivacao(Long id, boolean ativo) {

        Oficina oficina = oficinaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Oficina não encontrada."
                ));

        oficina.setAtivo(ativo);

        return oficinaRepository.save(oficina);
    }

    @Transactional(readOnly = true)
    public OficinaPublicaResponse buscarPorSlug(String slug) {

        Oficina oficina = oficinaRepository.findBySlug(slug)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Oficina não encontrada."
                ));

        return new OficinaPublicaResponse(
                oficina.getId(),
                oficina.getNome(),
                oficina.getSlug()
        );
    }
}
