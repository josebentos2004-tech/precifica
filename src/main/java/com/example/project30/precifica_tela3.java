package com.example.project30;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class precifica_tela3 {

    // Ligações com os inputs do formulário do Produtor
    @FXML
    private TextField txtMateriaPrima;

    @FXML
    private TextField txtMaoDeObra;

    @FXML
    private TextField txtGgf;

    @FXML
    private TextField txtServTerceiros;

    @FXML
    private Button btnAnterior;

    @FXML
    private Button btnSeguinte;

    /**
     * Este método roda automaticamente assim que a tela do Produtor é carregada.
     */
    @FXML
    public void initialize() {
        System.out.println("Tela do Fluxo de Produção inicializada com sucesso!");
    }

    /**
     * Ação do botão 'Anterior' - Volta para a Tela 1 (Identificação/Escolha de Fluxo)
     */
    @FXML
    void aoClicarAnterior(ActionEvent event) {
        try {
            StackPane conteudoDinamico = (StackPane) btnAnterior.getScene().lookup("#conteudoDinamico");
            if (conteudoDinamico != null) {
                conteudoDinamico.getChildren().clear();
                conteudoDinamico.getChildren().add(FXMLLoader.load(getClass().getResource("/com/example/project30/precifica_tela1.fxml")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Ação do botão 'Seguinte' - Processa os custos e avança para a Tela 4 (Definição de Lucro)
     */
    @FXML
    void aoClicarSeguinte(ActionEvent event) {
        // Valida campo obrigatório
        if (txtMateriaPrima.getText().trim().isEmpty() && txtMaoDeObra.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "⚠️ Preencha Matéria-Prima ou Mão de Obra!").showAndWait();
            return;
        }

        // Valida números
        try {
            for (TextField campo : new TextField[]{txtMateriaPrima, txtMaoDeObra, txtGgf, txtServTerceiros}) {
                String texto = campo.getText().trim();
                if (!texto.isEmpty()) {
                    double valor = Double.parseDouble(texto.replace(",", "."));
                    if (valor < 0) throw new NumberFormatException();
                }
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Use apenas números positivos! Ex: 1500,00").showAndWait();
            return;
        }

        // Avança
        try {
            StackPane conteudo = (StackPane) btnSeguinte.getScene().lookup("#conteudoDinamico");
            if (conteudo != null) {
                conteudo.getChildren().setAll(FXMLLoader.load(getClass().getResource("/com/example/project30/precifica_tela4.fxml")));
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Erro: " + e.getMessage()).showAndWait();
        }
    }
}