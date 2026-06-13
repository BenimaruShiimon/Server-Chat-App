package com.chat.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChatServerTest {
    public static final String TEST_SETTINGS = "test-server-settings.txt";

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(Path.of(TEST_SETTINGS));
        Files.deleteIfExists(Path.of("file.log"));
    }

    @Test
    void whenServerReadsPortFromSettingsThanWillBeSuccess() throws IOException {
        Files.writeString(Path.of(TEST_SETTINGS), "port=8080\n");
        int port = ChatServer.readPort(TEST_SETTINGS);
        assertEquals(8080, port, "Сервер должен прочитать порт 8888 из файла");
    }

    @Test
    void whenServerUsesDefaultPortWhenFileWillBeNotFound() {
        int port = ChatServer.readPort("non-exist-file.txt");
        assertEquals(12345, port, "Если файла нет, берет дефолтный порт");
    }

    @Test
    void whenFileWithSettingsNotFoundThatServerUsesDefaultPort() throws IOException {
        Files.writeString(Path.of(TEST_SETTINGS), "host=localhost\n");
        int port = ChatServer.readPort(TEST_SETTINGS);
        assertEquals(12345, port, "Если порт не указан в конфиг файле, то должен браться дефолтный порт");
    }

    @Test
    void whenServerCreatedWithCorrectPortThatPortMatchesWithTransmittedPort() {
        ChatServer server = new ChatServer(7777);
        assertEquals(7777, server.getPort(), "При передаче порта сервера он должен совпадать");
    }

    @Test
    void clientsCountsChangesCorrectly() {
        ChatServer server = new ChatServer(9999);
        assertEquals(0, server.getClientCount(), "При первом старте клиентов должно быть 0!");
    }
}

