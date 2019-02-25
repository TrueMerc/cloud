package ru.ryabtsev.cloud.common.message.client;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.common.message.Message;

/**
 * Implements client request to server to get structure of folder with given name.
 */
@Getter
public class FileStructureRequest extends Message {

    private String folderName;

    /**
     * Constructs request about contents of the folder with given name.
     * @param folderName name of the folder.
     */
    public FileStructureRequest(@NotNull final String folderName) {
        this.folderName = folderName;
    }

    public FileStructureRequest() {
        this("");
    }
}
