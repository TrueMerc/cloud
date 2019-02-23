package ru.ryabtsev.cloud.common.message.client;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.common.message.Message;

/**
 * Implements client request to server to get structure of folder with given name.
 */
@Getter
public class FolderStructureRequest implements Message {

    private String folderName;

    /**
     * Constructs request about contents of the folder with given name.
     * @param folderName name of the folder.
     */
    public FolderStructureRequest(@NotNull final String folderName) {
        this.folderName = folderName;
    }

    public FolderStructureRequest() {
        this("");
    }

    @Override
    public Class<? extends Message> type() {
        return this.getClass();
    }
}
