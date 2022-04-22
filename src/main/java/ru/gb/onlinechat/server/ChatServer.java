package ru.gb.onlinechat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

    private final AuthService authService;
    private final List<ClientHandler> clients;

    public ChatServer() {
        authService = new InMemoryAuthService();
        clients = new ArrayList<>();
        authService.start();
    }


    public void run() {
try (ServerSocket serverSocket = new ServerSocket(8189))
        {
    while (true){
        System.out.println("Сервер ожидает подключения клиента");
        Socket socket = serverSocket.accept();
        System.out.println("Клиент подключился");
        new ClientHandler(socket,this,authService);
    }

} catch (IOException e) {
    throw new RuntimeException("Ощибка сервера", e);
}
    }

    public AuthService getAuthService() {
        return authService;
    }

    public boolean isNickBusy(String nick) {
        for (ClientHandler client : clients) {
            if(client.getNick().equals(nick)){
                return true;
            }
        }
        return false;
    }

    public void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void subscribe(ClientHandler client) {
        clients.add(client);
    }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client);
    }

    public void privateMessage(String s, String message, String nick, ClientHandler i) {
        ClientHandler client = searchByNick(nick);
        if(client != null) {
            client.sendMessage(s + message);
            i.sendMessage(i.getNick() + ": " + message);
        }
    }
    public ClientHandler searchByNick(String nick){
        for (ClientHandler client : clients) {
            if(client.getNick().equals(nick)){
                return client;
            }
        }
        return null;
    }
}
