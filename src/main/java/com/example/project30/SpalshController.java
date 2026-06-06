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
        Stage stage =
                (Stage) ((Node) event.getSource())
                        .getScene()
                        .getWindow();

        ScreenManager.changeScreen(
                stage,
                "login.fxml"
        );
    }
}

