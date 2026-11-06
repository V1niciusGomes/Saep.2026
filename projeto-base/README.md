# Projeto Base — Esqueleto CRUD (Java + Swing + MySQL)

Um **esqueleto reutilizável** para montar rapidamente qualquer sistema de
cadastro (CRUD) desktop. Em vez de um domínio específico, ele traz entidades
**genéricas** que você copia e renomeia para a função que quiser: assistência
técnica, loja, clínica, estoque, etc.

Todo o código é **simples e muito comentado**. Procure pela marca `[MOLDE]`:
ela indica exatamente os pontos que você deve trocar ao adaptar.

---

## O que vem pronto

| Camada | Arquivos | O que é |
|--------|----------|---------|
| `app`   | `Main.java` | Ponto de entrada (liga o sistema e abre o login). |
| `view`  | `LoginView`, `MenuView`, `ItemView`, `ItemBView`, `ItemFilhoView`, `ItemLigacaoView` | Telas (Swing). |
| `dao`   | `UsuarioDAO`, `ItemDAO`, `ItemBDAO`, `ItemFilhoDAO`, `ItemLigacaoDAO` | Acesso ao banco (SQL). |
| `model` | `Usuario`, `Item`, `ItemB`, `ItemFilho`, `ItemLigacao` | Classes de dados. |
| `util`  | `ConnectionFactory`, `HashUtil` | Conexão e hash de senha. |
| `sql`   | `projeto_base_db.sql` | Cria o banco, as tabelas e dados de teste. |

### Os TRÊS moldes de entidade (o coração do esqueleto)

1. **`Item`** (e seu gêmeo `ItemB`) → molde de **cadastro simples e independente**.
   Existe sozinho. Ex.: Cliente, Produto, Técnico, Peça.
2. **`ItemFilho`** → molde de **entidade que pertence a UMA outra** (1 FK).
   Ex.: Telefone→Cliente, Endereço→Cliente, Item→Pedido.
3. **`ItemLigacao`** → molde de **entidade de LIGAÇÃO / CRUZAMENTO** (VÁRIAS FKs).
   Junta duas (ou mais) entidades e guarda dados próprios.
   **É o molde da Ordem de Serviço**, da Consulta (pet+vet), da Matrícula
   (aluno+turma), da Venda (cliente+vendedor), etc.

---

## Qual molde usar? (guia rápido)

Pergunte-se: **"esta entidade existe sozinha?"**

- **Sim, existe sozinha** → use **`Item`**. (Cliente, Técnico, Peça.)
- **Não, pertence a UMA outra** → use **`ItemFilho`**. (Telefone de um cliente.)
- **Não, ela LIGA várias outras** → use **`ItemLigacao`**. (Ordem de Serviço.)

### Exemplo: assistência técnica

| Entidade do seu sistema | Molde a copiar |
|-------------------------|----------------|
| Cliente                 | `Item`         |
| Técnico                 | `ItemB` (outro `Item`) |
| Peça                    | `Item`         |
| **Ordem de Serviço**    | **`ItemLigacao`** (liga Cliente + Técnico, com data/status/valor) |

> A OS **não** é um "filho" simples: ela referencia várias tabelas ao mesmo
> tempo. Por isso usa o molde de ligação (várias FKs), não o `ItemFilho`.

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

| Molde | Vira (ex.: assistência técnica) |
|-------|---------------------------------|
| `item`         | `cliente`         |
| `item_b`       | `tecnico`         |
| `item_filho`   | `telefone`        |
| `item_ligacao` | `ordem_servico`   |

### 2. Para um CADASTRO SIMPLES novo (molde `Item`/`ItemB`)
Copie o trio e renomeie:
- `model/Item.java` → sua entidade (troque os campos);
- `dao/ItemDAO.java` → troque nome da tabela, PK e colunas nos SQLs;
- `view/ItemView.java` → troque campos do formulário e colunas da tabela.
Por fim, adicione um botão no `MenuView.java` que abre a nova tela.

### 3. Para uma ENTIDADE QUE PERTENCE A OUTRA (molde `ItemFilho`)
Copie o trio `ItemFilho` e, além dos passos acima:
- mantenha o par **`idPai` (FK)** + **`nomePai` (exibição via JOIN)** no model;
- no DAO, faça o `JOIN` com a tabela do pai no `listar`;
- na view, o `JComboBox` lista os pais possíveis (vindos do DAO do pai).

### 4. Para a ENTIDADE CENTRAL / DE LIGAÇÃO (molde `ItemLigacao`)
Este é o molde da Ordem de Serviço. Copie o trio `ItemLigacao` e:
- no **model**, mantenha, para CADA pai, o par **`idX` (FK)** + **`nomeX` (exibição)**,
  mais os **campos próprios** (data, status, valor, descrição);
- no **DAO**, faça **um `JOIN` por pai** no `listar` e **uma validação por FK**
  no `inserir`;
- na **view**, haverá **um `JComboBox` por pai**, cada um alimentado pelo DAO
  correspondente;
- **para ligar MAIS de duas tabelas** (ex.: OS com Cliente + Técnico + Aparelho):
  acrescente, em cada camada, mais um bloco de FK/JOIN/combo — o padrão se repete.

> **Peças usadas numa OS** são o caso "muitos-para-muitos" (uma OS usa várias
> peças; uma peça entra em várias OS). Isso pede uma tabela de ligação própria
> (ex.: `os_peca`) com as FKs `id_ordem_servico` + `id_peca` e um campo
> `quantidade` — exatamente o molde `ItemLigacao` aplicado a Peça + OS.

### 5. Ajuste a identidade visual
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
