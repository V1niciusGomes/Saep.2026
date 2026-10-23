package model;

import java.time.LocalDateTime;

/**
 * Consulta — model que liga DUAS tabelas ao mesmo tempo (pet + veterinário).
 * Exemplo-mestre de entidade "de cruzamento" (uma agenda, um pedido, etc.).
 *
 * TEM DOIS PARES relacionamento+exibição:
 *   - idPet (FK)         / nomePet (vem do JOIN)
 *   - idVeterinario (FK) / nomeVet (vem do JOIN)
 *
 * dataHora usa LocalDateTime porque guarda DATA + HORA (ex.: 2026-10-20 09:00).
 *   Compare com o Pet, que usa LocalDate (só data).
 *
 * [MOLDE] Para uma entidade que conecta duas outras (ex.: Matricula ->
 *         Aluno + Turma, ItemPedido -> Pedido + Produto), copie este molde
 *         com os dois pares de id + nome.
 */
public class Consulta {
    private int id;                    // id_consulta (chave primária)
    private int idPet;                 // [MOLDE] FK para a 1ª tabela
    private int idVeterinario;         // [MOLDE] FK para a 2ª tabela
    private LocalDateTime dataHora;    // data + hora do agendamento
    private String motivo;
    private String observacao;
    private String nomePet;            // [MOLDE] exibição (JOIN): nome do pet
    private String nomeVet;            // [MOLDE] exibição (JOIN): nome do veterinário

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
