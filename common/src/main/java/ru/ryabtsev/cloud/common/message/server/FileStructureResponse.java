package ru.ryabtsev.cloud.common.message.server;

import ru.ryabtsev.cloud.common.FileDescription;
import ru.ryabtsev.cloud.common.message.Message;

import java.nio.file.Path;
import java.util.List;

public class FolderStructureResponse implements Message {

    private List<FileDescription> filesDescriptionList;

    @Override
    public Class<? extends Message> type() {
        return this.getClass();
    }

    FolderStructureResponse(final Path folderPath) {

    }
}
