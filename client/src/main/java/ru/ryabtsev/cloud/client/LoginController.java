package ru.ryabtsev.cloud.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import ru.ryabtsev.cloud.client.service.AuthenticationService;
import ru.ryabtsev.cloud.client.service.NetworkAuthenticationService;

/**
 * Implements login window controller.
 */
public class LoginController {
    @FXML
    TextField login;

    @FXML
    PasswordField password;

    @FXML
    BorderPane parentWindow;

    private AuthenticationService authenticationService = new NetworkAuthenticationService(
            ClientApplication.getNetworkService()
    );

    public void authenticate(ActionEvent actionEvent) {
        if( authenticationService.authenticate(login.getText(), password.getText()) ) {
            ClientApplication.userName = login.getText();
            ClientApplication.getInstance().setActiveScene(ClientApplication.SceneId.FILE_MANAGEMENT);
        }
    }
}