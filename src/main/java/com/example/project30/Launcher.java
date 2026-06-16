package com.example.project30;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class Launcher {

    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("splash.fxml"));
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);

        // Aqui você define tela cheia
        primaryStage.setMaximized(true);   // janela maximizada
          // modo fullscreen

        primaryStage.show();

    }
    public static void main(String[] args) {












        Application.launch(HelloApplication.class, args);
    }
}
