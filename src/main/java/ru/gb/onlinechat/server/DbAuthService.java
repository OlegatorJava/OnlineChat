package ru.gb.onlinechat.server;

import java.sql.*;

public class DbAuthService implements AuthService {

    private Connection connection;
    private Statement statement;

    public DbAuthService() {
        try {
            run();
            createTable();
            insertUnitsBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getNickByLoginAndPassword(String login2, String password2) {

        try(PreparedStatement prepInsert = connection.prepareStatement("SELECT nick FROM users WHERE login = ? AND password = ? ")){
            prepInsert.setString(1, login2);
            prepInsert.setString(2, password2);
            ResultSet rs = prepInsert.executeQuery();
            return rs.getString("nick");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void run() throws SQLException { //connection
        this.connection = DriverManager.getConnection("jdbc:sqlite:javadb.db");
        statement = connection.createStatement();
    }

    @Override
    public void close()  { //disconnection
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTable() throws SQLException {
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (\n" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                " login TEXT,\n" +
                " password TEXT,\n" +
                " nick TEXT\n" +
                ");");
    }

    private void insertUnitsBatch(){
        try(PreparedStatement prs = connection.prepareStatement(
                "INSERT INTO users (login, password, nick) " +
                "VALUES (?,?,?)")) {
            for (int i = 1; i <= 10; i++) {
                prs.setString(1, "login" + i);
                prs.setString(2, "pass" + i);
                prs.setString(3, "nick" + i);
                prs.addBatch();
            }
           prs.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        }
    }

