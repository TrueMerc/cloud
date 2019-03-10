package ru.ryabtsev.cloud.client.gui.dialog;

import javafx.scene.control.Alert;

/**
 * Implements 'no selected files' alert dialog.
 */
public class NoSelectedFilesAlert extends Alert {
    private static final String HEADER_TEXT = "No files to copy";
    private static final String EXPLANATION_TEXT = "There aren't selected files to copy.";

    /**
     * Constructs 'no selected files' alert dialog.
     */
    public NoSelectedFilesAlert() {
        super(AlertType.INFORMATION, EXPLANATION_TEXT);
        setTitle(HEADER_TEXT);
        setHeaderText(HEADER_TEXT);
    }
}
