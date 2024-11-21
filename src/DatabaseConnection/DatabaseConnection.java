package DatabaseConnection;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author amd
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/monitoramento";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexão com o banco de dados estabelecida!");
            } catch (SQLException e) {
                System.err.println("Erro ao conectar ao banco: " + e.getMessage());
            }
        }
        return connection;
    }

    public static void insertLeitura(float temperatura, float umidade) {
        String sql = "INSERT INTO leitura_sensor (temperatura, umidade) VALUES (?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setFloat(1, temperatura);
            stmt.setFloat(2, umidade);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao salvar dados: " + e.getMessage());
        }
    }

    public static List<String> getLeituras() {
        List<String> leituras = new ArrayList<>();
        String sql = "SELECT * FROM leitura_sensor ORDER BY data_hora DESC";

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                float temperatura = rs.getFloat("temperatura");
                float umidade = rs.getFloat("umidade");
                Timestamp dataHora = rs.getTimestamp("data_hora");
                leituras.add("ID: " + id + ", Temperatura: " + temperatura + "°C, Umidade: " + umidade + "%, Data: " + dataHora);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar dados: " + e.getMessage());
        }

        return leituras;
    }

    public static void insertLeitura(double temperaturaAtual, double umidadeAtual) {
        throw new UnsupportedOperationException("Not supported yet."); //
    }
}
