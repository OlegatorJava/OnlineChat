package ru.gb.onlinechat;

import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class ChatClient {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private final Controller controller;

    public ChatClient(Controller controller) {
        this.controller = controller;
    }

    public void openConnection() throws Exception {
        socket = new Socket("localhost", 8189);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        Thread timeThread = new Thread(() -> {
            try {
                sleep(120000);
                closeConnection();

            }
             catch (InterruptedException e) {
                 System.out.println("Прерывание");
            }
        });  timeThread.start();



        final Thread readThread = new Thread(() -> {
            try {
                waitAuthenticate();
                timeThread.interrupt();
                readMessage();
            } catch (IOException e) {
                System.out.println("Не успел");
            } finally {
                closeConnection();
            }
        });
        readThread.setDaemon(true);

        readThread.start();

    }

    private void readMessage() throws IOException {
        while (true) {
            final String message = in.readUTF();
            System.out.println("Receive message: " + message);
            if (Command.isCommand(message)) {
                final Command command = Command.getCommand(message);
                final String[] params = command.parse(message);
                if (command == Command.END) {
                    controller.setAuth(false);
                    break;
                }
                if (command == Command.ERROR) {
                    Platform.runLater(() -> controller.showError(params));
                    continue;
                }
                if (command == Command.CLIENTS) {
                    Platform.runLater(() -> controller.updateClientList(params));
                    continue;
                }
            }
            controller.addMessage(message);
        }
    }

    private void waitAuthenticate() throws IOException {
        while (true) {
            final String msgAuth = in.readUTF();
            if (Command.isCommand(msgAuth)) {
                final Command command = Command.getCommand(msgAuth);
                final String[] params = command.parse(msgAuth);
                if (command == Command.AUTHOK) {
                    final String nick = params[0];
                    controller.addMessage("Успешная авторизация под ником " + nick);
                    controller.setAuth(true);
                    break;
                }
                if (Command.ERROR.equals(command)) {
                    Platform.runLater(() -> controller.showError(params));
                }
            }
        }
    }

    private void closeConnection() {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (socket != null) {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        System.exit(0);
    }

    public void sendMessage(String message) {
        try {
            System.out.println("Send message: " + message);
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Command command, String... params) {
        sendMessage(command.collectMessage(params));
    }
}