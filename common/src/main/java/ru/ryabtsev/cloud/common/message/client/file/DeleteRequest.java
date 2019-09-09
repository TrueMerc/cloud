package ru.ryabtsev.cloud.common.message.client.file;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.common.message.UserDependentMessage;

/**
 * Implements file deletion request.
 */
@Getter
public class DeleteRequest extends UserDependentMessage implements FileRequest {

    private String fileName;


    /**
     * Constructs delete file request with given file name.
     * @param login user login.
     * @param fileName name of the file, which is requested by client.
     */
    public DeleteRequest(@NotNull final String login, @NotNull final String fileName) {
        super(login);
        this.fileName = fileName;
    }
}
