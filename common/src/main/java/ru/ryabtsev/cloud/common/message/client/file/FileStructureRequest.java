package ru.ryabtsev.cloud.common.message.client.file;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.common.message.client.UserDependentRequest;

/**
 * Implements client request to server to get structure of folder with given name.
 */
@Getter
public class FileStructureRequest extends UserDependentRequest {

    private String folderName;

    /**
     * Constructs request about contents of the folder with given name.
     * @param login user login.
     * @param folderName name of the folder.
     */
    public FileStructureRequest(@NotNull final String login, @NotNull final String folderName) {
        super(login);
        this.folderName = folderName;
    }

    /**
     * Constructs request about contents of the folder with given name.
     * @param login user login.
     */
    public FileStructureRequest(@NotNull final String login) {
        this(login, "");
    }
}
