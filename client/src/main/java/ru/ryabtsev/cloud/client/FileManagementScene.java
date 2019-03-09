package ru.ryabtsev.cloud.client;

import javafx.scene.Group;
import javafx.scene.Scene;

/**
 * Implements file management scene.
 */
public class FileManagementScene extends Scene {

    /**
     * Constructs new file management scene.
     * @param width scene width.
     * @param height scene height.
     */
    FileManagementScene(double width, double height) {
        super(new Group(), width, height);
    }
}
