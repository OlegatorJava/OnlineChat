package ru.gb.onlinechat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Socket socket;
    private ChatServer server;
    private String nick;
    private final DataOutputStream out;
    private final DataInputStream in;
    private AuthService authService;

    public ClientHandler(Socket socket, ChatServer server,AuthService authService) {

        try {
            this.socket = socket;
            this.server = server;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            this.authService = authService;

            new Thread(() -> {
                try {
                    authenticate();
                    readMessage();
                }finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка создания подключения к клиенту", e);

        }

    }

    private void authenticate() {
        while(true){
            try {
                String msg = in.readUTF(); // /auth login pass
                if(msg.startsWith("/auth")){
                    String[] s = msg.split(" ");
                    String login = s[1];
                    String password = s[2];
                    String nick = authService.getNickByLoginAndPassword(login, password);
                    if (nick != null){
                        if (server.isNickBusy(nick)){
                            sendMessage("Пользователь уже авторизован");
                            continue;
                        }
                        sendMessage("/authok " + nick);
                        this.nick = nick;
                        server.broadcast("Пользователь " + nick + " вошел в чат");
                        server.subscribe(this);

                        break;
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void readMessage() {
        while (true){
            try {
                String msg = in.readUTF();
                if("/end".equals(msg)){
                    break;
                }
                if(msg.startsWith("/w")){  // /w nick2 Hello Nick
                    String[] s = msg.split(" ", 3);
                    String nickRecipient = s[1];
                    String message = s[2];
                    server.privateMessage("Вам пишет " + nick + ": ",  message, nickRecipient, this);

                    continue;
                }
                System.out.println("Получено сообщение: " + msg);
                server.broadcast(nick + ": " + msg);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void closeConnection() {
        sendMessage("/end");
        try {
            if(in != null){
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка отключения", e);
        }
        try {
            if(out != null){
                out.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка отключения", e);
        }
        try {
            if(socket != null){
                server.unsubscribe(this);
                socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка отключения", e);
        }
    }

    public void sendMessage(String message) {
        try {
            System.out.println("Отправляю сообщение: " + message);
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNick() {
        return nick;
    }
}
