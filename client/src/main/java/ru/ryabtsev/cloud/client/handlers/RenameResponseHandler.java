package ru.ryabtsev.cloud.client.handlers;

import ru.ryabtsev.cloud.client.FileManagementController;
import ru.ryabtsev.cloud.common.interfaces.MessageHandler;
import ru.ryabtsev.cloud.common.message.server.file.RenameResponse;

/**
 * Handles rename response.
 */
public class RenameResponseHandler implements MessageHandler {

    private final FileManagementController controller;
    private final RenameResponse message;

    /**
     * Constructs rename response handler.
     * @param controller file management controller.
     * @param message rename response.
     */
    public RenameResponseHandler(FileManagementController controller, RenameResponse message) {
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
