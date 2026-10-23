package model;

import java.time.LocalDate;

/**
 * Transporta dado. Não abre conexão.
 * idTutor é o campo de relacionamento (vai na FK).
 * nomeTutor é campo de exibição: não é coluna da tabela pet, vem do JOIN.
 */
public class Pet {
    private int id;
    private String nome;
    private String especie;
    private String raca;
    private LocalDate dataNascimento;
    private int idTutor;
    private String nomeTutor;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }
    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public int getIdTutor() { return idTutor; }
    public void setIdTutor(int idTutor) { this.idTutor = idTutor; }
    public String getNomeTutor() { return nomeTutor; }
    public void setNomeTutor(String nomeTutor) { this.nomeTutor = nomeTutor; }
}
