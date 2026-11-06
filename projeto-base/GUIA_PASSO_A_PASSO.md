# Guia Passo a Passo — Como alterar este projeto (100% explicado)

Este documento ensina, do zero, **como adaptar o esqueleto para o seu sistema**.
Não precisa decorar nada: é só seguir a ordem e copiar os moldes que já existem.

> **Leia primeiro esta regra de ouro.** Antes de criar qualquer coisa,
> pergunte-se: **"esta entidade existe sozinha?"**
> - **Existe sozinha** (Cliente, Produto, Técnico) → use o molde **`Item`**.
> - **Pertence a UMA outra** (Telefone de um cliente) → use o molde **`ItemFilho`**.
> - **LIGA várias outras** (Ordem de Serviço) → use o molde **`ItemLigacao`**.

---

## Sumário

1. [Como o projeto está organizado](#1-como-o-projeto-está-organizado)
2. [O caminho de uma informação (a lógica do sistema)](#2-o-caminho-de-uma-informação)
3. [Por onde começar — a ordem certa](#3-por-onde-começar--a-ordem-certa)
4. [Preparar o ambiente (uma vez só)](#4-preparar-o-ambiente-uma-vez-só)
5. [RECEITA A — criar um cadastro simples (ex.: Produto)](#5-receita-a--criar-um-cadastro-simples)
6. [RECEITA B — criar uma entidade "filha" (1 FK)](#6-receita-b--criar-uma-entidade-filha)
7. [RECEITA C — criar uma entidade de ligação (Ordem de Serviço)](#7-receita-c--criar-uma-entidade-de-ligação)
8. [Como ligar MAIS de duas tabelas / muitos-para-muitos](#8-como-ligar-mais-de-duas-tabelas)
9. [Checklist final e erros comuns](#9-checklist-final-e-erros-comuns)

---

## 1. Como o projeto está organizado

O código fica em `src/main/java`, dividido em **5 pastas (camadas)**. Cada
camada tem UMA responsabilidade. Isso é o que deixa o sistema fácil de alterar.

| Pasta (camada) | Responsabilidade | Pense assim... |
|----------------|------------------|----------------|
| `model` | Guardar os **dados** (campos de uma entidade). | "a ficha de papel" |
| `dao`   | Conversar com o **banco** (todo o SQL mora aqui). | "o arquivista" |
| `view`  | Mostrar as **telas** e receber cliques. | "o balcão de atendimento" |
| `util`  | Ferramentas comuns (conexão, hash de senha). | "a caixa de ferramentas" |
| `app`   | O **start** do programa (método `main`). | "a chave de ignição" |

Além do código, há:
- `sql/projeto_base_db.sql` → cria o **banco** e as **tabelas**.
- `pom.xml` → lista a **biblioteca** do MySQL (o Maven baixa sozinho).

> **Regra que nunca se quebra:** SQL só existe na pasta `dao`. A `view` nunca
> fala com o banco direto — ela sempre pede para o `dao`.

---

## 2. O caminho de uma informação

Toda ação no sistema segue sempre o MESMO caminho. Entender isto resolve 90%
das dúvidas:

```
VOCÊ clica na TELA  →  a tela chama o DAO  →  o DAO roda o SQL no BANCO
   (view)                  (dao)                    (MySQL)
      ↑                                                 |
      └──────────  os dados voltam em um MODEL  ◄────────┘
                           (model)
```

Exemplo real: você clica em **Salvar** na tela de Produtos.
1. `ProdutoView` lê os campos digitados e monta um objeto `Produto` (model).
2. `ProdutoView` chama `produtoDAO.inserir(produto)`.
3. `ProdutoDAO` roda o `INSERT` no banco.
4. A tela recarrega a lista chamando `produtoDAO.listar(...)`.

**Para criar uma funcionalidade nova, você sempre cria/edita os mesmos 3
arquivos: um `model`, um `dao` e uma `view` — mais a tabela no SQL.**

---

## 3. Por onde começar — a ordem certa

Siga SEMPRE esta ordem. Cada passo depende do anterior.

```
1º) TABELA no SQL      → decida os campos e crie a tabela no banco.
2º) MODEL              → a classe Java com os mesmos campos da tabela.
3º) DAO                → os comandos SQL (listar/inserir/atualizar/excluir).
4º) VIEW               → a tela que usa o DAO.
5º) MENU               → um botão em MenuView para abrir a tela nova.
6º) TESTAR             → rode o Main e confira.
```

Por que essa ordem? Porque a tela (4º) precisa do DAO (3º), que precisa do
model (2º), que espelha a tabela (1º). É só seguir de cima para baixo.

---

## 4. Preparar o ambiente (uma vez só)

Antes de alterar qualquer coisa, deixe o projeto rodando como está:

1. **Instale** o Java 17+ e o MySQL.
2. **Crie o banco:** abra o MySQL Workbench, abra o arquivo
   `sql/projeto_base_db.sql` e clique no raio (Executar). Isso cria o banco
   `projeto_base_db` com as tabelas e dados de teste.
3. **Aponte a conexão:** abra `src/main/java/util/ConnectionFactory.java` e
   confira as 3 linhas marcadas com `[MOLDE]`:
   - `URL` → o endereço e o nome do banco;
   - `USER` → normalmente `root`;
   - `PASS` → a senha do seu MySQL (troque pela sua!).
4. **Rode:** execute `src/main/java/app/Main.java`. Abre a tela de login.
5. **Entre:** usuário `admin`, senha `123456`.

Se a tela de login abriu e você entrou, o ambiente está pronto. ✅

> **Dica:** quando mudar o `sql/`, rode o script de novo no Workbench para o
> banco refletir as mudanças. (Atenção: o script apaga e recria tudo.)

---

## 5. RECEITA A — criar um cadastro simples

**Quando usar:** a entidade existe sozinha (Cliente, Produto, Técnico, Peça).
**Molde a copiar:** `Item` (arquivos `Item.java`, `ItemDAO.java`, `ItemView.java`).

Vamos criar, como exemplo, um cadastro de **Produto** com os campos
`nome`, `preco` e `codigoBarras`.

### Passo A.1 — Criar a TABELA

Abra `sql/projeto_base_db.sql` e adicione uma tabela nova (copie a tabela
`item` como base):

```sql
CREATE TABLE produto (
  id_produto     INT AUTO_INCREMENT PRIMARY KEY,  -- PK: o banco numera sozinho
  nome           VARCHAR(80)  NOT NULL,
  preco          DECIMAL(10,2),                   -- número com 2 casas (dinheiro)
  codigo_barras  VARCHAR(40)  NOT NULL UNIQUE      -- UNIQUE: não pode repetir
);
```

Lembre-se de adicionar o `DROP TABLE IF EXISTS produto;` lá no topo (na seção
de DROP), para o script continuar recriável. Depois, rode o script no Workbench.

> **Conceitos:** `PK` (chave primária) é o identificador único de cada linha.
> `AUTO_INCREMENT` faz o banco numerar automático. `UNIQUE` proíbe valores
> repetidos naquela coluna.

### Passo A.2 — Criar o MODEL

Copie `src/main/java/model/Item.java`, cole como `Produto.java` e troque os
campos. O model é só um "pacote de dados" com campos + getters/setters:

```java
package model;

public class Produto {
    private int id;                  // sempre existe: é a PK (id_produto)
    private String nome;
    private double preco;
    private String codigoBarras;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }
    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }
}
```

> **Regra:** cada coluna da tabela vira um campo aqui. Tipos: `VARCHAR`→`String`,
> `INT`→`int`, `DECIMAL`→`double`, `DATE`→`LocalDate`, `DATETIME`→`LocalDateTime`.

### Passo A.3 — Criar o DAO

Copie `src/main/java/dao/ItemDAO.java`, cole como `ProdutoDAO.java` e ajuste:
- o nome da tabela (`item` → `produto`) e da PK (`id_item` → `id_produto`);
- a lista de colunas em cada SQL;
- os `rs.getX(...)` (leitura) e `ps.setX(...)` (escrita).

```java
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Produto;
import util.ConnectionFactory;

public class ProdutoDAO {

    // READ — lista por nome (filtro vazio = todos)
    public List<Produto> listar(String filtro) {
        String sql = "SELECT id_produto, nome, preco, codigo_barras FROM produto "
                   + "WHERE nome LIKE ? ORDER BY nome";
        List<Produto> lista = new ArrayList<>();
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, "%" + (filtro == null ? "" : filtro.trim()) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Produto p = new Produto();
                    p.setId(rs.getInt("id_produto"));
                    p.setNome(rs.getString("nome"));
                    p.setPreco(rs.getDouble("preco"));
                    p.setCodigoBarras(rs.getString("codigo_barras"));
                    lista.add(p);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar produtos: " + e.getMessage(), e);
        }
        return lista;
    }

    // CREATE — insere
    public void inserir(Produto p) {
        String sql = "INSERT INTO produto (nome, preco, codigo_barras) VALUES (?, ?, ?)";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setDouble(2, p.getPreco());
            ps.setString(3, p.getCodigoBarras());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir produto: " + e.getMessage(), e);
        }
    }

    // UPDATE — altera pelo id
    public void atualizar(Produto p) {
        String sql = "UPDATE produto SET nome=?, preco=?, codigo_barras=? WHERE id_produto=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setDouble(2, p.getPreco());
            ps.setString(3, p.getCodigoBarras());
            ps.setInt(4, p.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar produto: " + e.getMessage(), e);
        }
    }

    // DELETE — remove pelo id
    public void excluir(int id) {
        String sql = "DELETE FROM produto WHERE id_produto=?";
        try (Connection cn = ConnectionFactory.get();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir produto: " + e.getMessage(), e);
        }
    }
}
```

> **Entenda o padrão** (ele se repete em TODO DAO):
> 1. escreve o SQL com `?` no lugar dos valores;
> 2. abre conexão com `try (...)` (fecha sozinho no fim);
> 3. preenche os `?` com `ps.setX(posição, valor)` — a posição começa em 1;
> 4. `executeQuery()` para SELECT, `executeUpdate()` para gravar;
> 5. em erro, lança `RuntimeException` com mensagem clara.
>
> **Por que usar `?` e não colar o valor na string?** Para evitar *SQL Injection*
> (um usuário mal-intencionado digitar comando no campo). O `?` é obrigatório.

### Passo A.4 — Criar a VIEW (a tela)

Copie `src/main/java/view/ItemView.java`, cole como `ProdutoView.java` e ajuste
**4 pontos** (procure por eles na classe):

1. **Os campos da tela** (um `JTextField` por campo editável):
   ```java
   private final ProdutoDAO dao = new ProdutoDAO();
   private final JTextField campoNome = new JTextField();
   private final JTextField campoPreco = new JTextField();
   private final JTextField campoCodigo = new JTextField();
   ```
2. **As colunas da tabela** (os títulos que aparecem no topo da grade):
   ```java
   new String[] { "ID", "Nome", "Preço", "Código de Barras" }, 0)
   ```
3. **O preenchimento de cada linha** no método `carregar()` — a ORDEM aqui deve
   casar com a ordem das colunas acima:
   ```java
   modelo.addRow(new Object[] { p.getId(), p.getNome(), p.getPreco(), p.getCodigoBarras() });
   ```
4. **Ler/gravar os campos** nos métodos `preencherSelecionado()` e `salvar()`
   (troque os `campoX.getText()`/`setText(...)` pelos seus campos).

> **Dica para campos numéricos** (como `preco`): no `salvar()`, converta o texto
> para número com `Double.parseDouble(campoPreco.getText().trim().replace(",", "."))`
> dentro de um `try/catch` (veja como a `ItemFilhoView` faz com o campo `valor`).

### Passo A.5 — Adicionar o botão no MENU

Abra `src/main/java/view/MenuView.java` e faça 3 coisas (todas marcadas com `[MOLDE]`):

1. Crie o botão:
   ```java
   JButton btnProdutos = new JButton("Produtos");
   ```
2. Ligue a ação dele à sua tela:
   ```java
   btnProdutos.addActionListener(e -> new ProdutoView().setVisible(true));
   ```
3. Aumente o número de linhas do `GridLayout` e adicione o botão:
   ```java
   JPanel botoes = new JPanel(new GridLayout(6, 1, 10, 10)); // era 5, virou 6
   botoes.add(btnProdutos);
   ```

### Passo A.6 — Testar

Rode o `Main`, entre no sistema, clique em **Produtos** e teste: cadastrar,
buscar, clicar numa linha, editar, salvar e excluir. **Pronto, cadastro novo!**

---

## 6. RECEITA B — criar uma entidade "filha"

**Quando usar:** a entidade pertence a UMA outra (ex.: cada **Telefone**
pertence a um **Cliente**; cada **Endereço** pertence a um **Cliente**).
**Molde a copiar:** `ItemFilho` (`ItemFilho.java`, `ItemFilhoDAO.java`,
`ItemFilhoView.java`).

A receita é a MESMA da Receita A, com **3 diferenças**. Vamos usar o exemplo
**Telefone** (que pertence a um `Produto`... ou, na vida real, a um `Cliente`).

### Diferença 1 — a TABELA tem uma FK

A tabela filha ganha uma coluna que aponta para a PK do pai (a **chave
estrangeira / FK**):

```sql
CREATE TABLE telefone (
  id_telefone INT AUTO_INCREMENT PRIMARY KEY,
  numero      VARCHAR(20) NOT NULL,
  id_cliente  INT NOT NULL,                 -- FK: a qual cliente este telefone pertence
  CONSTRAINT fk_telefone_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
);
```

> **O que é FK?** É a coluna que amarra o filho ao pai. A `CONSTRAINT ... FOREIGN
> KEY` garante que você não consiga gravar um telefone apontando para um cliente
> que não existe. E também impede apagar um cliente que ainda tem telefones.

### Diferença 2 — o MODEL tem o par "id do pai" + "nome do pai"

Além dos campos próprios, o model filho tem:
- `idCliente` → o **número** (a FK) que vai gravado no banco;
- `nomeCliente` → o **nome** do pai, só para EXIBIR na tela (vem do JOIN, não é
  coluna desta tabela).

```java
private int idCliente;       // FK — vai pro banco
private String nomeCliente;  // exibição — vem do JOIN
```

### Diferença 3 — o DAO faz JOIN e a VIEW tem um ComboBox

- No **DAO**, o `listar` usa um `JOIN` para trazer o nome do pai junto:
  ```sql
  SELECT t.id_telefone, t.numero, t.id_cliente, c.nome AS nome_cliente
  FROM telefone t JOIN cliente c ON c.id_cliente = t.id_cliente
  ```
- No `inserir`, valide que o pai foi escolhido:
  ```java
  if (t.getIdCliente() <= 0) throw new IllegalArgumentException("Telefone sem cliente.");
  ```
- Na **VIEW**, em vez de um campo de texto para o pai, use um `JComboBox` que
  lista os pais existentes (copie como a `ItemFilhoView` monta o `comboItem`:
  ela chama o DAO do pai, preenche o combo, e na hora de salvar pega o id do
  item escolhido). Veja os métodos `carregarCombo()` e `salvar()` dela.

O resto (passos do menu e teste) é igual à Receita A.

---

## 7. RECEITA C — criar uma entidade de ligação

**Quando usar:** a entidade LIGA duas (ou mais) outras ao mesmo tempo.
**O exemplo clássico é a ORDEM DE SERVIÇO**, que junta um **Cliente** e um
**Técnico** e guarda dados próprios (data, status, valor, defeito).
**Molde a copiar:** `ItemLigacao` (`ItemLigacao.java`, `ItemLigacaoDAO.java`,
`ItemLigacaoView.java`).

> **Por que a Ordem de Serviço NÃO é um "filho" simples?** Porque ela não
> pertence a uma única tabela — ela referencia VÁRIAS (cliente, técnico,
> aparelho...). Por isso tem VÁRIAS FKs. Esse é o molde de ligação.

### Passo C.1 — a TABELA tem VÁRIAS FKs + campos próprios

```sql
CREATE TABLE ordem_servico (
  id_ordem_servico INT AUTO_INCREMENT PRIMARY KEY,
  id_cliente       INT NOT NULL,            -- FK 1 -> de quem é
  id_tecnico       INT NOT NULL,            -- FK 2 -> quem conserta
  data_abertura    DATETIME NOT NULL,       -- campo próprio (data + hora)
  status           VARCHAR(40),             -- "Aberta", "Em andamento", "Concluída"
  valor            DECIMAL(10,2),
  defeito          VARCHAR(255),
  CONSTRAINT fk_os_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
  CONSTRAINT fk_os_tecnico FOREIGN KEY (id_tecnico) REFERENCES tecnico(id_tecnico)
);
```

### Passo C.2 — o MODEL tem 3 grupos de campo

Espelhe o `ItemLigacao.java`. Para CADA pai, mantenha o par id+nome:
```java
// Relacionamento (FKs que vão pro banco)
private int idCliente;
private int idTecnico;
// Exibição (vêm do JOIN; não são colunas)
private String nomeCliente;
private String nomeTecnico;
// Campos próprios da OS
private LocalDateTime dataAbertura;
private String status;
private double valor;
private String defeito;
```

### Passo C.3 — o DAO faz UM JOIN por pai e UMA validação por FK

Copie o `ItemLigacaoDAO.java`. No `listar`, um `JOIN` por pai:
```sql
SELECT o.*, c.nome AS nome_cliente, t.nome AS nome_tecnico
FROM ordem_servico o
JOIN cliente c ON c.id_cliente = o.id_cliente
JOIN tecnico t ON t.id_tecnico = o.id_tecnico
```
No `inserir`, valide os dois pais:
```java
if (o.getIdCliente() <= 0) throw new IllegalArgumentException("OS sem cliente.");
if (o.getIdTecnico() <= 0) throw new IllegalArgumentException("OS sem técnico.");
```

> **Data e hora:** no banco é `DATETIME`; no Java é `LocalDateTime`. A ponte é a
> classe `Timestamp` (`Timestamp.valueOf(data)` ao gravar; `ts.toLocalDateTime()`
> ao ler). O `ItemLigacaoDAO` já mostra isso pronto.

### Passo C.4 — a VIEW tem UM ComboBox por pai

Copie a `ItemLigacaoView.java`. Ela tem DOIS combos (um por pai), cada um
alimentado pelo DAO correspondente (`carregarCombos()`), um campo de data/hora
(com validação de formato) e os demais campos próprios. Troque `Item`/`ItemB`
pelos seus pais (`Cliente`/`Tecnico`).

Depois, adicione o botão no menu (Passo A.5) e teste (Passo A.6).

---

## 8. Como ligar MAIS de duas tabelas

### Caso 1 — a OS também precisa do Aparelho (3 pais)
É só **repetir o padrão** mais uma vez, em cada camada:
- **SQL:** adicione `id_aparelho INT NOT NULL` + a `CONSTRAINT ... FOREIGN KEY`.
- **model:** adicione o par `idAparelho` + `nomeAparelho`.
- **dao:** adicione mais um `JOIN` e mais uma validação.
- **view:** adicione mais um `JComboBox` (copie o bloco de um combo existente).

> **Alternativa mais simples:** se você não quer um cadastro separado de
> aparelhos, guarde marca/modelo/defeito como **campos próprios da própria OS**
> (colunas comuns, sem FK). Use isto quando não precisar reaproveitar o aparelho.

### Caso 2 — muitos-para-muitos (ex.: quais PEÇAS entram em cada OS)
Uma OS usa várias peças; uma peça entra em várias OS. Isso pede uma **tabela de
ligação própria** (o mesmo molde `ItemLigacao`, aplicado a OS + Peça):

```sql
CREATE TABLE os_peca (
  id_os_peca       INT AUTO_INCREMENT PRIMARY KEY,
  id_ordem_servico INT NOT NULL,            -- FK 1 -> a OS
  id_peca          INT NOT NULL,            -- FK 2 -> a peça usada
  quantidade       INT NOT NULL,            -- campo próprio desta ligação
  CONSTRAINT fk_osp_os   FOREIGN KEY (id_ordem_servico) REFERENCES ordem_servico(id_ordem_servico),
  CONSTRAINT fk_osp_peca FOREIGN KEY (id_peca)          REFERENCES peca(id_peca)
);
```

Model/DAO/View seguem exatamente a Receita C (é mais uma entidade de ligação).

---

## 9. Checklist final e erros comuns

### Checklist ao criar qualquer entidade
- [ ] Criei a **tabela** no `sql/` e **rodei o script** no Workbench.
- [ ] Criei o **model** com um campo para cada coluna.
- [ ] Criei o **DAO** com `listar`, `inserir`, `atualizar`, `excluir`.
- [ ] Criei a **view** copiando o molde certo (A, B ou C).
- [ ] Adicionei o **botão no `MenuView`** (e ajustei o `GridLayout`).
- [ ] **Testei** criar, buscar, editar e excluir.

### Erros comuns (e a causa)
| Sintoma | Causa provável |
|---------|----------------|
| "Não conectou no MySQL" no login | Senha errada em `ConnectionFactory` ou MySQL desligado. |
| "Unknown column" / "Table doesn't exist" | O SQL do DAO não bate com a tabela. Confira nomes de coluna/tabela. |
| A coluna da tabela aparece vazia na tela | A ORDEM no `addRow(...)` não casa com a ordem dos títulos das colunas. |
| "Cannot delete or update a parent row" | Você tentou apagar um pai que ainda tem filhos (a FK protege). Apague os filhos antes. |
| "Duplicate entry" ao salvar | Você gravou um valor repetido numa coluna `UNIQUE` (ex.: código). |
| Combo de pai aparece vazio | Não há registros do pai cadastrados ainda. Cadastre o pai primeiro. |

### Mapa rápido "o que trocar" ao copiar um molde
Sempre que copiar uma classe-molde, troque, nesta ordem:
1. o **nome da classe** e o **nome do arquivo** (`Item` → `Produto`);
2. o **nome da tabela** e da **PK** nos SQLs (`item`/`id_item` → `produto`/`id_produto`);
3. a **lista de colunas** (SELECT/INSERT/UPDATE);
4. os **campos** (model) e os **componentes da tela** (view);
5. o **botão no menu**.

---

Com estas três receitas (A = simples, B = filho, C = ligação) você monta
qualquer sistema de gestão. Comece sempre pela tabela, siga a ordem, e copie o
molde mais parecido com o que precisa. Bom trabalho! 🚀
