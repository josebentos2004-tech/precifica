package com.example.project30;

import javafx.scene.control.Alert;
import javafx.event.ActionEvent; // IMPORTANTE: Import correto para o onAction
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javafx.scene.layout.StackPane;
import java.io.IOException;

public class precifica_tela2 {

    @FXML
    private TextField txtCustoCompra;

    @FXML
    private TextField txtFrete;

    @FXML
    private TextField txtEmbalagem;

    @FXML
    private Button btnAnterior;

    @FXML
    private Button btnSeguinte;

    @FXML
    public void initialize() {
        System.out.println("--> Tela 2 carregada e pronta para receber cliques!");
    }

    @FXML
    void aoClicarAnterior(ActionEvent event) {
        try {
            // CORREÇÃO: preCifica_tela1.fxml (com 'c' depois de 'pre')
            Parent tela1 = FXMLLoader.load(getClass().getResource("/com/example/project30/precifica_tela1.fxml"));

            Scene cenaAtual = btnAnterior.getScene();
            StackPane conteudoDinamico = (StackPane) cenaAtual.lookup("#conteudoDinamico");

            if (conteudoDinamico != null) {
                conteudoDinamico.getChildren().clear();
                conteudoDinamico.getChildren().add(tela1);
            }
        } catch (IOException e) {
            System.err.println("Erro ao voltar para a Tela 1: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void aoClicarSeguinte(ActionEvent event) {
        System.out.println("Botão Seguinte clicado!");

        // Verifica se os campos estão vazios
        String custoCompraTexto = txtCustoCompra.getText().trim();
        String freteTexto = txtFrete.getText().trim();
        String embalagemTexto = txtEmbalagem.getText().trim();

        if (custoCompraTexto.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Preencha o campo Custo Unitário de Compra!").showAndWait();
            return;
        }

        if (freteTexto.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Preencha o campo Transporte / Frete!").showAndWait();
            return;
        }

        if (embalagemTexto.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Preencha o campo Embalagens e Outros!").showAndWait();
            return;
        }

        // Verifica se os valores são números válidos
        try {
            double custoCompra = Double.parseDouble(custoCompraTexto.replace(",", "."));
            double frete = Double.parseDouble(freteTexto.replace(",", "."));
            double embalagem = Double.parseDouble(embalagemTexto.replace(",", "."));

            // Verifica se os números são positivos
            if (custoCompra <= 0) {
                new Alert(Alert.AlertType.WARNING, "O Custo Unitário de Compra deve ser maior que zero!").showAndWait();
                return;
            }

            if (frete < 0) {
                new Alert(Alert.AlertType.WARNING, "O valor do Frete não pode ser negativo!").showAndWait();
                return;
            }

            if (embalagem < 0) {
                new Alert(Alert.AlertType.WARNING, "O valor de Embalagens não pode ser negativo!").showAndWait();
                return;
            }

            // Se passou por todas as validações, avança para a próxima tela
            Parent tela4 = FXMLLoader.load(getClass().getResource("/com/example/project30/precifica_tela4.fxml"));
            Scene cenaAtual = btnSeguinte.getScene();
            StackPane conteudoDinamico = (StackPane) cenaAtual.lookup("#conteudoDinamico");

            if (conteudoDinamico != null) {
                conteudoDinamico.getChildren().clear();
                conteudoDinamico.getChildren().add(tela4);
            }

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Digite apenas números válidos! Use vírgula ou ponto para decimais.").showAndWait();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao avançar: " + e.getMessage()).showAndWait();
            System.err.println("Erro ao avançar para a Tela 4: " + e.getMessage());
            e.printStackTrace();
        }
    }
}