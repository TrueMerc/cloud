package ru.ryabtsev.cloud.client;


import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.client.service.NettyNetworkService;
import ru.ryabtsev.cloud.client.service.NetworkService;
import ru.ryabtsev.cloud.common.FileDescription;
import ru.ryabtsev.cloud.common.NetworkSettings;
import ru.ryabtsev.cloud.common.message.FileMessage;
import ru.ryabtsev.cloud.common.message.AbstractMessage;
import ru.ryabtsev.cloud.common.message.client.file.DownloadRequest;
import ru.ryabtsev.cloud.common.message.client.file.FileStructureRequest;
import ru.ryabtsev.cloud.common.message.client.HandshakeRequest;
import ru.ryabtsev.cloud.common.message.server.file.FileStructureResponse;
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

    private static final String DEFAULT_FOLDER_NAME = "./client_storage";

    private static final String DEFAULT_USER_NAME = "admin";

    private static final String DEFAULT_SERVER_HOST = "localhost";
    private static final int DEFAULT_SERVER_PORT = NetworkSettings.DEFAULT_PORT;

    private static final Logger LOGGER = Logger.getLogger(ClientApplication.class.getSimpleName());

    @FXML
    TableView<FileDescription> clientFilesView = new TableView<>();

    @FXML
    TableView<FileDescription> serverFilesView = new TableView<>();

    private static NetworkService networkService = new NettyNetworkService();;

    private String currentFolderName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeClientList();
        initializeServerList();
        initializeNetwork();
    }

    private void initializeClientList() {
        currentFolderName = DEFAULT_FOLDER_NAME;//System.getProperty("user.home");


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

    @FXML
    private void refreshClientFilesList() {
        File currentFolder = new File(currentFolderName);

        FileDescription folderDescription = new FileDescription(currentFolder);

        refreshFilesList(clientFilesView, folderDescription);
    }

    private void initializeServerList() {
        addColumnsToFilesTableView( serverFilesView );
    }

    private void initializeNetwork() {
        networkService.start(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT);

        Thread thread = new Thread(()->{
            LOGGER.info("Listener thread started.");
            try {
                while (true) {
                    AbstractMessage message = networkService.receiveMessage();
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

        AbstractMessage message = new HandshakeRequest();
        networkService.sendMessage(message);
    }

    private void processHandshakeResponse(HandshakeResponse message) {
        LOGGER.info(message.getClass().getSimpleName() + " received");
        System.out.println(message.getClass().getSimpleName() + " received");
        if(message.isSuccessful()) {
            refreshServerFilesList();
        }
        else {
            LOGGER.warning("Handshake failed!!!");
        }
    }

    @FXML
    private void refreshServerFilesList() {
        AbstractMessage fileStructureRequest = new FileStructureRequest(DEFAULT_USER_NAME);
        networkService.sendMessage(fileStructureRequest);
        LOGGER.info(FileStructureRequest.class.getSimpleName() + " sent");
    }

    private void processFileMessage(final FileMessage message) {
        LOGGER.info(message.getClass().getSimpleName() + " received");
        try {
            StandardOpenOption openOption = getOpenOption(message);
            Files.write(
                    Paths.get( formNewFileName(message.getFileName()) ),
                    message.getData(),
                    openOption
            );
            if( message.hasNext() ) {
                networkService.sendMessage(
                        new DownloadRequest(DEFAULT_USER_NAME, message.getFileName(), message.getPartNumber() + 1)
                );
            }
            else {
                refreshClientFilesList();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formNewFileName(final String fileName) {
        return currentFolderName + '/' + fileName;
    }

    private StandardOpenOption getOpenOption(final FileMessage message) {
        StandardOpenOption result = StandardOpenOption.CREATE;
        if(Files.exists(Paths.get(formNewFileName( message.getFileName() )))) {
            if(message.getPartNumber() == 0) {
                result = StandardOpenOption.WRITE;
            }
            else {
                result = StandardOpenOption.APPEND;
            }
        }
        return result;
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
        view.getItems().setAll(description.getChildDescriptionList());
    }

    public void download() {
        final ObservableList<FileDescription> fileDescriptionsList = serverFilesView.getSelectionModel().getSelectedItems();
        if( fileDescriptionsList == null || fileDescriptionsList.isEmpty() ) {
            LOGGER.warning("Empty files list to download");
        }
        for(int i = 0; i < fileDescriptionsList.size(); ++i) {
            FileDescription description = fileDescriptionsList.get(i);
            LOGGER.info("Copying file " + description.getName());
            sendFileRequest(description);
        }
    }

    private void sendFileRequest(@NotNull final FileDescription fileDescription) {
        String fileName = fileDescription.getName() + "." + fileDescription.getExtension();
        networkService.sendMessage(new DownloadRequest(DEFAULT_USER_NAME, fileName, 0));
    }
}
