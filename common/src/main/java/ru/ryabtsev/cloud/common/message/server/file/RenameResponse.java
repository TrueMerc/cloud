package ru.ryabtsev.cloud.common.message.server.file;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.common.message.AbstractMessage;

/**
 * Implements response to renaming operation request.
 */
@Getter
public class RenameResponse extends AbstractMessage implements FileResponse {

    private final String oldName;
    private final String newName;
    private boolean isSuccessful;

    /**
     * Constructs response to renaming operation request.
     */
    public RenameResponse (@NotNull final String oldName, @NotNull final String newName, boolean isSuccessful) {
        this.oldName = oldName;
        this.newName = newName;
        this.isSuccessful = isSuccessful;
    }
}
