package server;

import java.sql.*;

public class AuthService {

    //Соединение с базой данных
    private static Connection connection;
    // Запрос в базу данных
    private static Statement stmt;



    public static void connect() {
        //регистрация драйвера
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:users.db");
            stmt = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getNickByLoginAndPass(String login, String pass) {
        try {
            ResultSet rs =  stmt.executeQuery("SELECT nickname FROM main WHERE login = '" + login + "' AND password = '" + pass + "'");
            if (rs.next()) {
                return rs.getString(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}