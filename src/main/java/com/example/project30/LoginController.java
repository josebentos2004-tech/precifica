package com.example.project30;

import database.Conexao;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

/**
 * Controller de Login adaptado estritamente com o bloqueio de espaços maliciosos
 */
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

        // REGRA: Bloqueio de Espaços Vazios Maliciosos no login
        if (usuario == null || usuario.trim().isEmpty() || senha == null || senha.isEmpty()) {
            mostrarAlerta("Informação", "Preencha os dados!");
            return;
        }

        String sql = "SELECT * FROM usuarios WHERE email = ? AND senha = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.trim());
            stmt.setString(2, senha);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("nif")
                );

                openDashboard(event, user);

            } else {
                mostrarAlerta("Erro", "Usuário ou senha incorretos!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao conectar no banco!");
        }
    }

    public void mostrarAlerta(String titulo, String msg){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void openDashboard(ActionEvent event, User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
        Parent root = loader.load();

        DashboardController controller = loader.getController();
        controller.setUser(user);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void irParaCadastro(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("cadastrofuncio.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {}
}