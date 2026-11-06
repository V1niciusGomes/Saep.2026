package model;

/**
 * Tecnico — cadastro simples e independente (molde Item).
 */
public class Tecnico {
    private int id;            // id_tecnico (PK)
    private String nome;
    private String especialidade;
    private String telefone;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}
