# Assistência Técnica — Gestão de Ordens de Serviço

Sistema desktop (Java + Swing + MySQL/JDBC) para gerenciar uma assistência
técnica: clientes, técnicos, peças, aparelhos e ordens de serviço.

Foi construído a partir do esqueleto **`projeto-base`**, aplicando seus três
moldes de entidade. Mantém a mesma arquitetura em camadas.

---

## As entidades e qual molde cada uma usa

| Entidade | Molde do esqueleto | Papel no sistema |
|----------|--------------------|------------------|
| `Cliente` | `Item` (cadastro simples) | Quem traz o aparelho. |
| `Tecnico` | `Item` (cadastro simples) | Quem conserta. |
| `Peca`    | `Item` (cadastro simples) | Catálogo/estoque de peças. |
| `Aparelho` | `ItemFilho` (1 FK) | Pertence a um cliente (histórico). |
| `OrdemServico` | `ItemLigacao` (3 FKs) | **Entidade central**: liga cliente + técnico + aparelho. |
| `OrdemPeca` | `ItemLigacao` (muitos-para-muitos) | Quais peças (e quantidade) foram usadas em cada OS. |

### Como as tabelas se relacionam

```
Cliente ──< Aparelho ─┐
   │                   │
   └──────────┐        │        Tecnico
              ▼        ▼          │
           Ordem de Serviço  ◄────┘   (liga Cliente + Técnico + Aparelho)
              │
              └──< Ordem_Peca >── Peca   (quais peças a OS usou)
```

---

## Como rodar

1. **Banco**: no MySQL Workbench, abra e execute `sql/assistencia_db.sql`.
   Ele cria o banco `assistencia_db`, as 7 tabelas e dados de teste.
2. **Conexão**: ajuste `URL`, `USER` e `PASS` em `util/ConnectionFactory.java`.
3. **Rodar**: execute `app/Main.java`. Abre a tela de login.
4. **Login de teste**: `admin` / `123456`.

> Requisitos: Java 17+ e Maven (o driver do MySQL é baixado pelo `pom.xml`).

---

## Fluxo de uso sugerido

1. Cadastre **Clientes** e **Técnicos**.
2. Cadastre as **Peças** (estoque).
3. Cadastre os **Aparelhos**, escolhendo o cliente dono.
4. Abra uma **Ordem de Serviço**, escolhendo cliente, técnico e aparelho.
5. Em **Peças usadas nas OS**, lance as peças consumidas em cada ordem.

> Por causa das chaves estrangeiras (FK), o banco protege a integridade:
> não dá para abrir uma OS sem cliente/técnico/aparelho, nem apagar um
> cliente que ainda tenha aparelhos ou ordens.

---

## Estrutura do código

```
src/main/java/
├── app/   Main.java                         (ignição)
├── model/ Usuario, Cliente, Tecnico, Peca,
│          Aparelho, OrdemServico, OrdemPeca  (dados)
├── dao/   UsuarioDAO, ClienteDAO, TecnicoDAO, PecaDAO,
│          AparelhoDAO, OrdemServicoDAO, OrdemPecaDAO  (SQL)
├── view/  LoginView, MenuView, ClienteView, TecnicoView, PecaView,
│          AparelhoView, OrdemServicoView, OrdemPecaView  (telas)
└── util/  ConnectionFactory, HashUtil        (conexão e senha)
```

Mesmas convenções do esqueleto: SQL só nos DAOs, senha com hash, `?` nos
SQLs (PreparedStatement) e conexões em try-with-resources.
