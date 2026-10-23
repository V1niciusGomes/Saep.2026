package model;

import java.time.LocalDate;

/**
 * Pet — model que PERTENCE A outra tabela (tem dono). Exemplo-mestre de
 * relacionamento "muitos para um" (muitos pets para um tutor).
 *
 * DOIS TIPOS DE CAMPO (conceito importante do molde):
 *   1. Campo de RELACIONAMENTO -> idTutor
 *      É o número que amarra o pet ao tutor (a "chave estrangeira"/FK).
 *      É ele que vai gravado no banco.
 *   2. Campo de EXIBIÇÃO -> nomeTutor
 *      NÃO é coluna da tabela pet. Vem preenchido pelo JOIN no DAO só para
 *      a tela mostrar "Rex — dono: Ana" sem precisar de outra consulta.
 *
 * [MOLDE] Para um cadastro que "pertence a" outro (ex.: Produto -> Categoria,
 *         Pedido -> Cliente): copie este molde, mantendo o par
 *         idDono (relacionamento) + nomeDono (exibição).
 */
public class Pet {
    private int id;                    // id_pet (chave primária)
    private String nome;
    private String especie;
    private String raca;
    private LocalDate dataNascimento;  // data pura (sem hora). Veja ConsultaView para data+hora.
    private int idTutor;               // [MOLDE] FK: id do "dono". Vai gravado no banco.
    private String nomeTutor;          // [MOLDE] campo de exibição: vem do JOIN, não é coluna.

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
