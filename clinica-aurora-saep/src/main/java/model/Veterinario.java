package model;

/**
 * Veterinario — model de um cadastro simples e independente.
 *
 * PAPEL NA ARQUITETURA (camada model):
 *   Transporta dados. Não abre conexão, não tem SQL.
 *   Diferente do Tutor, ele tem o campo "crmv", que no banco é ÚNICO
 *   (não pode repetir). Isso é uma regra do banco, não deste model.
 *
 * [MOLDE] Use como molde para qualquer entidade "de apoio" que outras
 *         tabelas vão referenciar (ex.: Categoria, Setor, Fornecedor).
 */
public class Veterinario {
    private int id;                 // id_veterinario (chave primária)
    private String nome;
    private String crmv;            // [MOLDE] campo único (registro profissional). Troque por "codigo", "sku", etc.
    private String especialidade;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCrmv() { return crmv; }
    public void setCrmv(String crmv) { this.crmv = crmv; }
    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }
}
