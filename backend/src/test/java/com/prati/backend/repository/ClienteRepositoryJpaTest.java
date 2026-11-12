
package com.prati.backend.repository;

import com.prati.backend.entity.Cliente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:cliente_repository_test;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ClienteRepositoryJpaTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    void deveBuscarClientePeloCpf() {
        Cliente cliente = new Cliente();
        cliente.setNome("Maria da Silva");
        cliente.setCpf("52998224725");
        cliente.setTelefone("51988888888");
        cliente.setEmail("maria@email.com");
        cliente.setEndereco("Rua das Flores, 123");

        clienteRepository.save(cliente);

        Optional<Cliente> resultado =
                clienteRepository.findByCpf("52998224725");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Maria da Silva");
        assertThat(resultado.get().getCpf()).isEqualTo("52998224725");
    }

    @Test
    void deveRetornarVazioQuandoCpfNaoExistir() {
        Optional<Cliente> resultado =
                clienteRepository.findByCpf("52998224725");

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveBuscarClientePeloEmail() {
        Cliente cliente = new Cliente();
        cliente.setNome("Maria da Silva");
        cliente.setCpf("52998224725");
        cliente.setTelefone("51988888888");
        cliente.setEmail("maria@email.com");
        cliente.setEndereco("Rua das Flores, 123");

        clienteRepository.save(cliente);

        Optional<Cliente> resultado =
                clienteRepository.findByEmail("maria@email.com");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Maria da Silva");
        assertThat(resultado.get().getEmail()).isEqualTo("maria@email.com");
    }

    @Test
    void deveRetornarVazioQuandoEmailNaoExistir() {
        Optional<Cliente> resultado =
                clienteRepository.findByEmail("inexistente@email.com");

        assertThat(resultado).isEmpty();
    }
}
