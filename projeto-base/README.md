# Projeto Base — Esqueleto CRUD (Java + Swing + MySQL)

Um **esqueleto reutilizável** para montar rapidamente qualquer sistema de
cadastro (CRUD) desktop. Em vez de um domínio específico, ele traz entidades
**genéricas** (`Item` e `ItemFilho`) que você copia e renomeia para a função
que quiser: assistência técnica, loja, clínica, estoque, etc.

Todo o código é **simples e muito comentado**. Procure pela marca `[MOLDE]`:
ela indica exatamente os pontos que você deve trocar ao adaptar.

---

## O que vem pronto

| Camada | Arquivos | O que é |
|--------|----------|---------|
| `app`   | `Main.java` | Ponto de entrada (liga o sistema e abre o login). |
| `view`  | `LoginView`, `MenuView`, `ItemView`, `ItemFilhoView` | Telas (Swing). |
| `dao`   | `UsuarioDAO`, `ItemDAO`, `ItemFilhoDAO` | Acesso ao banco (SQL). |
| `model` | `Usuario`, `Item`, `ItemFilho` | Classes de dados. |
| `util`  | `ConnectionFactory`, `HashUtil` | Conexão e hash de senha. |
| `sql`   | `projeto_base_db.sql` | Cria o banco, as tabelas e dados de teste. |

### As duas entidades de exemplo
- **`Item`** → molde de **cadastro simples e independente** (a entidade principal).
- **`ItemFilho`** → molde de **entidade que pertence a outra** (tem chave estrangeira / FK para `Item`), com seletor de "pai" na tela.

---

## Como rodar

1. **Banco**: no MySQL Workbench, abra e execute `sql/projeto_base_db.sql`.
2. **Conexão**: ajuste `URL`, `USER` e `PASS` em `util/ConnectionFactory.java`
   conforme o seu MySQL.
3. **Rodar**: execute `app/Main.java`. Abre a tela de login.
4. **Login de teste**: `admin` / `123456`.

> Requisitos: Java 17+ e Maven (o driver do MySQL é baixado pelo `pom.xml`).

---

## Como adaptar para o SEU sistema (passo a passo)

A arquitetura segue sempre o mesmo caminho:

> **view** (tela) → chama o **dao** (SQL) → que usa o **model** (dados) → via **util** (conexão).

### 1. Renomeie o banco e as tabelas
No `sql/projeto_base_db.sql`, troque o nome do banco e das tabelas/colunas.
Exemplos de renomeação:

| Molde | Vira (ex.: assistência técnica) |
|-------|---------------------------------|
| `item`        | `cliente` ou `ordem_servico`    |
| `item_filho`  | `servico_executado` ou `aparelho` |

### 2. Para um CADASTRO SIMPLES novo (molde `Item`)
Copie o trio `Item` e renomeie:
- `model/Item.java` → sua entidade (troque os campos);
- `dao/ItemDAO.java` → troque nome da tabela, PK e colunas nos SQLs;
- `view/ItemView.java` → troque campos do formulário e colunas da tabela.
Por fim, adicione um botão no `MenuView.java` que abre a nova tela.

### 3. Para uma ENTIDADE QUE PERTENCE A OUTRA (molde `ItemFilho`)
Copie o trio `ItemFilho` e, além dos passos acima:
- mantenha o par **`idPai` (FK)** + **`nomePai` (exibição via JOIN)** no model;
- no DAO, faça o `JOIN` com a tabela do pai no `listar`;
- na view, o `JComboBox` lista os pais possíveis (vindos do DAO do pai).

### 4. Ajuste a identidade visual
Troque títulos e a cor do cabeçalho (procure `[MOLDE]` em `LoginView` e
`MenuView`).

---

## Convenções do projeto

- **Nunca** coloque SQL nas telas: SQL vive só nos DAOs.
- **Nunca** guarde senha em texto puro: use `HashUtil.sha256(...)` (já feito no login).
- Use sempre **`?` (PreparedStatement)** nos SQLs — evita SQL Injection.
- Abra conexão com **try-with-resources** — fecha sozinho, sem vazamento.
- O campo **`id`** (PK) é sempre gerado pelo banco (`AUTO_INCREMENT`).

---

Feito para ser copiado, renomeado e expandido. Comece pela marca `[MOLDE]`.
