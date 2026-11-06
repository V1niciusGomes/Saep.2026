package model;

/**
 * Item — model da entidade PRINCIPAL. É o EXEMPLO-MESTRE de um cadastro simples.
 *
 * PAPEL NA ARQUITETURA (camada model):
 *   Só transporta dados (id + campos). Nenhuma regra, nenhum SQL.
 *
 * [MOLDE] Para criar um cadastro novo (ex.: Cliente, Produto, OrdemDeServico):
 *   1. Copie esta classe e renomeie (Item -> Cliente).
 *   2. Troque os campos (nome, descricao, codigo) pelos do seu cadastro.
 *   3. Gere os getters/setters correspondentes.
 *   O campo "id" sempre existe: é a chave primária gerada pelo banco.
 */
public class Item {
    private int id;            // id_item (chave primária, gerada pelo MySQL)
    private String nome;       // [MOLDE] campo comum de quase todo cadastro
    private String descricao;  // [MOLDE] campo de texto livre
    private String codigo;     // [MOLDE] exemplo de campo único (UNIQUE no banco)

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
}
