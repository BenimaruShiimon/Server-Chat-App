package com.chat.client;

import com.chat.common.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class ChatClient {
    private final String host;
    private final int port;
    private final String username;

    public ChatClient(String host, int port, String username) {
        this.host = host;
        this.port = port;
        this.username = username;
    }

    public void startWithScanner() {
        try (
                Socket socket = new Socket(host, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                Scanner scanner = new Scanner(System.in)
        ) {
            Logger.log("Подключение успешно! Добро пожаловать " + username + " в сетевой чат!");
            out.println("JOIN:" + username);

            Thread receiver = new Thread(() -> receiveMessage(in));
            receiver.setDaemon(true);
            receiver.start();

            System.out.println("--------- ЧАТ НАЧАТ! ЕСЛИ ХОТИТЕ ПОКИНУТЬ ЧАТ ВВЕДИТЕ - /exit ---------");

            while (true) {
                String input = scanner.nextLine();

                if (input.equals("/exit")) {
                    out.println("/exit");
                    Logger.log("Выход из чата");
                    break;
                }

                if (!input.isBlank()) {
                    out.println(input); // отправляем серверу
                }
            }

        } catch (IOException e) {
            Logger.log("Ошибка подключения: " + e.getMessage());
            System.out.println("Не удалось подключиться к серверу!\nПроверь запущен ли сервер");
        }
    }

    private void receiveMessage(BufferedReader in) {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
                Logger.log("(получено): " + message);
            }
        } catch (IOException e) {
            System.out.println("------------ Разорвано соединение с сервером -----------");
        }
    }

    static void main(String[] args) {
        String host = readHostFromFile("client-settings.txt");
        int port = readPortFromFile("client-settings.txt");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите свое имя: ");
        String username = scanner.nextLine().trim();

        if (username.isBlank()) {
            username = "Anonym";
        }

        new ChatClient(host, port, username).startWithScanner();
    }


    public static String readHostFromFile(String filePath) {
        try {
            List<String> lines = Files.readAllLines(Path.of(filePath));
            for (String line : lines) {
                if (line.startsWith("host=")) {
                    return line.split("=")[1].trim();
                }
            }
        } catch (IOException e) {
            System.out.println("Файл с настройками не найден! Используется default");
        }
        return "localhost";
    }

    public static int readPortFromFile(String filePath) {
        try {
            List<String> lines = Files.readAllLines(Path.of(filePath));
            for (String line : lines) {
                if (line.startsWith("port=")) {
                    return Integer.parseInt(line.split("=")[1].trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Файл с настройками не найден! Используется default порт");
        }
        return 12345;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }
}