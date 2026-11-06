-- ============================================================================
-- PROJETO BASE — Esqueleto de banco de dados (molde reutilizável)
--
-- O QUE ESTE SCRIPT FAZ: cria o banco, as 3 tabelas de exemplo e insere
-- alguns dados de teste. No fim, conta as linhas de cada tabela.
--
-- COMO RODAR: no MySQL Workbench, abra este arquivo e clique no raio (Execute).
--
-- ATENÇÃO: começa com DROP TABLE. Ele RECRIA o banco do zero (apaga dados
-- existentes). Use para montar/remontar o banco limpo.
--
-- AS 3 TABELAS SÃO MOLDES:
--   usuario     -> molde de LOGIN (senha guardada como hash SHA-256)
--   item        -> molde de CADASTRO SIMPLES (a entidade principal)
--   item_filho  -> molde de ENTIDADE QUE PERTENCE A OUTRA (tem FK para item)
--
-- [MOLDE] Para adaptar a outro sistema (ex.: assistência técnica, loja...),
--         troque: o nome do banco, os nomes das tabelas/colunas e os INSERT.
--         A ESTRUTURA (PK, FK, UNIQUE, hash de senha, contagem) serve para
--         qualquer cadastro. Exemplos de renomeação:
--           item       -> cliente | produto | ordem_servico | funcionario
--           item_filho -> telefone | item_do_pedido | servico_executado
-- ============================================================================

-- Cria o banco se ainda não existir, com acentuação correta (utf8mb4).
CREATE DATABASE IF NOT EXISTS projeto_base_db
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- A partir daqui, todos os comandos valem para este banco.
USE projeto_base_db;

-- Apaga as tabelas na ordem certa: as "filhas" (que têm FK) antes das "mães".
-- FOREIGN_KEY_CHECKS=0 desliga a checagem para o DROP não travar.
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS item_filho;   -- filha de item
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS usuario;
SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------------------------------------------------------
-- usuario: quem faz login. A senha é guardada como HASH (nunca em texto puro).
-- [MOLDE] Tabela de login genérica. Costuma ficar igual entre projetos.
-- ----------------------------------------------------------------------------
CREATE TABLE usuario (
  id_usuario INT AUTO_INCREMENT PRIMARY KEY, -- PK: o banco numera sozinho
  login      VARCHAR(40)  NOT NULL UNIQUE,   -- UNIQUE: não pode haver login repetido
  senha      CHAR(64)     NOT NULL,          -- SHA-256 tem sempre 64 caracteres
  nome       VARCHAR(80)  NOT NULL
);

-- ----------------------------------------------------------------------------
-- item: a entidade PRINCIPAL. Molde de um cadastro simples e independente.
-- [MOLDE] Troque as colunas (nome, descricao, codigo...) pelas do seu cadastro.
--         A coluna "codigo" é UNIQUE: exemplo de campo que não pode repetir.
-- ----------------------------------------------------------------------------
CREATE TABLE item (
  id_item    INT AUTO_INCREMENT PRIMARY KEY,
  nome       VARCHAR(80)  NOT NULL,          -- campo comum de quase todo cadastro
  descricao  VARCHAR(255),
  codigo     VARCHAR(40)  NOT NULL UNIQUE    -- exemplo de campo único (não repete)
);

-- ----------------------------------------------------------------------------
-- item_filho: PERTENCE a um item. A coluna id_item é a FK que amarra os dois.
-- [MOLDE] Molde de "entidade filha" (pertence a outra). Ex.: Telefone->Cliente,
--         ItemDoPedido->Pedido, ServicoExecutado->OrdemDeServico.
-- ----------------------------------------------------------------------------
CREATE TABLE item_filho (
  id_item_filho INT AUTO_INCREMENT PRIMARY KEY,
  nome          VARCHAR(80)  NOT NULL,
  valor         DECIMAL(10,2),              -- exemplo de campo numérico com casas decimais
  id_item       INT NOT NULL,              -- FK obrigatória: todo filho tem um "pai"
  -- A FK garante que id_item exista na tabela item. Filho órfão é recusado.
  CONSTRAINT fk_item_filho_item FOREIGN KEY (id_item) REFERENCES item(id_item)
);

-- ============================================================================
-- DADOS DE TESTE
-- ============================================================================

-- Usuário admin. UPPER(SHA2('123456',256)) gera o hash da senha em MAIÚSCULO,
-- idêntico ao que o HashUtil.java gera no Java. Senha de acesso: 123456.
INSERT INTO usuario (login, senha, nome) VALUES
  ('admin', UPPER(SHA2('123456', 256)), 'Administrador');

-- Itens de exemplo (a entidade principal).
INSERT INTO item (nome, descricao, codigo) VALUES
  ('Item de exemplo A', 'Descrição livre do item A', 'COD-001'),
  ('Item de exemplo B', 'Descrição livre do item B', 'COD-002'),
  ('Item de exemplo C', 'Descrição livre do item C', 'COD-003');

-- Filhos de exemplo. O último número é o id_item (a FK): liga cada filho ao pai.
INSERT INTO item_filho (nome, valor, id_item) VALUES
  ('Filho 1 do item A', 10.50, 1),
  ('Filho 2 do item A', 20.00, 1),
  ('Filho 1 do item B', 99.90, 2);

-- Conferência: mostra quantas linhas ficaram em cada tabela.
-- Esperado: usuario 1, item 3, item_filho 3.
SELECT 'usuario'    AS tabela, COUNT(*) AS qtd FROM usuario
UNION ALL SELECT 'item',       COUNT(*) FROM item
UNION ALL SELECT 'item_filho', COUNT(*) FROM item_filho;
