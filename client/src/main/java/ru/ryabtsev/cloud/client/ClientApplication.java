package ru.ryabtsev.cloud.client;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


/**
 * Implements cloud client application.
 */
public class ClientApplication extends Application {

    private static final int DEFAULT_WIDTH = 640;
    private static final int DEFAULT_HEIGHT = 480;

    @Override
    public void start(Stage primaryStage) throws Exception {
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        Parent root =  fxmlLoader.load();
        LoginController controller = fxmlLoader.getController();

        Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene( scene );
        primaryStage.setTitle("Cloud client");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
