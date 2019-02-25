package ru.ryabtsev.cloud.common.message.server;

import lombok.Getter;
import ru.ryabtsev.cloud.common.FileDescription;
import ru.ryabtsev.cloud.common.message.Message;

/**
 * Implements server response about file with given description.
 */
@Getter
public class FileStructureResponse extends Message {

    private FileDescription description;

    /**
     * Constructs server response about file structure with given description.
     * @param description file structure description.
     */
    public FileStructureResponse(final FileDescription description) {
        this.description = description;
    }
}
