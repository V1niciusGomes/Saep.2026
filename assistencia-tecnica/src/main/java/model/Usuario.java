package model;

/**
 * Usuario — model de quem faz login. Só transporta dados (sem SQL, sem tela).
 * Não há campo "senha": depois do login o sistema não precisa mais dela.
 */
public class Usuario {
    private int id;
    private String login;
    private String nome;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
