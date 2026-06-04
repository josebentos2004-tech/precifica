package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private static final String URL =
            "jdbc:mysql://localhost:3306/sistema_precificacao";

    private static final String USER = "root";

    private static final String PASSWORD = "precifica";

    public static Connection conectar() {

        try {

            Connection conn = DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );

            System.out.println("Conectado com sucesso!");

            return conn;

        } catch (SQLException e) {

            System.out.println("Erro na conexão:");
            e.printStackTrace();

            return null;
        }
    }
}