package com.example.project30;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.IOException;

public class SpalshController {
    @FXML
    private Button init;
    @FXML
    private  void  btnInit(ActionEvent event) throws IOException {
        System.out.println("Clicado");
        init.setText("Entrando...");
        FXMLLoader loader=new FXMLLoader(getClass().getResource("cadastrofuncio.fxml"));
        Parent root=loader.load();
        //2
        Stage stage=(Stage) ((Node) event.getSource()).getScene().getWindow();
        //3
        Scene scene=new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}

