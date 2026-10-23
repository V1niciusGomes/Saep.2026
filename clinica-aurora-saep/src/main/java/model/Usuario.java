package model;

/**
 * Usuario — model (classe de dados) de quem faz login no sistema.
 *
 * PAPEL NA ARQUITETURA (camada model):
 *   Um model é só um "pacote de dados". Tem campos privados e seus
 *   getters/setters. NÃO abre conexão, NÃO tem SQL, NÃO tem lógica de tela.
 *   Serve para carregar informação entre as camadas (DAO -> tela).
 *
 * OBSERVE: não existe campo "senha" aqui de propósito. Depois do login,
 *   o sistema não precisa mais da senha, então nem a trazemos do banco.
 *
 * [MOLDE] Este é o model de autenticação. Em outro projeto com login,
 *         ele quase sempre fica igual (id, login, nome).
 */
public class Usuario {
    private int id;        // chave primária (id_usuario no banco)
    private String login;  // nome de acesso, ex.: "admin"
    private String nome;   // nome de exibição, ex.: "Administrador da Clínica"

    // Getters e setters: a forma padrão de ler e escrever cada campo.
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
