package ru.ryabtsev.cloud.client.handlers;

import ru.ryabtsev.cloud.client.FileManagementController;
import ru.ryabtsev.cloud.common.NetworkSettings;
import ru.ryabtsev.cloud.common.interfaces.MessageHandler;
import ru.ryabtsev.cloud.common.message.FileMessage;
import ru.ryabtsev.cloud.common.message.server.file.UploadResponse;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Handles upload response.
 */
public class UploadResponseHandler implements MessageHandler {

    private final FileManagementController controller;
    private final UploadResponse message;

    /**
     * Constructs upload response handler.
     */
    public UploadResponseHandler(FileManagementController controller, UploadResponse message) {
        this.controller = controller;
        this.message = message;
    }

    @Override
    public void handle() {
        if( message.isSuccessful() ) {
            if (!message.isCompleted()) {
                try {
                    controller.getNetworkService().sendMessage(
                            new FileMessage(
                                    controller.getUserName(),
                                    Paths.get(controller.getCurrentFolderName(), message.getFileName()),
                                    message.getNextNumber(),
                                    NetworkSettings.MAXIMAL_PAYLOAD_SIZE_IN_BYTES
                            )
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                controller.requestServerFilesList();
                final String name = message.getFileName();
                controller.getFilesToUpload().remove(name);
                final var filesToDelete = controller.getFilesToDelete();
                if(filesToDelete.contains(name)) {
                    controller.delete( Paths.get(controller.getCurrentFolderName(), name) );
                    filesToDelete.remove(name);
                }
            }
        }
    }
}
