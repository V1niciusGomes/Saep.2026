package model;

/**
 * ItemB — SEGUNDO cadastro simples e independente. Igual em espírito ao Item;
 * existe para ser o "segundo pai" da entidade de ligação (ItemLigacao).
 *
 * PAPEL NA ARQUITETURA (camada model):
 *   Só transporta dados (id + campos). Nenhuma regra, nenhum SQL.
 *
 * [MOLDE] Numa assistência técnica, se "Item" virar "Cliente", este aqui
 *         pode virar "Técnico" ou "Aparelho". É só mais um cadastro simples.
 */
public class ItemB {
    private int id;            // id_item_b (chave primária)
    private String nome;
    private String descricao;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
