package com.netex.apps.ctrl;

import com.netex.apps.mods.Model;
import com.netex.apps.util.Utilities;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    public MenuItem miExit;
    public TextField srcPath;
    public Button btnSrcPath;
    public Label lblSrcFile;
    public TextField destPath;
    public Button btnDestPath;
    public ChoiceBox<String> cboDestFileFormat;
    public TextField txtFuzzySrcFileName;
    public Button btnStart;
    public Button btnCancel;
    public TextArea textareaLogInfo;
    public ProgressBar progressIndicator;
    public CheckBox cbxIndicatorForBatch;
    public TextField txtDestPrefixName;
    public CheckBox cbxNeedFileHeader;

    private Stage stage;
    private double dragAnchorX, dragAnchorY;
    private ChangeListener<String> listener;
    private StringProperty title = new SimpleStringProperty();

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Model model = new Model();
        srcPath.textProperty().bindBidirectional(model.srcPathProperty());
        txtFuzzySrcFileName.textProperty().bindBidirectional(model.srcFuzzyNameProperty());
        destPath.textProperty().bindBidirectional(model.destPathProperty());
        txtDestPrefixName.textProperty().bindBidirectional(model.destNamedToProperty());
        cbxNeedFileHeader.indeterminateProperty().bindBidirectional(model.isWithHeaderProperty());
        cbxIndicatorForBatch.indeterminateProperty().bindBidirectional(model.isForBatchProperty());
        cboDestFileFormat.setItems(model.getDestFormat());
        textareaLogInfo.textProperty().bindBidirectional(model.logInfoProperty());
        stage.setResizable(true);
        cbxIndicatorForBatch.selectedProperty().bindBidirectional(stage.resizableProperty());
        resizeNodes();
    }

    private void resizeNodes() {
        Scene scene = stage.getScene();
        Parent root = scene.getRoot();
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            double delta = newValue.doubleValue() - oldValue.doubleValue();
            ObservableList<Node> components = Pane.class.cast(root).getChildren();
            if (components != null && components.size() > 0) {
                components.parallelStream()
                    .filter(
                        node -> node.isResizable() && !Labeled.class.isAssignableFrom(node.getClass()) && Control.class
                            .isAssignableFrom(node.getClass()))
                    .forEach(node -> Utilities.adjustSize(node, "Width", delta));
            }
        });
        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            double delta = newValue.doubleValue() - oldValue.doubleValue();
            ObservableList<Node> components = Pane.class.cast(root).getChildren();
            if (components != null && components.size() > 0) {
                components.parallelStream()
                    .filter(node -> node.isResizable() && TextArea.class.isAssignableFrom(node.getClass()))
                    .forEach(node -> Utilities.adjustSize(node, "Height", delta));
            }
        });
    }

    private String initDirectory(String fullFilePath) {
        if (fullFilePath == null || fullFilePath.trim().length() == 0) return System.getProperty("user.home");

        String destination;
        File targetFile = new File(fullFilePath);
        if (targetFile.isFile()) {
            if (targetFile.exists()) {
                destination = targetFile.getParent();
                return destination;
            } else {
                destination = fullFilePath.substring(0, fullFilePath.lastIndexOf("\\"));
                File directory = new File(destination);
                if (directory.isDirectory()) {
                    return destination;
                } else {
                    return initDirectory(destination);
                }
            }
        } else {
            destination = fullFilePath.substring(0, fullFilePath.lastIndexOf("\\"));
            File directory = new File(destination);
            if (directory.isDirectory()) {
                return destination;
            } else {
                return initDirectory(destination);
            }
        }
    }

    private void addTextChangeListener(TextField instance, String toolTips) {
        instance.tooltipProperty().setValue(new Tooltip(toolTips));
        instance.textProperty().addListener((observable, oldItem, newItem) -> {
            if (newItem != null && !newItem.equals(oldItem)) {
                instance.setText(newItem);
            }
        });
    }

    private void addFileChooserListener(TextField instance) {
        instance.textProperty().addListener(
            (observable, oldItem, newItem) -> {
                if (newItem != null) {
                    instance.tooltipProperty().setValue(null);
                    instance.setStyle("-fx-text-fill: BLACK");

                    File file = new File(newItem.trim());
                    if (!file.isFile() || !file.exists()) {
                        instance.setStyle("-fx-text-fill: RED");
                        instance.tooltipProperty()
                            .setValue(new Tooltip("The file is not existed, please double check!"));
                    }
                }
            }
        );
    }

    private void createMessageDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Look, a Warning Dialog");
        alert.setContentText(message + "\r\nBe Careful For The Next Step!");
        alert.showAndWait();
    }

    private Boolean isFileAvailable(TextField instance) {
        Boolean available = true;
        StringProperty property = instance.textProperty();
        if (property.getValue() == null || property.getValue().trim().length() == 0) {
            property.setValue("");
            createMessageDialog("The file is required!");
            instance.setStyle("-fx-background-color: RED");
            instance.requestFocus();
            available = false;
        } else {
            File sourceLogFile = new File(property.getValue());
            if (sourceLogFile.isFile() && sourceLogFile.exists()) {
                instance.setStyle("-fx-background-color: WHITE");
            } else {
                property.setValue("");
                createMessageDialog("The file specified is not found, please double check first!");
                instance.setStyle("-fx-background-color: RED");
                instance.requestFocus();
                available = false;
            }
        }
        return available;
    }


    public void exitApp() {

    }

    public void startWork(ActionEvent keyEvent) {
    }

    public void cancelWork(ActionEvent keyEvent) {
    }

    public void validate(ActionEvent actionEvent) {
    }

    public void showSrcPathTips(MouseEvent mouseEvent) {
    }

    public void chooseSrcPath(ActionEvent actionEvent) {
        choosePath(srcPath);
    }

    private void choosePath(TextField receiver) {
        boolean isForBatch = cbxIndicatorForBatch.isSelected();
        String workDirectory = initDirectory(receiver.getText() != null ? receiver.getText().trim() : null);
        if (isForBatch) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("View Directory");
            directoryChooser.setInitialDirectory(new File(workDirectory));
            File parent = directoryChooser.showDialog(stage);
            Optional.ofNullable(parent).ifPresent(directory -> receiver.setText(directory.getPath()));
        } else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("View File");
            fileChooser.setInitialDirectory(new File(workDirectory));
            fileChooser.getExtensionFilters()
                .addAll(
                    new FileChooser.ExtensionFilter("Files", "*.txt", "*.csv", "*.xls", "*.xlsx"),
                    new FileChooser.ExtensionFilter("Normal text file", "*.txt"),
                    new FileChooser.ExtensionFilter("Comma Separated Values text file", "*.csv"),
                    new FileChooser.ExtensionFilter("Microsoft Excel Spreadsheet", "*.xls"),
                    new FileChooser.ExtensionFilter("Office Open XML Workbook", "*.xlsx")
                );
            File file = fileChooser.showOpenDialog(stage);
            Optional.ofNullable(file).ifPresent(path -> receiver.setText(file.getPath()));
        }
    }

    public void showDestPathTips(MouseEvent mouseEvent) {
    }

    public void chooseDestPath(ActionEvent actionEvent) {
        choosePath(destPath);
    }

    public void chooseDestFileFormat(ActionEvent actionEvent) {
    }

    public void chooseSrcFuzzyName(ActionEvent actionEvent) {
    }

    public void chooseDestPrefixName(ActionEvent actionEvent) {
    }

    public void mouseDraggedHandler(MouseEvent mouseEvent) {
        stage.setX(mouseEvent.getScreenX() - dragAnchorX);
        stage.setY(mouseEvent.getScreenY() - dragAnchorY);
    }
}
