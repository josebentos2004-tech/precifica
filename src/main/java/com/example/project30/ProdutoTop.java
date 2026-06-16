package com.example.project30;

import javafx.beans.property.*;

public class ProdutoTop {
    private final StringProperty nome = new SimpleStringProperty();
    private final DoubleProperty lucroUnitario = new SimpleDoubleProperty();
    private final DoubleProperty margem = new SimpleDoubleProperty();

    public ProdutoTop(String nome, double lucroUnitario, double margem) {
        setNome(nome);
        setLucroUnitario(lucroUnitario);
        setMargem(margem);
    }

    public String getNome() { return nome.get(); }
    public void setNome(String value) { nome.set(value); }
    public StringProperty nomeProperty() { return nome; }

    public double getLucroUnitario() { return lucroUnitario.get(); }
    public void setLucroUnitario(double value) { lucroUnitario.set(value); }
    public DoubleProperty lucroUnitarioProperty() { return lucroUnitario; }

    public double getMargem() { return margem.get(); }
    public void setMargem(double value) { margem.set(value); }
    public DoubleProperty margemProperty() { return margem; }
}