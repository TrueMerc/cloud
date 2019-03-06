package ru.ryabtsev.cloud.client;


import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Window;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.client.gui.dialog.AboutDialog;
import ru.ryabtsev.cloud.client.service.NetworkService;
import ru.ryabtsev.cloud.common.FileDescription;
import ru.ryabtsev.cloud.common.FileOperations;
import ru.ryabtsev.cloud.common.NetworkSettings;
import ru.ryabtsev.cloud.common.message.FileMessage;
import ru.ryabtsev.cloud.common.message.AbstractMessage;
import ru.ryabtsev.cloud.common.message.Message;
import ru.ryabtsev.cloud.common.message.client.file.DownloadRequest;
import ru.ryabtsev.cloud.common.message.client.file.FileStructureRequest;
import ru.ryabtsev.cloud.common.message.server.file.FileStructureResponse;
import ru.ryabtsev.cloud.common.message.server.HandshakeResponse;
import ru.ryabtsev.cloud.common.message.server.file.UploadResponse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Implements client application controller.
 */
public class ClientApplicationController implements Initializable {

    private static final String DEFAULT_FOLDER_NAME = "./client_storage";

    private static final Logger LOGGER = Logger.getLogger(ClientApplication.class.getSimpleName());

    @FXML
    TableView<FileDescription> clientFilesView = new TableView<>();

    @FXML
    TableView<FileDescription> serverFilesView = new TableView<>();

    private static NetworkService networkService = ClientApplication.networkService();

    private String userName = ClientApplication.userName;

    private String currentFolderName;

    private enum ApplicationSide {
        CLIENT, SERVER
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeClientList();
        initializeServerList();
        initializeNetwork();
    }

    private void initializeClientList() {
        currentFolderName = DEFAULT_FOLDER_NAME;

        initializeTableView( clientFilesView );
        refreshClientFilesList();
    }

    private static void initializeTableView(TableView<FileDescription> tableView) {
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

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    private void refreshClientFilesList() {
        File currentFolder = new File(currentFolderName);

        FileDescription folderDescription = new FileDescription(currentFolder);

        refreshFilesList(clientFilesView, folderDescription);
    }

    private void initializeServerList() {
        initializeTableView( serverFilesView );
    }

    private void initializeNetwork() {
        //networkService.start(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT);

        Thread thread = new Thread(()->{
            LOGGER.info("Listener thread started.");
            try {
                while (true) {
                    AbstractMessage message = networkService.receiveMessage();
                    if(message == null) {
                        LOGGER.warning("null message received.");
                        continue;
                    }
                    logMessage(message);

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
                    else if(messageType.equals(UploadResponse.class)) {
                        processUploadResponse((UploadResponse)message);
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

        networkService.sendMessage(new FileStructureRequest(userName));
    }

    private void logMessage(final Message message) {
        LOGGER.info(message.getClass().getSimpleName() + " received");
    }

    private void processHandshakeResponse(HandshakeResponse message) {
        if(message.isSuccessful()) {
            refreshServerFilesList();
        }
        else {
            LOGGER.warning("Handshake failed!!!");
        }
    }

    @FXML
    private void refreshServerFilesList() {
        AbstractMessage fileStructureRequest = new FileStructureRequest(userName);
        networkService.sendMessage(fileStructureRequest);
        LOGGER.info(FileStructureRequest.class.getSimpleName() + " sent");
    }

    private void processFileMessage(final FileMessage message) {
        try {
            StandardOpenOption openOption = getOpenOption(message);
            Files.write(
                    Paths.get( formDirectoryDependentFileName(message.getFileName()) ),
                    message.getData(),
                    openOption
            );
            if( message.hasNext() ) {
                networkService.sendMessage(
                        new DownloadRequest(userName, message.getFileName(), message.getPartNumber() + 1)
                );
            }
            else {
                refreshClientFilesList();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formDirectoryDependentFileName(final String fileName) {
        return currentFolderName + '/' + fileName;
    }

    private StandardOpenOption getOpenOption(final FileMessage message) {
        return FileOperations.getOpenOption(formDirectoryDependentFileName(message.getFileName()), message.getPartNumber() == 0);
    }

    private void processFileStructureResponse(FileStructureResponse message) {
        if (Platform.isFxApplicationThread()) {
            refreshFilesList(serverFilesView, message.getDescription());
        } else {
            Platform.runLater(() -> refreshFilesList(serverFilesView, message.getDescription()));
        }
    }

    private void processUploadResponse(final UploadResponse response) {
        if( response.isSuccessful() ) {
            if (!response.isComplete()) {
                try {
                    networkService.sendMessage(
                            new FileMessage(
                                    Paths.get(formDirectoryDependentFileName(response.getFileName())),
                                    response.getNextNumber(),
                                    NetworkSettings.MAXIMAL_PAYLOAD_SIZE_IN_BYTES
                            )
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                refreshServerFilesList();
            }
        }
    }

    private static void refreshFilesList(
            @NotNull final TableView<FileDescription> view,
            @NotNull final FileDescription description
    )
    {
        view.getItems().setAll(description.getChildDescriptionList());
    }



    @SneakyThrows
    public void delete() {
        final ObservableList<FileDescription> selectedFilesDescription = getSelectedFiles(ApplicationSide.CLIENT);
        for( FileDescription description : selectedFilesDescription ) {
            final String fileName = formDirectoryDependentFileName(description.getFullName());
            Files.delete(Paths.get(fileName));
        }
        refreshClientFilesList();
    }

    public void download() {
        copy(ApplicationSide.SERVER, ApplicationSide.CLIENT);
    }

//    @SneakyThrows
//    public void downloadAndCut() {
//        download();
//        delete();
//    }

    public void upload() {
        copy(ApplicationSide.CLIENT, ApplicationSide.SERVER);
    }

    @SneakyThrows
    public void cutAndUpload() {
        upload();
        delete();
    }

    private void copy(ApplicationSide from, ApplicationSide to) {
        final ObservableList<FileDescription> selectedFilesDescription = getSelectedFiles(from);
        if( selectedFilesDescription == null || selectedFilesDescription.isEmpty() ) {
            LOGGER.warning( "There aren't files to copy");
            return;
        }

        Consumer<FileDescription> onCopySendMethod = getOnCopySendMethod(from);
        for( FileDescription description : selectedFilesDescription ) {
            onCopySendMethod.accept(description);
        }
    }

    private ObservableList<FileDescription> getSelectedFiles(final ApplicationSide side) throws RuntimeException {
        return getView(side).getSelectionModel().getSelectedItems();
    }

    private TableView<FileDescription> getView(ApplicationSide side) throws RuntimeException {
        switch(side) {
            case CLIENT:
                return clientFilesView;
            case SERVER:
                return serverFilesView;
            default:
                throw new RuntimeException("Unexpected client-server application side.");
        }
    }

    private Consumer<FileDescription> getOnCopySendMethod(ApplicationSide from) throws RuntimeException {
        switch(from) {
            case CLIENT:
                return this::sendUploadRequest;
            case SERVER:
                return this::sendDownloadRequest;
            default:
                throw new RuntimeException("Unexpected client-server application size.");
        }
    }

    private void sendUploadRequest(@NotNull final FileDescription fileDescription) {
        try {
            networkService.sendMessage(
                    new FileMessage(
                            Paths.get(formDirectoryDependentFileName(fileDescription.getFullName())),
                            0,
                            NetworkSettings.MAXIMAL_PAYLOAD_SIZE_IN_BYTES
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendDownloadRequest(@NotNull final FileDescription fileDescription) {
        String fileName = fileDescription.getName() + "." + fileDescription.getExtension();
        networkService.sendMessage(new DownloadRequest(userName, fileName, 0));
    }

    @FXML
    private void exitApplication() {
        Platform.exit();
    }

    @FXML
    private void aboutDialog() {
        try {
            AboutDialog dialog = new AboutDialog(ClientApplication.MAIN_WINDOW);
            Window window = dialog.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest(event -> window.hide());
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
