package model;

/**
 * OrdemPeca — peças usadas numa ordem de serviço (molde ItemLigacao /
 * muitos-para-muitos). Liga OrdemServico + Peca, com a quantidade usada.
 *
 * Grupos de campo:
 *   1. Relacionamento (FKs): idOrdemServico, idPeca -> vão pro banco.
 *   2. Exibição (JOIN): nomePeca, descricaoOrdem -> só pra tela.
 *   3. Próprio: quantidade.
 */
public class OrdemPeca {
    private int id;                  // id_ordem_peca (PK)

    // 1. Relacionamento
    private int idOrdemServico;
    private int idPeca;

    // 2. Exibição
    private String descricaoOrdem;   // ex.: "OS #1 - Ana Souza"
    private String nomePeca;

    // 3. Próprio
    private int quantidade;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdOrdemServico() { return idOrdemServico; }
    public void setIdOrdemServico(int idOrdemServico) { this.idOrdemServico = idOrdemServico; }
    public int getIdPeca() { return idPeca; }
    public void setIdPeca(int idPeca) { this.idPeca = idPeca; }
    public String getDescricaoOrdem() { return descricaoOrdem; }
    public void setDescricaoOrdem(String descricaoOrdem) { this.descricaoOrdem = descricaoOrdem; }
    public String getNomePeca() { return nomePeca; }
    public void setNomePeca(String nomePeca) { this.nomePeca = nomePeca; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}
