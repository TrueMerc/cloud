package ru.ryabtsev.cloud.client;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.ryabtsev.cloud.common.FileDescription;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Implements client application controller.
 */
public class ClientApplicationController implements Initializable {

    private static final String COPY_BUTTON_TEXT = "Copy";
    private static final String CUT_BUTTON_TEXT = "Cut";
    private static final String DELETE_BUTTON_TEXT = "Delete";

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeClientList();
        initializeServerList();
        initializeButtons();
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
        String userHomePath = System.getProperty("user.home");
        System.out.println( "User home path: " + userHomePath );
        File homeFile = new File(userHomePath);

        File[] files = homeFile.listFiles();
        FileDescription[] filesDescriptions = new FileDescription[files.length];

        for(int i = 0; i < filesDescriptions.length; ++i) {
            filesDescriptions[i] = new FileDescription(files[i]);
        }

        addColumnsToFilesTableView( clientFilesView );

        clientFilesView.getItems().addAll( filesDescriptions );
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
}
