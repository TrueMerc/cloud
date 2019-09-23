package ru.ryabtsev.cloud.client.commands;

import javafx.collections.ObservableList;
import ru.ryabtsev.cloud.client.FileManagementController;
import ru.ryabtsev.cloud.client.service.NetworkService;
import ru.ryabtsev.cloud.common.FileDescription;
import ru.ryabtsev.cloud.common.message.ApplicationSide;
import ru.ryabtsev.cloud.common.message.client.file.DeleteRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Implements deletion command.
 */
public class Delete extends SequentialCommand {

    private final FileManagementController controller;
    private final ObservableList<FileDescription> files;
    private final ApplicationSide side;

    /**
     * Constructs deletion command.
     */
    public Delete(FileManagementController controller, ObservableList<FileDescription> files, ApplicationSide side) {
        this.controller = controller;
        this.files = files;
        this.side = side;
    }

    @Override
    public void execute() {
        for( FileDescription description : files ) {
            if(ApplicationSide.CLIENT == side) {
                final String name = description.getFullName();
                if( controller.getFilesToUpload().contains(name) ) {
                    controller.getFilesToDelete().add(name);
                }
                else {
                    try {
                        Files.delete( Paths.get(controller.getCurrentFolderName(), name) );
                    }
                    catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                String user = controller.getUserName();
                NetworkService network = controller.getNetworkService();
                network.sendMessage(new DeleteRequest(user, description.getFullName()));
            }
        }
    }
}
