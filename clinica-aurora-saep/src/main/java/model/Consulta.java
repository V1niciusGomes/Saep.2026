package model;

import java.time.LocalDateTime;

/**
 * Transporta dado. Não abre conexão.
 * idPet e idVeterinario são campos de relacionamento (vão na FK).
 * nomePet e nomeVet são campos de exibição: vêm do JOIN.
 */
public class Consulta {
    private int id;
    private int idPet;
    private int idVeterinario;
    private LocalDateTime dataHora;
    private String motivo;
    private String observacao;
    private String nomePet;
    private String nomeVet;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdPet() { return idPet; }
    public void setIdPet(int idPet) { this.idPet = idPet; }
    public int getIdVeterinario() { return idVeterinario; }
    public void setIdVeterinario(int idVeterinario) { this.idVeterinario = idVeterinario; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public String getNomePet() { return nomePet; }
    public void setNomePet(String nomePet) { this.nomePet = nomePet; }
    public String getNomeVet() { return nomeVet; }
    public void setNomeVet(String nomeVet) { this.nomeVet = nomeVet; }
}
