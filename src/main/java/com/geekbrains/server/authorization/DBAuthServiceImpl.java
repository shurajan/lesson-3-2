package com.geekbrains.server.authorization;

import java.sql.*;

public class DBAuthServiceImpl implements AuthService {
    private static Connection connection;
    private static Statement stmt;

    @Override
    public void start() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:chat.db");
            stmt = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized String getNickNameByLoginAndPassword(String login, String password) {
        try (ResultSet rs = stmt.executeQuery(
                "SELECT nickname " +
                        "FROM users " +
                        "WHERE login = '" + login + "' " +
                        "AND password = '" + password + "';")
        ) {

            while (rs.next()) {
                return rs.getString("nickname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void end() {
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void changeNickName(String nickName, String newNickName) {
        try {
            stmt.executeUpdate("UPDATE users " +
                    "SET nickname = '" + newNickName + "' " +
                    "WHERE nickname = '" + nickName + "';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
