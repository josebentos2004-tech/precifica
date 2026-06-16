package com.example.project30;

public class SessaoEdicao {
    private static SessaoEdicao instancia;
    private int idProdutoEdicao;
    private String tipoProdutoEdicao;

    private SessaoEdicao() {}
    public static SessaoEdicao getInstancia() {
        if (instancia == null) instancia = new SessaoEdicao();
        return instancia;
    }
    public int getIdProdutoEdicao() { return idProdutoEdicao; }
    public void setIdProdutoEdicao(int id) { this.idProdutoEdicao = id; }
    public String getTipoProdutoEdicao() { return tipoProdutoEdicao; }
    public void setTipoProdutoEdicao(String tipo) { this.tipoProdutoEdicao = tipo; }
    public void limpar() { idProdutoEdicao = -1; tipoProdutoEdicao = null; }
}