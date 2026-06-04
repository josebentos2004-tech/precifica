package com.example.project30;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class precifica_tela5 {

    @FXML
    private PieChart graficoCustos;

    @FXML
    public void initialize() {
        System.out.println("Carregando resultados e conectando à Base de Dados...");
        carregarDadosDoGrafico();
    }

    private void carregarDadosDoGrafico() {
        // Criamos uma lista observável para injetar os dados no gráfico
        ObservableList<PieChart.Data> dadosPizza = FXCollections.observableArrayList();

        // 1. Configuração da sua Conexão (Ajuste com os seus dados locais se necessário)
        String url = "jdbc:mysql://localhost:3306/precifica_db";
        String usuario = "root";
        String senha = "";

        // SQL de exemplo para buscar o último cálculo efetuado na tabela de produção
        String sql = "SELECT materia_prima, mao_de_obra, ggf, serv_terceiros FROM custos_producao ORDER BY id DESC LIMIT 1";

        try (Connection conn = DriverManager.getConnection(url, usuario, senha);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                // Recupera os valores decimais das colunas da BD
                double materiaPrima = rs.getDouble("materia_prima");
                double maoDeObra = rs.getDouble("mao_de_obra");
                double ggf = rs.getDouble("ggf");
                double servTerceiros = rs.getDouble("serv_terceiros");

                // Adiciona dinamicamente as fatias ao gráfico com os valores reais
                dadosPizza.add(new PieChart.Data("Matéria-Prima", materiaPrima));
                dadosPizza.add(new PieChart.Data("Mão de Obra", maoDeObra));
                dadosPizza.add(new PieChart.Data("GGF (Fábrica)", ggf));
                dadosPizza.add(new PieChart.Data("Serv. Terceiros", servTerceiros));

                // Atualiza o componente visual da tela
                graficoCustos.setData(dadosPizza);
                System.out.println("Gráfico gerado com sucesso com os dados da BD!");

            } else {
                // Caso a BD esteja vazia, coloca valores padrão apenas para exibição limpa
                System.out.println("Nenhum dado encontrado na BD. Exibindo gráfico zerado.");
                graficoCustos.setTitle("Sem dados cadastrados");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao conectar ou buscar dados para o gráfico: " + e.getMessage());
            e.printStackTrace();

            // Fallback: Evita crash exibindo dados fictícios se o banco falhar no teste rápido
            dadosPizza.addAll(
                    new PieChart.Data("Matéria-Prima (Exemplo)", 40),
                    new PieChart.Data("Mão de Obra (Exemplo)", 30),
                    new PieChart.Data("Outros (Exemplo)", 30)
            );
            graficoCustos.setData(dadosPizza);
        }
    }
}