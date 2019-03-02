package ru.ryabtsev.cloud.common.message.client.file;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.common.message.client.UserDependentRequest;

/**
 * Implements 'download request' message from client to server.
 */
@Getter
public class DownloadRequest extends UserDependentRequest implements FileRequest {
    private String fileName;
    private int partNumber;

    /**
     * Constructs download file request with given file name.
     * @param fileName name of the file, which is requested by client.
     */
    public DownloadRequest(@NotNull final String login, @NotNull final String fileName, int partNumber) {
        super(login);
        this.fileName = fileName;
        this.partNumber = partNumber;
    }
}
