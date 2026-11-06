package model;

import java.time.LocalDateTime;

/**
 * OrdemServico — a ENTIDADE CENTRAL (molde ItemLigacao). Liga TRÊS pais
 * (cliente + tecnico + aparelho) e guarda dados próprios.
 *
 * Três grupos de campo:
 *   1. Relacionamento (FKs): idCliente, idTecnico, idAparelho -> vão pro banco.
 *   2. Exibição (JOIN): nomeCliente, nomeTecnico, descricaoAparelho -> só pra tela.
 *   3. Próprios: dataAbertura, status, valorMaoObra, defeitoRelatado.
 */
public class OrdemServico {
    private int id;                  // id_ordem_servico (PK)

    // 1. Relacionamento
    private int idCliente;
    private int idTecnico;
    private int idAparelho;

    // 2. Exibição (vêm do JOIN; não são colunas)
    private String nomeCliente;
    private String nomeTecnico;
    private String descricaoAparelho; // ex.: "Celular Marca A Modelo X"

    // 3. Próprios
    private LocalDateTime dataAbertura;
    private String status;
    private double valorMaoObra;
    private String defeitoRelatado;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public int getIdTecnico() { return idTecnico; }
    public void setIdTecnico(int idTecnico) { this.idTecnico = idTecnico; }
    public int getIdAparelho() { return idAparelho; }
    public void setIdAparelho(int idAparelho) { this.idAparelho = idAparelho; }
    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }
    public String getNomeTecnico() { return nomeTecnico; }
    public void setNomeTecnico(String nomeTecnico) { this.nomeTecnico = nomeTecnico; }
    public String getDescricaoAparelho() { return descricaoAparelho; }
    public void setDescricaoAparelho(String descricaoAparelho) { this.descricaoAparelho = descricaoAparelho; }
    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getValorMaoObra() { return valorMaoObra; }
    public void setValorMaoObra(double valorMaoObra) { this.valorMaoObra = valorMaoObra; }
    public String getDefeitoRelatado() { return defeitoRelatado; }
    public void setDefeitoRelatado(String defeitoRelatado) { this.defeitoRelatado = defeitoRelatado; }
}
