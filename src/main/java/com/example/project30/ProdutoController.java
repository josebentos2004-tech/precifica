package com.example.project30;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import database.Conexao;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.sql.*;

public class ProdutoController {

    @FXML private TableView<ProdutoModel> tabelaProdutos;

    @FXML private Label totalProdutos;
    @FXML private Label custoTotal;
    @FXML private Label vendaTotal;
    @FXML private Label lucroTotal;
    @FXML private TextField nomeProduto;

    @FXML private TableColumn<ProdutoModel, Void> colAcoes;
    @FXML private TableColumn<ProdutoModel, Integer> colId;
    @FXML private TableColumn<ProdutoModel, String> colNome;
    @FXML private TableColumn<ProdutoModel, Double> colCusto;
    @FXML private TableColumn<ProdutoModel, Double> colVenda;
    @FXML private TableColumn<ProdutoModel, Double> colMargem;
    @FXML private TableColumn<ProdutoModel, Double> colLucro;

    // Obtém o ID do usuário logado (você já tem a classe SessaoUsuario)
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
              AND p.deleted_at IS NULL
              AND pr.deleted_at IS NULL
        """;

        ObservableList<ProdutoModel> lista = FXCollections.observableArrayList();

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nome + "%");
            stmt.setInt(2, idUsuario);

            ResultSet rs = stmt.executeQuery();
            int c=0;

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

        String sql = """
            SELECT 
                COUNT(p.id) AS total_produtos,
                SUM(pr.preco_custo) AS custo_total,
                SUM(p.preco_produto) AS venda_total,
                SUM(ROUND(pr.preco_custo * pr.margem_selecionada / 100, 2)) AS lucro_total
            FROM Produtos p
            LEFT JOIN Precificacao pr ON pr.id_produto = p.id
            WHERE p.id_usuario = ?
              AND p.deleted_at IS NULL
              AND pr.deleted_at IS NULL
        """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                totalProdutos.setText(String.valueOf(rs.getInt("total_produtos")));
                custoTotal.setText(String.format("%.2f Kz", rs.getDouble("custo_total")));
                vendaTotal.setText(String.format("%.2f Kz", rs.getDouble("venda_total")));
                lucroTotal.setText(String.format("%.2f Kz", rs.getDouble("lucro_total")));
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
            JOIN Precificacao pr ON pr.id_produto = p.id
            WHERE p.id_usuario = ?
              AND p.deleted_at IS NULL
              AND pr.deleted_at IS NULL
            order by id desc
        """;

        ObservableList<ProdutoModel> lista = FXCollections.observableArrayList();

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            int c=0;
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

    private void editarProduto(ProdutoModel produto) {
        int idProduto = produto.getId();
        String tipo = obterTipoProduto(idProduto);
        System.out.println("ID:"+idProduto);

        if (tipo == null) {
            new Alert(Alert.AlertType.ERROR, "Não foi possível identificar o tipo do produto.").showAndWait();
            return;
        }

        SessaoEdicao.getInstancia().setIdProdutoEdicao(idProduto);
        SessaoEdicao.getInstancia().setTipoProdutoEdicao(tipo);

        try {
            String fxml;
            if ("comprado".equals(tipo)) {
                fxml = "/com/example/project30/editar_produto_comprado.fxml";
            } else if ("fabricado".equals(tipo)) {
                fxml = "/com/example/project30/editar_produto_fabricado.fxml";
            } else {
                throw new Exception("Tipo de produto inválido: " + tipo);
            }

            Parent editScreen = FXMLLoader.load(getClass().getResource(fxml));
            // Obtém o StackPane que contém a tabela (deve existir no FXML da listagem)
            StackPane conteudoDinamico = (StackPane) tabelaProdutos.getScene().lookup("#conteudoDinamico");
            conteudoDinamico.getChildren().setAll(editScreen);

            // ⚠️ Não chame carregarDados() aqui – a listagem sumiu, então não faz sentido.
            // O recarregamento será feito pelo controller da edição, quando o usuário voltar.

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erro ao abrir tela de edição: " + e.getMessage()).showAndWait();
        }
    }

    // Método auxiliar que consulta o tipo diretamente no banco
    private String obterTipoProduto(int idProduto) {
        String sql = "SELECT tipo FROM produtos WHERE id = ? AND deleted_at IS NULL";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProduto);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("tipo");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void eliminarProduto(ProdutoModel produto) {
        int idUsuario = getIdUsuarioLogado();
        int idProduto = produto.getId();

        String sqlProduto = "UPDATE produtos SET deleted_at = NOW() WHERE id = ? AND id_usuario = ?";
        String sqlPrecificacao = "UPDATE precificacao SET deleted_at = NOW() WHERE id_produto = ?";

        Connection conn = null;
        PreparedStatement stmtProduto = null;
        PreparedStatement stmtPrecificacao = null;

        try {
            conn = Conexao.conectar();
            conn.setAutoCommit(false); // inicia transação

            // 1. Marca produto como deletado
            stmtProduto = conn.prepareStatement(sqlProduto);
            stmtProduto.setInt(1, idProduto);
            stmtProduto.setInt(2, idUsuario);
            int rowsProduto = stmtProduto.executeUpdate();

            // 2. Marca a precificação associada como deletada (se existir)
            stmtPrecificacao = conn.prepareStatement(sqlPrecificacao);
            stmtPrecificacao.setInt(1, idProduto);
            int rowsPrecificacao = stmtPrecificacao.executeUpdate();

            if (rowsProduto > 0) {
                conn.commit(); // confirma ambas as atualizações
                // Recarrega a tabela e os totais
                carregarDados();
                carregarTotais();
            } else {
                conn.rollback(); // nenhum produto foi afetado, desfaz tudo
                System.out.println("Produto não pertence ao usuário ou já foi excluído.");
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            // Fecha recursos na ordem inversa
            try { if (stmtPrecificacao != null) stmtPrecificacao.close(); } catch (SQLException e) {}
            try { if (stmtProduto != null) stmtProduto.close(); } catch (SQLException e) {}
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException e) {}
        }
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("position"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCusto.setCellValueFactory(new PropertyValueFactory<>("custo"));
        colVenda.setCellValueFactory(new PropertyValueFactory<>("venda"));
        colMargem.setCellValueFactory(new PropertyValueFactory<>("margem"));
        colLucro.setCellValueFactory(new PropertyValueFactory<>("lucro"));

        colAcoes.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("✏");
            private final Button btnEliminar = new Button("🗑");
            private final HBox box = new HBox(10, btnEditar, btnEliminar);

            {
                btnEditar.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white;");
                btnEliminar.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white;");

                btnEditar.setOnAction(event -> {
                    ProdutoModel produto = getTableView().getItems().get(getIndex());
                    if (produto != null) editarProduto(produto);
                });

                btnEliminar.setOnAction(event -> {
                    ProdutoModel produto = getTableView().getItems().get(getIndex());
                    if (produto == null) return;

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Confirmar exclusão");
                    confirm.setHeaderText("Eliminar \"" + produto.getNome() + "\"?");
                    confirm.setContentText("Esta ação não pode ser desfeita.");

                    confirm.showAndWait().ifPresent(resposta -> {
                        if (resposta == ButtonType.OK) eliminarProduto(produto);
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
            }
        });

        nomeProduto.setOnAction(event -> pesquisarProduto());

        // Carrega dados iniciais apenas do usuário logado
        this.carregarDados();
        this.carregarTotais();
    }
}