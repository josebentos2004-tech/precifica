package com.example.project30;

import database.Conexao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Reciclagem implements Initializable {

    @FXML private TableView<ProdutoModel> tabelaProdutos;
    @FXML private Label totalExcluidos;
    @FXML private TextField nomeProduto;
    @FXML private TableColumn<ProdutoModel, Void> colAcoes;
    @FXML private TableColumn<ProdutoModel, Integer> colId;
    @FXML private TableColumn<ProdutoModel, String> colNome;
    @FXML private TableColumn<ProdutoModel, Double> colCusto;
    @FXML private TableColumn<ProdutoModel, Double> colVenda;
    @FXML private TableColumn<ProdutoModel, Double> colMargem;
    @FXML private TableColumn<ProdutoModel, Double> colLucro;

    private int getIdUsuarioLogado() {
        return SessaoUsuario.getInstancia().getId();
    }

    @FXML
    public void pesquisarProduto() {
        String nome = nomeProduto.getText();
        int idUsuario = getIdUsuarioLogado();

        String sql = """
            SELECT 
                p.id,
                p.nome,
                p.preco_produto AS venda,
                pr.preco_custo AS custo,
                pr.margem_selecionada AS margem,
                ROUND((pr.preco_custo * pr.margem_selecionada / 100), 2) AS lucro
            FROM Produtos p
            LEFT JOIN Precificacao pr ON pr.id_produto = p.id
            WHERE p.nome LIKE ?
              AND p.id_usuario = ?
              AND p.deleted_at IS NOT NULL
              AND (pr.deleted_at IS NULL OR pr.deleted_at IS NOT NULL)
            ORDER BY p.deleted_at DESC
        """;

        ObservableList<ProdutoModel> lista = FXCollections.observableArrayList();

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%");
            stmt.setInt(2, idUsuario);
            ResultSet rs = stmt.executeQuery();
            int c = 0;
            while (rs.next()) {
                c++;
                lista.add(new ProdutoModel(
                        rs.getInt("id"),
                        c,
                        rs.getString("nome"),
                        rs.getDouble("custo"),
                        rs.getDouble("venda"),
                        rs.getDouble("margem"),
                        rs.getDouble("lucro")
                ));
            }
            tabelaProdutos.setItems(lista);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void carregarTotais() {
        int idUsuario = getIdUsuarioLogado();
        String sql = "SELECT COUNT(*) AS total FROM Produtos WHERE id_usuario = ? AND deleted_at IS NOT NULL";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                totalExcluidos.setText(String.valueOf(rs.getInt("total")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void carregarDados() {
        int idUsuario = getIdUsuarioLogado();
        String sql = """
            SELECT 
                p.id,
                p.nome,
                p.preco_produto AS venda,
                pr.preco_custo AS custo,
                pr.margem_selecionada AS margem,
                ROUND((pr.preco_custo * pr.margem_selecionada / 100), 2) AS lucro
            FROM Produtos p
            LEFT JOIN Precificacao pr ON pr.id_produto = p.id
            WHERE p.id_usuario = ?
              AND p.deleted_at IS NOT NULL
            ORDER BY p.deleted_at DESC
        """;

        ObservableList<ProdutoModel> lista = FXCollections.observableArrayList();

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            int c = 0;
            while (rs.next()) {
                c++;
                lista.add(new ProdutoModel(
                        rs.getInt("id"),
                        c,
                        rs.getString("nome"),
                        rs.getDouble("custo"),
                        rs.getDouble("venda"),
                        rs.getDouble("margem"),
                        rs.getDouble("lucro")
                ));
            }
            tabelaProdutos.setItems(lista);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void restaurarProduto(ProdutoModel produto) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar restauração");
        confirm.setHeaderText("Restaurar produto \"" + produto.getNome() + "\"?");
        confirm.setContentText("O produto voltará a aparecer na listagem principal. Deseja continuar?");

        confirm.showAndWait().ifPresent(resposta -> {
            if (resposta == ButtonType.OK) {
                int idProduto = produto.getId();
                int idUsuario = getIdUsuarioLogado();

                String sqlProduto = "UPDATE produtos SET deleted_at = NULL WHERE id = ? AND id_usuario = ?";
                String sqlPrecificacao = "UPDATE precificacao SET deleted_at = NULL WHERE id_produto = ?";

                Connection conn = null;
                try {
                    conn = Conexao.conectar();
                    conn.setAutoCommit(false);

                    try (PreparedStatement stmtProd = conn.prepareStatement(sqlProduto)) {
                        stmtProd.setInt(1, idProduto);
                        stmtProd.setInt(2, idUsuario);
                        stmtProd.executeUpdate();
                    }

                    try (PreparedStatement stmtPrec = conn.prepareStatement(sqlPrecificacao)) {
                        stmtPrec.setInt(1, idProduto);
                        stmtPrec.executeUpdate();
                    }

                    conn.commit();
                    new Alert(Alert.AlertType.INFORMATION, "Produto restaurado com sucesso!").showAndWait();
                    carregarDados();
                    carregarTotais();
                } catch (SQLException e) {
                    if (conn != null) {
                        try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
                    }
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Erro ao restaurar produto: " + e.getMessage()).showAndWait();
                } finally {
                    if (conn != null) { try { conn.close(); } catch (SQLException e) { } }
                }
            }
        });
    }



    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("position"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCusto.setCellValueFactory(new PropertyValueFactory<>("custo"));
        colVenda.setCellValueFactory(new PropertyValueFactory<>("venda"));
        colMargem.setCellValueFactory(new PropertyValueFactory<>("margem"));
        colLucro.setCellValueFactory(new PropertyValueFactory<>("lucro"));

        colAcoes.setCellFactory(param -> new TableCell<>() {
            private final Button btnRestaurar = new Button("↺ Restaurar");

                      private final HBox box = new HBox(10, btnRestaurar);

            {
                btnRestaurar.setStyle("-fx-background-color: #10b981; -fx-text-fill: white;");


                btnRestaurar.setOnAction(event -> {
                    ProdutoModel produto = getTableView().getItems().get(getIndex());
                    if (produto != null) restaurarProduto(produto);
                });


            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
            }
        });

        nomeProduto.setOnAction(event -> pesquisarProduto());

        carregarDados();
        carregarTotais();
    }
}