
package com.prati.backend.service;

import com.prati.backend.dto.LoginRequest;
import com.prati.backend.dto.LoginResponse;
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

import java.util.Locale;

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

        Usuario usuario = usuarioRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new AutenticacaoException(
                        "E-mail ou senha inválidos."
                ));

        if (!senhaService.comparar(
                request.getSenha(),
                usuario.getSenha()
        )) {
            throw new AutenticacaoException(
                    "E-mail ou senha inválidos."
            );
        }

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new AutenticacaoException(
                    "Usuário inativo."
            );
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

        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        // Se já existe uma conta, não envia outro código.
        if (usuarioRepository.existsByEmail(email)) {
            return;
        }

        // Procura um cliente cadastrado com esse e-mail.
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElse(null);

        // Se não encontrar, encerra sem revelar essa informação.
        if (cliente == null) {
            return;
        }

        // Gera e envia o código de primeiro acesso.
        codigoVerificacaoService.gerarCodigo(
                email,
                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO
        );
    }

    @Transactional(noRollbackFor = AutenticacaoException.class)
    public void confirmarPrimeiroAcesso(ConfirmarPrimeiroAcessoRequest request) {

        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        // Impede a criação de uma segunda conta para o mesmo e-mail.
        if (usuarioRepository.existsByEmail(email)) {
            throw new AutenticacaoException(
                    "Não foi possível concluir o primeiro acesso. Verifique os dados e tente novamente."
            );
        }

        // Verifica se existe um cliente cadastrado com esse e-mail.
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new AutenticacaoException(
                        "Não foi possível concluir o primeiro acesso. Verifique os dados e tente novamente."
                ));

        // Valida o código enviado para o e-mail.
        boolean codigoValido = codigoVerificacaoService.validarCodigo(
                email,
                request.getCodigo(),
                FinalidadeCodigoVerificacao.PRIMEIRO_ACESSO
        );

        if (!codigoValido) {
            throw new AutenticacaoException(
                    "Não foi possível concluir o primeiro acesso. Verifique os dados e tente novamente."
            );
        }

        // Cria a conta vinculada ao cliente existente.
        usuarioService.cadastrarUsuarioCliente(
                email,
                request.getSenha(),
                cliente.getId()
        );
    }
}
