CREATE TABLE ordens_servico (
    id BIGSERIAL PRIMARY KEY,
    oficina_id BIGINT NOT NULL,
    cliente_id BIGINT NOT NULL,
    veiculo_id BIGINT NOT NULL,
    descricao TEXT NOT NULL,
    status VARCHAR(30) NOT NULL,
    data_abertura TIMESTAMP NOT NULL,
    data_conclusao TIMESTAMP,
    valor NUMERIC(10, 2),

    CONSTRAINT fk_os_oficina
        FOREIGN KEY (oficina_id)
        REFERENCES oficinas(id),

    CONSTRAINT fk_os_cliente
        FOREIGN KEY (cliente_id)
        REFERENCES clientes(id),

    CONSTRAINT fk_os_veiculo
        FOREIGN KEY (veiculo_id)
        REFERENCES veiculos(id),

    CONSTRAINT ck_os_status
        CHECK (status IN (
            'ABERTA',
            'EM_ANDAMENTO',
            'CONCLUIDA',
            'CANCELADA'
        ))
);