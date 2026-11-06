package model;

/**
 * Aparelho — PERTENCE a um cliente (molde ItemFilho). Guarda o par:
 *   - idCliente  (FK) -> o número gravado no banco;
 *   - nomeCliente     -> vem do JOIN, só para exibir na tela (não é coluna).
 */
public class Aparelho {
    private int id;              // id_aparelho (PK)
    private String tipo;         // Celular, Notebook, TV...
    private String marca;
    private String modelo;
    private String numeroSerie;
    private int idCliente;       // FK -> dono do aparelho
    private String nomeCliente;  // exibição (JOIN)

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }
}
