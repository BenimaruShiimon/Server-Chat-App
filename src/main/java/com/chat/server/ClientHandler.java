package com.chat.server;

import com.chat.common.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ChatServer server;
    private PrintWriter out;
    private String username;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }


    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            this.out = writer;
            String firstMessage = in.readLine();
            if (firstMessage != null && firstMessage.startsWith("JOIN:")) {
                username = firstMessage.substring(5);
            } else {
                username = "Anonym";
            }
            Logger.log("Пользователь вошел в чат: " + username);
            server.broadcast("SERVER: " + username + " вошел в чат:", this);
            String message;
            while ((message = in.readLine()) != null) {
                if (message.equals("/exit")) {
                    break;
                }
                String formatted = username + ": " + message;
                server.broadcast(formatted, this);
            }
        } catch (IOException e) {
            Logger.log("Соединение с " + username + " прервано: " + e.getLocalizedMessage());
        } finally {
            disconnect();
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    private void disconnect() {
        server.removeClient(this);
        Logger.log("Пользователь вышел из чата: " + username);
        server.broadcast("SERVER: " + username + " покинул в чат:", this);

        try {
            socket.close();
        } catch (IOException e) {
            Logger.log("Ошибка закрытия сокета: " + e.getLocalizedMessage());
        }
    }

    public String getUsername() {
        return username;
    }
}
