package com.example.project30;

public class User {
    private int id;
    private String nome;
    private String email;
    private String nif;
    private String telefone;
    private String senha;

    // Construtor com 5 parâmetros (para o cadastro - sem senha)
    public User(int id, String nome, String email, String nif, String telefone) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.nif = nif;
        this.telefone = telefone;
        this.senha = null;  // Sem senha
    }

    // Construtor com 6 parâmetros (para o login - com senha)
    public User(int id, String nome, String email, String nif, String telefone, String senha) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.nif = nif;
        this.telefone = telefone;
        this.senha = senha;
    }

    // Getters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getNif() { return nif; }
    public String getTelefone() { return telefone; }
    public String getSenha() { return senha; }
}