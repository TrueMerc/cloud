package ru.ryabtsev.cloud.client.gui.dialog;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Dialog;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Implements dialog which contains program description.
 */
public class AboutDialog extends Dialog {

    /**
     * Construct new dialog which contains application description.
     * @param parent
     */
    public AboutDialog(final Stage parent) throws Exception {
        this.initOwner(parent);
        this.initModality(Modality.APPLICATION_MODAL);

        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AboutDialog.fxml"));
        Parent root  = fxmlLoader.load();

        this.setTitle("About Cloud client");
        this.getDialogPane().setContent(root);
    }
}
