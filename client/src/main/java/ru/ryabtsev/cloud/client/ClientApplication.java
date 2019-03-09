package ru.ryabtsev.cloud.client;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.client.service.NettyNetworkService;
import ru.ryabtsev.cloud.client.service.NetworkService;
import ru.ryabtsev.cloud.common.NetworkSettings;

import java.io.IOException;

/**
 * Implements cloud client application.
 */
public class ClientApplication extends Application {

    /**
     * Enumeration contains possible application scenes identifiers.
     */
    public enum SceneId {
        LOGIN,
        FILE_MANAGEMENT
    }

    private static final String LOGIN_FXML_FILE_NAME = "/Login.fxml";
    private static final String FILE_MANAGEMENT_FXML_FILE_NAME = "/FileManagement.fxml";

    private static final int DEFAULT_WIDTH = 640;
    private static final int DEFAULT_HEIGHT = 480;

    public static Stage primaryStage;

    public static String userName = "";

    private static final String DEFAULT_SERVER_HOST = "localhost";
    private static final int DEFAULT_SERVER_PORT = NetworkSettings.DEFAULT_PORT;

    public static NetworkService networkService = new NettyNetworkService();

    private static final ClientApplication INSTANCE = new ClientApplication();

    @Override
    public void start(Stage primaryStage) {
        ClientApplication.primaryStage = primaryStage;
        initPrimaryStage(primaryStage);

        networkService.start(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT);
        primaryStage.show();
    }

    public static ClientApplication getInstance() {
        return INSTANCE;
    }

    private void initPrimaryStage(Stage stage) {
        stage.setTitle("Cloud client");
        stage.getIcons().add(new Image("/cloud_up_icon_white_background.png"));
        stage.setWidth(DEFAULT_WIDTH);
        stage.setHeight(DEFAULT_HEIGHT);

        setActiveScene(SceneId.LOGIN);
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Returns application network service.
     * @return application network service.
     */
    public static NetworkService networkService() {
        return networkService;
    }

    /**
     * Sets active application scene by given identificator.
     * @param sceneId scene identifier.
     */
    public void setActiveScene(SceneId sceneId) {
        switch (sceneId) {
            case LOGIN:
                setScene(LOGIN_FXML_FILE_NAME);
                break;
            case FILE_MANAGEMENT:
                setScene(FILE_MANAGEMENT_FXML_FILE_NAME);
                break;
            default:
                throw new RuntimeException("Unexpected application scene identifier.");
        }
    }

    private void setScene(@NotNull final String fxmlFilename) {
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFilename));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    }
}
