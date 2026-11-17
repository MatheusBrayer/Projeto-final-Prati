
package com.prati.backend.service;

import com.prati.backend.dto.LoginRequest;
import com.prati.backend.dto.LoginResponse;
import com.prati.backend.entity.Oficina;
import com.prati.backend.entity.Usuario;
import com.prati.backend.exception.AutenticacaoException;
import com.prati.backend.repository.UsuarioRepository;
import com.prati.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.prati.backend.dto.PrimeiroAcessoRequest;
import com.prati.backend.entity.Cliente;
import com.prati.backend.enums.FinalidadeCodigoVerificacao;
import com.prati.backend.repository.ClienteRepository;
import com.prati.backend.dto.ConfirmarPrimeiroAcessoRequest;
import org.springframework.transaction.annotation.Transactional;
import com.prati.backend.enums.Perfil;
import java.util.Locale;import com.prati.backend.dto.VerificarCodigoRequest;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final SenhaService senhaService;
    private final JwtService jwtService;
    private final ClienteRepository clienteRepository;
    private final CodigoVerificacaoService codigoVerificacaoService;
    private final UsuarioService usuarioService;



    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AutenticacaoException(
                        "E-mail ou senha inválidos."
                ));

        // Valida a conta e as credenciais.
        if (!Boolean.TRUE.equals(usuario.getAtivo())
                || usuario.getSenha() == null
                || !senhaService.comparar(request.getSenha(), usuario.getSenha())) {
            throw new AutenticacaoException("E-mail ou senha inválidos.");
        }

        // Impede o login de administradores vinculados
        // a oficinas inativas ou inexistentes.
        if (usuario.getPerfil() == Perfil.ADM_OFICINA) {
            Oficina oficina = usuario.getOficina();

            if (oficina == null || !oficina.isAtivo()) {
                throw new AutenticacaoException(
                        "O acesso à sua oficina está temporariamente suspenso. "
                                + "Entre em contato com o administrador da plataforma."
                );
            }
        }

        String token = jwtService.gerarToken(usuario.getEmail());

        return new LoginResponse(
                token,
                "Bearer",
                usuario.getId(),
                usuario.getEmail(),
                usuario.getPerfil()
        );
    }




    public void solicitarPrimeiroAcesso(PrimeiroAcessoRequest request) {

        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElse(null);

        // Cenário 1: já existe um usuário.
        if (usuario != null) {

            // Somente administradores de oficina que ainda
            // não ativaram a conta podem fazer primeiro acesso.
            boolean administradorPendente =
                    usuario.getPerfil() == Perfil.ADM_OFICINA
                            && !Boolean.TRUE.equals(usuario.getAtivo())
                            && usuario.getSenha() == null
                            && usuario.getOficina() != null;

            if (!administradorPendente) {
                return;
            }

            codigoVerificacaoService.gerarCodigo(
                    email,
                    FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO
            );

            return;
        }

        // Cenário 2: cliente cadastrado, mas sem usuário.
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElse(null);

        if (cliente == null) {
            return;
        }

        codigoVerificacaoService.gerarCodigo(
                email,
                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO
        );
    }



    @Transactional(noRollbackFor = AutenticacaoException.class)
    public void confirmarPrimeiroAcesso(
            ConfirmarPrimeiroAcessoRequest request) {

        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        String mensagemErro =
                "Não foi possível concluir o primeiro acesso. " +
                        "Verifique os dados e tente novamente.";

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElse(null);

        Cliente cliente = null;

        // Cenário 1: usuário existente.
        if (usuario != null) {

            boolean administradorPendente =
                    usuario.getPerfil() == Perfil.ADM_OFICINA
                            && !Boolean.TRUE.equals(usuario.getAtivo())
                            && usuario.getSenha() == null
                            && usuario.getOficina() != null;

            if (!administradorPendente) {
                throw new AutenticacaoException(mensagemErro);
            }

        } else {

            // Cenário 2: cliente ainda sem usuário.
            cliente = clienteRepository.findByEmail(email)
                    .orElseThrow(() ->
                            new AutenticacaoException(mensagemErro)
                    );
        }

        // Ambos os cenários exigem um código válido.
        boolean codigoValido = codigoVerificacaoService.validarCodigo(
                email,
                request.getCodigo(),
                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO
        );

        if (!codigoValido) {
            throw new AutenticacaoException(mensagemErro);
        }

        if (usuario != null) {

            // Ativa a conta do administrador existente.
            usuario.setSenha(senhaService.criptografar(
                    request.getSenha()
            ));
            usuario.setAtivo(true);

            usuarioRepository.save(usuario);

        } else {

            // Mantém o cadastro original dos clientes.
            usuarioService.cadastrarUsuarioCliente(
                    email,
                    request.getSenha(),
                    cliente.getId()
            );
        }
    }


    @Transactional(noRollbackFor = AutenticacaoException.class)
    public void verificarCodigoPrimeiroAcesso(
            VerificarCodigoRequest request
    ) {
        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        boolean codigoValido = codigoVerificacaoService.verificarCodigo(
                email,
                request.getCodigo(),
                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO
        );

        if (!codigoValido) {
            throw new AutenticacaoException(
                    "Código inválido ou expirado. Verifique e tente novamente."
            );
        }
    }


}
