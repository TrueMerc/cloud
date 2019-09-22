package ru.ryabtsev.cloud.client.handlers;

import javafx.application.Platform;
import ru.ryabtsev.cloud.client.FileManagementController;
import ru.ryabtsev.cloud.common.interfaces.MessageHandler;
import ru.ryabtsev.cloud.common.message.server.file.FileStructureResponse;

/**
 * Handles file structure response.
 */
public class FileStructureResponseHandler implements MessageHandler {

    private final FileManagementController controller;
    private final FileStructureResponse message;

    /**
     * Constructs file structure response handler.
     */
    public FileStructureResponseHandler(FileManagementController controller, FileStructureResponse message) {
        this.controller = controller;
        this.message = message;
    }

    @Override
    public void handle() {
        if (Platform.isFxApplicationThread()) {
            controller.refreshServerFilesList(message.getDescription());
        } else {
            Platform.runLater(() -> controller.refreshServerFilesList(message.getDescription()));
        }
    }
}
