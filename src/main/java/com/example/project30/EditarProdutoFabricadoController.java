package com.example.project30;

import database.Conexao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EditarProdutoFabricadoController implements Initializable {

    @FXML private VBox rootContainer;
    @FXML private TextField txtproduto;
    @FXML private TextField txtQuantidadeTotal;
    @FXML private VBox insumosContainer;
    @FXML private VBox custosContainer;
    @FXML private Slider sliderLucro;
    @FXML private Label lblLucroPercentual;
    @FXML private Label lblPrecoMinimo;
    @FXML private Label lblPrecoRecomendado;
    @FXML private Label lblLucroUnidade;
    @FXML private Label lblMargemLucro;
    @FXML private Button btnAnterior;
    @FXML private Button btnAtualizar;

    private final ObservableList<InsumoRow> insumoRows = FXCollections.observableArrayList();
    private final ObservableList<CustoRow> custoRows = FXCollections.observableArrayList();
    private double custoTotal = 0;
    private double lucroPercentual = 45;
    private int idProdutoEdicao;

    // Classes para linhas dinâmicas
    private static class InsumoRow {
        TextField nameField;
        TextField valueField;
        HBox rowBox;
    }

    private static class CustoRow {
        TextField nameField;
        TextField valueField;
        HBox rowBox;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        aplicarFiltroNumerico(txtQuantidadeTotal);
        txtQuantidadeTotal.textProperty().addListener((obs, old, novo) -> calcularResultado());

        sliderLucro.valueProperty().addListener((obs, old, novo) -> {
            lucroPercentual = novo.intValue();
            lblLucroPercentual.setText(lucroPercentual + "%");
            calcularResultado();
        });

        idProdutoEdicao = SessaoEdicao.getInstancia().getIdProdutoEdicao();
        if (idProdutoEdicao > 0) {
            carregarDadosProduto();
        } else {
            new Alert(Alert.AlertType.ERROR, "Nenhum produto selecionado para edição.").showAndWait();
            voltarParaListagem();
        }

        try {
            String cssPath = getClass().getResource("css/precifica_tela2.css").toExternalForm();
            rootContainer.getStylesheets().add(cssPath);
        } catch (Exception e) { }
    }

    // Filtros e conversões
    private void aplicarFiltroNumerico(TextField campo) {
        if (campo == null) return;
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            String novoTexto = change.getControlNewText();
            if (novoTexto.matches("\\d*([.,]\\d*)?")) return change;
            return null;
        });
        campo.setTextFormatter(formatter);
    }

    private double converterParaDouble(String valor) {
        if (valor == null || valor.trim().isEmpty()) return 0.0;
        try {
            return Double.parseDouble(valor.replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private String formatarValor(double valor) {
        return String.format("%.2f", valor).replace(".", ",");
    }

    // Cálculo do resultado
    private void calcularResultado() {
        try {
            double totalInsumos = 0.0;
            for (InsumoRow row : insumoRows) {
                totalInsumos += converterParaDouble(row.valueField.getText());
            }

            double totalCustos = 0.0;
            for (CustoRow row : custoRows) {
                totalCustos += converterParaDouble(row.valueField.getText());
            }

            custoTotal = totalInsumos + totalCustos;

            double quantidade = converterParaDouble(txtQuantidadeTotal.getText());
            if (quantidade <= 0) quantidade = 1;

            double precoUnitario = custoTotal / quantidade;
            double precoMinimo = precoUnitario;
            double precoRecomendado = precoUnitario * (1 + lucroPercentual / 100.0);
            double lucroUnidade = precoRecomendado - precoUnitario;

            lblPrecoMinimo.setText(String.format("%.2f Kz", precoMinimo));
            lblPrecoRecomendado.setText(String.format("%.2f Kz", precoRecomendado));
            lblLucroUnidade.setText(String.format("%.2f Kz", lucroUnidade));
            lblMargemLucro.setText(lucroPercentual + "%");
        } catch (Exception e) { }
    }

    // --- INSUMOS DINÂMICOS ---
    @FXML
    private void adicionarInsumo() {
        InsumoRow row = new InsumoRow();
        row.nameField = new TextField();
        row.nameField.setPromptText("Ex: Farinha, Açúcar...");
        row.nameField.getStyleClass().add("custom-text-field");

        row.valueField = new TextField("0,00");
        row.valueField.getStyleClass().add("custom-text-field");
        aplicarFiltroNumerico(row.valueField);
        row.valueField.textProperty().addListener((obs, old, novo) -> calcularResultado());

        Button btnRemover = new Button();
        btnRemover.setGraphic(new FontIcon("bi-trash"));
        btnRemover.getStyleClass().add("remove-cost-button");
        btnRemover.setOnAction(e -> removerInsumo(row));

        row.rowBox = new HBox(10);
        row.rowBox.getChildren().addAll(row.nameField, row.valueField, btnRemover);
        HBox.setHgrow(row.nameField, Priority.ALWAYS);
        HBox.setHgrow(row.valueField, Priority.ALWAYS);

        insumosContainer.getChildren().add(row.rowBox);
        insumoRows.add(row);
    }

    private void removerInsumo(InsumoRow row) {
        if (insumoRows.size() <= 1) {
            new Alert(Alert.AlertType.WARNING, "Deve haver pelo menos um insumo.").showAndWait();
            return;
        }
        insumosContainer.getChildren().remove(row.rowBox);
        insumoRows.remove(row);
        calcularResultado();
    }

    private void adicionarInsumoExistente(String nome, double valor) {
        InsumoRow row = new InsumoRow();
        row.nameField = new TextField(nome);
        row.nameField.getStyleClass().add("custom-text-field");
        row.valueField = new TextField(formatarValor(valor));
        row.valueField.getStyleClass().add("custom-text-field");
        aplicarFiltroNumerico(row.valueField);
        row.valueField.textProperty().addListener((obs, old, novo) -> calcularResultado());

        Button btnRemover = new Button();
        btnRemover.setGraphic(new FontIcon("bi-trash"));
        btnRemover.getStyleClass().add("remove-cost-button");
        btnRemover.setOnAction(e -> removerInsumo(row));

        row.rowBox = new HBox(10);
        row.rowBox.getChildren().addAll(row.nameField, row.valueField, btnRemover);
        HBox.setHgrow(row.nameField, Priority.ALWAYS);
        HBox.setHgrow(row.valueField, Priority.ALWAYS);

        insumosContainer.getChildren().add(row.rowBox);
        insumoRows.add(row);
    }

    // --- CUSTOS DINÂMICOS ---
    @FXML
    private void adicionarCustoProducao() {
        CustoRow row = new CustoRow();
        row.nameField = new TextField();
        row.nameField.setPromptText("Ex: Mão de obra, Energia...");
        row.nameField.getStyleClass().add("custom-text-field");

        row.valueField = new TextField("0,00");
        row.valueField.getStyleClass().add("custom-text-field");
        aplicarFiltroNumerico(row.valueField);
        row.valueField.textProperty().addListener((obs, old, novo) -> calcularResultado());

        Button btnRemover = new Button();
        btnRemover.setGraphic(new FontIcon("bi-trash"));
        btnRemover.getStyleClass().add("remove-cost-button");
        btnRemover.setOnAction(e -> removerCustoProducao(row));

        row.rowBox = new HBox(10);
        row.rowBox.getChildren().addAll(row.nameField, row.valueField, btnRemover);
        HBox.setHgrow(row.nameField, Priority.ALWAYS);
        HBox.setHgrow(row.valueField, Priority.ALWAYS);

        custosContainer.getChildren().add(row.rowBox);
        custoRows.add(row);
    }

    private void removerCustoProducao(CustoRow row) {
        if (custoRows.size() <= 1) {
            new Alert(Alert.AlertType.WARNING, "Deve haver pelo menos um custo de produção.").showAndWait();
            return;
        }
        custosContainer.getChildren().remove(row.rowBox);
        custoRows.remove(row);
        calcularResultado();
    }

    private void adicionarCustoExistente(String nome, double valor) {
        CustoRow row = new CustoRow();
        row.nameField = new TextField(nome);
        row.nameField.getStyleClass().add("custom-text-field");
        row.valueField = new TextField(formatarValor(valor));
        row.valueField.getStyleClass().add("custom-text-field");
        aplicarFiltroNumerico(row.valueField);
        row.valueField.textProperty().addListener((obs, old, novo) -> calcularResultado());

        Button btnRemover = new Button();
        btnRemover.setGraphic(new FontIcon("bi-trash"));
        btnRemover.getStyleClass().add("remove-cost-button");
        btnRemover.setOnAction(e -> removerCustoProducao(row));

        row.rowBox = new HBox(10);
        row.rowBox.getChildren().addAll(row.nameField, row.valueField, btnRemover);
        HBox.setHgrow(row.nameField, Priority.ALWAYS);
        HBox.setHgrow(row.valueField, Priority.ALWAYS);

        custosContainer.getChildren().add(row.rowBox);
        custoRows.add(row);
    }

    // --- CARREGAR DADOS DO BANCO ---
    private void carregarDadosProduto() {
        // Produto
        String sqlProduto = "SELECT nome, quantidade FROM Produtos WHERE id = ? AND deleted_at IS NULL";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sqlProduto)) {
            stmt.setInt(1, idProdutoEdicao);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                txtproduto.setText(rs.getString("nome"));
                txtQuantidadeTotal.setText(String.valueOf(rs.getInt("quantidade")));
            } else {
                throw new Exception("Produto não encontrado.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erro ao carregar produto: " + e.getMessage()).showAndWait();
            voltarParaListagem();
            return;
        }

        // Insumos (tabela Insumos)
        String sqlInsumos = "SELECT nome_insumo, v_total FROM Insumos WHERE id_produto = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sqlInsumos)) {
            stmt.setInt(1, idProdutoEdicao);
            ResultSet rs = stmt.executeQuery();
            boolean hasInsumos = false;
            while (rs.next()) {
                hasInsumos = true;
                String nome = rs.getString("nome_insumo");
                double valor = rs.getDouble("v_total");
                adicionarInsumoExistente(nome, valor);
            }
            if (!hasInsumos) {
                // Adiciona uma linha padrão vazia
                adicionarInsumo();
            }
        } catch (SQLException e) { e.printStackTrace(); }

        // Custos de produção (tabela Custos)
        String sqlCustos = "SELECT nome_custo, v_total FROM Custos WHERE id_produto = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sqlCustos)) {
            stmt.setInt(1, idProdutoEdicao);
            ResultSet rs = stmt.executeQuery();
            boolean hasCustos = false;
            while (rs.next()) {
                hasCustos = true;
                String nome = rs.getString("nome_custo");
                double valor = rs.getDouble("v_total");
                adicionarCustoExistente(nome, valor);
            }
            if (!hasCustos) {
                adicionarCustoProducao();
            }
        } catch (SQLException e) { e.printStackTrace(); }

        // Precificação
        String sqlPreco = "SELECT margem_selecionada FROM Precificacao WHERE id_produto = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sqlPreco)) {
            stmt.setInt(1, idProdutoEdicao);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                lucroPercentual = rs.getDouble("margem_selecionada");
                sliderLucro.setValue(lucroPercentual);
                lblLucroPercentual.setText(lucroPercentual + "%");
            }
        } catch (SQLException e) { e.printStackTrace(); }

        calcularResultado();
    }

    // --- ATUALIZAR PRODUTO ---
    @FXML
    private void atualizarProduto() {
        try {
            if (txtproduto.getText().trim().isEmpty())
                throw new Exception("Preencha o Nome do Produto!");
            double quantidade = converterParaDouble(txtQuantidadeTotal.getText());
            if (quantidade <= 0)
                throw new Exception("Quantidade total deve ser maior que zero!");
            if (custoTotal <= 0)
                throw new Exception("Adicione pelo menos um insumo ou custo de produção com valor positivo!");

            double precoCusto = custoTotal / quantidade;
            double precoVenda = precoCusto * (1 + lucroPercentual / 100.0);

            Connection conn = null;
            try {
                conn = Conexao.conectar();
                conn.setAutoCommit(false);

                // 1. Atualizar Produtos
                String sqlUpdProd = "UPDATE Produtos SET nome=?, quantidade=?, preco_produto=? WHERE id=? AND id_usuario=?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlUpdProd)) {
                    stmt.setString(1, txtproduto.getText().trim());
                    stmt.setInt(2, (int) quantidade);
                    stmt.setDouble(3, precoVenda);
                    stmt.setInt(4, idProdutoEdicao);
                    stmt.setInt(5, SessaoUsuario.getInstancia().getId());
                    stmt.executeUpdate();
                }

                // 2. Remover insumos antigos
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Insumos WHERE id_produto = ?")) {
                    stmt.setInt(1, idProdutoEdicao);
                    stmt.executeUpdate();
                }

                // 3. Inserir novos insumos
                String sqlInsInsumo = "INSERT INTO Insumos (id_produto, nome_insumo, v_total) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sqlInsInsumo)) {
                    for (InsumoRow row : insumoRows) {
                        String nome = row.nameField.getText().trim();
                        if (nome.isEmpty()) continue;
                        double valor = converterParaDouble(row.valueField.getText());
                        if (valor <= 0) continue;
                        stmt.setInt(1, idProdutoEdicao);
                        stmt.setString(2, nome);
                        stmt.setDouble(3, valor);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }

                // 4. Remover custos antigos
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Custos WHERE id_produto = ?")) {
                    stmt.setInt(1, idProdutoEdicao);
                    stmt.executeUpdate();
                }

                // 5. Inserir novos custos
                String sqlInsCusto = "INSERT INTO Custos (id_produto, nome_custo, v_total) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sqlInsCusto)) {
                    for (CustoRow row : custoRows) {
                        String nome = row.nameField.getText().trim();
                        if (nome.isEmpty()) continue;
                        double valor = converterParaDouble(row.valueField.getText());
                        if (valor <= 0) continue;
                        stmt.setInt(1, idProdutoEdicao);
                        stmt.setString(2, nome);
                        stmt.setDouble(3, valor);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }

                // 6. Atualizar precificação
                String sqlUpdPrec = "UPDATE Precificacao SET margem_selecionada=?, preco_custo=? WHERE id_produto=?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlUpdPrec)) {
                    stmt.setDouble(1, lucroPercentual);
                    stmt.setDouble(2, precoCusto);
                    stmt.setInt(3, idProdutoEdicao);
                    stmt.executeUpdate();
                }

                conn.commit();
                new Alert(Alert.AlertType.INFORMATION, "Produto fabricado atualizado com sucesso!").showAndWait();
                voltarParaListagem();

            } catch (Exception e) {
                if (conn != null) conn.rollback();
                throw e;
            } finally {
                if (conn != null) conn.close();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            e.printStackTrace();
        }
    }

    // --- VOLTAR PARA LISTAGEM ---
    @FXML
    private void voltarParaListagem() {
        try {
            Parent telaProdutos = FXMLLoader.load(getClass().getResource("/com/example/project30/produto_total.fxml"));
            StackPane conteudoDinamico = (StackPane) btnAnterior.getScene().lookup("#conteudoDinamico");
            conteudoDinamico.getChildren().setAll(telaProdutos);
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erro ao voltar: " + e.getMessage()).showAndWait();
        }
    }
}