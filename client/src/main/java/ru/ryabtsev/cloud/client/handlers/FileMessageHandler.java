package ru.ryabtsev.cloud.client.handlers;

import ru.ryabtsev.cloud.client.FileManagementController;
import ru.ryabtsev.cloud.common.FileOperations;
import ru.ryabtsev.cloud.common.interfaces.MessageHandler;
import ru.ryabtsev.cloud.common.message.FileMessage;
import ru.ryabtsev.cloud.common.message.client.file.DownloadRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Handles file message.
 */
public class FileMessageHandler implements MessageHandler {

    private final FileManagementController controller;
    private final FileMessage message;

    /**
     * Constructs file messages handler.
     * @param controller file management controller.
     */
    FileMessageHandler(final FileManagementController controller, final FileMessage message) {
        this.controller = controller;
        this.message = message;
    }

    @Override
    public void handle() {
        try {
            Path path = Paths.get( controller.getCurrentFolderName(), message.getFileName() );
            StandardOpenOption openOption = FileOperations.getOpenOption(path.toString(), message.isFirstPart());

            Files.write(path, message.getData(), openOption);

            if( message.hasNext() ) {
                final String user = controller.getUserName();
                final String file = message.getFileName();
                final int next = message.getPartNumber() + 1;
                controller.getNetworkService().sendMessage(new DownloadRequest(user, file, next));
            }
            else {
                controller.refreshClientFilesList();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
