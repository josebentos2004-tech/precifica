package com.example.project30;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;

public class preficica_tela1 {

    @FXML
    private VBox btnComerciante;

    @FXML
    private VBox btnProdutor;

    @FXML
    void clicouComerciante(MouseEvent event) {
        System.out.println("Clicou em Comerciante!");

        try {
            // 1. Carrega o FXML da tela do Comerciante
            Parent telaComerciante = FXMLLoader.load(getClass().getResource("precifica_tela2.fxml"));

            // 2. O SEGREDO: Pegamos a Scene atual através do botão que foi clicado
            Scene cenaAtual = btnComerciante.getScene();

            // 3. Procuramos o StackPane do Dashboard na árvore visual usando o ID dele
            StackPane conteudoDinamico = (StackPane) cenaAtual.lookup("#conteudoDinamico");

            if (conteudoDinamico != null) {
                // 4. Limpa a Tela 1 e coloca a tela do Comerciante no mesmo sítio!
                conteudoDinamico.getChildren().clear();
                conteudoDinamico.getChildren().add(telaComerciante);
                System.out.println("Tela do Comerciante injetada com sucesso!");
            } else {
                System.err.println("Erro: Não foi possível encontrar o #conteudoDinamico do Dashboard.");
            }

        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela do Comerciante: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void clicouProdutor(MouseEvent event) {
        System.out.println("Clicou em Produtor!");

        try {
            // 1. Carrega o FXML da Tela 2 (Produtor)
            Parent telaProdutor = FXMLLoader.load(getClass().getResource("/com/example/project30/precifica_tela3.fxml"));

            // 2. Pegamos a Scene atual através do botão do Produtor
            Scene cenaAtual = btnProdutor.getScene();

            // 3. Procuramos o ID do painel do Dashboard
            StackPane conteudoDinamico = (StackPane) cenaAtual.lookup("#conteudoDinamico");

            if (conteudoDinamico != null) {
                // 4. Limpa a Tela 1 e coloca a Tela 2 do Produtor no mesmo sítio!
                conteudoDinamico.getChildren().clear();
                conteudoDinamico.getChildren().add(telaProdutor);
                System.out.println("Tela 2 do Produtor injetada com sucesso!");
            } else {
                System.err.println("Erro: Não foi possível encontrar o #conteudoDinamico do Dashboard.");
            }

        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela do Produtor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // Inicialização automática da tela
    }
}