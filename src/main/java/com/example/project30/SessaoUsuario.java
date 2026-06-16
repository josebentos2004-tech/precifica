package com.example.project30;

public class SessaoUsuario {
    private static SessaoUsuario instancia;
    private User usuarioLogado;

    private SessaoUsuario() {}

    public static SessaoUsuario getInstancia() {
        if (instancia == null) {
            instancia = new SessaoUsuario();
        }
        return instancia;
    }

    public void setUsuario(User user) {
        this.usuarioLogado = user;
    }

    public int getId() {
        return usuarioLogado != null ? usuarioLogado.getId() : -1;
    }

    public String getNome() {
        return usuarioLogado != null ? usuarioLogado.getNome() : "";
    }

    public String getEmail() {
        return usuarioLogado != null ? usuarioLogado.getEmail() : "";
    }

    public String getNif() {
        return usuarioLogado != null ? usuarioLogado.getNif() : "";
    }

    public String getTelefone() {
        return usuarioLogado != null ? usuarioLogado.getTelefone() : "";
    }
    public String getSenha() {
        return usuarioLogado != null ? usuarioLogado.getSenha() : "";
    }

    public boolean isLogado() {
        return usuarioLogado != null;
    }

    public void logout() {
        usuarioLogado = null;
    }
}