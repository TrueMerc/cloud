package ru.ryabtsev.cloud.common.message;

import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Implements message which contains file or its part.
 */
@Getter
public class FileMessage implements Message {
    private String fileName;
    private byte[] data;

    /**
     * Constructs file message with given file name.
     * @param path path corresponding to the file,
     * which is contained in the message in whole or in part.
     * @throws IOException
     */
    public FileMessage(Path path) throws IOException {
        fileName = path.getFileName().toString();
        data = Files.readAllBytes(path);
    }

    @Override
    public Class<? extends Message> type() {
        return this.getClass();
    }
}
