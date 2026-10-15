-- Clínica Vet Aurora — Semana 1
-- Rodar no MySQL Workbench (uma vez). Depois: refresh e conferir 3 tutores + 1 usuário.

CREATE DATABASE IF NOT EXISTS saep_db
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE saep_db;

DROP TABLE IF EXISTS tutor;
DROP TABLE IF EXISTS usuario;

CREATE TABLE usuario (
  id_usuario INT AUTO_INCREMENT PRIMARY KEY,
  login      VARCHAR(40) NOT NULL UNIQUE,
  senha      CHAR(64)    NOT NULL,
  nome       VARCHAR(80) NOT NULL
);

CREATE TABLE tutor (
  id_tutor INT AUTO_INCREMENT PRIMARY KEY,
  nome     VARCHAR(80) NOT NULL,
  telefone VARCHAR(20),
  email    VARCHAR(80)
);

-- senha padrão da sala: 123456  (SHA2 256, igual ao HashUtil.java)
INSERT INTO usuario (login, senha, nome) VALUES
  ('admin', SHA2('123456', 256), 'Administrador da Clínica');

INSERT INTO tutor (nome, telefone, email) VALUES
  ('Ana Souza',    '48999990001', 'ana.souza@email.com'),
  ('Bruno Lima',   '48999990002', 'bruno.lima@email.com'),
  ('Carla Mendes', '48999990003', 'carla.mendes@email.com');

SELECT id_usuario, login, nome FROM usuario;
SELECT * FROM tutor;
