package com.chat.common;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoggerTest {
    @AfterEach
    void cleanUp() throws IOException {
        Files.deleteIfExists(Path.of("file.log"));
    }

    @Test
    void whenLogWritesToFileThanLogWillBeWrite() throws IOException{
        Logger.log("test text in message");
        String content = Files.readString(Path.of("file.log"));
        assertTrue(content.contains("test text in message"), "Файл должен содержать записанное сообщение");
    }

    @Test
    void whenLogAppendsToFile() throws IOException {
        Logger.log("first message");
        Logger.log("second message");

        String content = Files.readString(Path.of("file.log"));

        assertTrue(content.contains("first message"));
        assertTrue(content.contains("second message"));
    }
}
