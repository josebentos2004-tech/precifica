package com.example.project30;

import database.Conexao;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.controlsfx.dialog.CommandLinksDialog;
import org.controlsfx.dialog.CommandLinksDialog.CommandLinksButtonType;
import javafx.scene.layout.StackPane;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class DashboardController implements Initializable {

    @FXML private StackPane conteudoDinamico;
    @FXML private Label name;
    @FXML private Label nif;
    @FXML private Label nom;
    @FXML private PieChart lucroPieChart;
    @FXML private TableView<ProdutoLucro> topLucroTable;
    @FXML private TableColumn<ProdutoLucro, Number> colunaLucro; // injetada do FXML
    @FXML private Label totalProdutosLabel;
    @FXML private Label lucroEstimadoLabel;
    @FXML private Label alertasLabel;

    private Node dashboardNodeOriginal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String nome = SessaoUsuario.getInstancia().getNome();
        String email = SessaoUsuario.getInstancia().getEmail();
        name.setText("Bem-vindo, " + nome + " 👋");
        nif.setText(email);
        nom.setText("Olá, " + nome);

        // Formata a coluna de lucro para duas casas decimais
        colunaLucro.setCellFactory(tc -> new TableCell<ProdutoLucro, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item.doubleValue()));
                }
            }
        });

        // Guarda o conteúdo original do dashboard para não recarregar o FXML
        if (!conteudoDinamico.getChildren().isEmpty()) {
            dashboardNodeOriginal = conteudoDinamico.getChildren().get(0);
        }

        carregarDadosDashboard();
    }

    private void carregarDadosDashboard() {
        carregarCards();
        carregarTopProdutosELucro();
    }

    private void carregarCards() {
        int idUsuario = SessaoUsuario.getInstancia().getId();
        String sqlProd = "SELECT COUNT(*) FROM Produtos WHERE id_usuario = ? AND deleted_at IS NULL";
        String sqlLucro = "SELECT SUM((p.preco_produto - pc.preco_custo) * p.quantidade) " +
                "FROM Produtos p " +
                "INNER JOIN Precificacao pc ON p.id = pc.id_produto " +
                "WHERE p.id_usuario = ? AND p.deleted_at IS NULL AND pc.deleted_at IS NULL";
        String sqlAlertas = "SELECT COUNT(*) FROM Produtos p " +
                "INNER JOIN Precificacao pc ON p.id = pc.id_produto " +
                "WHERE p.id_usuario = ? AND p.deleted_at IS NULL AND pc.deleted_at IS NULL " +
                "AND (p.preco_produto - pc.preco_custo) = 0";

        try (Connection conn = Conexao.conectar()) {
            try (PreparedStatement ps = conn.prepareStatement(sqlProd)) {
                ps.setInt(1, idUsuario);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) totalProdutosLabel.setText(String.valueOf(rs.getInt(1)));
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlLucro)) {
                ps.setInt(1, idUsuario);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    double lucro = rs.getDouble(1);
                    lucroEstimadoLabel.setText(String.format("%.2f kz", lucro));
                } else {
                    lucroEstimadoLabel.setText("0 kz");
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlAlertas)) {
                ps.setInt(1, idUsuario);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) alertasLabel.setText(String.valueOf(rs.getInt(1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Fallback com dados mock
            totalProdutosLabel.setText("3");
            lucroEstimadoLabel.setText("15.000 kz");
            alertasLabel.setText("1");
        }
    }

    private void carregarTopProdutosELucro() {
        int idUsuario = SessaoUsuario.getInstancia().getId();
        String sql = "SELECT p.nome, (p.preco_produto - pc.preco_custo) AS lucro_unitario, " +
                "pc.margem_selecionada, p.quantidade " +
                "FROM Produtos p " +
                "INNER JOIN Precificacao pc ON p.id = pc.id_produto " +
                "WHERE p.id_usuario = ? AND p.deleted_at IS NULL AND pc.deleted_at IS NULL " +
                "ORDER BY lucro_unitario DESC LIMIT 5";

        ObservableList<ProdutoLucro> lista = FXCollections.observableArrayList();

        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String nome = rs.getString("nome");
                double lucroUnit = rs.getDouble("lucro_unitario");
                int qtd = rs.getInt("quantidade");
                double lucroTotal = lucroUnit * qtd;
                double margem = rs.getDouble("margem_selecionada");
                lista.add(new ProdutoLucro(nome, lucroTotal, margem));
            }
            topLucroTable.setItems(lista);
            carregarPieChart();
        } catch (SQLException e) {
            e.printStackTrace();
            // Dados mock
            lista.add(new ProdutoLucro("Arroz 25kg", 5000, 15));
            lista.add(new ProdutoLucro("Refrigerante", -1200, 8));
            lista.add(new ProdutoLucro("Sabão", 3000, 25));
            topLucroTable.setItems(lista);
            carregarPieChartMock();
        }
    }

    private void carregarPieChart() {
        int idUsuario = SessaoUsuario.getInstancia().getId();
        String sqlPie = "SELECT p.nome, SUM((p.preco_produto - pc.preco_custo) * p.quantidade) AS lucro_total " +
                "FROM Produtos p " +
                "INNER JOIN Precificacao pc ON p.id = pc.id_produto " +
                "WHERE p.id_usuario = ? AND p.deleted_at IS NULL AND pc.deleted_at IS NULL " +
                "GROUP BY p.id";

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        double totalLucroPositivo = 0;
        java.util.Map<String, Double> map = new java.util.HashMap<>();

        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sqlPie)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String nome = rs.getString("nome");
                double lucroTotal = rs.getDouble("lucro_total");
                if (lucroTotal > 0) {
                    map.put(nome, lucroTotal);
                    totalLucroPositivo += lucroTotal;
                }
            }
            for (java.util.Map.Entry<String, Double> entry : map.entrySet()) {
                double percent = (entry.getValue() / totalLucroPositivo) * 100;
                pieData.add(new PieChart.Data(entry.getKey() + " (" + String.format("%.1f", percent) + "%)", entry.getValue()));
            }
            lucroPieChart.setData(pieData);
            lucroPieChart.setTitle("Distribuição do Lucro");
        } catch (SQLException e) {
            carregarPieChartMock();
        }
    }

    private void carregarPieChartMock() {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        pieData.add(new PieChart.Data("Arroz 25kg (62%)", 5000));
        pieData.add(new PieChart.Data("Sabão (38%)", 3000));
        lucroPieChart.setData(pieData);
        lucroPieChart.setTitle("Distribuição do Lucro");
    }

    // Método para voltar ao dashboard sem recarregar o FXML
    @FXML
    private void abrirDashboard() {
        if (dashboardNodeOriginal != null && !conteudoDinamico.getChildren().contains(dashboardNodeOriginal)) {
            conteudoDinamico.getChildren().clear();
            conteudoDinamico.getChildren().add(dashboardNodeOriginal);
            carregarDadosDashboard(); // atualiza os dados
        }
    }

    // Navegação para outras telas (substitui o conteúdo do StackPane)
    @FXML
    private void abrirproduto() {
        trocarTela("produto_total.fxml");
    }

    @FXML
    private void abrirTelaPrecificacao() {
        trocarTela("precifica_tela1.fxml");
    }

    @FXML
    private void abrirperfil() {
        trocarTela("perfil1.fxml");
    }

    @FXML
    private void abrirTelaProduto() {
        trocarTela("cadastro_produto.fxml");
    }

    @FXML
    private void abrirReciclagem() {
        trocarTela("reciclagem.fxml");
    }

    private void trocarTela(String fxml) {
        try {
            Parent novaTela = FXMLLoader.load(getClass().getResource(fxml));
            conteudoDinamico.getChildren().clear();
            conteudoDinamico.getChildren().add(novaTela);
        } catch (IOException e) {
            System.err.println("Erro ao carregar tela: " + fxml + " - " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setContentText("Não foi possível abrir a tela solicitada.");
            alert.showAndWait();
        }
    }

    @FXML
    private void logoutL(ActionEvent event) {
        CommandLinksDialog dialog = new CommandLinksDialog(
                new CommandLinksButtonType("Sim, terminar sessão", true),
                new CommandLinksButtonType("Não, continuar", false)
        );
        dialog.setTitle("Confirmação de Logout");
        dialog.setHeaderText("Deseja realmente terminar a sessão?");
        dialog.setResizable(false);

        dialog.showAndWait().ifPresent(result -> {
            if (result.getText().startsWith("Sim")) {
                SessaoUsuario.getInstancia().logout();
                try {
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    Parent login = FXMLLoader.load(getClass().getResource("login.fxml"));
                    stage.setScene(new Scene(login));
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Classe auxiliar para a tabela
    public static class ProdutoLucro {
        private final SimpleStringProperty nome;
        private final SimpleDoubleProperty lucro;
        private final SimpleDoubleProperty margem;

        public ProdutoLucro(String nome, double lucro, double margem) {
            this.nome = new SimpleStringProperty(nome);
            this.lucro = new SimpleDoubleProperty(lucro);
            this.margem = new SimpleDoubleProperty(margem);
        }

        public String getNome() { return nome.get(); }
        public double getLucro() { return lucro.get(); }
        public double getMargem() { return margem.get(); }
        public SimpleStringProperty nomeProperty() { return nome; }
        public SimpleDoubleProperty lucroProperty() { return lucro; }
        public SimpleDoubleProperty margemProperty() { return margem; }
    }
}