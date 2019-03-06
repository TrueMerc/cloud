package ru.ryabtsev.cloud.client;



import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ru.ryabtsev.cloud.client.service.NettyNetworkService;
import ru.ryabtsev.cloud.client.service.NetworkService;
import ru.ryabtsev.cloud.common.NetworkSettings;

/**
 * Implements cloud client application.
 */
public class ClientApplication extends Application {

    private static final int DEFAULT_WIDTH = 640;
    private static final int DEFAULT_HEIGHT = 480;

    public static Stage MAIN_WINDOW;

    private static final String DEFAULT_SERVER_HOST = "localhost";
    private static final int DEFAULT_SERVER_PORT = NetworkSettings.DEFAULT_PORT;

    public static NetworkService networkService = new NettyNetworkService();

    @Override
    public void start(Stage primaryStage) throws Exception {
        MAIN_WINDOW = primaryStage;
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        Parent root =  fxmlLoader.load();

        networkService.start(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT);

        Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene( scene );
        primaryStage.setTitle("Cloud client");
        primaryStage.getIcons().add(new Image("/cloud_up_icon_white_background.png"));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static NetworkService networkService() {
        return networkService;
    }
}
