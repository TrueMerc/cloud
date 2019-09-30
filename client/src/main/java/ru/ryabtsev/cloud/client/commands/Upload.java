package ru.ryabtsev.cloud.client.commands;

import javafx.collections.ObservableList;
import ru.ryabtsev.cloud.client.FileManagementController;
import ru.ryabtsev.cloud.common.FileDescription;
import ru.ryabtsev.cloud.common.NetworkSettings;
import ru.ryabtsev.cloud.common.message.ApplicationSide;
import ru.ryabtsev.cloud.common.message.FileMessage;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Implements upload command.
 */
public class Upload extends Copy {

    public Upload(FileManagementController controller, ObservableList<FileDescription> files) {
        super(controller, files, ApplicationSide.CLIENT);
    }

    @Override
    protected void onSendMethod(FileDescription file) {
        final var network = controller.getNetworkService();
        final var user = controller.getUserName();
        final var folder = controller.getCurrentFolderName();
        try {
            network.sendMessage(
                    new FileMessage(
                            user,
                            Paths.get(folder, file.getFullName()),
                            0,
                            NetworkSettings.MAXIMAL_PAYLOAD_SIZE_IN_BYTES
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
