package ru.ryabtsev.cloud.client.gui.dialog;

import javafx.scene.control.Alert;

/**
 * Implements 'bad rename arguments' alert dialog.
 */
public class BadRenameArgumentsAlert extends Alert {
    private static final String HEADER_TEXT = "Can't rename file";
    private static final String EXPLANATION_TEXT = "You should choose the only one file to rename.";

    /**
     * Constructs 'no selected files' alert dialog.
     */
    public BadRenameArgumentsAlert() {
        super(AlertType.WARNING, EXPLANATION_TEXT);
        setTitle(HEADER_TEXT);
        setHeaderText(HEADER_TEXT);
    }
}
