package com.chat.server;

import com.chat.common.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {
    private final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private final int port;

    public ChatServer(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void start() {
        Logger.log("Сервер запущен на порту " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Logger.log("Сервер запущен. Ожидайте подключений пользователей....");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Logger.log("Новое подключение: " + clientSocket.getInetAddress().getHostAddress());
                ClientHandler handler = new ClientHandler(clientSocket, this);
                clients.add(handler);
                Thread thread = new Thread(handler);
                thread.start();
            }

        } catch (IOException e) {
            Logger.log("Ошибка сервера: " + e.getMessage());
        }
    }

    public void broadcast(String message, ClientHandler sender) {
        Logger.log(message);
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void removeClient(ClientHandler handler) {
        clients.remove(handler);
    }

    public int getClientCount() {
        return clients.size();
    }

    static void main() {
        int port = readPort("server-settings.txt");
        new ChatServer(port).start();
    }

    public static int readPort(String filePath) {
        try {
            List<String> lines = Files.readAllLines(Path.of(filePath));
            for (String line : lines) {
                if (line.startsWith("port=")) {
                    return Integer.parseInt(line.split("=")[1].trim());
                }
            }
        } catch (IOException e) {
            Logger.log("Ошибка чтения настроек, используется default порт: 12345");
        }
        return 12345;
    }
}
