package ru.ryabtsev.cloud.client.gui;

import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.ryabtsev.cloud.common.FileDescription;

/**
 * Implements table view for file system elements.
 */
public class FilesTableView extends TableView<FileDescription> {

    /**
     * Constructs files table view.
     */
    public FilesTableView() {
        super();
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

        this.getColumns().setAll( tcName, tcExtension, tcSize, tcDate, tcAttributes );

        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
}
