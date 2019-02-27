package ru.ryabtsev.cloud.common.message.client;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.common.message.Message;

/**
 * Implements 'file request' message from client to server.
 */
@Getter
public class FileRequest extends UserRequest {
    private String fileName;
    private int partNumber;

    /**
     * Constructs file request with given file name.
     * @param fileName name of the file, which is requested by client.
     */
    public FileRequest(@NotNull final String login, @NotNull final String fileName, int partNumber) {
        super(login);
        this.fileName = fileName;
        this.partNumber = partNumber;
    }
}
