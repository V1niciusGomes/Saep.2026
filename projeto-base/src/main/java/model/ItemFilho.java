package model;

/**
 * ItemFilho — model que PERTENCE A outra tabela (tem um "pai"). Exemplo-mestre
 * de relacionamento "muitos para um" (muitos filhos para um item).
 *
 * DOIS TIPOS DE CAMPO (conceito importante do molde):
 *   1. Campo de RELACIONAMENTO -> idItem
 *      É o número que amarra o filho ao item (a "chave estrangeira"/FK).
 *      É ele que vai gravado no banco.
 *   2. Campo de EXIBIÇÃO -> nomeItem
 *      NÃO é coluna da tabela item_filho. Vem preenchido pelo JOIN no DAO só
 *      para a tela mostrar "Filho 1 — pai: Item A" sem outra consulta.
 *
 * [MOLDE] Para um cadastro que "pertence a" outro (ex.: ItemDoPedido->Pedido,
 *         Telefone->Cliente): copie este molde, mantendo o par
 *         idPai (relacionamento) + nomePai (exibição).
 */
public class ItemFilho {
    private int id;             // id_item_filho (chave primária)
    private String nome;
    private double valor;       // exemplo de campo numérico (DECIMAL no banco)
    private int idItem;         // [MOLDE] FK: id do "pai". Vai gravado no banco.
    private String nomeItem;    // [MOLDE] campo de exibição: vem do JOIN, não é coluna.

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public int getIdItem() { return idItem; }
    public void setIdItem(int idItem) { this.idItem = idItem; }
    public String getNomeItem() { return nomeItem; }
    public void setNomeItem(String nomeItem) { this.nomeItem = nomeItem; }
}
