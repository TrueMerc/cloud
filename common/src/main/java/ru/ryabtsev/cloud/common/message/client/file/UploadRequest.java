package ru.ryabtsev.cloud.common.message.client.file;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.common.message.client.UserDependentRequest;

/**
 * Implements 'upload request' message from client to server.
 */
@Getter
public class UploadRequest extends UserDependentRequest implements FileRequest {

    private String fileName;
    private int partNumber;

    /**
     * Constructs upload request with given file name.
     * @param login user login.
     * @param fileName name of the file, which is requested by client.
     * @param partNumber number of the part of the file.
     */
    public UploadRequest(@NotNull final String login, @NotNull final String fileName, int partNumber) {
        super(login);
        this.fileName = fileName;
        this.partNumber = partNumber;
    }
}
