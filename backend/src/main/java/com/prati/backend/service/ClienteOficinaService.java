package com.prati.backend.service;

import com.prati.backend.entity.Cliente;
import com.prati.backend.entity.ClienteOficina;
import com.prati.backend.entity.Oficina;
import com.prati.backend.exception.ConflitoCadastroException;
import com.prati.backend.exception.RecursoNaoEncontradoException;
import com.prati.backend.repository.ClienteOficinaRepository;
import com.prati.backend.repository.ClienteRepository;
import com.prati.backend.repository.OficinaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteOficinaService {

    private final ClienteRepository clienteRepository;
    private final OficinaRepository oficinaRepository;
    private final ClienteOficinaRepository clienteOficinaRepository;

    @Transactional
    public ClienteOficina vincularClienteOficina (long ClienteID, long OficinaID) {

        Cliente cliente = clienteRepository.findById(ClienteID).orElseThrow(() -> new RecursoNaoEncontradoException(
                "Cliente não encontrado."
        ));

        Oficina oficina = oficinaRepository.findById(OficinaID).orElseThrow(() -> new RecursoNaoEncontradoException(
                "Oficina não encontrada."
        ));

        boolean vinculoClienteOficina = clienteOficinaRepository.existsByClienteIdAndOficinaId(ClienteID, OficinaID);

        if (vinculoClienteOficina) {
            throw new ConflitoCadastroException(
                    "Este cliente já está vinculado a esta oficina.");
        }

        ClienteOficina vinculo = new ClienteOficina();
        vinculo.setCliente(cliente);
        vinculo.setOficina(oficina);

        return clienteOficinaRepository.save(vinculo);
    };
}
