package ru.ryabtsev.cloud.client;


import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.client.commands.Copy;
import ru.ryabtsev.cloud.client.commands.Delete;
import ru.ryabtsev.cloud.client.commands.Download;
import ru.ryabtsev.cloud.client.commands.Upload;
import ru.ryabtsev.cloud.client.gui.FilesTableView;
import ru.ryabtsev.cloud.client.gui.dialog.BadRenameArgumentsAlert;
import ru.ryabtsev.cloud.client.gui.dialog.NoSelectedFilesAlert;
import ru.ryabtsev.cloud.client.gui.dialog.RenameDialog;
import ru.ryabtsev.cloud.client.handlers.ClientMessageHandlerFactory;
import ru.ryabtsev.cloud.client.service.NetworkService;
import ru.ryabtsev.cloud.common.message.ApplicationSide;
import ru.ryabtsev.cloud.common.FileDescription;
import ru.ryabtsev.cloud.common.NetworkSettings;
import ru.ryabtsev.cloud.common.message.FileMessage;
import ru.ryabtsev.cloud.common.message.AbstractMessage;
import ru.ryabtsev.cloud.common.message.Message;
import ru.ryabtsev.cloud.common.message.client.file.DeleteRequest;
import ru.ryabtsev.cloud.common.message.client.file.DownloadRequest;
import ru.ryabtsev.cloud.common.message.client.file.FileStructureRequest;
import ru.ryabtsev.cloud.common.message.client.file.RenameRequest;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private final String userName = ClientApplication.userName;

    private final String currentFolderName = DEFAULT_FOLDER_NAME;

    private final List<String> filesToUpload = new LinkedList<>();

    private final List<String> filesToDelete = new LinkedList<>();

    private final ClientMessageHandlerFactory messageHandlerFactory = new ClientMessageHandlerFactory(this);

    /**
     * Returns current folder name.
     * @return current folder name.
     */
    public String getCurrentFolderName() {
        return currentFolderName;
    }

    /**
     * Returns user name.
     * @return user name.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Returns network service.
     * @return network service.
     */
    public NetworkService getNetworkService() {
        return networkService;
    }

    /**
     * Returns list of files which should be uploaded.
     * @return
     */
    public List<String> getFilesToUpload() {
        return filesToUpload;
    }

    /**
     * Returns list of files which should be deleted.
      * @return
     */
    public List<String> getFilesToDelete() {
        return filesToDelete;
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
                    Message message = networkService.receiveMessage();
                    var handler = messageHandlerFactory.getHandler(message);
                    handler.handle();
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
    public void refreshClientFilesList() {
        File currentFolder = new File(currentFolderName);

        FileDescription folderDescription = new FileDescription(currentFolder);

        refreshFilesList(clientFilesView, folderDescription);
    }

    @FXML
    public void refreshServerFilesList(FileDescription description) {
        refreshFilesList(serverFilesView, description);
    }

    private void initializeServerList() {
        requestServerFilesList();
    }

    @FXML
    public void requestServerFilesList() {
        AbstractMessage fileStructureRequest = new FileStructureRequest(userName);
        networkService.sendMessage(fileStructureRequest);
        LOGGER.info(FileStructureRequest.class.getSimpleName() + " sent");
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
        new Delete(this, selectedFilesDescription, side).execute();
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
        Copy copy =  (ApplicationSide.CLIENT == from) ?
                new Upload(this, selectedFilesDescription) :
                new Download( this, selectedFilesDescription);
        copy.execute();
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
                Files.move(Paths.get(currentFolderName, oldName), Paths.get(currentFolderName, newName));
                refreshClientFilesList();
            }
            else {
                networkService.sendMessage(new RenameRequest(userName, oldName, newName));
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

//    private Consumer<FileDescription> getOnCopySendMethod(ApplicationSide from) throws RuntimeException {
//        switch(from) {
//            case CLIENT:
//                return this::sendUploadRequest;
//            case SERVER:
//                return this::sendDownloadRequest;
//            default:
//                throw new RuntimeException("Unexpected client-server application side.");
//        }
//    }
//
//    private void sendUploadRequest(@NotNull final FileDescription fileDescription) {
//        try {
//            networkService.sendMessage(
//                    new FileMessage(
//                            userName,
//                            Paths.get(currentFolderName, fileDescription.getFullName()),
//                            0,
//                            NetworkSettings.MAXIMAL_PAYLOAD_SIZE_IN_BYTES
//                    )
//            );
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void sendDownloadRequest(@NotNull final FileDescription fileDescription) {
//        String fileName = fileDescription.getName() + "." + fileDescription.getExtension();
//        networkService.sendMessage(new DownloadRequest(userName, fileName, 0));
//    }
}
