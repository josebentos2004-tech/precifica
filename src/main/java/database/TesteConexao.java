package  database;

import database.Conexao;

public class TesteConexao {

    public static void main(String[] args) {
        try{
            Conexao.conectar();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }



    }
}