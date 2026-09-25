-- Dados atuais
INSERT INTO clientes (id,nome,cpf_cnpj,telefone,email,cep,rua,numero,bairro,cidade,estado,ativo,created_at,updated_at) VALUES
(1,'Jeferson Oliveira Forte','82599936255','51992332502','jefersonof@hotmail.com','91910-170','Rua Victor Silva','212','Camaquã','Porto Alegre','RS',1,'2026-08-18 16:26:27','2026-08-18 16:26:27'),
(2,'Jose Tadeu','98745632155','5132665577','saojosefutebol1@gmail.com','91910-170','Rua Victor Silva','212','Camaquã','Porto Alegre','RS',1,'2026-08-25 20:41:06','2026-08-25 20:41:06');

INSERT INTO funcionarios (id,nome,telefone,email,cargo,data_admissao,salario,percentual_comissao,ativo,created_at,updated_at,tipo_vinculo,valor_diaria) VALUES
(1,'Carlos Silva','51999990000','carlos@oficina.com','MECANICO','2026-08-24',2500.00,10.00,1,'2026-08-24 13:20:07','2026-08-24 13:20:07','FIXO',NULL),
(3,'Pedro Souza',NULL,NULL,'MECANICO',NULL,NULL,NULL,1,'2026-08-31 13:45:07','2026-08-31 13:45:07','FREELANCE',200.00);

INSERT INTO pecas (id,nome,descricao,marca,valor_custo,valor_venda,estoque,estoque_minimo,ativo,created_at,updated_at) VALUES
(1,'Filtro de óleo','Filtro de óleo do motor','Tecfil',20.00,35.00,9,2,1,'2026-08-24 13:20:16','2026-08-24 13:27:04');

INSERT INTO servicos (id,nome,descricao,valor_padrao,ativo,created_at,updated_at) VALUES
(1,'Troca de óleo','Troca de óleo',80.00,1,'2026-08-18 17:39:26','2026-08-18 17:39:26');

INSERT INTO veiculos (id,cliente_id,placa,marca,modelo,ano,ativo,created_at,updated_at) VALUES
(1,1,'EMM2B23','Volkswagen','Fusca',2010,1,'2026-08-18 16:26:27','2026-09-14 14:30:21'),
(2,1,'EMM2B24','Ford','Fiesta',2010,1,'2026-08-18 16:51:01','2026-08-18 16:51:01'),
(3,2,'RPM9K12','Volkswagen','Fusca',2010,1,'2026-08-25 20:41:06','2026-09-14 14:30:21'),
(4,2,'EMN2B24','Ford','Ka',2010,1,'2026-08-25 20:42:09','2026-08-25 20:42:09');

INSERT INTO ordens_servico (id,veiculo_id,data_abertura,data_fechamento,status,problema_relatado,observacoes,valor_servicos,valor_pecas,created_at,updated_at) VALUES
(1,2,'2026-08-24 13:20:37','2026-08-24 13:27:32','FINALIZADA','Cliente solicita troca de óleo.','Veículo recebido para manutenção preventiva.',80.00,35.00,'2026-08-24 13:20:37','2026-08-24 13:27:32');

INSERT INTO ordem_servico_servicos (id,ordem_servico_id,servico_id,quantidade,valor_unitario,observacao,created_at,updated_at,funcionario_id) VALUES
(1,1,1,1,80.00,'Troca de óleo realizada.','2026-08-24 13:24:40','2026-08-24 13:24:40',1);

INSERT INTO ordem_servico_pecas (id,ordem_servico_id,peca_id,quantidade,valor_unitario,created_at,updated_at) VALUES
(1,1,1,1,35.00,'2026-08-24 13:25:04','2026-08-24 13:25:04');

INSERT INTO movimentacoes_estoque (id,peca_id,tipo,quantidade,valor_unitario,ordem_servico_id,ordem_servico_peca_id,fornecedor_id,compra_id,observacao,data_movimentacao,created_at) VALUES
(1,1,'SAIDA',1,NULL,1,1,NULL,NULL,'Peça utilizada na OS 1','2026-08-24 13:25:23','2026-08-24 13:25:23');

-- Ajuste das identities após inserções com IDs explícitos
SELECT setval(pg_get_serial_sequence('clientes','id'), COALESCE((SELECT MAX(id) FROM clientes),1), EXISTS(SELECT 1 FROM clientes));
SELECT setval(pg_get_serial_sequence('funcionarios','id'), COALESCE((SELECT MAX(id) FROM funcionarios),1), EXISTS(SELECT 1 FROM funcionarios));
SELECT setval(pg_get_serial_sequence('pecas','id'), COALESCE((SELECT MAX(id) FROM pecas),1), EXISTS(SELECT 1 FROM pecas));
SELECT setval(pg_get_serial_sequence('servicos','id'), COALESCE((SELECT MAX(id) FROM servicos),1), EXISTS(SELECT 1 FROM servicos));
SELECT setval(pg_get_serial_sequence('veiculos','id'), COALESCE((SELECT MAX(id) FROM veiculos),1), EXISTS(SELECT 1 FROM veiculos));
SELECT setval(pg_get_serial_sequence('ordens_servico','id'), COALESCE((SELECT MAX(id) FROM ordens_servico),1), EXISTS(SELECT 1 FROM ordens_servico));
SELECT setval(pg_get_serial_sequence('ordem_servico_servicos','id'), COALESCE((SELECT MAX(id) FROM ordem_servico_servicos),1), EXISTS(SELECT 1 FROM ordem_servico_servicos));
SELECT setval(pg_get_serial_sequence('ordem_servico_pecas','id'), COALESCE((SELECT MAX(id) FROM ordem_servico_pecas),1), EXISTS(SELECT 1 FROM ordem_servico_pecas));
SELECT setval(pg_get_serial_sequence('movimentacoes_estoque','id'), COALESCE((SELECT MAX(id) FROM movimentacoes_estoque),1), EXISTS(SELECT 1 FROM movimentacoes_estoque));