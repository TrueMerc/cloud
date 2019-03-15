package ru.ryabtsev.cloud.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.stage.Window;
import ru.ryabtsev.cloud.client.gui.dialog.AboutDialog;

/**
 * Implements menu bar controller.
 */
public class MenuBarController {
    @FXML
    private void exitApplication() {
        Platform.exit();
    }

    @FXML
    private void disconnect() {
        ClientApplication.getNetworkService().stop();
        ClientApplication.getInstance().setActiveScene(ClientApplication.SceneId.LOGIN);
    }

    @FXML
    private void aboutDialog() {
        try {
            AboutDialog dialog = new AboutDialog(ClientApplication.primaryStage);
            Window window = dialog.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest(event -> window.hide());
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
