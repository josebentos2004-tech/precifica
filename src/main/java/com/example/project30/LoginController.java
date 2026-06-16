/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package com.example.project30;

import database.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class LoginController implements Initializable {

    @FXML
    private TextField usuarioCampo;
    @FXML
    private PasswordField senhaCampo;
    @FXML
    private Button btnEntrar;

    @FXML
    private void hoverOn(MouseEvent e) {
        btnEntrar.setStyle("-fx-background-color: #166b38; -fx-text-fill: white;");
    }

    @FXML
    private void hoverOff(MouseEvent e) {
        btnEntrar.setStyle("-fx-background-color: #1C8D48; -fx-text-fill: white;");
    }

    @FXML
    private void btnEntrarAction(ActionEvent event) {
        String usuario = usuarioCampo.getText();
        String senha = senhaCampo.getText();

        if (usuario.trim().isEmpty() || senha.isEmpty()) {
            mostrarAlerta("Informação", "Preencha os dados!");
            return;
        }

        String sql = "SELECT * FROM usuarios WHERE email = ? AND senha = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario);
            stmt.setString(2, senha);

            var rs = stmt.executeQuery();

            if (rs.next()) {
                // Cria o objeto User com os dados do banco
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("nif"),
                        rs.getString("telefone")
                );

                // 🔥 ARMAZENA NA SESSÃO GLOBAL
                SessaoUsuario.getInstancia().setUsuario(user);

                mostrarAlerta("Sucesso", "Logado com sucesso");

                // Abre o dashboard (não precisa mais passar o user)
                openDashboard(event);

            } else {
                mostrarAlerta("Erro", "Usuário ou senha incorretos!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao conectar no banco!");
        }
    }

    public void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void openDashboard(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("dashboard.fxml")
        );
        Parent root = loader.load();

        // Não precisa mais enviar o user via controller
        // O DashboardController acessará diretamente SessaoUsuario

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.show();
    }

    @FXML
    private void irParaCadastro(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        ScreenManager.changeScreen(stage, "cadastrofuncio.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialização, se necessário
    }
}