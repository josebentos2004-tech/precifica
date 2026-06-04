package com.example.project30;

import database.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Controller de Cadastro com as regras estritas solicitadas para o Precifica
 */
public class CadastrofuncioController implements Initializable {

    @FXML
    private TextField username;
    @FXML
    private TextField email;
    @FXML
    private TextField nif;
    @FXML
    private TextField s1;
    @FXML
    private TextField s2;

    @FXML
    private void fazerLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void cadastrar(ActionEvent event) throws IOException {
        String nome = username.getText();
        String emaiL = email.getText();
        String niF = nif.getText();
        String ps1 = s1.getText();
        String ps2 = s2.getText();
        String tipo = "Vendedor";

        // REGRA: Bloqueio de Espaços Vazios Maliciosos e preenchimento obrigatório
        if (nome == null || nome.trim().isEmpty() ||
                emaiL == null || emaiL.trim().isEmpty() ||
                niF == null || niF.trim().isEmpty() ||
                ps1 == null || ps1.isEmpty() ||
                ps2 == null || ps2.isEmpty()) {

            mostrarAlerta("Informação", "Preencha os dados!");
            return;
        }

        // REGRA: Validação de Formato de E-mail (deve conter @ e terminar com um domínio válido)
        String emailLower = emaiL.trim().toLowerCase();
        if (!emailLower.contains("@") ||
                !(emailLower.endsWith(".com") || emailLower.endsWith(".net") || emailLower.endsWith(".org") || emailLower.endsWith(".ao"))) {
            mostrarAlerta("Erro", "O e-mail inserido é inválido! Deve conter '@' e terminar com um domínio válido (ex: .com, .net).");
            return;
        }

        // REGRA: Segurança de Senha Curta (menos de 4 caracteres)
        if (ps1.length() < 4) {
            mostrarAlerta("Erro", "A senha é muito curta! Deve conter pelo menos 4 caracteres.");
            return;
        }

        // Validação básica se as senhas coincidem
        if (!ps1.equals(ps2)) {
            mostrarAlerta("Erro", "As senhas não coincidem!");
            return;
        }

        // REGRA: Verificação de Duplicados (NIF e E-mail) com redirecionamento para o login
        String sqlVerificar = "SELECT id FROM usuarios WHERE email = ? OR nif = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmtCheck = conn.prepareStatement(sqlVerificar)) {

            stmtCheck.setString(1, emaiL.trim());
            stmtCheck.setString(2, niF.trim());
            ResultSet rs = stmtCheck.executeQuery();

            if (rs.next()) {
                mostrarAlerta("Conta Já Existente", "Esta conta já existe no sistema! Redirecionando para a tela de login...");
                fazerLogin(event);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Falha ao verificar dados existentes.");
            return;
        }

        // Inserção normal caso passe em todas as validações anteriores
        String sqlInsert = "INSERT INTO usuarios (nome,email,senha,tipo,nif) VALUES(?,?,?,?,?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement smt = conn.prepareStatement(sqlInsert)) {

            smt.setString(1, nome.trim());
            smt.setString(2, emaiL.trim());
            smt.setString(3, ps1);
            smt.setString(4, tipo);
            smt.setString(5, niF.trim());

            smt.executeUpdate();

            User user = new User(0, nome, emaiL, niF);

            if (user.getNome() != null && !user.getNome().isEmpty()) {
                redirectDashboard(event, "dashboard.fxml", user);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Falha ao inserir");
        }
    }

    public void mostrarAlerta(String titulo, String msg){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void redirectDashboard(ActionEvent event, String archive, User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(archive));
        Parent root = loader.load();

        DashboardController controller = loader.getController();
        controller.setUser(user);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {}
}