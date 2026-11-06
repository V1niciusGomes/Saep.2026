-- ============================================================================
-- PROJETO BASE — Esqueleto de banco de dados (molde reutilizável)
--
-- O QUE ESTE SCRIPT FAZ: cria o banco, as 5 tabelas de exemplo e insere
-- alguns dados de teste. No fim, conta as linhas de cada tabela.
--
-- COMO RODAR: no MySQL Workbench, abra este arquivo e clique no raio (Execute).
--
-- ATENÇÃO: começa com DROP TABLE. Ele RECRIA o banco do zero (apaga dados
-- existentes). Use para montar/remontar o banco limpo.
--
-- AS 5 TABELAS SÃO MOLDES:
--   usuario       -> molde de LOGIN (senha guardada como hash SHA-256)
--   item          -> molde de CADASTRO SIMPLES (entidade independente)
--   item_b        -> OUTRO cadastro simples (o 2º "pai" da ligação)
--   item_filho    -> molde de ENTIDADE QUE PERTENCE A OUTRA (1 FK para item)
--   item_ligacao  -> molde de ENTIDADE DE LIGAÇÃO/CRUZAMENTO (VÁRIAS FKs)
--
-- [MOLDE] Para adaptar a outro sistema (ex.: assistência técnica, loja...),
--         troque: o nome do banco, os nomes das tabelas/colunas e os INSERT.
--         A ESTRUTURA (PK, FK, UNIQUE, hash de senha, contagem) serve para
--         qualquer cadastro. Exemplos de renomeação:
--           item         -> cliente | produto | tecnico | funcionario
--           item_b       -> aparelho | peca | fornecedor
--           item_filho   -> telefone | endereco | item_do_pedido
--           item_ligacao -> ordem_servico | consulta | matricula | venda
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
DROP TABLE IF EXISTS item_ligacao; -- liga item + item_b (tem 2 FKs)
DROP TABLE IF EXISTS item_filho;   -- filha de item
DROP TABLE IF EXISTS item_b;
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
-- item_b: OUTRO cadastro simples e independente. É o SEGUNDO "pai" da ligação.
-- [MOLDE] Mesmo molde do item. Numa assistência técnica, enquanto "item" vira
--         "cliente", este aqui poderia virar "tecnico" ou "aparelho".
-- ----------------------------------------------------------------------------
CREATE TABLE item_b (
  id_item_b  INT AUTO_INCREMENT PRIMARY KEY,
  nome       VARCHAR(80)  NOT NULL,
  descricao  VARCHAR(255)
);

-- ----------------------------------------------------------------------------
-- item_filho: PERTENCE a um item. A coluna id_item é a FK que amarra os dois.
-- [MOLDE] Molde de "entidade filha" (pertence a UMA outra). Ex.: Telefone->Cliente,
--         Endereco->Cliente, ItemDoPedido->Pedido.
-- ----------------------------------------------------------------------------
CREATE TABLE item_filho (
  id_item_filho INT AUTO_INCREMENT PRIMARY KEY,
  nome          VARCHAR(80)  NOT NULL,
  valor         DECIMAL(10,2),              -- exemplo de campo numérico com casas decimais
  id_item       INT NOT NULL,              -- FK obrigatória: todo filho tem um "pai"
  -- A FK garante que id_item exista na tabela item. Filho órfão é recusado.
  CONSTRAINT fk_item_filho_item FOREIGN KEY (id_item) REFERENCES item(id_item)
);

-- ----------------------------------------------------------------------------
-- item_ligacao: molde de ENTIDADE DE LIGAÇÃO / CRUZAMENTO.
-- Diferença para item_filho: aqui há DUAS (ou mais) FKs, ligando DUAS tabelas
-- diferentes ao mesmo tempo. É o coração de muitos sistemas.
--
-- Este é o molde da ORDEM DE SERVIÇO de uma assistência técnica: ela junta
-- um cliente (item) e um técnico/aparelho (item_b), e guarda dados próprios
-- (data, status, valor, descrição). Também é o molde de "consulta" (pet+vet),
-- "matrícula" (aluno+turma), "venda" (cliente+vendedor), etc.
--
-- [MOLDE] Para a sua entidade central:
--   - troque id_item/id_item_b pelas FKs reais (ex.: id_cliente, id_tecnico);
--   - para LIGAR MAIS de duas tabelas, basta acrescentar mais colunas FK + CONSTRAINT;
--   - mantenha/renomeie os campos próprios (data, status, valor, descricao).
-- ----------------------------------------------------------------------------
CREATE TABLE item_ligacao (
  id_item_ligacao INT AUTO_INCREMENT PRIMARY KEY,
  id_item         INT NOT NULL,          -- FK 1 -> o 1º "pai" (ex.: cliente)
  id_item_b       INT NOT NULL,          -- FK 2 -> o 2º "pai" (ex.: tecnico/aparelho)
  data_registro   DATETIME NOT NULL,     -- guarda data E hora (ex.: abertura da OS)
  status          VARCHAR(40),           -- ex.: "Aberta", "Em andamento", "Concluída"
  valor           DECIMAL(10,2),         -- ex.: valor total do serviço
  descricao       VARCHAR(255),          -- ex.: defeito relatado / observações
  CONSTRAINT fk_ligacao_item   FOREIGN KEY (id_item)   REFERENCES item(id_item),
  CONSTRAINT fk_ligacao_item_b FOREIGN KEY (id_item_b) REFERENCES item_b(id_item_b)
);

-- ============================================================================
-- DADOS DE TESTE
-- ============================================================================

-- Usuário admin. UPPER(SHA2('123456',256)) gera o hash da senha em MAIÚSCULO,
-- idêntico ao que o HashUtil.java gera no Java. Senha de acesso: 123456.
INSERT INTO usuario (login, senha, nome) VALUES
  ('admin', UPPER(SHA2('123456', 256)), 'Administrador');

-- Itens de exemplo (1º pai — ex.: clientes).
INSERT INTO item (nome, descricao, codigo) VALUES
  ('Item de exemplo A', 'Descrição livre do item A', 'COD-001'),
  ('Item de exemplo B', 'Descrição livre do item B', 'COD-002'),
  ('Item de exemplo C', 'Descrição livre do item C', 'COD-003');

-- Itens B de exemplo (2º pai — ex.: técnicos/aparelhos).
INSERT INTO item_b (nome, descricao) VALUES
  ('Item B — Um',  'Segundo cadastro, opção 1'),
  ('Item B — Dois','Segundo cadastro, opção 2');

-- Filhos de exemplo. O último número é o id_item (a FK): liga cada filho ao pai.
INSERT INTO item_filho (nome, valor, id_item) VALUES
  ('Filho 1 do item A', 10.50, 1),
  ('Filho 2 do item A', 20.00, 1),
  ('Filho 1 do item B', 99.90, 2);

-- Ligações de exemplo. Os dois primeiros números são as FKs (id_item, id_item_b).
INSERT INTO item_ligacao (id_item, id_item_b, data_registro, status, valor, descricao) VALUES
  (1, 1, '2026-01-10 09:00:00', 'Aberta',       150.00, 'Exemplo: liga o item A ao item B-Um'),
  (2, 2, '2026-01-11 14:30:00', 'Em andamento', 320.50, 'Exemplo: liga o item B ao item B-Dois'),
  (1, 2, '2026-01-12 11:15:00', 'Concluída',     80.00, 'Exemplo: o mesmo item A, outro pai B');

-- Conferência: mostra quantas linhas ficaram em cada tabela.
-- Esperado: usuario 1, item 3, item_b 2, item_filho 3, item_ligacao 3.
SELECT 'usuario'      AS tabela, COUNT(*) AS qtd FROM usuario
UNION ALL SELECT 'item',         COUNT(*) FROM item
UNION ALL SELECT 'item_b',       COUNT(*) FROM item_b
UNION ALL SELECT 'item_filho',   COUNT(*) FROM item_filho
UNION ALL SELECT 'item_ligacao', COUNT(*) FROM item_ligacao;
