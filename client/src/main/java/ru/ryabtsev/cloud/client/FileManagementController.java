package ru.ryabtsev.cloud.client;


import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.client.gui.FilesTableView;
import ru.ryabtsev.cloud.client.gui.dialog.BadRenameArgumentsAlert;
import ru.ryabtsev.cloud.client.gui.dialog.NoSelectedFilesAlert;
import ru.ryabtsev.cloud.client.gui.dialog.RenameDialog;
import ru.ryabtsev.cloud.client.service.NetworkService;
import ru.ryabtsev.cloud.common.FileDescription;
import ru.ryabtsev.cloud.common.FileOperations;
import ru.ryabtsev.cloud.common.NetworkSettings;
import ru.ryabtsev.cloud.common.message.FileMessage;
import ru.ryabtsev.cloud.common.message.AbstractMessage;
import ru.ryabtsev.cloud.common.message.Message;
import ru.ryabtsev.cloud.common.message.client.file.DeleteRequest;
import ru.ryabtsev.cloud.common.message.client.file.DownloadRequest;
import ru.ryabtsev.cloud.common.message.client.file.FileStructureRequest;
import ru.ryabtsev.cloud.common.message.client.file.RenameRequest;
import ru.ryabtsev.cloud.common.message.server.file.DeleteResponse;
import ru.ryabtsev.cloud.common.message.server.file.FileStructureResponse;
import ru.ryabtsev.cloud.common.message.server.file.RenameResponse;
import ru.ryabtsev.cloud.common.message.server.file.UploadResponse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Implements client application controller.
 */
public class FileManagementController implements Initializable {

    private static final String DEFAULT_FOLDER_NAME = "./client_storage";

    private static final Logger LOGGER = Logger.getLogger(ClientApplication.class.getSimpleName());

    @FXML
    FilesTableView clientFilesView = new FilesTableView();

    @FXML
    FilesTableView serverFilesView = new FilesTableView();

    private static NetworkService networkService = ClientApplication.getNetworkService();

    private String userName = ClientApplication.userName;

    private String currentFolderName = DEFAULT_FOLDER_NAME;

    private List<String> filesToUpload = new LinkedList<>();

    private Set<String> filesToDelete = new LinkedHashSet<>();

    private enum ApplicationSide {
        CLIENT, SERVER
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNetwork();
        initializeClientList();
        initializeServerList();
    }

    private void initializeNetwork() {
        Thread thread = new Thread(()->{
            LOGGER.info("Listener thread started.");
            try {
                while (networkService.isConnected()) {
                    AbstractMessage message = networkService.receiveMessage();
                    if(message == null) {
                        LOGGER.warning("null message received.");
                        continue;
                    }
                    logMessage(message);

                    Class<?> messageType = message.type();
                    if(messageType.equals(FileMessage.class)) {
                        processFileMessage((FileMessage)message);
                    }
                    else if(messageType.equals(FileStructureResponse.class)) {
                        processFileStructureResponse((FileStructureResponse)message);
                    }
                    else if(messageType.equals(UploadResponse.class)) {
                        processUploadResponse((UploadResponse)message);
                    }
                    else if(messageType.equals(DeleteResponse.class)) {
                        processDeleteResponse((DeleteResponse) message);
                    }
                    else if(messageType.equals(RenameResponse.class)) {
                        processRenameResponse((RenameResponse)message);
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
    }

    private void initializeClientList() {
        refreshClientFilesList();
    }

    @FXML
    private void refreshClientFilesList() {
        File currentFolder = new File(currentFolderName);

        FileDescription folderDescription = new FileDescription(currentFolder);

        refreshFilesList(clientFilesView, folderDescription);
    }

    private void initializeServerList() {
        refreshServerFilesList();
    }

    @FXML
    private void refreshServerFilesList() {
        AbstractMessage fileStructureRequest = new FileStructureRequest(userName);
        networkService.sendMessage(fileStructureRequest);
        LOGGER.info(FileStructureRequest.class.getSimpleName() + " sent");
    }

    private void logMessage(final Message message) {
        LOGGER.info(message.getClass().getSimpleName() + " received");
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
            if (!response.isCompleted()) {
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
                final String name = response.getFileName();
                filesToUpload.remove(name);
                if( filesToDelete.contains(name) ) {
                    delete( formDirectoryDependentFileName(name) );
                    filesToDelete.remove(name);
                }
            }
        }
    }

    private void processRenameResponse(final RenameResponse response) {
        if(response.isSuccessful()) {
            refreshServerFilesList();
        }
    }

    private static void refreshFilesList(
            @NotNull final TableView<FileDescription> view,
            @NotNull final FileDescription description
    )
    {
        view.getItems().setAll(description.getChildDescriptionList());
    }

    public void clientDelete() {
        delete(ApplicationSide.CLIENT);
        refreshClientFilesList();
    }

    public void serverDelete() {
        delete(ApplicationSide.SERVER);
    }

    public void delete(ApplicationSide side) {
        final ObservableList<FileDescription> selectedFilesDescription = getSelectedFiles(side);
        for( FileDescription description : selectedFilesDescription ) {
            if(ApplicationSide.CLIENT == side) {
                final String name = description.getFullName();
                if( filesToUpload.contains(name) ) {
                    filesToDelete.add(name);
                }
                else {
                    delete( formDirectoryDependentFileName(name) );
                }
            }
            else {
                networkService.sendMessage(new DeleteRequest(userName, description.getFullName()));
            }
        }
    }

    @SneakyThrows
    private void delete(@NotNull final String fileName) {
        Files.delete(Paths.get(fileName));
        refreshClientFilesList();
    }

    public void download() {
        copy(ApplicationSide.SERVER, ApplicationSide.CLIENT);
    }

    public void cutAndDownload() {
        download();
        serverDelete();
    }

    public void upload() {
        copy(ApplicationSide.CLIENT, ApplicationSide.SERVER);
    }

    public void cutAndUpload() {
        upload();
        clientDelete();
    }

    private void copy(ApplicationSide from, ApplicationSide to) {
        final ObservableList<FileDescription> selectedFilesDescription = getSelectedFiles(from);
        if( selectedFilesDescription == null || selectedFilesDescription.isEmpty() ) {
            Alert alert = new NoSelectedFilesAlert();
            alert.showAndWait();
            return;
        }

        if(ApplicationSide.CLIENT == from) {
            filesToUpload = selectedFilesDescription.stream()
                    .map(FileDescription::getFullName)
                    .collect(Collectors.toList());
        }

        Consumer<FileDescription> onCopySendMethod = getOnCopySendMethod(from);
        for( FileDescription description : selectedFilesDescription ) {
            onCopySendMethod.accept(description);
        }
    }

    public void clientRename() {
        rename(ApplicationSide.CLIENT);
    }

    public void serverRename() {
        rename(ApplicationSide.SERVER);
    }

    @SneakyThrows
    private void rename(ApplicationSide side) {
        ObservableList<FileDescription> selectedFiles = getSelectedFiles(side);
        if(selectedFiles.size() != 1) {
            Alert alert = new BadRenameArgumentsAlert();
            alert.showAndWait();
            return;
        }

        FileDescription description = selectedFiles.get(0);
        String oldName = description.getFullName();
        RenameDialog dialog = new RenameDialog(oldName);
        Optional<String> result = dialog.showAndWait();
        if(result.isPresent() && !("".equals(result.get())) && !existsInCurrentFolder(result.get(), side)) {
            String newName = result.get();
            if(ApplicationSide.CLIENT == side) {
                Files.move(
                        Paths.get(formDirectoryDependentFileName(oldName)), Paths.get(formDirectoryDependentFileName(newName))
                );
                refreshClientFilesList();
            }
            else {
                networkService.sendMessage(new RenameRequest(oldName, newName));
            }
        }
    }

    boolean existsInCurrentFolder(final String fileName, ApplicationSide side) {
        ObservableList<FileDescription> descriptions = getView(side).getItems();
        for(FileDescription description : descriptions) {
            if( fileName.equals(description.getFullName())) {
                return true;
            }
        }

        return false;
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


    private void processDeleteResponse(DeleteResponse response) {
        if(response.isSuccessful()) {
            refreshServerFilesList();
        }
        else {
            LOGGER.warning("File deletion problem.");
        }
    }
}
