package ru.gb.onlinechat.server;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import ru.gb.onlinechat.Command;

public class ClientHandler {
    private final Socket socket;
    private final ChatServer server;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final AuthService authService;
    private Path file;
    private String nick;
    private String oldNick;


    public ClientHandler(Socket socket, ChatServer server, AuthService authService,ExecutorService service) {
        try {
            this.nick = "";
            this.socket = socket;
            this.server = server;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.authService = authService;


            service.execute(() -> {
                try {
                    authenticate();
                    readMessages();
                } finally {
                    closeConnection();
                }});
            service.shutdown();
            /*new Thread(() -> {
                try {
                    authenticate();
                    readMessages();
                } finally {
                    closeConnection();
                }
            }).start();*/

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void closeConnection() {
        sendMessage(Command.END);
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket != null) {
                server.unsubscribe(this);
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void authenticate() {
        while (true) {
            try {

                    final String str = in.readUTF();
                    if (Command.isCommand(str)) {
                        final Command command = Command.getCommand(str);
                        final String[] params = command.parse(str);

                        if (command == Command.AUTH) {
                            final String login = params[0];
                            final String password = params[1];
                            final String nick = authService.getNickByLoginAndPassword(login, password);
                            if (nick != null) {
                                if (server.isNickBusy(nick)) {
                                    sendMessage(Command.ERROR, "Пользователь уже авторизован");
                                    continue;
                                }
                                this.nick = nick;
                                this.oldNick = nick;
                                sendMessage(Command.AUTHOK, nick);

                                server.broadcast("Пользователь " + nick + " зашел в чат");
                                server.subscribe(this);
                                break;
                            } else {
                                sendMessage(Command.ERROR, "Неверные логин и пароль");
                            }
                        }
                    }

            } catch (EOFException a){
                continue;
            }catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void sendMessage(Command command, String... params) {
        sendMessage(command.collectMessage(params));
    }

    public void sendMessage(String message) {
        try {
            System.out.println("SERVER: Send message to " + nick);
            out.writeUTF(message);

            try(DataOutputStream out2 = new DataOutputStream(new FileOutputStream(("history_" + oldNick + ".txt"), true));
                    DataOutputStream out3 = new DataOutputStream(new FileOutputStream(("history.txt"), true))) {
                out3.writeUTF(message);
                out3.writeUTF("\n");
                out2.writeUTF(message);
                out2.writeUTF("\n");

            }catch (IOException e){
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessages() {
        try {
            while (true) {
                final String msg = in.readUTF();
                System.out.println("Receive message: " + msg);
                if (Command.isCommand(msg)) {
                    final Command command = Command.getCommand(msg);
                    final String[] params = command.parse(msg);
                    if (command == Command.END) {
                        break;
                    }
                    if (command == Command.PRIVATE_MESSAGE) {
                        server.sendMessageToClient(this, params[0], params[1]);
                        continue;
                    }
                    if (command == Command.CHANGE_NICK) {
                        server.broadcast("Пользователь " + nick + " сменил ник на: " + params[0]);
                        String oldNick = getNick();
                        this.nick = params[0];
                        server.subscribeChange(oldNick, this);
                        continue;
                    }
                }if(!Files.exists(Path.of("history.txt"))){
                    file = Files.createFile(Path.of("history.txt"));
                }
                if(!Files.exists(Path.of("history_" + oldNick + ".txt")) && nick != null){
                    file = Files.createFile(Path.of("history_" + oldNick + ".txt"));
                }

                server.broadcast(nick + ": " + msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getNick() {
        return nick;
    }
}