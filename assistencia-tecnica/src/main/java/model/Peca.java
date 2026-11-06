package model;

/**
 * Peca — catálogo/estoque de peças. Cadastro simples e independente (molde Item).
 */
public class Peca {
    private int id;          // id_peca (PK)
    private String nome;
    private String codigo;   // UNIQUE no banco
    private double preco;
    private int estoque;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }
    public int getEstoque() { return estoque; }
    public void setEstoque(int estoque) { this.estoque = estoque; }
}
