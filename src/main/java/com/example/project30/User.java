package com.example.project30;

public class User {

    private int id;
    private String nome;
    private String email;
    private  String nif;
    private  String telefone;

    public User(int id, String nome, String email,String nif) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.nif=nif;
    }

    public int getId() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public String getEmail() {
        return this.email;
    }

 public String getNif() {
        return this.nif;
    }
    public String getTelefone() {
        return this.telefone;
    }
}