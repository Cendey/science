package com.netex.apps.ctrl;

import com.google.common.io.Files;
import com.netex.apps.meta.FileExtension;
import com.netex.apps.mods.Model;
import com.netex.apps.util.Utilities;
import javafx.beans.property.StringProperty;
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
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import opennlp.tools.util.StringUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.application.Platform.exit;

public class Controller implements Initializable {

    private static final String FROM_SOURCE = "source";
    private static final String FROM_TARGET = "target";

    public MenuItem miExit;
    public TextField srcPath;
    public Button btnSrcPath;
    public Label lblSrcFile;
    public TextField destPath;
    public Button btnDestPath;
    public ChoiceBox<FileExtension> cboDestFileFormat;
    public TextField txtFuzzySrcFileName;
    public Button btnStart;
    public Button btnCancel;
    public TextArea textareaLogInfo;
    public ProgressBar progressIndicator;
    public CheckBox cbxIndicatorForBatch;
    public TextField txtDestPrefixName;
    public CheckBox cbxNeedFileHeader;

    private Stage stage;
    private Model model;

    public void setStage(Stage stage) {
        this.stage = stage;
        stage.titleProperty().bindBidirectional(model.titleProperty());
        addResizeListener();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = new Model();
        srcPath.textProperty().bindBidirectional(model.srcPathProperty());
        String tipsForSrc = "1. For batch operation, tick batch checkbox first, then choose a work directory.\n"
            .concat("2. Or, choose a single file to conversion, you can ignore the source file name.");
        addTextChangeListener(srcPath, tipsForSrc);
        addFileListener(srcPath);
        txtFuzzySrcFileName.textProperty().bindBidirectional(model.srcFuzzyNameProperty());
        String tipsForSrcFuzzyName =
            "Specify the source file name to match, which is only available for batch files conversion.\n"
                .concat("In single file conversion, the source name to matched will be ignored!");
        addTextChangeListener(txtFuzzySrcFileName, tipsForSrcFuzzyName);
        txtFuzzySrcFileName.setEditable(false);
        destPath.textProperty().bindBidirectional(model.destPathProperty());
        String tipsForDestPath = "1. For batch operation, tick batch checkbox first, then choose a work directory.\n"
            .concat("2. Or, choose a single file to conversion, you can ignore the source file name.");
        addTextChangeListener(destPath, tipsForDestPath);
        addFileListener(destPath);
        txtDestPrefixName.textProperty().bindBidirectional(model.destRenameToProperty());
        String tipsForDestPrefixName =
            "Specify the prefix file name to generate target files, which is only available for batch files conversion.\n"
                .concat("In single file conversion, the target prefix file name is optional!");
        addTextChangeListener(txtDestPrefixName, tipsForDestPrefixName);
        cbxNeedFileHeader.indeterminateProperty().bindBidirectional(model.isWithHeaderProperty());
        cbxIndicatorForBatch.indeterminateProperty().bindBidirectional(model.isForBatchProperty());
        cboDestFileFormat.setConverter(new FileExtension.FileExtensionConvert());
        cboDestFileFormat.getItems().addAll(model.getDestFormat());
        textareaLogInfo.textProperty().bindBidirectional(model.logInfoProperty());
    }

    private void addResizeListener() {
        Scene scene = stage.getScene();
        Parent root = scene.getRoot();
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            double delta = newValue.doubleValue() - oldValue.doubleValue();
            ObservableList<Node> components = Pane.class.cast(root).getChildren();
            if (components != null && components.size() > 0) {
                components.parallelStream().filter(Node::isResizable).forEach(node -> adjust(node, "Width", delta));
            }
        });

        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            double delta = newValue.doubleValue() - oldValue.doubleValue();
            ObservableList<Node> components = Pane.class.cast(root).getChildren();
            if (components != null && components.size() > 0) {
                components.parallelStream().filter(Node::isResizable).forEach(node -> adjust(node, "Height", delta));
            }
        });
    }


    private void adjust(Node node, String propertyName, double delta) {
        if (node.isResizable()) {
            if (ClassUtils.isAssignable(node.getClass(), Pane.class)) {
                List<Node> children = Pane.class.cast(node).getChildren();
                for (Node child : children) {
                    adjust(child, propertyName, delta);
                }
            } else {
                //Adjust orientation only for TextField.class or TextArea.class
                if (StringUtils.equalsIgnoreCase(propertyName, "Width")) {
                    if (ClassUtils.isAssignable(node.getClass(), TextInputControl.class)) {
                        Utilities.adjustSize(node, propertyName, delta);
                    }
                } else if (StringUtils.equalsIgnoreCase(propertyName, "Height")) {
                    if (ClassUtils.isAssignable(node.getClass(), TextArea.class)) {
                        Utilities.adjustSize(node, propertyName, delta);
                    }
                }
            }
        }
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

    private void addFileListener(TextField instance) {
        instance.textProperty().addListener(
            (observable, oldItem, newItem) -> {
                if (newItem != null) {
                    File file = new File(newItem.trim());
                    if ((file.isFile() || file.isDirectory()) && file.exists()) {
                        instance.setStyle("-fx-text-fill: GREEN");
                    } else {
                        instance.setStyle("-fx-text-fill: RED");
                    }
                }
            }
        );
    }

    //http://fxexperience.com/controlsfx/
    private void createMessageDialog(String message) {
        //http://code.makery.ch/blog/javafx-dialogs-official/
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Look, a Warning Dialog");
        alert.setContentText(message + "\r\nBe Careful For The Next Step!");
        alert.showAndWait();
    }

    public void available(InputMethodEvent inputMethodEvent) {
        final Object source = inputMethodEvent.getSource();
        if (ClassUtils.isAssignable(source.getClass(), TextField.class)) {
            TextField instance = TextField.class.cast(source);
            StringProperty property = instance.textProperty();
            final File path = new File(property.getValue().trim());
            if (instance.equals(srcPath)) {
                handleFilePath(instance, path, FROM_SOURCE);
            } else if (instance.equals(txtFuzzySrcFileName)) {
                handlePrefixFileName(instance, property, FROM_SOURCE);
            } else if (instance.equals(destPath)) {
                handleFilePath(instance, path, FROM_TARGET);
            } else if (instance.equals(txtDestPrefixName)) {
                handlePrefixFileName(instance, property, FROM_TARGET);
            }
        }
    }

    private void handlePrefixFileName(TextField instance, StringProperty property, String from) {
        String name = FilenameUtils.normalize(property.getValue());
        Optional.of(StringUtils.equalsIgnoreCase(property.getValue(), name)).ifPresent(decision -> {
            if (decision) instance.setStyle("-fx-text-fill: GREEN");
            else {
                createMessageDialog(String.format("%s%s%s", "This is not a valid prefix of ", from, " file name!"));
                instance.setStyle("-fx-text-fill: RED");
                instance.requestFocus();
            }
        });
    }

    private void handleFilePath(TextField instance, File path, String from) {
        if (cbxIndicatorForBatch.isSelected()) {
            Optional.of(Files.isDirectory().apply(path)).ifPresent(decision -> {
                if (decision) instance.setStyle("-fx-text-fill: GREEN");
                else {
                    createMessageDialog(String.format("%s%s%s", "This is not a valid ", from, " directory!"));
                    instance.setStyle("-fx-text-fill: RED");
                    instance.requestFocus();
                }
            });
        } else {
            Optional.of(Files.isFile().apply(path)).ifPresent(decision -> {
                if (decision) instance.setStyle("-fx-text-fill: GREEN");
                else {
                    createMessageDialog(String.format("%s%s%s", "This is not a valid ", from, " file!"));
                    instance.setStyle("-fx-text-fill: RED");
                    instance.requestFocus();
                }
            });
        }
    }


    public void exitApp() {
        stage.hide();
        exit();
    }

    @SuppressWarnings(value = {"unused"})
    public void startWork(ActionEvent keyEvent) {

    }

    @SuppressWarnings(value = {"unused"})
    public void cancelWork(ActionEvent keyEvent) {

    }

    @SuppressWarnings(value = {"unused"})
    public void validate(ActionEvent actionEvent) {
        if (cbxIndicatorForBatch.isSelected()) {
            txtFuzzySrcFileName.setEditable(true);
            srcPath.textProperty().setValue(StringUtils.EMPTY);
            destPath.textProperty().setValue(StringUtils.EMPTY);

            stage.titleProperty().setValue("Batch Files Conversion");
        } else {
            txtFuzzySrcFileName.setEditable(false);
            stage.titleProperty().setValue("Single File Conversion");
        }
    }

    @SuppressWarnings(value = {"unused"})
    public void chooseSrcPath(ActionEvent actionEvent) {
        choosePath(srcPath, false);
    }

    private void choosePath(TextField receiver, Boolean needDirectory) {
        boolean isForBatch = cbxIndicatorForBatch.isSelected();
        String workDirectory = initDirectory(receiver.getText() != null ? receiver.getText().trim() : null);
        if (isForBatch || needDirectory) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("View Directory");
            directoryChooser.setInitialDirectory(new File(workDirectory));
            File parent = directoryChooser.showDialog(stage);
            Optional.ofNullable(parent).ifPresent(directory -> receiver.textProperty().setValue(directory.getPath()));
        } else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("View File");
            fileChooser.setInitialDirectory(new File(workDirectory));
            fileChooser.getExtensionFilters()
                .addAll(
                    new FileChooser.ExtensionFilter("All", "*.*"),
                    new FileChooser.ExtensionFilter("Files", "*.txt", "*.csv", "*.xls", "*.xlsx"),
                    new FileChooser.ExtensionFilter("Normal text file", "*.txt", "*.text"),
                    new FileChooser.ExtensionFilter("Comma Separated Values text file", "*.csv"),
                    new FileChooser.ExtensionFilter("Microsoft Excel Spreadsheet", "*.xls"),
                    new FileChooser.ExtensionFilter("Office Open XML Workbook", "*.xlsx")
                );
            File file = fileChooser.showOpenDialog(stage);
            Optional.ofNullable(file).ifPresent(path -> receiver.setText(file.getPath()));
        }
    }

    @SuppressWarnings(value = {"unused"})
    public void chooseDestPath(ActionEvent actionEvent) {
        choosePath(destPath, true);
    }

    private void fuzzyFileName(String initDirectory, TextField instance) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("View File");
        fileChooser.setInitialDirectory(new File(initDirectory));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Files", "*.*"));
        File file = fileChooser.showOpenDialog(stage);
        Optional.ofNullable(file)
            .ifPresent(path -> instance.textProperty().setValue(FilenameUtils.removeExtension(path.getName())));
    }

    @SuppressWarnings(value = {"unused"})
    public void chooseSrcFuzzyName(MouseEvent actionEvent) {
        if (txtFuzzySrcFileName.isEditable() && actionEvent.getClickCount() == 2) {
            if (StringUtil.isEmpty(txtFuzzySrcFileName.textProperty().getValue())) {
                fuzzyFileName(System.getProperty("user.home"), txtFuzzySrcFileName);
            } else {
                String fileName = FilenameUtils.normalize(txtFuzzySrcFileName.textProperty().getValue());
                if (!StringUtils.equals(txtFuzzySrcFileName.textProperty().getValue(), fileName)) {
                    createMessageDialog("Source file name is illegal.");
                }
            }
        }
    }

    @SuppressWarnings(value = {"unused"})
    public void chooseDestPrefixName(MouseEvent actionEvent) {
        if (actionEvent.getClickCount() == 2) {
            if (StringUtils.isEmpty(txtDestPrefixName.textProperty().getValue())) {
                fuzzyFileName(System.getProperty("user.home"), txtDestPrefixName);
            } else {
                String fileName = FilenameUtils.normalize(txtDestPrefixName.textProperty().getValue());
                if (!StringUtils.equals(txtDestPrefixName.textProperty().getValue(), fileName)) {
                    createMessageDialog("Target file name is illegal.");
                }
            }
        }
    }
}
