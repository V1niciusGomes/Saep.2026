-- ============================================================================
-- ASSISTÊNCIA TÉCNICA — Banco de dados
-- Construído a partir do esqueleto "projeto-base", aplicando seus 3 moldes:
--   molde Item        (cadastro simples)   -> cliente, tecnico, peca
--   molde ItemFilho   (pertence a 1 pai)   -> aparelho (pertence a um cliente)
--   molde ItemLigacao (liga vários pais)   -> ordem_servico, ordem_peca
--
-- COMO RODAR: no MySQL Workbench, abra este arquivo e clique no raio (Execute).
-- ATENÇÃO: começa com DROP TABLE. Recria o banco do zero (apaga dados).
-- ============================================================================

CREATE DATABASE IF NOT EXISTS assistencia_db
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE assistencia_db;

-- Apaga na ordem certa: filhas/ligações antes das mães.
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS ordem_peca;      -- liga ordem_servico + peca
DROP TABLE IF EXISTS ordem_servico;   -- liga cliente + tecnico + aparelho
DROP TABLE IF EXISTS aparelho;        -- pertence a cliente
DROP TABLE IF EXISTS peca;
DROP TABLE IF EXISTS tecnico;
DROP TABLE IF EXISTS cliente;
DROP TABLE IF EXISTS usuario;
SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------------------------------------------------------
-- usuario: quem faz login (senha guardada como hash SHA-256).
-- ----------------------------------------------------------------------------
CREATE TABLE usuario (
  id_usuario INT AUTO_INCREMENT PRIMARY KEY,
  login      VARCHAR(40)  NOT NULL UNIQUE,
  senha      CHAR(64)     NOT NULL,
  nome       VARCHAR(80)  NOT NULL
);

-- ----------------------------------------------------------------------------
-- cliente (molde Item): cadastro simples e independente.
-- ----------------------------------------------------------------------------
CREATE TABLE cliente (
  id_cliente INT AUTO_INCREMENT PRIMARY KEY,
  nome       VARCHAR(80)  NOT NULL,
  telefone   VARCHAR(20),
  email      VARCHAR(80),
  cpf        VARCHAR(14)  UNIQUE          -- não pode repetir
);

-- ----------------------------------------------------------------------------
-- tecnico (molde Item): cadastro simples e independente.
-- ----------------------------------------------------------------------------
CREATE TABLE tecnico (
  id_tecnico    INT AUTO_INCREMENT PRIMARY KEY,
  nome          VARCHAR(80) NOT NULL,
  especialidade VARCHAR(60),
  telefone      VARCHAR(20)
);

-- ----------------------------------------------------------------------------
-- peca (molde Item): catálogo/estoque de peças. Cadastro simples.
-- ----------------------------------------------------------------------------
CREATE TABLE peca (
  id_peca      INT AUTO_INCREMENT PRIMARY KEY,
  nome         VARCHAR(80)  NOT NULL,
  codigo       VARCHAR(40)  NOT NULL UNIQUE,  -- código da peça (não repete)
  preco        DECIMAL(10,2),
  estoque      INT NOT NULL DEFAULT 0
);

-- ----------------------------------------------------------------------------
-- aparelho (molde ItemFilho): PERTENCE a um cliente (histórico de aparelhos).
-- ----------------------------------------------------------------------------
CREATE TABLE aparelho (
  id_aparelho    INT AUTO_INCREMENT PRIMARY KEY,
  tipo           VARCHAR(40) NOT NULL,   -- ex.: Celular, Notebook, TV
  marca          VARCHAR(40),
  modelo         VARCHAR(60),
  numero_serie   VARCHAR(60),
  id_cliente     INT NOT NULL,           -- FK: a qual cliente o aparelho pertence
  CONSTRAINT fk_aparelho_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
);

-- ----------------------------------------------------------------------------
-- ordem_servico (molde ItemLigacao): a ENTIDADE CENTRAL do sistema.
-- Liga TRÊS pais (cliente + tecnico + aparelho) e guarda dados próprios.
-- ----------------------------------------------------------------------------
CREATE TABLE ordem_servico (
  id_ordem_servico INT AUTO_INCREMENT PRIMARY KEY,
  id_cliente       INT NOT NULL,         -- FK 1 -> dono
  id_tecnico       INT NOT NULL,         -- FK 2 -> responsável
  id_aparelho      INT NOT NULL,         -- FK 3 -> o que será consertado
  data_abertura    DATETIME NOT NULL,
  status           VARCHAR(40),          -- Aberta / Em andamento / Concluída / Cancelada
  valor_mao_obra   DECIMAL(10,2),        -- valor da mão de obra (peças somam à parte)
  defeito_relatado VARCHAR(255),
  CONSTRAINT fk_os_cliente  FOREIGN KEY (id_cliente)  REFERENCES cliente(id_cliente),
  CONSTRAINT fk_os_tecnico  FOREIGN KEY (id_tecnico)  REFERENCES tecnico(id_tecnico),
  CONSTRAINT fk_os_aparelho FOREIGN KEY (id_aparelho) REFERENCES aparelho(id_aparelho)
);

-- ----------------------------------------------------------------------------
-- ordem_peca (molde ItemLigacao / muitos-para-muitos):
-- quais PEÇAS foram usadas em cada ORDEM DE SERVIÇO, e em qual quantidade.
-- ----------------------------------------------------------------------------
CREATE TABLE ordem_peca (
  id_ordem_peca    INT AUTO_INCREMENT PRIMARY KEY,
  id_ordem_servico INT NOT NULL,         -- FK 1 -> a OS
  id_peca          INT NOT NULL,         -- FK 2 -> a peça usada
  quantidade       INT NOT NULL DEFAULT 1,
  CONSTRAINT fk_op_os   FOREIGN KEY (id_ordem_servico) REFERENCES ordem_servico(id_ordem_servico),
  CONSTRAINT fk_op_peca FOREIGN KEY (id_peca)          REFERENCES peca(id_peca)
);

-- ============================================================================
-- DADOS DE TESTE
-- ============================================================================

-- Login: admin / 123456
INSERT INTO usuario (login, senha, nome) VALUES
  ('admin', UPPER(SHA2('123456', 256)), 'Administrador');

INSERT INTO cliente (nome, telefone, email, cpf) VALUES
  ('Ana Souza',    '48999990001', 'ana.souza@email.com',    '111.222.333-44'),
  ('Bruno Lima',   '48999990002', 'bruno.lima@email.com',   '222.333.444-55'),
  ('Carla Mendes', '48999990003', 'carla.mendes@email.com', '333.444.555-66');

INSERT INTO tecnico (nome, especialidade, telefone) VALUES
  ('Paulo Nunes',  'Celulares',  '48988880001'),
  ('Lívia Ramos',  'Notebooks',  '48988880002');

INSERT INTO peca (nome, codigo, preco, estoque) VALUES
  ('Tela Celular XYZ',    'PC-001', 180.00, 10),
  ('Bateria Notebook',    'PC-002', 220.00,  5),
  ('Conector de Carga',   'PC-003',  45.00, 30);

-- Aparelhos: o último número é o id_cliente (a FK).
INSERT INTO aparelho (tipo, marca, modelo, numero_serie, id_cliente) VALUES
  ('Celular',  'Marca A', 'Modelo X', 'SN-0001', 1),
  ('Notebook', 'Marca B', 'Modelo Y', 'SN-0002', 2),
  ('Celular',  'Marca A', 'Modelo Z', 'SN-0003', 3);

-- Ordens de serviço: (id_cliente, id_tecnico, id_aparelho, ...).
INSERT INTO ordem_servico (id_cliente, id_tecnico, id_aparelho, data_abertura, status, valor_mao_obra, defeito_relatado) VALUES
  (1, 1, 1, '2026-02-01 09:00:00', 'Aberta',       80.00, 'Tela quebrada'),
  (2, 2, 2, '2026-02-02 14:30:00', 'Em andamento', 120.00, 'Não liga'),
  (3, 1, 3, '2026-02-03 11:00:00', 'Concluída',    60.00, 'Não carrega');

-- Peças usadas nas OS: (id_ordem_servico, id_peca, quantidade).
INSERT INTO ordem_peca (id_ordem_servico, id_peca, quantidade) VALUES
  (1, 1, 1),   -- OS 1 usou 1 tela
  (2, 2, 1),   -- OS 2 usou 1 bateria
  (3, 3, 2);   -- OS 3 usou 2 conectores

-- Conferência.
SELECT 'usuario'       AS tabela, COUNT(*) AS qtd FROM usuario
UNION ALL SELECT 'cliente',       COUNT(*) FROM cliente
UNION ALL SELECT 'tecnico',       COUNT(*) FROM tecnico
UNION ALL SELECT 'peca',          COUNT(*) FROM peca
UNION ALL SELECT 'aparelho',      COUNT(*) FROM aparelho
UNION ALL SELECT 'ordem_servico', COUNT(*) FROM ordem_servico
UNION ALL SELECT 'ordem_peca',    COUNT(*) FROM ordem_peca;
