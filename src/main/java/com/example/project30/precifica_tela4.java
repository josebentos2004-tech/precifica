package com.example.project30;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
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
     * Evento para voltar à tela anterior (Tela 3)
     */
    @FXML
    void aoClicarAnterior(ActionEvent event) {
        try {
            // Carrega o FXML da tela anterior (Tela 3)
            Parent tela3 = FXMLLoader.load(getClass().getResource("/com/example/project30/precifica_tela2.fxml"));

            // Captura a cena através do botão clicado
            Scene cenaAtual = btnAnterior.getScene();
            StackPane conteudoDinamico = (StackPane) cenaAtual.lookup("#conteudoDinamico");

            if (conteudoDinamico != null) {
                conteudoDinamico.getChildren().clear();
                conteudoDinamico.getChildren().add(tela3);
            } else {
                System.err.println("Erro: Elemento #conteudoDinamico não encontrado!");
            }
        } catch (IOException e) {
            System.err.println("Erro ao voltar para a Tela 3: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Evento acionado ao clicar no botão "Calcular Preço Final"
     */
    @FXML
    void SubmeterLucro(ActionEvent event) {
        String margemTexto = txtMargemLucro.getText();
        String precoMercadoTexto = txtPrecoMercado.getText();

        if (margemTexto.isEmpty()) {
            System.out.println("Erro: A margem de lucro é obrigatória!");
            // Você pode adicionar um alerta visual aqui
            return;
        }

        try {
            double margem = Double.parseDouble(margemTexto.replace(",", "."));
            System.out.println("Margem de lucro definida para o cálculo do Markup: " + margem + "%");

            if (!precoMercadoTexto.isEmpty()) {
                double precoMercado = Double.parseDouble(precoMercadoTexto.replace(",", "."));
                System.out.println("Preço de mercado do concorrente para comparação: " + precoMercado);
            }

            // TODO: Aqui você vai juntar os custos coletados nas telas anteriores
            // e aplicar a fórmula do preço de venda final.

            // Exemplo de navegação após calcular (opcional)
            // navegarParaProximaTela();

        } catch (NumberFormatException e) {
            System.err.println("Erro de digitação: Certifique-se de usar números válidos.");
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