package ru.gb.onlinechat.server;

import java.sql.*;

public class DbAuthService implements AuthService {

    private Connection connection;

    public DbAuthService() {
            run();
    }

    @Override
    public String getNickByLoginAndPassword(String login2, String password2) {

        try(PreparedStatement prepInsert = connection.prepareStatement(
                "SELECT nick FROM client WHERE login = ? AND password = ? ")){
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
    public void run() { //connection
        try{
            this.connection = DriverManager.getConnection("jdbc:sqlite:javadb.db");
        }catch (SQLException e) {
            throw new RuntimeException("Ошибка подключения к БД", e);
        }
    }

    @Override
    public void close()  { //disconnection
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

