package ru.gb.onlinechat.server;

import java.io.Closeable;
import java.io.IOException;

public interface AuthService extends Closeable {

    String getNickByLoginAndPassword(String login, String password);

    void start();

    @Override
    void close() throws IOException;
}
