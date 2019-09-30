package ru.ryabtsev.cloud.client.commands;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import ru.ryabtsev.cloud.client.FileManagementController;
import ru.ryabtsev.cloud.client.gui.dialog.NoSelectedFilesAlert;
import ru.ryabtsev.cloud.common.FileDescription;
import ru.ryabtsev.cloud.common.message.ApplicationSide;

import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Implements copying command.
 */
public abstract class Copy extends SequentialCommand {

    protected final FileManagementController controller;
    private final ObservableList<FileDescription> files;
    private final ApplicationSide from;

    /**
     * Constructs copying command.
     */
    Copy(FileManagementController controller, ObservableList<FileDescription> files, ApplicationSide from) {
        this.controller = controller;
        this.files = files;
        this.from = from;
    }

    @Override
    public void execute() {

        if(ApplicationSide.CLIENT == from) {
            final var upload = controller.getFilesToUpload();
            upload.clear();
            upload.addAll(  files.stream()
                            .map(FileDescription::getFullName)
                            .collect(Collectors.toList())
            );
        }

        for( FileDescription file : files ) {
            onSendMethod(file);
        }
    }

    protected abstract void onSendMethod(FileDescription file);

}
