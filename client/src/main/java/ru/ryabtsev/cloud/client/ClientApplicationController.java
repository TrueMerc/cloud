package ru.ryabtsev.cloud.client;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.ryabtsev.cloud.common.FileDescription;


import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;


/**
 * Implements client application controller.
 */
public class ClientApplicationController implements Initializable {

    private static final String COPY_BUTTON_TEXT = "Copy";
    private static final String CUT_BUTTON_TEXT = "Cut";
    private static final String DELETE_BUTTON_TEXT = "Delete";

    private static final String NAME_COLUMN_TEXT = "Name";

    @FXML
    TableView<FileDescription> clientFilesListView = new TableView<>();

    @FXML
    TableView<String> serverFilesListView = new TableView<>();

    @FXML
    Button clientCopyButton = new Button();

    @FXML
    Button clientCutButton = new Button();

    @FXML
    Button clientDeleteButton = new Button();

    @FXML
    Button serverCopyButton = new Button("&Copy");

    @FXML
    Button serverCutButton = new Button( "Cu&t");

    @FXML
    Button serverDeleteButton = new Button( "&Delete");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeClientList();
        initializeServerList();
        initializeButtons();
//        clientFilesListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
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
        List<String> filesList = Arrays.asList(homeFile.list());
        if( filesList != null && !filesList.isEmpty() ) {
            for(String file : filesList) {
                System.out.println( file );
            }
        }

        File[] files = homeFile.listFiles();
        FileDescription[] filesDescriptions = new FileDescription[files.length];

        for(int i = 0; i < filesDescriptions.length; ++i) {
            filesDescriptions[i] = new FileDescription(files[i]);
        }

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

        clientFilesListView.getColumns().addAll( tcName, tcExtension, tcSize, tcDate, tcAttributes );

        clientFilesListView.getItems().addAll( filesDescriptions );
    }

    private void initializeServerList() {


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
