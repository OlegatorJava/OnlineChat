package ru.gb.onlinechat.server;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;

public interface AuthService extends Closeable {
    String getNickByLoginAndPassword(String login, String password);

    void run() throws SQLException;

    @Override
    void close() throws IOException;
}