package com.chat.common;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    public static final String LOG_FILE = "file.log";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void log(String message) {
        String time = LocalDateTime.now().format(FORMATTER);
        String line = "[" + time + "] " + message;
        IO.println(line);

        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(line);
        } catch (IOException e) {
            System.err.println("Ошибка записи в лог: " + e.getLocalizedMessage());
        }
    }
}
