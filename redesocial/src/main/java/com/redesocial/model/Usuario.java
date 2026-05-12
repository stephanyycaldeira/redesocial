package com.redesocial.model;

public class Usuario {

    private String id;
    private String nome;
    private String email;
    private int idade;
    private String cidade;

    public Usuario(String id, String nome, String email, int idade, String cidade) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.idade = idade;
        this.cidade = cidade;
    }

    public String getId()     { return id; }
    public String getNome()   { return nome; }
    public String getEmail()  { return email; }
    public int    getIdade()  { return idade; }
    public String getCidade() { return cidade; }

    public void setNome(String nome)     { this.nome = nome; }
    public void setEmail(String email)   { this.email = email; }
    public void setIdade(int idade)      { this.idade = idade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    @Override
    public String toString() {
        return String.format("Usuario{id='%s', nome='%s', email='%s', idade=%d, cidade='%s'}",
                id, nome, email, idade, cidade);
    }
}
