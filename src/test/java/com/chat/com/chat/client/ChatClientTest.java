package com.chat.com.chat.client;

import com.chat.client.ChatClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChatClientTest {
    public static final String TEST_SETTINGS = "test-server-settings.txt";

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(Path.of(TEST_SETTINGS));
        Files.deleteIfExists(Path.of("file.log"));
    }

    @Test
    void whenClientReadsHostFromSettingsFileThanHostWillBeCorrectly() throws IOException {
        Files.writeString(Path.of(TEST_SETTINGS), "host=192.168.1.1\nport=8080\n");
        String host = ChatClient.readHostFromFile(TEST_SETTINGS);
        assertEquals("192.168.1.1", host, "Клиент должен прочитать хост из файла");
    }

    @Test
    void whenClientReadsPortFromSettingsFileThanPortWillBeCorrectly() throws IOException {
        Files.writeString(Path.of(TEST_SETTINGS), "host=localhost\nport=8080\n");
        int port = ChatClient.readPortFromFile(TEST_SETTINGS);
        assertEquals(8080, port, "Клиент должен прочитать записаный порт из файла");
    }

    @Test
    void whenClientUsesDefaultsSettingIfFileNotFound() {
        String host = ChatClient.readHostFromFile("non-exist.txt");
        int port = ChatClient.readPortFromFile("non-exist.txt");
        assertEquals("localhost", host);
        assertEquals(12345, port);
    }

    @Test
    void whenUsernameIsEmptyThanUsernameWillBeAnonym() {
        String username = " ".trim();
        if (username.isBlank()) {
            username = "Anonym";
        }
        assertEquals("Anonym", username, "Если пользователь не написал своего имени, то присваивается имя Anonym");
    }

    @Test
    void whenClientCreatedWithCorrectParameter() {
        ChatClient client = new ChatClient("localhost", 12345, "Avraam");
        assertEquals("localhost", client.getHost());
        assertEquals(12345, client.getPort());
        assertEquals("Avraam", client.getUsername());
    }
}
