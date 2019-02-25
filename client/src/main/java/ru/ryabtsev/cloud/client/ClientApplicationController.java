package ru.ryabtsev.cloud.client;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.client.service.NettyNetworkService;
import ru.ryabtsev.cloud.client.service.NetworkService;
import ru.ryabtsev.cloud.common.FileDescription;
import ru.ryabtsev.cloud.common.PortInformation;
import ru.ryabtsev.cloud.common.message.FileMessage;
import ru.ryabtsev.cloud.common.message.Message;
import ru.ryabtsev.cloud.common.message.client.FileStructureRequest;
import ru.ryabtsev.cloud.common.message.client.HandshakeRequest;
import ru.ryabtsev.cloud.common.message.server.FileStructureResponse;
import ru.ryabtsev.cloud.common.message.server.HandshakeResponse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Implements client application controller.
 */
public class ClientApplicationController implements Initializable {

    private static final String COPY_BUTTON_TEXT = "Copy";
    private static final String CUT_BUTTON_TEXT = "Cut";
    private static final String DELETE_BUTTON_TEXT = "Delete";

    private static final String DEFAULT_SERVER_HOST = "localhost";
    private static final int DEFAULT_SERVER_PORT = PortInformation.DEFAULT_PORT;

    private static final Logger LOGGER = Logger.getLogger(ClientApplication.class.getSimpleName());

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

    private static NetworkService networkService = new NettyNetworkService();;

    private String currentFolderName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeClientList();
        initializeServerList();
        initializeButtons();
        initializeNetwork();
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

        FileDescription folderDescription = new FileDescription(currentFolder);

        refreshFilesList(clientFilesView, folderDescription);
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
        networkService.start(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT);

        Thread thread = new Thread(()->{
            LOGGER.info("Listener thread started.");
            try {
                while (true) {
                    Message message = networkService.receiveMessage();
                    if(message == null) {
                        LOGGER.warning("null message received.");
                        continue;
                    }
                    Class<?> messageType = message.type();
                    if (messageType.equals(HandshakeResponse.class)) {
                        processHandshakeResponse((HandshakeResponse)message);
                    }
                    else if(messageType.equals(FileMessage.class)) {
                        processFileMessage((FileMessage)message);
                    }
                    else if(messageType.equals(FileStructureResponse.class)) {
                        processFileStructureResponse((FileStructureResponse)message);
                    }
                    else {
                        LOGGER.warning("Unexpected message received with type " + message.type());
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
                networkService.stop();
                LOGGER.info("Listener thread finished.");
            }
        });
        thread.setDaemon(true);
        thread.start();

        Message message = new HandshakeRequest();
        networkService.sendMessage(message);
    }

    private void processHandshakeResponse(HandshakeResponse message) {
        LOGGER.info(message.getClass().getSimpleName() + " received");
        System.out.println(message.getClass().getSimpleName() + " received");
        if(message.isSuccessful()) {
            Message fileStructureRequest = new FileStructureRequest();
            networkService.sendMessage(fileStructureRequest);
            LOGGER.info(FileStructureRequest.class.getSimpleName() + " sent");
        }
        else {
            LOGGER.warning("Handshake failed!!!");
        }
    }

    private void processFileMessage(final FileMessage message) {
        LOGGER.info(message.getClass().getSimpleName() + " received");
        try {
            Files.write(
                    Paths.get(currentFolderName + '/' + message.getFileName()),
                    message.getData(),
                    StandardOpenOption.CREATE
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        refreshClientFilesList();
    }

    private void processFileStructureResponse(FileStructureResponse message) {
        LOGGER.info(message.getClass().getSimpleName() + " received");
        if (Platform.isFxApplicationThread()) {
            refreshFilesList(serverFilesView, message.getDescription());
        } else {
            Platform.runLater(() -> refreshFilesList(serverFilesView, message.getDescription()));
        }
    }

    private static void refreshFilesList(
            @NotNull final TableView<FileDescription> view,
            @NotNull final FileDescription description
    )
    {
        view.getItems().addAll(description.getChildDescriptionList());
    }
}
