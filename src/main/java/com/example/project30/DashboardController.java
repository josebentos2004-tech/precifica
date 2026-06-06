package com.example.project30;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.controlsfx.dialog.CommandLinksDialog;
import org.controlsfx.dialog.CommandLinksDialog.CommandLinksButtonType;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class DashboardController {

    @FXML
    private StackPane conteudoDinamico;

    @FXML
    private Label name;

    @FXML
    private Label nif;

    private User user;

    public void setUser(User user) {
        this.user = user;
        name.setText("Bem-vindo, " + user.getNome() + " 👋");
        nif.setText(user.getEmail());
    }

    @FXML
    private void abrirTelaPrecificacao() {
        try {
            Parent novaTela = FXMLLoader.load(getClass().getResource("precifica_tela1.fxml"));
            conteudoDinamico.getChildren().clear();
            conteudoDinamico.getChildren().add(novaTela);
        } catch (IOException e) {
            System.err.println("Erro ao mudar de tela: " + e.getMessage());
        }
    }

    @FXML
    private void abrirproduto() {
        try {
            Parent novaTela = FXMLLoader.load(getClass().getResource("produto_total.fxml"));
            conteudoDinamico.getChildren().clear();
            conteudoDinamico.getChildren().add(novaTela);
        } catch (IOException e) {
            System.err.println("Erro ao mudar de tela: " + e.getMessage());
        }
    }

    @FXML
    private void abrirDashboard() {
        try {
            Parent novaTela = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
            conteudoDinamico.getChildren().clear();
            conteudoDinamico.getChildren().add(novaTela);
        } catch (IOException e) {
            System.err.println("Erro ao mudar de tela: " + e.getMessage());
        }
    }

    @FXML
    private void logoutL(ActionEvent event) {
        CommandLinksDialog dialog = new CommandLinksDialog(
                new CommandLinksButtonType("Sim, terminar sessão", true),
                new CommandLinksButtonType("Não, continuar", false)
        );
        dialog.setTitle("Confirmação de Logout");
        dialog.setHeaderText("Deseja realmente terminar a sessão?");
        dialog.setResizable(false);

        dialog.showAndWait().ifPresent(result -> {
            if (result.getText().startsWith("Sim")) {
                try {
                    this.user = null;
                    Stage stage =
                            (Stage) ((Node) event.getSource())
                                    .getScene()
                                    .getWindow();

                    ScreenManager.changeScreen(
                            stage,
                            "login.fxml"
                    );
                } catch (Error e) {
                    e.printStackTrace();
                }
            }
        });
    }
}