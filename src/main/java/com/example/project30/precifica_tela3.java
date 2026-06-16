package com.example.project30;

import database.Conexao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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

public class precifica_tela3 implements Initializable {

    @FXML private VBox rootContainer;
    @FXML private TextField txtproduto;
    @FXML private TextField txtQuantidadeTotal;
    @FXML private Slider sliderLucro;
    @FXML private Label lblLucroPercentual;
    @FXML private Button btnAnterior;
    @FXML private Button btnSeguinte;
    @FXML private Label lblPrecoMinimo;
    @FXML private Label lblPrecoRecomendado;
    @FXML private Label lblLucroUnidade;
    @FXML private Label lblMargemLucro;
    @FXML private VBox insumosContainer;
    @FXML private VBox custosContainer;

    private final ObservableList<InsumoRow> insumoRows = FXCollections.observableArrayList();
    private final ObservableList<CustoRow> custoRows = FXCollections.observableArrayList();

    private double custoTotal = 0;
    private double lucroPercentual = 45;

    // Classes auxiliares para as linhas dinâmicas
    private static class InsumoRow {
        TextField nomeField;
        TextField valorField;
        HBox rowBox;
    }

    private static class CustoRow {
        TextField nomeField;
        TextField valorField;
        HBox rowBox;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Filtro numérico para quantidade
        aplicarFiltroNumerico(txtQuantidadeTotal);

        // Listener da quantidade
        txtQuantidadeTotal.textProperty().addListener((obs, old, novo) -> calcularResultado());

        // Slider de lucro
        sliderLucro.valueProperty().addListener((obs, old, novo) -> {
            lucroPercentual = novo.intValue();
            lblLucroPercentual.setText(lucroPercentual + "%");
            calcularResultado();
        });

        // Criar linha padrão para Insumos
        criarLinhaInsumoPadrao();
        // Criar linha padrão para Custos
        criarLinhaCustoPadrao();

        calcularResultado();

        // Carregar CSS
        try {
            String cssPath = getClass().getResource("css/precifica_tela2.css").toExternalForm();
            rootContainer.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.err.println("Erro ao carregar CSS: " + e.getMessage());
        }
    }

    // Filtro numérico (apenas dígitos e vírgula/ponto)
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

    // ---- CRIAÇÃO DAS LINHAS PADRÃO ----
    private void criarLinhaInsumoPadrao() {
        InsumoRow row = new InsumoRow();
        row.nomeField = new TextField();
        row.nomeField.setPromptText("Ex: Farinha");
        row.nomeField.getStyleClass().add("custom-text-field");

        row.valorField = new TextField("0,00");
        row.valorField.getStyleClass().add("custom-text-field");
        aplicarFiltroNumerico(row.valorField);
        row.valorField.textProperty().addListener((obs, old, novo) -> calcularResultado());

        Button btnRemover = new Button();
        FontIcon trashIcon = new FontIcon("bi-trash");
        trashIcon.setIconSize(14);
        btnRemover.setGraphic(trashIcon);
        btnRemover.getStyleClass().add("remove-cost-button");
        btnRemover.setOnAction(e -> removerInsumo(row));

        row.rowBox = new HBox(10);
        row.rowBox.getChildren().addAll(row.nomeField, row.valorField, btnRemover);
        HBox.setHgrow(row.nomeField, Priority.ALWAYS);
        HBox.setHgrow(row.valorField, Priority.ALWAYS);

        insumosContainer.getChildren().add(row.rowBox);
        insumoRows.add(row);
    }

    private void criarLinhaCustoPadrao() {
        CustoRow row = new CustoRow();
        row.nomeField = new TextField();
        row.nomeField.setPromptText("Ex: Mão de obra");
        row.nomeField.getStyleClass().add("custom-text-field");

        row.valorField = new TextField("0,00");
        row.valorField.getStyleClass().add("custom-text-field");
        aplicarFiltroNumerico(row.valorField);
        row.valorField.textProperty().addListener((obs, old, novo) -> calcularResultado());

        Button btnRemover = new Button();
        FontIcon trashIcon = new FontIcon("bi-trash");
        trashIcon.setIconSize(14);
        btnRemover.setGraphic(trashIcon);
        btnRemover.getStyleClass().add("remove-cost-button");
        btnRemover.setOnAction(e -> removerCustoProducao(row));

        row.rowBox = new HBox(10);
        row.rowBox.getChildren().addAll(row.nomeField, row.valorField, btnRemover);
        HBox.setHgrow(row.nomeField, Priority.ALWAYS);
        HBox.setHgrow(row.valorField, Priority.ALWAYS);

        custosContainer.getChildren().add(row.rowBox);
        custoRows.add(row);
    }

    // ---- ADICIONAR / REMOVER LINHAS ----
    @FXML
    private void adicionarInsumo() {
        InsumoRow row = new InsumoRow();
        row.nomeField = new TextField();
        row.nomeField.setPromptText("Ex: Fermento, Sal...");
        row.nomeField.getStyleClass().add("custom-text-field");

        row.valorField = new TextField("0,00");
        row.valorField.getStyleClass().add("custom-text-field");
        aplicarFiltroNumerico(row.valorField);
        row.valorField.textProperty().addListener((obs, old, novo) -> calcularResultado());

        Button btnRemover = new Button();
        FontIcon trashIcon = new FontIcon("bi-trash");
        trashIcon.setIconSize(14);
        btnRemover.setGraphic(trashIcon);
        btnRemover.getStyleClass().add("remove-cost-button");
        btnRemover.setOnAction(e -> removerInsumo(row));

        row.rowBox = new HBox(10);
        row.rowBox.getChildren().addAll(row.nomeField, row.valorField, btnRemover);
        HBox.setHgrow(row.nomeField, Priority.ALWAYS);
        HBox.setHgrow(row.valorField, Priority.ALWAYS);

        insumosContainer.getChildren().add(row.rowBox);
        insumoRows.add(row);
    }

    private void removerInsumo(InsumoRow row) {
        // Impede remover a última linha (opcional)
        if (insumoRows.size() <= 1) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Deve haver pelo menos um insumo.", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        insumosContainer.getChildren().remove(row.rowBox);
        insumoRows.remove(row);
        calcularResultado();
    }

    @FXML
    private void adicionarCustoProducao() {
        CustoRow row = new CustoRow();
        row.nomeField = new TextField();
        row.nomeField.setPromptText("Ex: Energia, Aluguel...");
        row.nomeField.getStyleClass().add("custom-text-field");

        row.valorField = new TextField("0,00");
        row.valorField.getStyleClass().add("custom-text-field");
        aplicarFiltroNumerico(row.valorField);
        row.valorField.textProperty().addListener((obs, old, novo) -> calcularResultado());

        Button btnRemover = new Button();
        FontIcon trashIcon = new FontIcon("bi-trash");
        trashIcon.setIconSize(14);
        btnRemover.setGraphic(trashIcon);
        btnRemover.getStyleClass().add("remove-cost-button");
        btnRemover.setOnAction(e -> removerCustoProducao(row));

        row.rowBox = new HBox(10);
        row.rowBox.getChildren().addAll(row.nomeField, row.valorField, btnRemover);
        HBox.setHgrow(row.nomeField, Priority.ALWAYS);
        HBox.setHgrow(row.valorField, Priority.ALWAYS);

        custosContainer.getChildren().add(row.rowBox);
        custoRows.add(row);
    }

    private void removerCustoProducao(CustoRow row) {
        if (custoRows.size() <= 1) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Deve haver pelo menos um custo de produção.", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        custosContainer.getChildren().remove(row.rowBox);
        custoRows.remove(row);
        calcularResultado();
    }

    // ---- CÁLCULO DO RESULTADO ----
    private void calcularResultado() {
        try {
            double totalInsumos = 0.0;
            for (InsumoRow row : insumoRows) {
                totalInsumos += converterParaDouble(row.valorField.getText());
            }

            double totalCustos = 0.0;
            for (CustoRow row : custoRows) {
                totalCustos += converterParaDouble(row.valorField.getText());
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
        } catch (Exception e) {
            // ignora durante digitação
        }
    }

    // ---- SALVAR PRODUTO ----
    @FXML
    void aoClicarSeguinte(ActionEvent event) {
        try {
            // Validações
            if (txtproduto.getText().trim().isEmpty())
                throw new Exception("Preencha o Nome do Produto!");
            if (custoTotal <= 0)
                throw new Exception("Adicione pelo menos um insumo ou custo de produção com valor positivo!");
            double quantidade = converterParaDouble(txtQuantidadeTotal.getText());
            if (quantidade <= 0)
                throw new Exception("Quantidade total deve ser maior que zero!");

            // Limite de 50 produtos
            if (contarProdutosDoUsuario() >= 50) {
                throw new Exception("Limite de 50 produtos atingido! Você não pode cadastrar mais produtos.");
            }

            double precoCusto = custoTotal / quantidade;
            double precoVenda = precoCusto * (1 + lucroPercentual / 100.0);
            int idUsuario = SessaoUsuario.getInstancia().getId();

            // 1. Inserir Produto
            String sqlProduto = "INSERT INTO Produtos (nome, tipo, quantidade, preco_produto, id_usuario) VALUES (?, 'fabricado', ?, ?, ?)";
            int idProduto = -1;
            try (Connection conn = Conexao.conectar();
                 PreparedStatement stmt = conn.prepareStatement(sqlProduto, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, txtproduto.getText().trim());
                stmt.setInt(2, (int) quantidade);
                stmt.setDouble(3, precoVenda);
                stmt.setInt(4, idUsuario);
                stmt.executeUpdate();
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) idProduto = rs.getInt(1);
                else throw new Exception("Erro ao obter ID do produto.");
            }

            // 2. Inserir Insumos (apenas com nome preenchido e valor > 0)
            String sqlInsumo = "INSERT INTO Insumos (id_produto, nome_insumo, v_total) VALUES (?, ?, ?)";
            try (Connection conn = Conexao.conectar();
                 PreparedStatement stmt = conn.prepareStatement(sqlInsumo)) {
                for (InsumoRow row : insumoRows) {
                    String nome = row.nomeField.getText().trim();
                    if (nome.isEmpty()) continue;
                    double valor = converterParaDouble(row.valorField.getText());
                    if (valor <= 0) continue;
                    stmt.setInt(1, idProduto);
                    stmt.setString(2, nome);
                    stmt.setDouble(3, valor);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            // 3. Inserir Custos de produção
            String sqlCusto = "INSERT INTO Custos (id_produto, nome_custo, v_total) VALUES (?, ?, ?)";
            try (Connection conn = Conexao.conectar();
                 PreparedStatement stmt = conn.prepareStatement(sqlCusto)) {
                for (CustoRow row : custoRows) {
                    String nome = row.nomeField.getText().trim();
                    if (nome.isEmpty()) continue;
                    double valor = converterParaDouble(row.valorField.getText());
                    if (valor <= 0) continue;
                    stmt.setInt(1, idProduto);
                    stmt.setString(2, nome);
                    stmt.setDouble(3, valor);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            // 4. Inserir Precificação
            String sqlPrecificacao = "INSERT INTO Precificacao (id_produto, margem_selecionada, preco_custo) VALUES (?, ?, ?)";
            try (Connection conn = Conexao.conectar();
                 PreparedStatement stmt = conn.prepareStatement(sqlPrecificacao)) {
                stmt.setInt(1, idProduto);
                stmt.setDouble(2, lucroPercentual);
                stmt.setDouble(3, precoCusto);
                stmt.executeUpdate();
            }

            ScreenManager.mostrarAlerta("Sucesso", "Produto fabricado, insumos e custos salvos com sucesso!");

            // Navegar para tela 4
            Parent novaTela = FXMLLoader.load(getClass().getResource("/com/example/project30/precifica_tela4.fxml"));
            StackPane conteudoDinamico = (StackPane) btnSeguinte.getScene().lookup("#conteudoDinamico");
            TransitionManager.trocarTela(conteudoDinamico, novaTela, TransitionManager.TransitionType.MORPH, 500);

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            e.printStackTrace();
        }
    }

    // Conta produtos ativos do usuário (não deletados)
    private int contarProdutosDoUsuario() throws SQLException {
        String sql = "SELECT COUNT(*) FROM produtos WHERE id_usuario = ? AND deleted_at IS NULL";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, SessaoUsuario.getInstancia().getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
            return 0;
        }
    }

    @FXML
    void aoClicarAnterior(ActionEvent event) {
        try {
            Parent tela2 = FXMLLoader.load(getClass().getResource("/com/example/project30/precifica_tela2.fxml"));
            StackPane conteudoDinamico = (StackPane) btnAnterior.getScene().lookup("#conteudoDinamico");
            conteudoDinamico.getChildren().setAll(tela2);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao voltar: " + e.getMessage()).showAndWait();
            e.printStackTrace();
        }
    }
}