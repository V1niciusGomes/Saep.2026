package model;

import java.time.LocalDateTime;

/**
 * ItemLigacao — model da ENTIDADE DE LIGAÇÃO / CRUZAMENTO.
 * É o molde da ORDEM DE SERVIÇO: junta DUAS entidades ao mesmo tempo
 * (um Item e um ItemB) e guarda dados próprios do registro.
 *
 * DIFERENÇA PARA O ItemFilho:
 *   - ItemFilho tem UMA FK (pertence a um único pai).
 *   - ItemLigacao tem DUAS (ou mais) FKs: ela CRUZA duas tabelas diferentes.
 *
 * TRÊS GRUPOS DE CAMPO (conceito importante do molde):
 *   1. Campos de RELACIONAMENTO -> idItem, idItemB
 *      São os números (FKs) que amarram a ligação às duas entidades.
 *      São eles que vão gravados no banco.
 *   2. Campos de EXIBIÇÃO -> nomeItem, nomeItemB
 *      NÃO são colunas da tabela. Vêm do JOIN no DAO, só para a tela mostrar
 *      os nomes dos dois pais sem consultas extras.
 *   3. Campos PRÓPRIOS -> dataRegistro, status, valor, descricao
 *      Dados que pertencem à própria ligação (ex.: data e status da OS).
 *
 * [MOLDE] Para a sua entidade central (OrdemServico, Consulta, Matricula...):
 *   - renomeie idItem/idItemB para as FKs reais (idCliente, idTecnico...);
 *   - para LIGAR MAIS de duas entidades, acrescente mais pares idX + nomeX;
 *   - troque os campos próprios pelos do seu domínio.
 */
public class ItemLigacao {
    private int id;                     // id_item_ligacao (chave primária)

    // 1. Relacionamento (FKs que vão gravadas no banco)
    private int idItem;                 // FK 1 -> o 1º pai (ex.: cliente)
    private int idItemB;                // FK 2 -> o 2º pai (ex.: técnico/aparelho)

    // 2. Exibição (vêm do JOIN; não são colunas desta tabela)
    private String nomeItem;            // nome do 1º pai
    private String nomeItemB;           // nome do 2º pai

    // 3. Campos próprios da ligação
    private LocalDateTime dataRegistro; // data + hora (ex.: abertura da OS)
    private String status;              // ex.: "Aberta", "Em andamento", "Concluída"
    private double valor;               // ex.: valor total do serviço
    private String descricao;           // ex.: defeito relatado / observações

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdItem() { return idItem; }
    public void setIdItem(int idItem) { this.idItem = idItem; }
    public int getIdItemB() { return idItemB; }
    public void setIdItemB(int idItemB) { this.idItemB = idItemB; }
    public String getNomeItem() { return nomeItem; }
    public void setNomeItem(String nomeItem) { this.nomeItem = nomeItem; }
    public String getNomeItemB() { return nomeItemB; }
    public void setNomeItemB(String nomeItemB) { this.nomeItemB = nomeItemB; }
    public LocalDateTime getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDateTime dataRegistro) { this.dataRegistro = dataRegistro; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
