package ru.ryabtsev.cloud.client.handlers;

import ru.ryabtsev.cloud.client.FileManagementController;
import ru.ryabtsev.cloud.common.interfaces.MessageHandler;
import ru.ryabtsev.cloud.common.message.server.file.DeleteResponse;

/**
 * Handles delete response.
 */
public class DeleteResponseHandler implements MessageHandler {
    private final FileManagementController controller;
    private final DeleteResponse message;

    /**
     * Constructs delete response handler.
     */
    public DeleteResponseHandler(FileManagementController controller, DeleteResponse message) {
        this.controller = controller;
        this.message = message;
    }

    @Override
    public void handle() {
        if(message.isSuccessful()) {
            controller.requestServerFilesList();
        }
    }
}
