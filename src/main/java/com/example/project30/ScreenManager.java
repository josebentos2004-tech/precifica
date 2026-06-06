package com.example.project30;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ScreenManager {
    public    static  void  mostrarAlerta(String titulo, String msg){
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void changeScreen(Stage stage, String fxml) {
        try {
            Parent root = FXMLLoader.load(ScreenManager.class.getResource(fxml));
            Scene newScene = new Scene(root);

            // Transição suave: fade in
            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            // Aplica a nova cena
            stage.setScene(newScene);

            // Abre maximizado (sem mensagem ESC)


            stage.show();

            // Inicia a animação7
            fadeIn.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
