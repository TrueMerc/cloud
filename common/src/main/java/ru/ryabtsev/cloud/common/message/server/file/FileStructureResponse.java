package ru.ryabtsev.cloud.common.message.server.file;

import lombok.Getter;
import ru.ryabtsev.cloud.common.FileDescription;
import ru.ryabtsev.cloud.common.message.AbstractMessage;

/**
 * Implements server response about file with given description.
 */
@Getter
public class FileStructureResponse extends AbstractMessage implements FileResponse {

    private FileDescription description;

    /**
     * Constructs server response about file structure with given description.
     * @param description file structure description.
     */
    public FileStructureResponse(final FileDescription description) {
        this.description = description;
    }

    @Override
    public boolean isSuccessful() {
        return true;
    }
}
