package ru.ryabtsev.cloud.client.commands;

import javafx.collections.ObservableList;
import ru.ryabtsev.cloud.client.FileManagementController;
import ru.ryabtsev.cloud.common.FileDescription;
import ru.ryabtsev.cloud.common.message.ApplicationSide;
import ru.ryabtsev.cloud.common.message.client.file.DownloadRequest;

/**
 * Implements download command.
 */
public class Download extends Copy {

    public Download(FileManagementController controller, ObservableList<FileDescription> files) {
        super(controller, files, ApplicationSide.SERVER);
    }

    @Override
    protected void onSendMethod(FileDescription file) {
        final String name = file.getName() + "." + file.getExtension();
        final var network = controller.getNetworkService();
        final var user = controller.getUserName();
        network.sendMessage(new DownloadRequest(user, name, 0));
        super.execute();
    }
}
