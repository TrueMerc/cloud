package ru.ryabtsev.cloud.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.ryabtsev.cloud.client.service.AuthencticationService;
import ru.ryabtsev.cloud.client.service.DummyAuthenticationService;
import ru.ryabtsev.cloud.client.service.NetworkAuthenticationService;

import java.io.IOException;


/**
 * Implements login window controller.
 */
public class LoginController {
    @FXML
    TextField login;

    @FXML
    PasswordField password;

    @FXML
    VBox parentWindow;

    private AuthencticationService authencticationService = new NetworkAuthenticationService(
            ClientApplication.networkService()
    );

    public void authenticate(ActionEvent actionEvent) {

        if( authencticationService.authenticate(login.getText(), password.getText()) ) {
            try {
                ClientApplication.userName = login.getText();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ClientApplication.fxml"));
                final Parent root = fxmlLoader.load();
                final Scene scene = new Scene( root, parentWindow.getWidth(), parentWindow.getHeight() );
                ((Stage)parentWindow.getScene().getWindow()).setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}