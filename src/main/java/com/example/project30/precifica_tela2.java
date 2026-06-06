package com.example.project30;
import database.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class precifica_tela2 {

    @FXML
    private TextField txtproduto;

    @FXML
    private TextField txtquantidade;

    @FXML
    private TextField txtcusto;

    @FXML
    private Button btnAnterior;

    @FXML
    private Button btnSeguinte;

    @FXML
    public void initialize() {
        System.out.println("--> Tela 2 carregada e pronta para receber cliques!");
    }

    @FXML  // ← Adicione esta anotação
    void aoClicarSeguinte(ActionEvent event) {
        try {
            // Valida campos
            if (txtproduto.getText().trim().isEmpty())
                throw new Exception("Preencha o Nome do Produto!");
            if (txtcusto.getText().trim().isEmpty())
                throw new Exception("Preencha o Custo Total de Compra!");
            if (txtquantidade.getText().trim().isEmpty())
                throw new Exception("Preencha a Quantidade Compradas!");

            // Converte valores
            double custoTotal = Double.parseDouble(txtcusto.getText().replace(",", "."));
            double quantidade = Double.parseDouble(txtquantidade.getText().replace(",", "."));

            if (custoTotal <= 0) throw new Exception("Custo Total deve ser maior que zero!");
            if (quantidade <= 0) throw new Exception("Quantidade deve ser maior que zero!");

            double precoProduto = custoTotal / quantidade;

            // Insere no banco
            String sql = "INSERT INTO Produtos (nome, tipo, quantidade, preco_produto, id_usuario) VALUES (?, 'comprado', ?, ?, 1)";

            try (PreparedStatement stmt = Conexao.conectar().prepareStatement(sql)) {
                stmt.setString(1, txtproduto.getText().trim());
                stmt.setInt(2, (int) quantidade);
                stmt.setDouble(3, precoProduto);
                stmt.executeUpdate();

                System.out.println("Produto inserido!");
                ScreenManager.mostrarAlerta("Sucesso","Produto cadastrado com sucesso");
            }

            // Avança
            Parent tela4 = FXMLLoader.load(getClass().getResource("/com/example/project30/precifica_tela4.fxml"));
            StackPane conteudoDinamico = (StackPane) btnSeguinte.getScene().lookup("#conteudoDinamico");
            conteudoDinamico.getChildren().setAll(tela4);

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            e.printStackTrace();
        }
    }

    @FXML  // ← Adicione esta anotação
    void aoClicarAnterior(ActionEvent event) {
        try {  // ← Adicione o try-catch
            Parent tela1 = FXMLLoader.load(getClass().getResource("/com/example/project30/precifica_tela1.fxml"));  // ← Corrigido o caminho
            StackPane conteudoDinamico = (StackPane) btnSeguinte.getScene().lookup("#conteudoDinamico");
            conteudoDinamico.getChildren().setAll(tela1);
        } catch (IOException e) {  // ← Capture a exceção
            new Alert(Alert.AlertType.ERROR, "Erro ao voltar: " + e.getMessage()).showAndWait();
            e.printStackTrace();
        }
    }
}