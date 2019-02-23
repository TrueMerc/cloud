package ru.ryabtsev.cloud.client;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.ryabtsev.cloud.client.service.NettyNetworkService;
import ru.ryabtsev.cloud.client.service.NetworkService;
import ru.ryabtsev.cloud.common.FileDescription;
import ru.ryabtsev.cloud.common.PortInformation;
import ru.ryabtsev.cloud.common.message.FileMessage;
import ru.ryabtsev.cloud.common.message.Message;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;

/**
 * Implements client application controller.
 */
public class ClientApplicationController implements Initializable {

    private static final String COPY_BUTTON_TEXT = "Copy";
    private static final String CUT_BUTTON_TEXT = "Cut";
    private static final String DELETE_BUTTON_TEXT = "Delete";

    private static final String DEFAULT_SERVER_HOST = "localhost";
    private static final int DEFAULT_SERVER_PORT = PortInformation.DEFAULT_PORT;

    @FXML
    TableView<FileDescription> clientFilesView = new TableView<>();

    @FXML
    TableView<FileDescription> serverFilesView = new TableView<>();

    @FXML
    Button clientCopyButton = new Button();

    @FXML
    Button clientCutButton = new Button();

    @FXML
    Button clientDeleteButton = new Button();

    @FXML
    Button serverCopyButton = new Button();

    @FXML
    Button serverCutButton = new Button();

    @FXML
    Button serverDeleteButton = new Button();

    private NetworkService networkService;

    private String currentFolderName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeClientList();
        initializeServerList();
        initializeButtons();
        initializeNetwork();
//        clientFilesView.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent mouseEvent) {
//                if(mouseEvent.getClickCount() >= 2) {
//                    //System.
//                }
//            }
//        });
    }

    private void initializeClientList() {
        currentFolderName = System.getProperty("user.home");
        System.out.println( "User home path: " + currentFolderName );

        addColumnsToFilesTableView( clientFilesView );
        refreshClientFilesList();
    }

    private static void addColumnsToFilesTableView(TableView<FileDescription> tableView) {
        TableColumn<FileDescription, String> tcName = new TableColumn<>("Name");
        tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<FileDescription, String> tcExtension = new TableColumn<>("Extension");
        tcExtension.setCellValueFactory(new PropertyValueFactory<>("extension"));

        TableColumn<FileDescription, String> tcSize = new TableColumn<>("Size");
        tcSize.setCellValueFactory(new PropertyValueFactory<>("size"));

        TableColumn<FileDescription, String> tcDate = new TableColumn<>("Date");
        tcDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<FileDescription, String> tcAttributes = new TableColumn<>("Attributes");
        tcAttributes.setCellValueFactory(new PropertyValueFactory<>("attributes"));

        tableView.getColumns().addAll( tcName, tcExtension, tcSize, tcDate, tcAttributes );
    }

    private void refreshClientFilesList() {
        File currentFolder = new File(currentFolderName);

        File[] files = currentFolder.listFiles();
        FileDescription[] filesDescriptions = new FileDescription[files.length];

        for(int i = 0; i < filesDescriptions.length; ++i) {
            filesDescriptions[i] = new FileDescription(files[i]);
        }

        clientFilesView.getItems().addAll( filesDescriptions );
    }

    private void initializeServerList() {
        addColumnsToFilesTableView( serverFilesView );
    }

    private void initializeButtons() {
        clientCopyButton.setText(COPY_BUTTON_TEXT);
        clientCutButton.setText(CUT_BUTTON_TEXT);
        clientDeleteButton.setText(DELETE_BUTTON_TEXT);

        serverCopyButton.setText(COPY_BUTTON_TEXT);
        serverCutButton.setText(CUT_BUTTON_TEXT);
        serverDeleteButton.setText(DELETE_BUTTON_TEXT);
    }

    private void initializeNetwork() {
        networkService = new NettyNetworkService();
        networkService.start(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT);

        Thread thread = new Thread(()->{
            try {
                while (true) {
                    Message message = networkService.receiveMessage();
                    Class<?> messageType = message.type();
                    if (messageType.equals(FileMessage.class)) {
                        FileMessage fileMessage = (FileMessage) message;
                        Files.write(
                                Paths.get(currentFolderName + '/' + fileMessage.getFileName()),
                                fileMessage.getData(),
                                StandardOpenOption.CREATE
                        );
                        refreshClientFilesList();
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
                networkService.stop();
            }
        });
        thread.setDaemon(true);
        thread.start();
        refreshClientFilesList();
    }
}
