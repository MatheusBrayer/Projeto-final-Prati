package com.prati.backend.service;

import com.prati.backend.dto.ClienteRequest;
import com.prati.backend.entity.Cliente;
import com.prati.backend.entity.ClienteOficina;
import com.prati.backend.entity.Oficina;
import com.prati.backend.entity.Usuario;
import com.prati.backend.enums.Perfil;
import com.prati.backend.exception.AutenticacaoException;
import com.prati.backend.exception.ConflitoCadastroException;
import com.prati.backend.exception.DadosInvalidosException;
import com.prati.backend.exception.RecursoNaoEncontradoException;
import com.prati.backend.repository.ClienteOficinaRepository;
import com.prati.backend.repository.ClienteRepository;
import com.prati.backend.repository.UsuarioRepository;
import com.prati.backend.validation.CpfValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.prati.backend.dto.ClienteConsultaResponse;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteOficinaRepository clienteOficinaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Cliente cadastrarCliente(ClienteRequest request) {

        // Normaliza e valida o CPF.
        String cpf = request.getCpf().trim();

        if (!CpfValidator.isValid(cpf)) {
            throw new DadosInvalidosException("O CPF informado é inválido.");
        }

        // Identifica o administrador autenticado.
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AutenticacaoException("Usuário não autenticado.");
        }

        String emailAdministrador = authentication.getName();

        Usuario administrador = usuarioRepository
                .findByEmail(emailAdministrador)
                .orElseThrow(() -> new AutenticacaoException(
                        "Usuário autenticado não encontrado."
                ));

        // Garante que somente administradores de oficina realizem o cadastro.
        if (administrador.getPerfil() != Perfil.ADM_OFICINA
                || administrador.getOficina() == null) {
            throw new AutenticacaoException(
                    "Usuário não possui permissão para cadastrar clientes."
            );
        }

        Oficina oficina = administrador.getOficina();

        // Normaliza o email do cliente.
        String emailCliente = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        // Procura o cliente global pelo CPF.
        Optional<Cliente> clienteExistente =
                clienteRepository.findByCpf(cpf);

        Cliente cliente;

        if (clienteExistente.isPresent()) {

            cliente = clienteExistente.get();

            // Verifica se o email pertence a outro cliente.
            Optional<Cliente> clientePorEmail =
                    clienteRepository.findByEmail(emailCliente);

            if (clientePorEmail.isPresent()
                    && !clientePorEmail.get().getId().equals(cliente.getId())) {
                throw new ConflitoCadastroException(
                        "O e-mail informado já está vinculado a outro cliente."
                );
            }

            // Atualiza o cadastro global.
            atualizarDadosCliente(cliente, request, cpf, emailCliente);

        } else {

            // Impede que um email já cadastrado seja reutilizado.
            if (clienteRepository.existsByEmail(emailCliente)) {
                throw new ConflitoCadastroException(
                        "O e-mail informado já pertence a outro cliente."
                );
            }

            cliente = new Cliente();
            atualizarDadosCliente(cliente, request, cpf, emailCliente);
        }

        cliente = clienteRepository.save(cliente);

        // Cria o vínculo somente se ainda não existir.
        boolean vinculoExistente =
                clienteOficinaRepository.existsByClienteIdAndOficinaId(
                        cliente.getId(),
                        oficina.getId()
                );

        if (!vinculoExistente) {
            ClienteOficina vinculo = new ClienteOficina();
            vinculo.setCliente(cliente);
            vinculo.setOficina(oficina);

            clienteOficinaRepository.save(vinculo);
        }

        return cliente;
    }

    private void atualizarDadosCliente(
            Cliente cliente,
            ClienteRequest request,
            String cpf,
            String email
    ) {
        cliente.setNome(request.getNome());
        cliente.setCpf(cpf);
        cliente.setTelefone(request.getTelefone());
        cliente.setEmail(email);
        cliente.setEndereco(request.getEndereco());
    }

    public Cliente buscarClientePorEmail(String email) {
        return clienteRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Cliente não encontrado."
                        ));
    }

    @Transactional(readOnly = true)
    public ClienteConsultaResponse buscarPorCpf(String cpf) {

        String cpfNormalizado = cpf.trim();

        if (!CpfValidator.isValid(cpfNormalizado)) {
            throw new DadosInvalidosException("O CPF informado é inválido.");
        }

        Cliente cliente = clienteRepository.findByCpf(cpfNormalizado)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cliente não encontrado."
                ));

        return ClienteConsultaResponse.fromEntity(cliente);
    }
}