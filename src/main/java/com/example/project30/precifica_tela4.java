package com.example.project30;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class precifica_tela4 {

    @FXML
    private TextField txtMargemLucro;

    @FXML
    private TextField txtPrecoMercado;

    @FXML
    private Button btnAnterior;

    @FXML
    private Button btnSubmeter;

    /**
     * Roda automaticamente ao carregar a tela
     */
    @FXML
    public void initialize() {
        System.out.println("Tela de Definição de Lucro pronta.");
    }



    /**
     * Evento acionado ao clicar no botão "Calcular Preço Final"
     */
    @FXML
    void Voltar(ActionEvent event) {

        try {
            Parent tela5 = FXMLLoader.load(getClass().getResource("/com/example/project30/precifica_tela1.fxml"));
            Scene cenaAtual = btnSubmeter.getScene();
            StackPane conteudoDinamico = (StackPane) cenaAtual.lookup("#conteudoDinamico");

            if (conteudoDinamico != null) {
                conteudoDinamico.getChildren().clear();
                conteudoDinamico.getChildren().add(tela5);
            }
        } catch (IOException e) {
            System.err.println("Erro ao navegar para próxima tela: " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Método opcional para navegar para a próxima tela após o cálculo
     */
    private void navegarParaProximaTela() {
        try {
            Parent tela5 = FXMLLoader.load(getClass().getResource("/com/example/project30/precifica_tela5.fxml"));
            Scene cenaAtual = btnSubmeter.getScene();
            StackPane conteudoDinamico = (StackPane) cenaAtual.lookup("#conteudoDinamico");

            if (conteudoDinamico != null) {
                conteudoDinamico.getChildren().clear();
                conteudoDinamico.getChildren().add(tela5);
            }
        } catch (IOException e) {
            System.err.println("Erro ao navegar para próxima tela: " + e.getMessage());
            e.printStackTrace();
        }
    }
}