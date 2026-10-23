package model;

/**
 * Tutor — model do dono do pet. É o EXEMPLO-MESTRE de um cadastro simples.
 *
 * PAPEL NA ARQUITETURA (camada model):
 *   Só transporta dados (id + campos). Nenhuma regra, nenhum SQL.
 *
 * [MOLDE] Para criar um cadastro novo (ex.: Cliente, Produto, Funcionário):
 *   1. Copie esta classe e renomeie (Tutor -> Cliente).
 *   2. Troque os campos (nome, telefone, email, cpf) pelos do seu cadastro.
 *   3. Gere os getters/setters correspondentes.
 *   O campo "id" sempre existe: é a chave primária gerada pelo banco.
 */
public class Tutor {
    private int id;           // id_tutor (chave primária, gerada pelo MySQL)
    private String nome;      // [MOLDE] campo comum de quase todo cadastro
    private String telefone;
    private String email;
    private String cpf;       // guardado criptografado (AES) no banco; aqui fica em texto puro

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
}
