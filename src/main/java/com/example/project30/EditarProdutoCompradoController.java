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

public class EditarProdutoCompradoController implements Initializable {

    @FXML private VBox rootContainer;
    @FXML private TextField txtproduto;
    @FXML private TextField txtNumCaixas;
    @FXML private TextField txtUnidPorCaixa;
    @FXML private Label lblQuantidadeTotal;
    @FXML private TextField txtCompra;
    @FXML private TextField txtTransporte;
    @FXML private TextField txtEmbalagem;
    @FXML private TextField txtEnergia;
    @FXML private VBox customCostsContainer;
    @FXML private Slider sliderLucro;
    @FXML private Label lblLucroPercentual;
    @FXML private Label lblPrecoMinimo;
    @FXML private Label lblPrecoRecomendado;
    @FXML private Label lblLucroUnidade;
    @FXML private Label lblMargemLucro;
    @FXML private Button btnAnterior;
    @FXML private Button btnAtualizar;

    private final ObservableList<CustomCostRow> customCostRows = FXCollections.observableArrayList();
    private double custoTotal = 0;
    private double lucroPercentual = 45;
    private int idProdutoEdicao;

    private static class CustomCostRow {
        TextField nameField;
        TextField valueField;
        HBox rowBox;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        aplicarFiltroNumerico(txtNumCaixas);
        aplicarFiltroNumerico(txtUnidPorCaixa);
        aplicarFiltroNumerico(txtCompra);
        aplicarFiltroNumerico(txtTransporte);
        aplicarFiltroNumerico(txtEmbalagem);
        aplicarFiltroNumerico(txtEnergia);

        txtNumCaixas.textProperty().addListener((obs, old, novo) -> calcularQuantidadeTotal());
        txtUnidPorCaixa.textProperty().addListener((obs, old, novo) -> calcularQuantidadeTotal());

        txtCompra.textProperty().addListener((obs, old, novo) -> calcularResultado());
        txtTransporte.textProperty().addListener((obs, old, novo) -> calcularResultado());
        txtEmbalagem.textProperty().addListener((obs, old, novo) -> calcularResultado());
        txtEnergia.textProperty().addListener((obs, old, novo) -> calcularResultado());

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

    private void calcularQuantidadeTotal() {
        try {
            double caixas = converterParaDouble(txtNumCaixas.getText());
            double unidPorCaixa = converterParaDouble(txtUnidPorCaixa.getText());
            double total = caixas * unidPorCaixa;
            lblQuantidadeTotal.setText(String.format("%.0f", total));
        } catch (Exception e) {
            lblQuantidadeTotal.setText("0");
        }
    }

    private void calcularResultado() {
        try {
            double compra = converterParaDouble(txtCompra.getText());
            double transporte = converterParaDouble(txtTransporte.getText());
            double embalagem = converterParaDouble(txtEmbalagem.getText());
            double energia = converterParaDouble(txtEnergia.getText());

            double somaFixos = compra + transporte + embalagem + energia;

            double somaDinamicos = 0.0;
            for (CustomCostRow row : customCostRows) {
                somaDinamicos += converterParaDouble(row.valueField.getText());
            }

            custoTotal = somaFixos + somaDinamicos;
            double quantidadeTotal = converterParaDouble(lblQuantidadeTotal.getText());
            if (quantidadeTotal <= 0) quantidadeTotal = 1;

            double precoUnitario = custoTotal / quantidadeTotal;
            double precoMinimo = precoUnitario;
            double precoRecomendado = precoUnitario * (1 + lucroPercentual / 100.0);
            double lucroUnidade = precoRecomendado - precoUnitario;

            lblPrecoMinimo.setText(String.format("%.2f Kz", precoMinimo));
            lblPrecoRecomendado.setText(String.format("%.2f Kz", precoRecomendado));
            lblLucroUnidade.setText(String.format("%.2f Kz", lucroUnidade));
            lblMargemLucro.setText(lucroPercentual + "%");
        } catch (Exception e) { }
    }

    // Custos personalizados
    @FXML
    private void adicionarLinhaCusto() {
        CustomCostRow row = new CustomCostRow();
        row.nameField = new TextField();
        row.nameField.setPromptText("Ex: Armazenagem, Seguro");
        row.nameField.getStyleClass().add("custom-text-field");

        row.valueField = new TextField("0,00");
        row.valueField.getStyleClass().add("custom-text-field");
        aplicarFiltroNumerico(row.valueField);
        row.valueField.textProperty().addListener((obs, old, novo) -> calcularResultado());

        Button btnRemover = new Button();
        btnRemover.setGraphic(new FontIcon("bi-trash"));
        btnRemover.getStyleClass().add("remove-cost-button");
        btnRemover.setOnAction(e -> removerLinhaCusto(row));

        row.rowBox = new HBox(10);
        row.rowBox.getChildren().addAll(row.nameField, row.valueField, btnRemover);
        HBox.setHgrow(row.nameField, Priority.ALWAYS);
        HBox.setHgrow(row.valueField, Priority.ALWAYS);

        customCostsContainer.getChildren().add(row.rowBox);
        customCostRows.add(row);
    }

    private void removerLinhaCusto(CustomCostRow row) {
        customCostsContainer.getChildren().remove(row.rowBox);
        customCostRows.remove(row);
        calcularResultado();
    }

    private void adicionarLinhaExistente(String nome, double valor) {
        CustomCostRow row = new CustomCostRow();
        row.nameField = new TextField(nome);
        row.nameField.getStyleClass().add("custom-text-field");
        row.valueField = new TextField(String.format("%.2f", valor).replace(".", ","));
        row.valueField.getStyleClass().add("custom-text-field");
        aplicarFiltroNumerico(row.valueField);
        row.valueField.textProperty().addListener((obs, old, novo) -> calcularResultado());

        Button btnRemover = new Button();
        btnRemover.setGraphic(new FontIcon("bi-trash"));
        btnRemover.getStyleClass().add("remove-cost-button");
        btnRemover.setOnAction(e -> removerLinhaCusto(row));

        row.rowBox = new HBox(10);
        row.rowBox.getChildren().addAll(row.nameField, row.valueField, btnRemover);
        HBox.setHgrow(row.nameField, Priority.ALWAYS);
        HBox.setHgrow(row.valueField, Priority.ALWAYS);

        customCostsContainer.getChildren().add(row.rowBox);
        customCostRows.add(row);
    }

    // Carregar dados do produto do banco
    private void carregarDadosProduto() {
        // Produto
        String sqlProduto = "SELECT nome, quantidade FROM Produtos WHERE id = ? AND deleted_at IS NULL";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sqlProduto)) {
            stmt.setInt(1, idProdutoEdicao);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                txtproduto.setText(rs.getString("nome"));
                int qtdTotal = rs.getInt("quantidade");
                txtNumCaixas.setText("1");
                txtUnidPorCaixa.setText(String.valueOf(qtdTotal));
                calcularQuantidadeTotal();
            } else {
                throw new Exception("Produto não encontrado.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erro ao carregar produto: " + e.getMessage()).showAndWait();
            voltarParaListagem();
            return;
        }

        // Custos
        String sqlCustos = "SELECT nome_custo, v_total FROM Custos WHERE id_produto = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sqlCustos)) {
            stmt.setInt(1, idProdutoEdicao);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String nome = rs.getString("nome_custo");
                double valor = rs.getDouble("v_total");
                switch (nome) {
                    case "Custo de compra": txtCompra.setText(String.format("%.2f", valor).replace(".", ",")); break;
                    case "Transporte": txtTransporte.setText(String.format("%.2f", valor).replace(".", ",")); break;
                    case "Embalagem": txtEmbalagem.setText(String.format("%.2f", valor).replace(".", ",")); break;
                    case "Energia": txtEnergia.setText(String.format("%.2f", valor).replace(".", ",")); break;
                    default: adicionarLinhaExistente(nome, valor);
                }
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

    // Atualizar produto
    @FXML
    private void atualizarProduto() {
        try {
            if (txtproduto.getText().trim().isEmpty())
                throw new Exception("Preencha o Nome do Produto!");
            double quantidadeTotal = converterParaDouble(lblQuantidadeTotal.getText());
            if (quantidadeTotal <= 0)
                throw new Exception("Quantidade total deve ser maior que zero!");
            if (custoTotal <= 0)
                throw new Exception("Informe pelo menos um custo positivo!");

            double precoCusto = custoTotal / quantidadeTotal;
            double precoVenda = precoCusto * (1 + lucroPercentual / 100.0);

            Connection conn = null;
            try {
                conn = Conexao.conectar();
                conn.setAutoCommit(false);

                // Atualizar produto
                String sqlUpdProd = "UPDATE Produtos SET nome=?, quantidade=?, preco_produto=? WHERE id=? AND id_usuario=?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlUpdProd)) {
                    stmt.setString(1, txtproduto.getText().trim());
                    stmt.setInt(2, (int) quantidadeTotal);
                    stmt.setDouble(3, precoVenda);
                    stmt.setInt(4, idProdutoEdicao);
                    stmt.setInt(5, SessaoUsuario.getInstancia().getId());
                    stmt.executeUpdate();
                }

                // Remover custos antigos
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Custos WHERE id_produto = ?")) {
                    stmt.setInt(1, idProdutoEdicao);
                    stmt.executeUpdate();
                }

                // Inserir novos custos
                String sqlInsCusto = "INSERT INTO Custos (id_produto, nome_custo, v_total) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sqlInsCusto)) {
                    Object[][] fixos = {
                            {"Custo de compra", converterParaDouble(txtCompra.getText())},
                            {"Transporte", converterParaDouble(txtTransporte.getText())},
                            {"Embalagem", converterParaDouble(txtEmbalagem.getText())},
                            {"Energia", converterParaDouble(txtEnergia.getText())}
                    };
                    for (Object[] fixo : fixos) {
                        double val = (double) fixo[1];
                        if (val > 0) {
                            stmt.setInt(1, idProdutoEdicao);
                            stmt.setString(2, (String) fixo[0]);
                            stmt.setDouble(3, val);
                            stmt.addBatch();
                        }
                    }
                    for (CustomCostRow row : customCostRows) {
                        String nome = row.nameField.getText().trim();
                        if (nome.isEmpty()) continue;
                        double val = converterParaDouble(row.valueField.getText());
                        if (val <= 0) continue;
                        stmt.setInt(1, idProdutoEdicao);
                        stmt.setString(2, nome);
                        stmt.setDouble(3, val);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }

                // Atualizar precificação
                String sqlUpdPrec = "UPDATE Precificacao SET margem_selecionada=?, preco_custo=? WHERE id_produto=?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlUpdPrec)) {
                    stmt.setDouble(1, lucroPercentual);
                    stmt.setDouble(2, precoCusto);
                    stmt.setInt(3, idProdutoEdicao);
                    stmt.executeUpdate();
                }

                conn.commit();
                new Alert(Alert.AlertType.INFORMATION, "Produto atualizado com sucesso!").showAndWait();
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

    // Voltar para a listagem
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