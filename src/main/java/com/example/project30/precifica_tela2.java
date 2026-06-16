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

public class precifica_tela2 implements Initializable {

    // --- Componentes do FXML ---
    @FXML private VBox rootContainer;
    @FXML private TextField txtproduto;
    @FXML private TextField txtNumCaixas;
    @FXML private TextField txtUnidPorCaixa;
    @FXML private Label lblQuantidadeTotal;
    @FXML private TextField txtCompra;
    @FXML private TextField txtTransporte;
    @FXML private TextField txtEmbalagem;
    @FXML private TextField txtEnergia;
    @FXML private Slider sliderLucro;
    @FXML private Label lblLucroPercentual;
    @FXML private Button btnAnterior;
    @FXML private Button btnSeguinte;
    @FXML private VBox customCostsContainer;   // contêiner para linhas dinâmicas

    // Labels do resultado
    @FXML private Label lblPrecoMinimo;
    @FXML private Label lblPrecoRecomendado;
    @FXML private Label lblLucroUnidade;
    @FXML private Label lblMargemLucro;

    private double custoTotal = 0;
    private double lucroPercentual = 45;

    // Lista para armazenar as linhas dinâmicas
    private final ObservableList<CustomCostRow> customCostRows = FXCollections.observableArrayList();

    // Classe auxiliar para representar uma linha de custo personalizado
    private static class CustomCostRow {
        TextField nameField;
        TextField valueField;
        HBox rowBox;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Filtrar campos numéricos
        aplicarFiltroNumerico(txtNumCaixas);
        aplicarFiltroNumerico(txtUnidPorCaixa);
        aplicarFiltroNumerico(txtCompra);
        aplicarFiltroNumerico(txtTransporte);
        aplicarFiltroNumerico(txtEmbalagem);
        aplicarFiltroNumerico(txtEnergia);

        // Listeners para quantidade total
        txtNumCaixas.textProperty().addListener((obs, old, novo) -> calcularQuantidadeTotal());
        txtUnidPorCaixa.textProperty().addListener((obs, old, novo) -> calcularQuantidadeTotal());

        // Listeners para custos fixos
        txtCompra.textProperty().addListener((obs, old, novo) -> calcularResultado());
        txtTransporte.textProperty().addListener((obs, old, novo) -> calcularResultado());
        txtEmbalagem.textProperty().addListener((obs, old, novo) -> calcularResultado());
        txtEnergia.textProperty().addListener((obs, old, novo) -> calcularResultado());

        // Slider de lucro
        sliderLucro.valueProperty().addListener((obs, old, novo) -> {
            lucroPercentual = novo.intValue();
            lblLucroPercentual.setText(lucroPercentual + "%");
            calcularResultado();
        });

        // Inicializar valores
        calcularQuantidadeTotal();
        calcularResultado();

        // Carregar CSS
        try {
            String cssPath = getClass().getResource("css/precifica_tela2.css").toExternalForm();
            rootContainer.getStylesheets().add(cssPath);
            System.out.println("CSS carregado: " + cssPath);
        } catch (Exception e) {
            System.err.println("Erro ao carregar CSS: " + e.getMessage());
        }
    }


    // Aplica filtro numérico (aceita apenas números, vírgula e ponto)
    private void aplicarFiltroNumerico(TextField campo) {
        if (campo == null) return;
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            String novoTexto = change.getControlNewText();
            if (novoTexto.matches("\\d*([.,]\\d*)?")) {
                return change;
            }
            return null;
        });
        campo.setTextFormatter(formatter);
    }

    // Converte string com vírgula ou ponto para double
    private double converterParaDouble(String valor) {
        if (valor == null || valor.trim().isEmpty()) return 0.0;
        return Double.parseDouble(valor.replace(",", "."));
    }

    // Calcula quantidade total = caixas * unidades por caixa
    // Verifica quantos produtos ativos o usuário já possui
    private int contarProdutosDoUsuario() throws SQLException {
        String sql = "SELECT COUNT(*) FROM produtos WHERE id_usuario = ? AND deleted_at IS NULL";
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, SessaoUsuario.getInstancia().getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
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

    // Calcula preços e atualiza o card de resultado
    private void calcularResultado() {
        try {
            // Custos fixos
            double compra = converterParaDouble(txtCompra.getText());
            double transporte = converterParaDouble(txtTransporte.getText());
            double embalagem = converterParaDouble(txtEmbalagem.getText());
            double energia = converterParaDouble(txtEnergia.getText());


            double somaFixos = compra + transporte + embalagem + energia;

            // Custos dinâmicos
            double somaDinamicos = 0.0;
            for (CustomCostRow row : customCostRows) {
                double valor = converterParaDouble(row.valueField.getText());
                somaDinamicos += valor;
            }

            custoTotal = somaFixos + somaDinamicos;
            double quantidadeTotal = converterParaDouble(lblQuantidadeTotal.getText());
            if (quantidadeTotal <= 0) quantidadeTotal = 1;

            double precoUnitario = custoTotal / quantidadeTotal;
            double precoMinimo = precoUnitario;
            double precoRecomendado = precoUnitario * (1 + lucroPercentual / 100.0);
            double lucroUnidade = precoRecomendado - precoUnitario;

            if (lblPrecoMinimo != null) lblPrecoMinimo.setText(String.format("%.2f Kz", precoMinimo));
            if (lblPrecoRecomendado != null) lblPrecoRecomendado.setText(String.format("%.2f Kz", precoRecomendado));
            if (lblLucroUnidade != null) lblLucroUnidade.setText(String.format("%.2f Kz", lucroUnidade));
            if (lblMargemLucro != null) lblMargemLucro.setText(lucroPercentual + "%");

        } catch (Exception e) {
            // ignora durante a digitação
        }
    }

    // Adiciona uma nova linha de custo personalizado
    @FXML
    private void adicionarLinhaCusto() {
        CustomCostRow row = new CustomCostRow();

        // Campo nome
        row.nameField = new TextField();
        row.nameField.setPromptText("Ex: Armazenagem, Seguro, etc.");
        row.nameField.getStyleClass().add("custom-text-field");

        // Campo valor
        row.valueField = new TextField("0,00");
        row.valueField.getStyleClass().add("custom-text-field");
        aplicarFiltroNumerico(row.valueField);
        row.valueField.textProperty().addListener((obs, old, novo) -> calcularResultado());

        // Botão remover
        Button btnRemover = new Button();
        FontIcon trashIcon = new FontIcon("bi-trash");
        trashIcon.setIconSize(14);
        btnRemover.setGraphic(trashIcon);
        btnRemover.getStyleClass().add("remove-cost-button");
        btnRemover.setOnAction(e -> removerLinhaCusto(row));

        // Montar a linha
        row.rowBox = new HBox(10);
        row.rowBox.getChildren().addAll(row.nameField, row.valueField, btnRemover);
        HBox.setHgrow(row.nameField, Priority.ALWAYS);
        HBox.setHgrow(row.valueField, Priority.ALWAYS);

        customCostsContainer.getChildren().add(row.rowBox);
        customCostRows.add(row);
    }

    // Remove uma linha de custo personalizado
    private void removerLinhaCusto(CustomCostRow row) {
        customCostsContainer.getChildren().remove(row.rowBox);
        customCostRows.remove(row);
        calcularResultado();
    }

    // Salvar produto, custos e precificação
    @FXML
    void aoClicarSeguinte(ActionEvent event) {
        try {
            // Validações
            if (txtproduto.getText().trim().isEmpty())
                throw new Exception("Preencha o Nome do Produto!");
            int totalProdutos = contarProdutosDoUsuario();
            if (totalProdutos >= 50) {
                throw new Exception("Limite de 50 produtos atingido! Você não pode cadastrar mais produtos.");
            }
            if (custoTotal <= 0)
                throw new Exception("Informe pelo menos um custo positivo!");
            double quantidadeTotal = converterParaDouble(lblQuantidadeTotal.getText());
            if (quantidadeTotal <= 0)
                throw new Exception("Quantidade total deve ser maior que zero!");

            double precoProduto = custoTotal / quantidadeTotal; // preço de custo unitário
            double precoVenda = precoProduto * (1 + lucroPercentual / 100.0);

            // 1. Inserir Produto e obter ID gerado
            String sqlProduto = "INSERT INTO Produtos (nome, tipo, quantidade, preco_produto, id_usuario) VALUES (?, 'comprado', ?, ?, ?)";
            int idProduto = -1;

            try (Connection conn = Conexao.conectar();
                 PreparedStatement stmt = conn.prepareStatement(sqlProduto, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, txtproduto.getText().trim());
                stmt.setInt(2, (int) quantidadeTotal);
                stmt.setDouble(3, precoVenda);
                stmt.setInt(4, SessaoUsuario.getInstancia().getId());
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    idProduto = rs.getInt(1);
                } else {
                    throw new Exception("Falha ao obter ID do produto inserido.");
                }
            }

            // 2. Inserir custos fixos e dinâmicos na tabela Custos
            String sqlCusto = "INSERT INTO Custos (id_produto, nome_custo, v_total) VALUES (?, ?, ?)";
            try (Connection conn = Conexao.conectar();
                 PreparedStatement stmt = conn.prepareStatement(sqlCusto)) {

                // Custos fixos (apenas os com valor > 0)
                Object[][] custosFixos = {
                        {"Custo de compra", converterParaDouble(txtCompra.getText())},
                        {"Transporte", converterParaDouble(txtTransporte.getText())},
                        {"Embalagem", converterParaDouble(txtEmbalagem.getText())},
                        {"Energia", converterParaDouble(txtEnergia.getText())},

                };

                for (Object[] custo : custosFixos) {
                    String nome = (String) custo[0];
                    double valor = (double) custo[1];
                    if (valor > 0) {
                        stmt.setInt(1, idProduto);
                        stmt.setString(2, nome);
                        stmt.setDouble(3, valor);
                        stmt.addBatch();
                    }
                }

                // Custos dinâmicos
                for (CustomCostRow row : customCostRows) {
                    String nomeCusto = row.nameField.getText().trim();
                    if (nomeCusto.isEmpty()) continue; // ignora linhas sem nome
                    double valorCusto = converterParaDouble(row.valueField.getText());
                    if (valorCusto <= 0) continue;
                    stmt.setInt(1, idProduto);
                    stmt.setString(2, nomeCusto);
                    stmt.setDouble(3, valorCusto);
                    stmt.addBatch();
                }

                stmt.executeBatch();
            }

            // 3. Inserir precificação
            String sqlPrecificacao = "INSERT INTO Precificacao (id_produto, margem_selecionada, preco_custo) VALUES (?, ?, ?)";
            try (Connection conn = Conexao.conectar();
                 PreparedStatement stmt = conn.prepareStatement(sqlPrecificacao)) {
                stmt.setInt(1, idProduto);
                stmt.setDouble(2, lucroPercentual);
                stmt.setDouble(3, precoProduto);
                stmt.executeUpdate();
            }

            // Sucesso
            ScreenManager.mostrarAlerta("Sucesso", "Produto, custos e precificação salvos com sucesso!");

            // Navegação para tela seguinte
            Parent tela4 = FXMLLoader.load(getClass().getResource("/com/example/project30/precifica_tela4.fxml"));
            StackPane conteudoDinamico = (StackPane) btnSeguinte.getScene().lookup("#conteudoDinamico");
            conteudoDinamico.getChildren().setAll(tela4);

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    void aoClicarAnterior(ActionEvent event) {
        try {
            Parent tela1 = FXMLLoader.load(getClass().getResource("/com/example/project30/precifica_tela1.fxml"));
            StackPane conteudoDinamico = (StackPane) btnSeguinte.getScene().lookup("#conteudoDinamico");
            conteudoDinamico.getChildren().setAll(tela1);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao voltar: " + e.getMessage()).showAndWait();
            e.printStackTrace();
        }
    }
}