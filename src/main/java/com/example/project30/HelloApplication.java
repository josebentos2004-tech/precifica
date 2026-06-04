package com.example.project30;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1. Carrega o arquivo FXML da pasta de resources
        Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));

        // 2. Cria a cena sem tamanhos fixos para permitir a responsividade total
        Scene scene = new Scene(root);

        // 3. Configura o Stage (Janela principal)
        primaryStage.setTitle("Precifica - Identificação");
        primaryStage.setScene(scene);

        // 4. Comandos para preencher toda a tela automaticamente
        primaryStage.setMaximized(true); // Abre a janela maximizada

        primaryStage.show();
    }
    // O MÉTODO MAIN FICA EXATAMENTE AQUI:
    public static void main(String[] args) {
        launch(args);
    }
}
