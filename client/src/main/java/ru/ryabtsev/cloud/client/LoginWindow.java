package ru.ryabtsev.cloud.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * Implements login window class
 */
public class LoginWindow extends Parent {

    private static final String FXML_FILE_NAME = "/Login.fxml";

    /**
     * Constructs new login window.
     */
    public LoginWindow() {
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_FILE_NAME));
        Parent root =  fxmlLoader.load();
    }
}
