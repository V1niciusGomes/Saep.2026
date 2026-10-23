-- Clínica Vet Aurora — SAEP Final (Semana 2)
-- 5 tabelas em 3FN, SHA2, FK, AES no CPF. Banco completo + dados de teste.
-- Rodar no Workbench. Depois: refresh e conferir a contagem no final.

CREATE DATABASE IF NOT EXISTS saep_db
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE saep_db;

-- apaga na ordem certa (filhas antes das mães)
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS consulta;
DROP TABLE IF EXISTS pet;
DROP TABLE IF EXISTS veterinario;
DROP TABLE IF EXISTS tutor;
DROP TABLE IF EXISTS usuario;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE usuario (
  id_usuario INT AUTO_INCREMENT PRIMARY KEY,
  login      VARCHAR(40)  NOT NULL UNIQUE,
  senha      CHAR(64)     NOT NULL,
  nome       VARCHAR(80)  NOT NULL
);

CREATE TABLE tutor (
  id_tutor  INT AUTO_INCREMENT PRIMARY KEY,
  nome      VARCHAR(80)  NOT NULL,
  telefone  VARCHAR(20),
  email     VARCHAR(80),
  cpf       VARBINARY(255)
);

CREATE TABLE pet (
  id_pet           INT AUTO_INCREMENT PRIMARY KEY,
  nome             VARCHAR(60) NOT NULL,
  especie          VARCHAR(40) NOT NULL,
  raca             VARCHAR(40),
  data_nascimento  DATE,
  id_tutor         INT NOT NULL,
  CONSTRAINT fk_pet_tutor FOREIGN KEY (id_tutor) REFERENCES tutor(id_tutor)
);

CREATE TABLE veterinario (
  id_veterinario INT AUTO_INCREMENT PRIMARY KEY,
  nome           VARCHAR(80) NOT NULL,
  crmv           VARCHAR(20) NOT NULL UNIQUE,
  especialidade  VARCHAR(60)
);

CREATE TABLE consulta (
  id_consulta    INT AUTO_INCREMENT PRIMARY KEY,
  id_pet         INT NOT NULL,
  id_veterinario INT NOT NULL,
  data_hora      DATETIME NOT NULL,
  motivo         VARCHAR(120),
  observacao     TEXT,
  CONSTRAINT fk_consulta_pet FOREIGN KEY (id_pet) REFERENCES pet(id_pet),
  CONSTRAINT fk_consulta_vet FOREIGN KEY (id_veterinario) REFERENCES veterinario(id_veterinario)
);

-- senha: 123456 (SHA2 256). O SHA2 do MySQL devolve hex minúsculo;
-- o UPPER deixa as 64 letras maiúsculas, igual ao HashUtil.java.
INSERT INTO usuario (login, senha, nome) VALUES
  ('admin', UPPER(SHA2('123456', 256)), 'Administrador da Clínica');

-- chave AES da sala: SENAI2026
-- para ler: CAST(AES_DECRYPT(cpf, 'SENAI2026') AS CHAR)
INSERT INTO tutor (nome, telefone, email, cpf) VALUES
  ('Ana Souza',    '48999990001', 'ana.souza@email.com',    AES_ENCRYPT('11122233344', 'SENAI2026')),
  ('Bruno Lima',   '48999990002', 'bruno.lima@email.com',   AES_ENCRYPT('22233344455', 'SENAI2026')),
  ('Carla Mendes', '48999990003', 'carla.mendes@email.com', AES_ENCRYPT('33344455566', 'SENAI2026')),
  ('João Mandela', '48999990004', 'joao.mandela@email.com', AES_ENCRYPT('44455566677', 'SENAI2026'));

INSERT INTO pet (nome, especie, raca, data_nascimento, id_tutor) VALUES
  ('Rex',  'Cão',  'Labrador',  '2020-03-15', 1),
  ('Mimi', 'Gato', 'Siamês',    '2021-07-22', 2),
  ('Bolt', 'Cão',  'Vira-lata', '2019-11-08', 3),
  ('Nina', 'Gato', 'Persa',     '2022-01-30', 4);

INSERT INTO veterinario (nome, crmv, especialidade) VALUES
  ('Dra. Helena Costa', 'SC-12345', 'Clínica Geral'),
  ('Dr. Paulo Nunes',   'SC-23456', 'Cirurgia'),
  ('Dra. Lívia Ramos',  'SC-34567', 'Dermatologia');

INSERT INTO consulta (id_pet, id_veterinario, data_hora, motivo, observacao) VALUES
  (1, 1, '2026-10-20 09:00:00', 'Vacina anual', 'Animal saudável.'),
  (2, 3, '2026-10-21 14:30:00', 'Coceira',      'Receitou pomada.'),
  (3, 2, '2026-10-22 11:00:00', 'Castração',    'Agendada cirurgia.'),
  (4, 1, '2026-10-23 16:00:00', 'Check-up',     'Retorno em 30 dias.');

-- conferência: 1 usuario, 4 tutores, 4 pets, 3 vets e 4 consultas
SELECT 'usuario' AS tabela, COUNT(*) AS qtd FROM usuario
UNION ALL SELECT 'tutor',       COUNT(*) FROM tutor
UNION ALL SELECT 'pet',         COUNT(*) FROM pet
UNION ALL SELECT 'veterinario', COUNT(*) FROM veterinario
UNION ALL SELECT 'consulta',    COUNT(*) FROM consulta;
