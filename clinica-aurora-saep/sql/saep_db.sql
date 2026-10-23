-- ============================================================================
-- Clínica Vet Aurora — SAEP Final (Semana 2)
-- Script ÚNICO: cria o banco, as 5 tabelas e insere os dados de teste.
--
-- COMO RODAR: no MySQL Workbench, abra este arquivo e clique no raio (Execute).
-- No fim, a última consulta mostra a contagem de linhas de cada tabela.
--
-- ATENÇÃO: este script começa com DROP TABLE. Ele RECRIA o banco do zero,
-- então apaga dados existentes. Use para montar/remontar o banco limpo.
--
-- CONCEITOS QUE ELE EXERCITA: 3FN, chave primária (PK), chave estrangeira (FK),
-- hash de senha (SHA2) e criptografia de dado sensível (AES).
--
-- [MOLDE] Para adaptar a outro sistema, troque: o nome do banco (saep_db),
--         os nomes das tabelas/colunas e os INSERT de dados. A ESTRUTURA
--         (PK, FK, SHA2, AES, contagem) serve para qualquer cadastro.
-- ============================================================================

-- Cria o banco se ainda não existir, com acentuação correta (utf8mb4).
CREATE DATABASE IF NOT EXISTS saep_db
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- A partir daqui, todos os comandos valem para este banco.
USE saep_db;

-- Apaga as tabelas na ordem certa: as "filhas" (que têm FK) antes das "mães".
-- FOREIGN_KEY_CHECKS=0 desliga a checagem para o DROP não travar.
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS consulta;     -- filha de pet e veterinario
DROP TABLE IF EXISTS pet;          -- filha de tutor
DROP TABLE IF EXISTS veterinario;
DROP TABLE IF EXISTS tutor;
DROP TABLE IF EXISTS usuario;
SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------------------------------------------------------
-- usuario: quem faz login. A senha é guardada como HASH (nunca em texto puro).
-- ----------------------------------------------------------------------------
CREATE TABLE usuario (
  id_usuario INT AUTO_INCREMENT PRIMARY KEY, -- PK: o banco numera sozinho
  login      VARCHAR(40)  NOT NULL UNIQUE,   -- UNIQUE: não pode haver login repetido
  senha      CHAR(64)     NOT NULL,          -- SHA-256 tem sempre 64 caracteres
  nome       VARCHAR(80)  NOT NULL
);

-- ----------------------------------------------------------------------------
-- tutor: dono do pet. O cpf é guardado CRIPTOGRAFADO (tipo VARBINARY).
-- [MOLDE] molde de cadastro simples com um dado sensível.
-- ----------------------------------------------------------------------------
CREATE TABLE tutor (
  id_tutor  INT AUTO_INCREMENT PRIMARY KEY,
  nome      VARCHAR(80)  NOT NULL,
  telefone  VARCHAR(20),
  email     VARCHAR(80),
  cpf       VARBINARY(255)  -- binário porque o AES_ENCRYPT devolve bytes
);

-- ----------------------------------------------------------------------------
-- pet: PERTENCE a um tutor. A coluna id_tutor é a FK que amarra os dois.
-- [MOLDE] molde de "entidade filha" (pertence a outra).
-- ----------------------------------------------------------------------------
CREATE TABLE pet (
  id_pet           INT AUTO_INCREMENT PRIMARY KEY,
  nome             VARCHAR(60) NOT NULL,
  especie          VARCHAR(40) NOT NULL,
  raca             VARCHAR(40),
  data_nascimento  DATE,
  id_tutor         INT NOT NULL,  -- FK obrigatória: todo pet tem dono
  -- A FK garante que id_tutor exista na tabela tutor. Pet órfão é recusado.
  CONSTRAINT fk_pet_tutor FOREIGN KEY (id_tutor) REFERENCES tutor(id_tutor)
);

-- ----------------------------------------------------------------------------
-- veterinario: cadastro independente. crmv é ÚNICO (não repete).
-- ----------------------------------------------------------------------------
CREATE TABLE veterinario (
  id_veterinario INT AUTO_INCREMENT PRIMARY KEY,
  nome           VARCHAR(80) NOT NULL,
  crmv           VARCHAR(20) NOT NULL UNIQUE, -- registro profissional, sem repetição
  especialidade  VARCHAR(60)
);

-- ----------------------------------------------------------------------------
-- consulta: LIGA pet e veterinario. Tem DUAS FKs.
-- [MOLDE] molde de "entidade de cruzamento" (junta duas outras).
-- ----------------------------------------------------------------------------
CREATE TABLE consulta (
  id_consulta    INT AUTO_INCREMENT PRIMARY KEY,
  id_pet         INT NOT NULL,       -- FK 1
  id_veterinario INT NOT NULL,       -- FK 2
  data_hora      DATETIME NOT NULL,  -- guarda data E hora
  motivo         VARCHAR(120),
  observacao     TEXT,
  CONSTRAINT fk_consulta_pet FOREIGN KEY (id_pet) REFERENCES pet(id_pet),
  CONSTRAINT fk_consulta_vet FOREIGN KEY (id_veterinario) REFERENCES veterinario(id_veterinario)
);

-- ============================================================================
-- DADOS DE TESTE
-- ============================================================================

-- Usuário admin. UPPER(SHA2('123456',256)) gera o hash da senha em MAIÚSCULO,
-- idêntico ao que o HashUtil.java gera no Java. Senha de acesso: 123456.
INSERT INTO usuario (login, senha, nome) VALUES
  ('admin', UPPER(SHA2('123456', 256)), 'Administrador da Clínica');

-- Tutores. O CPF entra com AES_ENCRYPT(valor, 'SENAI2026').
-- Para LER depois: CAST(AES_DECRYPT(cpf, 'SENAI2026') AS CHAR).
-- [MOLDE] 'SENAI2026' é a chave da sala; troque pela sua se for o caso.
INSERT INTO tutor (nome, telefone, email, cpf) VALUES
  ('Ana Souza',    '48999990001', 'ana.souza@email.com',    AES_ENCRYPT('11122233344', 'SENAI2026')),
  ('Bruno Lima',   '48999990002', 'bruno.lima@email.com',   AES_ENCRYPT('22233344455', 'SENAI2026')),
  ('Carla Mendes', '48999990003', 'carla.mendes@email.com', AES_ENCRYPT('33344455566', 'SENAI2026')),
  ('João Mandela', '48999990004', 'joao.mandela@email.com', AES_ENCRYPT('44455566677', 'SENAI2026'));

-- Pets. O último número é o id_tutor (a FK): liga cada pet ao seu dono.
INSERT INTO pet (nome, especie, raca, data_nascimento, id_tutor) VALUES
  ('Rex',  'Cão',  'Labrador',  '2020-03-15', 1),
  ('Mimi', 'Gato', 'Siamês',    '2021-07-22', 2),
  ('Bolt', 'Cão',  'Vira-lata', '2019-11-08', 3),
  ('Nina', 'Gato', 'Persa',     '2022-01-30', 4);

-- Veterinários.
INSERT INTO veterinario (nome, crmv, especialidade) VALUES
  ('Dra. Helena Costa', 'SC-12345', 'Clínica Geral'),
  ('Dr. Paulo Nunes',   'SC-23456', 'Cirurgia'),
  ('Dra. Lívia Ramos',  'SC-34567', 'Dermatologia');

-- Consultas. Os dois primeiros números são as FKs (id_pet, id_veterinario).
INSERT INTO consulta (id_pet, id_veterinario, data_hora, motivo, observacao) VALUES
  (1, 1, '2026-10-20 09:00:00', 'Vacina anual', 'Animal saudável.'),
  (2, 3, '2026-10-21 14:30:00', 'Coceira',      'Receitou pomada.'),
  (3, 2, '2026-10-22 11:00:00', 'Castração',    'Agendada cirurgia.'),
  (4, 1, '2026-10-23 16:00:00', 'Check-up',     'Retorno em 30 dias.');

-- Conferência: mostra quantas linhas ficaram em cada tabela.
-- Esperado: usuario 1, tutor 4, pet 4, veterinario 3, consulta 4.
SELECT 'usuario' AS tabela, COUNT(*) AS qtd FROM usuario
UNION ALL SELECT 'tutor',       COUNT(*) FROM tutor
UNION ALL SELECT 'pet',         COUNT(*) FROM pet
UNION ALL SELECT 'veterinario', COUNT(*) FROM veterinario
UNION ALL SELECT 'consulta',    COUNT(*) FROM consulta;
