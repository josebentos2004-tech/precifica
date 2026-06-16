package com.example.project30;

public class ProdutoModel {
    private int id;
    private int position;
    private String nome;
    private double custo;
    private double venda;
    private double margem;
    private double lucro;
    private  String tipo;

    public ProdutoModel(int id,int position, String nome, double custo, double venda, double margem, double lucro) {
        this.id = id;
        this.nome = nome;
        this.custo = custo;
        this.venda = venda;
        this.margem = margem;
        this.lucro = lucro;
        this.position=position;
        this.tipo="";
    }

    // getters (OBRIGATÓRIO)
    public int getId() { return id; }
    public int getPosition() { return position; }
    public String getNome() { return nome; }
    public double getCusto() { return custo; }
    public double getVenda() { return venda; }
    public double getMargem() { return margem; }
    public double getLucro() { return lucro; }
    public String getTipo() { return tipo; }
}
