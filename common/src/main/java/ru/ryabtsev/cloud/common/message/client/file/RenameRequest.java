package ru.ryabtsev.cloud.common.message.client.file;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.common.message.UserDependentMessage;

/**
 * Implements file renaming request.
 */
@Getter
public class RenameRequest extends UserDependentMessage implements FileRequest {

    private final String oldName;
    private final String newName;

    /**
     * Constructs file renaming request.
     * @param oldName old file name.
     * @param newName new file name.
     */
    public RenameRequest(@NotNull final String login, @NotNull final String oldName, @NotNull final String newName) {
        super(login);
        this.oldName = oldName;
        this.newName = newName;
    }
}
