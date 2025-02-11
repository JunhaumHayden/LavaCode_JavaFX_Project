CREATE DATABASE IF NOT EXISTS db_lava_jato;

USE db_lava_jato;

-- Tabela de marcas
CREATE TABLE IF NOT EXISTS marca (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE
    ) ENGINE=InnoDB;


-- Tabela de motor
CREATE TABLE IF NOT EXISTS motor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    potencia INT NOT NULL,
    tipo_combustivel ENUM('GASOLINA', 'ETANOL', 'FLEX', 'DIESEL', 'GNV', 'OUTRO') NOT NULL
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS modelo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    id_marca INT NOT NULL,
    id_motor INT NOT NULL,
    categoria ENUM('PEQUENO', 'MEDIO', 'GRANDE', 'MOTO', 'PADRAO') NOT NULL,
    FOREIGN KEY (id_marca) REFERENCES marca(id),
    FOREIGN KEY (id_motor) REFERENCES motor(id) ON DELETE CASCADE
    )ENGINE=InnoDB;

-- Tabela de cores
CREATE TABLE IF NOT EXISTS cor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE
    ) ENGINE=InnoDB;

-- Tabelas de clientes
    -- As chaves primárias das tabelas filhas serão iguais às da tabela pai para manter a integridade referencial.
-- Tabela Cliente (entidade pai)
CREATE TABLE cliente (
                         id INT NOT NULL AUTO_INCREMENT,
                         nome VARCHAR(100) NOT NULL,
                         telefone VARCHAR(20),
                         email VARCHAR(100),
                         data_cadastro DATE NOT NULL,
                         CONSTRAINT pk_cliente PRIMARY KEY (id)
) ENGINE=InnoDB;

-- Tabela PessoaFisica (entidade filha)
CREATE TABLE pessoa_fisica (
                               id_cliente INT NOT NULL,
                               data_nascimento DATE NOT NULL,
                               cpf VARCHAR(14) NOT NULL,
                               CONSTRAINT pk_pessoa_fisica PRIMARY KEY (id_cliente),
                               CONSTRAINT fk_pessoa_fisica_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id)
                                   ON DELETE CASCADE
                                   ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Tabela PessoaJuridica (entidade filha)
CREATE TABLE pessoa_juridica (
                                 id_cliente INT NOT NULL,
                                 inscricao_estadual VARCHAR(20),
                                 cpf_cnpj VARCHAR(18) NOT NULL,
                                 CONSTRAINT pk_pessoa_juridica PRIMARY KEY (id_cliente),
                                 CONSTRAINT fk_pessoa_juridica_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id)
                                     ON DELETE CASCADE
                                     ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Tabela PontuacaoFidelidade (associada ao cliente)
CREATE TABLE IF NOT EXISTS pontuacao_fidelidade (
                                      id_cliente INT NOT NULL,
                                      pontos INT NOT NULL DEFAULT 0,
                                      CONSTRAINT pk_pontuacao_fidelidade PRIMARY KEY (id_cliente),
                                      CONSTRAINT fk_pontuacao_fidelidade_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id)
                                          ON DELETE CASCADE
                                          ON UPDATE CASCADE
) ENGINE=InnoDB;


-- tabela de veiculos
CREATE TABLE IF NOT EXISTS veiculo (
                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                    placa VARCHAR(20) NOT NULL UNIQUE,
                                    observacoes TEXT,
                                    id_cor INT NOT NULL,
                                    id_modelo INT NOT NULL,
                                    id_cliente INT NOT NULL,
                                    CONSTRAINT fk_veiculo_cor FOREIGN KEY (id_cor) REFERENCES cor(id),
                                    CONSTRAINT fk_veiculo_modelo FOREIGN KEY (id_modelo) REFERENCES modelo(id),
                                    CONSTRAINT fk_veiculo_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id)
                                        ON DELETE CASCADE
                                        ON UPDATE CASCADE
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS servico (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       descricao VARCHAR(255) NOT NULL,
                                       valor DECIMAL(10, 2) NOT NULL,
                                       categoria ENUM('PEQUENO', 'MEDIO', 'GRANDE', 'MOTO', 'PADRAO') NOT NULL,
                                       pontos INT
) ENGINE=InnoDB;

-- Tabela para Ordem de Serviço
CREATE TABLE IF NOT EXISTS ordem_servico (
                                id INT AUTO_INCREMENT,
                                numero VARCHAR(255) NOT NULL UNIQUE,
                                total DECIMAL(10, 2),
                                desconto DECIMAL(10, 2) NOT NULL,
                                agenda DATE,
                                id_veiculo INT NOT NULL,
                                status ENUM('ABERTA', 'FECHADA', 'CANCELADA') NOT NULL DEFAULT 'ABERTA',
                                CONSTRAINT pk_os PRIMARY KEY(id),
                                CONSTRAINT fk_os_veiculo FOREIGN KEY (id_veiculo) REFERENCES veiculo(id)
) ENGINE=InnoDB;

-- Tabela para itens da Ordem de Serviço
CREATE TABLE IF NOT EXISTS item_os (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        observacao TEXT,
                        valor_servico DECIMAL(10, 2) NOT NULL,
                        id_servico INT NOT NULL,
                        id_ordemServico INT NOT NULL,
                        FOREIGN KEY (id_servico) REFERENCES Servico(id),
                        FOREIGN KEY (id_ordemServico) REFERENCES ordem_servico(id)
                            ON DELETE CASCADE
) ENGINE=InnoDB;

-- ##########   Inserçao de valores  #########
-- Marcas
INSERT INTO marca (nome) VALUES
                              ('Honda'),
                              ('Toyota'),
                              ('Nissan'),
                              ('Ford'),
                              ('Volks');
-- Motor
INSERT INTO motor (potencia, tipo_combustivel) VALUES (100, 'GASOLINA');
INSERT INTO motor (potencia, tipo_combustivel) VALUES (120, 'ETANOL');
INSERT INTO motor (potencia, tipo_combustivel) VALUES (140, 'FLEX');
INSERT INTO motor (potencia, tipo_combustivel) VALUES (180, 'DIESEL');
INSERT INTO motor (potencia, tipo_combustivel) VALUES (90, 'GNV');

-- Inserir dados na tabela modelo
INSERT INTO modelo (descricao, id_marca, id_motor, categoria)
VALUES ('GOL', 5, 1, 'PEQUENO');
INSERT INTO modelo (descricao, id_marca, id_motor, categoria)
VALUES ('CIVIC', 1, 3, 'MEDIO');
INSERT INTO modelo (descricao, id_marca, id_motor, categoria)
VALUES ('FRONTIER', 3, 4, 'GRANDE');

-- Cores
INSERT INTO cor (nome) VALUES
                             ('Azul'),
                             ('Preto'),
                             ('Vermelho'),
                             ('Prata'),
                             ('Branco');

-- Inserindo dados na tabela servicos
INSERT INTO servico (descricao, valor, categoria, pontos)
VALUES
    ('Lavagem simples', 50.00, 'PEQUENO', 10),
    ('Lavagem completa', 80.00, 'MEDIO', 20),
    ('Polimento externo', 150.00, 'GRANDE', 30),
    ('Lavagem rápida', 30.00, 'MOTO', 5),
    ('Higienização interna', 100.00, 'PADRAO', 15),
    ('Lavagem completa premium', 120.00, 'GRANDE', 25),
    ('Enceramento', 70.00, 'PEQUENO', 15),
    ('Lavagem detalhada', 90.00, 'MEDIO', 20),
    ('Aspiração',50.00,'PEQUENO',30),
    ('Lavagem Externa', 40.00,'PEQUENO',30),
    ('Lavagem Motor',40.00,'PEQUENO',40),
    ('Cera Liquida', 25.00,'PEQUENO',10),
    ('Cera Pastosa',40.00, 'PEQUENO',30),
    ('Pretinho Pneu',50.00,'PEQUENO',10),
    ('Secagem',15.00,'PEQUENO',25) ;


-- Inserindo dados na tabela cliente
INSERT INTO cliente (nome, telefone, email, data_cadastro)
VALUES
    ('Ana Amalia', '99999-1111', 'ana@email.com', '2024-12-01');
-- Inserindo dados na tabela pessoa_fisica
INSERT INTO pessoa_fisica (id_cliente, data_nascimento, cpf)
VALUES
    ((SELECT max(id) FROM cliente), '1990-03-15', '123.456.789-00');

-- Inserindo dados na tabela cliente
INSERT INTO cliente (nome, telefone, email, data_cadastro)
VALUES
    ('Bia Bernardes', '88888-2222', 'bia.bernardes@email.com', '2024-12-05');
-- Inserindo dados na tabela pessoa_fisica
INSERT INTO pessoa_fisica (id_cliente, data_nascimento, cpf)
VALUES
    ((SELECT max(id) FROM cliente), '1985-07-20', '987.654.321-00');

-- Inserindo dados na tabela cliente
INSERT INTO cliente (nome, telefone, email, data_cadastro)
VALUES
    ('Zaza Enterprise', '77777-3333', 'contato@zaza.com', '2024-12-10');
-- Inserindo dados na tabela pessoa_juridica
INSERT INTO pessoa_juridica (id_cliente, inscricao_estadual, cpf_cnpj)
VALUES
    ((SELECT max(id) FROM cliente), '123456789', '12.345.678/0001-00');


-- Inserindo dados na tabela pontuacao_fidelidade
INSERT INTO pontuacao_fidelidade (id_cliente, pontos)
VALUES
    (1, 100),
    (2, 200),
    (3, 500);

-- Supondo que as tabelas cor, modelo e cliente já tenham dados inseridos

-- Inserindo dados na tabela veiculo
INSERT INTO veiculo (placa, observacoes, id_cor, id_modelo, id_cliente)
VALUES
    ('AAA1111', 'Sem observações.', 1, 1, 1),
    ('BBB2222', 'Cliente solicita cuidado extra ao estacionar.', 2, 4, 2),
    ('ZZZ1010', 'Veículo novo, ainda em garantia.', 3, 6, 3);

INSERT INTO ordem_servico (numero, total, desconto, agenda, id_veiculo, status)
VALUES
    (10001, 250.00, 10.0, '2025-01-22', 1, 'ABERTA'),
    (10002, 500.00, 15.0, '2025-01-23', 2, 'FECHADA'),
    (10003, 150.00, 5.0, '2025-01-24', 3, 'CANCELADA');

INSERT INTO item_os (observacao, valor_servico, id_servico, id_ordemServico)
VALUES
    ('Troca de óleo completa', 100.00, 1, 1),
    ('Lavagem interna e externa', 50.00, 2, 2),
    ('Troca de filtro de ar', 120.00, 3, 3),
    ('Alinhamento e balanceamento', 80.00, 4, 3),
    ('Higienização do ar-condicionado', 150.00, 5, 3);


-- Consultar todos os clientes
SELECT * FROM cliente;

-- Consultar pessoas físicas com seus dados
SELECT c.nome, c.telefone, c.email, c.data_cadastro, pf.data_nascimento, pf.cpf
FROM cliente c
         JOIN pessoa_fisica pf ON c.id = pf.id_cliente;

-- Consultar pessoas jurídicas com seus dados
SELECT c.nome, c.telefone, c.email, c.data_cadastro, pj.inscricao_estadual, pj.cpf_cnpj
FROM cliente c
         JOIN pessoa_juridica pj ON c.id = pj.id_cliente;

-- Consultar saldo de pontuação dos clientes
SELECT c.nome, pf.pontos
FROM cliente c
         JOIN pontuacao_fidelidade pf ON c.id = pf.id_cliente;

SELECT * FROM cliente c LEFT JOIN pessoa_fisica pf on pf.id_cliente = c.id LEFT JOIN pessoa_juridica pj on pj.id_cliente = c.id WHERE id=1;

SELECT * FROM cliente c LEFT JOIN pessoa_fisica pf on pf.id_cliente = c.id LEFT JOIN pessoa_juridica pj on pj.id_cliente = c.id