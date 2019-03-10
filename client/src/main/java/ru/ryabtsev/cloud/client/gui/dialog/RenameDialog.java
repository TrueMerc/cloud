package ru.ryabtsev.cloud.client.gui.dialog;

import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;

/**
 * Implements dialog for file renaming operation.
 */
public class RenameDialog extends TextInputDialog {

    /**
     * Constructs dialog for file renaming operation.
     * @param fileName name of the file which will be renamed.
     */
    public RenameDialog(final String fileName) {
        super(fileName);
        this.initModality(Modality.APPLICATION_MODAL);
        this.setTitle("Rename");
        this.setHeaderText("Please, enter new name");
    }
}
