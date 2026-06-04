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

    // Só precisamos do ID do painel onde a tela vai aparecer
    @FXML
    private StackPane conteudoDinamico;

    // Método direto que o botão vai chamar
    @FXML
    private void abrirTelaPrecificacao() {
        try {
            // 1. Carrega a tela diretamente
            Parent novaTela = FXMLLoader.load(getClass().getResource("precifica_tela1.fxml"));

            // 2. Limpa o painel e joga a nova tela lá dentro
            conteudoDinamico.getChildren().clear();
            conteudoDinamico.getChildren().add(novaTela);

        } catch (IOException e) {
            System.err.println("Erro ao mudar de tela: " + e.getMessage());
        }
    }

    @FXML
    private void abrirDashboard() {
        try {
            // 1. Carrega a tela diretamente
            Parent novaTela = FXMLLoader.load(getClass().getResource("dasboard1.fxml"));

            // 2. Limpa o painel e joga a nova tela lá dentro
            conteudoDinamico.getChildren().clear();
            conteudoDinamico.getChildren().add(novaTela);

        } catch (IOException e) {
            System.err.println("Erro ao mudar de tela: " + e.getMessage());
        }
    }
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
    private void logoutL(ActionEvent event) {
        // Criar diálogo de confirmação com dois botões
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
                    // Destruir dados do usuário
                    this.user = null;

                    // Redirecionar para tela de login
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
                    Parent root = loader.load();

                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
