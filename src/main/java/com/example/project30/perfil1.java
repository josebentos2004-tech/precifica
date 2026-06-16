package com.example.project30;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.net.URL;
import database.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class perfil1 {


    @FXML
    private TextField txtNomeCompleto;

    @FXML
    private Label txtemail;

    @FXML
    private Label txtplana;

    @FXML
    private TextField txtNif;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txttelefone;

    @FXML
    private PasswordField txtSenhaAtual;

    @FXML
    private PasswordField txtNovaSenha;

    @FXML
    private PasswordField txtConfirmarNovaSenha;

    // --- Inicialização ---

    @FXML
    public void initialize() {
        // Pega a instância da sessão (já tem os dados do usuário)
        SessaoUsuario sessao = SessaoUsuario.getInstancia();

        if (sessao.isLogado()) {  // Verifica se tem alguém logado
            txtNomeCompleto.setText(sessao.getNome());     // ← usa getNome() da sessão
            txtEmail.setText(sessao.getEmail());           // ← usa getEmail() da sessão
            txtemail.setText(sessao.getEmail());
            txtNif.setText(sessao.getNif());               // ← usa getNif() da sessão
            txtplana.setText(sessao.getNif());
            txtSenhaAtual.setText(sessao.getSenha());

            String telefone = sessao.getTelefone();        // ← usa getTelefone() da sessão
            if (telefone != null && !telefone.isEmpty()) {
                txttelefone.setText(telefone);
            } else {
                txttelefone.setText("");
            }
        }
    }
    @FXML
    private void handleEditarInformacoes() {
        boolean habilitar = txtNomeCompleto.isDisabled();

        txtNomeCompleto.setDisable(!habilitar);
        txtEmail.setDisable(!habilitar);
        txtNif.setDisable(!habilitar);
        txttelefone.setDisable(!habilitar);
        txtSenhaAtual.setDisable(!habilitar);
        txtNovaSenha.setDisable(!habilitar);
        txtConfirmarNovaSenha.setDisable(!habilitar);

    }
    @FXML
    private void handleAtualizarPerfil(ActionEvent event) {
        // 1. Pega a instância da sessão única
        SessaoUsuario sessao = SessaoUsuario.getInstancia();

        // 2. Verifica se tem usuário logado usando isLogado()
        if (!sessao.isLogado()) {
            exibirAlerta(AlertType.WARNING, "Erro", "Sessão Expirada", "Faça login novamente!");
            return;
        }

        String novoNome = txtNomeCompleto.getText().trim();
        String novoEmail = txtEmail.getText().trim();
        String novoNif = txtNif.getText().trim();
        String novoTelefone = txttelefone.getText().trim();
        String novaSenha = txtNovaSenha.getText().trim();

        boolean alterarSenha = !novaSenha.isEmpty();

        // Query dinâmica simples utilizando operador ternário
        String sql = alterarSenha
                ? "UPDATE usuarios SET nome = ?, email = ?, nif = ?, telefone = ?, senha = ? WHERE id = ?"
                : "UPDATE usuarios SET nome = ?, email = ?, nif = ?, telefone = ? WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Configuração dos parâmetros sequenciais
            int index = 1;
            stmt.setString(index++, novoNome);
            stmt.setString(index++, novoEmail);
            stmt.setString(index++, novoNif);
            stmt.setString(index++, novoTelefone);

            if (alterarSenha) {
                stmt.setString(index++, novaSenha);
            }

            stmt.setInt(index, sessao.getId()); // Usa sessao.getId() diretamente

            stmt.executeUpdate();

            // Atualiza a sessão com os novos dados
            String senhaFinal = alterarSenha ? novaSenha : sessao.getSenha();
            User usuarioAtualizado = new User(
                    sessao.getId(),
                    novoNome,
                    novoEmail,
                    novoNif,
                    novoTelefone,
                    senhaFinal
            );
            sessao.setUsuario(usuarioAtualizado);

            exibirAlerta(AlertType.INFORMATION, "Sucesso", "Perfil Atualizado", "Dados salvos com sucesso!");

            // Limpeza de campos
            txtSenhaAtual.clear();
            txtNovaSenha.clear();
            txtConfirmarNovaSenha.clear();
            handleEditarInformacoes();

        } catch (Exception e) {
            e.printStackTrace();
            exibirAlerta(AlertType.ERROR, "Erro", "Banco de Dados", "Erro ao atualizar!");
        }
    }
    /**
     * Ação: Alterar Senha
     */
    @FXML
    private void handleAlterarSenha(ActionEvent event) {
        String atual = txtSenhaAtual.getText();
        String nova = txtNovaSenha.getText();
        String confirmacao = txtConfirmarNovaSenha.getText();

        if (atual.isEmpty() || nova.isEmpty() || confirmacao.isEmpty()) {
            exibirAlerta(AlertType.WARNING, "Campos Vazios", "Aviso de Validação", "Preencha todos os campos de segurança.");
            return;
        }

        if (!nova.equals(confirmacao)) {
            exibirAlerta(AlertType.ERROR, "Erro", "Senhas Não Coincidem", "A nova senha e a confirmação não são iguais.");
            return;
        }

        exibirAlerta(AlertType.INFORMATION, "Sucesso", "Senha Alterada", "A sua senha foi modificada com sucesso!");

        txtSenhaAtual.clear();
        txtNovaSenha.clear();
        txtConfirmarNovaSenha.clear();
    }




    private void exibirAlerta(AlertType tipo, String titulo, String cabecalho, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecalho);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}