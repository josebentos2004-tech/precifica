/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.example.project30;
import database.Conexao;
import  java.sql.Connection;
import  java.sql.PreparedStatement;
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
 * FXML Controller class
 *
 * @author PcJ
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
        //redirectDashboard(event, "login.fxml",);
        Stage stage =
                (Stage) ((Node) event.getSource())
                        .getScene()
                        .getWindow();

        ScreenManager.changeScreen(
                stage,
                "login.fxml"
        );
    }
    @FXML
    private void cadastrar(ActionEvent event) throws IOException {
        String nome = username.getText();
        String emaiL = email.getText();
        String niF = nif.getText();
        String ps1 = s1.getText();
        String ps2 = s2.getText();
        String tipo="Vendedor";
        if(!ps1.equals(ps2)){
            ScreenManager.mostrarAlerta("Erro","Verique as senhas inseridas");

            return;

        }

       else  if (nome.trim().isEmpty() || emaiL.trim().isEmpty()
                || niF.trim().isEmpty() || ps1.trim().isEmpty()
                || ps2.trim().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Informação");
            alert.setHeaderText(null);
            alert.setContentText("Preencha os dados!");


            alert.showAndWait();
            return;
        }

        String sql = "INSERT INTO usuarios (nome,email,senha,tipo,nif) VALUES(?,?,?,?,?)";

        try (
                Connection conn = Conexao.conectar();
                PreparedStatement smt = conn.prepareStatement(sql)
        ) {

            smt.setString(1, nome);
            smt.setString(2, emaiL);
            smt.setString(3, ps1);
            smt.setString(4,tipo);
            smt.setString(5,niF);

            smt.executeUpdate();
            User user=new User(0,nome,emaiL,niF);

            if(!user.getNome().isEmpty())
                // Redireciona após cadastro bem sucedido
            redirectDashboard(event,"dashboard.fxml",user);

        } catch (Exception e) {
            e.printStackTrace();
            ScreenManager.mostrarAlerta("Erro", "Falha ao inserir");
        }
    }



    //redirect dashboard
    private void redirectDashboard(ActionEvent event, String archive, User user) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(archive)
        );

        Parent root = loader.load();

        DashboardController controller = loader.getController();

        // ENVIAR USER PARA DASHBOARD
        controller.setUser(user);

        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(new Scene(root));
        stage.setFullScreen(true);
        stage.show();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
}
