package ru.ryabtsev.cloud.common.message.client.file;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.common.message.AbstractMessage;

/**
 * Implements file renaming request.
 */
@Getter
public class RenameRequest extends AbstractMessage implements FileRequest {

    private final String oldName;
    private final String newName;

    /**
     * Constructs file renaming request.
     * @param oldName old file name.
     * @param newName new file name.
     */
    public RenameRequest(@NotNull final String oldName, @NotNull final String newName) {
        this.oldName = oldName;
        this.newName = newName;
    }
}
