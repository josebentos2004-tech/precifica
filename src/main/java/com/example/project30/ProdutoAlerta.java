package com.example.project30;

import javafx.beans.property.*;

public class ProdutoAlerta {
    private final StringProperty nome = new SimpleStringProperty();
    private final DoubleProperty margem = new SimpleDoubleProperty();
    private final DoubleProperty precoVenda = new SimpleDoubleProperty();
    private final DoubleProperty precoCusto = new SimpleDoubleProperty();

    public ProdutoAlerta(String nome, double margem, double precoVenda, double precoCusto) {
        setNome(nome);
        setMargem(margem);
        setPrecoVenda(precoVenda);
        setPrecoCusto(precoCusto);
    }

    public String getNome() { return nome.get(); }
    public void setNome(String value) { nome.set(value); }
    public StringProperty nomeProperty() { return nome; }

    public double getMargem() { return margem.get(); }
    public void setMargem(double value) { margem.set(value); }
    public DoubleProperty margemProperty() { return margem; }

    public double getPrecoVenda() { return precoVenda.get(); }
    public void setPrecoVenda(double value) { precoVenda.set(value); }
    public DoubleProperty precoVendaProperty() { return precoVenda; }

    public double getPrecoCusto() { return precoCusto.get(); }
    public void setPrecoCusto(double value) { precoCusto.set(value); }
    public DoubleProperty precoCustoProperty() { return precoCusto; }
}