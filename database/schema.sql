CREATE TABLE oficinas (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    telefone VARCHAR(20) NOT NULL,
    email VARCHAR(150) NOT NULL,
    endereco VARCHAR(255)
);

CREATE TABLE clientes (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    telefone VARCHAR(20) NOT NULL,
    email VARCHAR(150),
    endereco VARCHAR(255)
);

CREATE TABLE clientes_oficinas (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    oficina_id BIGINT NOT NULL,

    CONSTRAINT fk_cliente_oficina_cliente
        FOREIGN KEY (cliente_id)
        REFERENCES clientes(id),

    CONSTRAINT fk_cliente_oficina_oficina
        FOREIGN KEY (oficina_id)
        REFERENCES oficinas(id),

    CONSTRAINT uk_cliente_oficina
        UNIQUE (cliente_id, oficina_id)
);

CREATE TABLE veiculos (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    oficina_id BIGINT NOT NULL,
    placa VARCHAR(10) NOT NULL,
    marca VARCHAR(80) NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    ano INTEGER NOT NULL,

    CONSTRAINT fk_veiculo_cliente
        FOREIGN KEY (cliente_id)
        REFERENCES clientes(id),

    CONSTRAINT fk_veiculo_oficina
        FOREIGN KEY (oficina_id)
        REFERENCES oficinas(id),

    CONSTRAINT uk_veiculo_oficina_placa
        UNIQUE (oficina_id, placa)
);

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

CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil VARCHAR(30) NOT NULL,
    oficina_id BIGINT,
    cliente_id BIGINT UNIQUE,

    CONSTRAINT fk_usuario_oficina
        FOREIGN KEY (oficina_id)
        REFERENCES oficinas(id),

    CONSTRAINT fk_usuario_cliente
        FOREIGN KEY (cliente_id)
        REFERENCES clientes(id),

    CONSTRAINT ck_usuario_perfil
        CHECK (perfil IN (
            'ADMIN_SISTEMA',
            'ADMIN_OFICINA',
            'CLIENTE'
        ))
);