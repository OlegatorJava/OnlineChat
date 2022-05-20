package ru.gb.onlinechat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.gb.onlinechat.Command;

public class ChatServer {

    private final Map<String, ClientHandler> clients;


    public ChatServer() {
        this.clients = new HashMap<>();
    }

    public void run() {
        ExecutorService service = Executors.newCachedThreadPool();
        try (ServerSocket serverSocket = new ServerSocket(8189);
             AuthService authService = new DbAuthService()) {
            while (true) {
                System.out.println("Wait client connection...");
                final Socket socket = serverSocket.accept();
                new ClientHandler(socket, this, authService,service);
                System.out.println("Client connected");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public boolean isNickBusy(String nick) {
        return clients.containsKey(nick);
    }


    public void subscribeChange(String oldNick, ClientHandler client) {
        clients.remove(oldNick);
        clients.put(client.getNick(), client);

        broadcastClientList();
    }
    public void subscribe(ClientHandler client) {
        clients.put(client.getNick(), client);

            broadcastClientList();
    }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client.getNick());

        broadcastClientList();
    }

    private void  broadcastClientList() {
        synchronized (this){
        StringBuilder nicks = new StringBuilder();
        for (ClientHandler value : clients.values()) {
            nicks.append(value.getNick()).append(" ");
        }
        broadcast(Command.CLIENTS, nicks.toString().trim());
    }}

    private void broadcast(Command command, String nicks) {
        for (ClientHandler client : clients.values()) {
            client.sendMessage(command, nicks);
        }
    }

    public void broadcast(String msg) {
        clients.values().forEach(client -> client.sendMessage(msg));
    }

    public void sendMessageToClient(ClientHandler sender, String to, String message) {
        final ClientHandler receiver = clients.get(to);
        if (receiver != null) {
            receiver.sendMessage("от " + sender.getNick() + ": " + message);
            sender.sendMessage("участнику " + to + ": " + message);
        } else {
            sender.sendMessage(Command.ERROR, "Участника с ником " + to + " нет в чате!");
        }
    }
}