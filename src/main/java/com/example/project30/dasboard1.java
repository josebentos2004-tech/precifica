package com.example.project30;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class dasboard1 {

    // Adicionado para fazer a ligação com o fx:id="conteudoDinamico" do FXML
    @FXML
    private StackPane conteudoDinamico;

    @FXML
    private VBox btnComerciante;

    @FXML
    private VBox btnProdutor;

    @FXML
    public void initialize() {
        // Código que roda automaticamente quando a tela abre
    }

    /**
     * Função que os botões do menu vão chamar para abrir a precificação
     */
    @FXML
    private void abrirTelaPrecificacao() {
        try {
            // Carrega o FXML da etapa 1 de dentro da pasta do pacote
            Parent novaTela = FXMLLoader.load(getClass().getResource("/com/example/project30/precifica_etapa1.fxml"));

            // Limpa o painel central e injeta a nova tela
            conteudoDinamico.getChildren().clear();
            conteudoDinamico.getChildren().add(novaTela);

        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela de precificação: " + e.getMessage());
            e.printStackTrace();
        }
    }
}