package com.example.project30;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("splash.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);


        stage.setTitle("Precifica");
        stage.setScene(scene);
        stage.centerOnScreen();  // Centraliza a janela
        stage.setResizable(false); // Opcional: impede redimensionamento
        stage.show();
    }
}
