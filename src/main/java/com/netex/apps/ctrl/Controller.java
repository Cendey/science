package com.netex.apps.ctrl;

import com.google.common.io.Files;
import com.netex.apps.exts.ParallelGroup;
import com.netex.apps.meta.FileExtensions;
import com.netex.apps.meta.TaskMeta;
import com.netex.apps.mods.Model;
import com.netex.apps.util.Utilities;
import javafx.application.Platform;
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
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static javafx.application.Platform.exit;

public class Controller implements Initializable {

    private static final Logger logger = LogManager.getLogger(Controller.class);
    private static ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

    private static final String FROM_SOURCE = "source";
    private static final String FROM_TARGET = "target";

    public MenuItem miExit;
    public TextField srcPath;
    public Button btnSrcPath;
    public Label lblSrcFile;
    public TextField destPath;
    public Button btnDestPath;
    public ChoiceBox<FileExtensions> cboDestFileFormat;
    public TextField txtFuzzySrcFileName;
    public Button btnStart;
    public Button btnCancel;
    public ProgressBar progressIndicator;
    public CheckBox cbxIndicatorForBatch;
    public TextField txtDestPrefixName;
    public CheckBox cbxNeedFileHeader;
    public TreeView<File> logTreeViewer;

    private Stage stage;
    private Model model;

    private ParallelGroup classifier;
    private ExecutorService service;

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
        cboDestFileFormat.setConverter(new FileExtensions.FileExtensionConvert());
        cboDestFileFormat.getItems().addAll(model.getDestFormat());
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
            available(instance);
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

    private void available(TextField instance) {
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

    private void handlePrefixFileName(TextField instance, StringProperty property, String from) {
        Optional.of(Utilities.isValidName(property.getValue())).ifPresent(decision -> {
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
        Runnable runnable = () -> {
            btnStart.setDisable(true);
            final List<TaskMeta> taskMetas = prepare();
            classifier = new ParallelGroup(taskMetas, 1);
            try {
                List<Future<List<String>>> result = classifier.classify();
                Optional.of(result).ifPresent(
                    futures -> Platform
                        .runLater(
                            () -> logTreeViewer.setRoot(createTree(new File(Paths.get(model.getDestPath()).toUri()))))
                );
            } catch (InterruptedException e) {
                logger.error(e.getCause().getMessage());
            } finally {
                classifier.destroy();
                btnStart.setDisable(false);
            }
        };
        service = Executors.newSingleThreadExecutor();
        service.execute(runnable);
        service.shutdown();
    }

    private List<TaskMeta> prepare() {
        List<Pair<File, Integer>> lstFilesInfo =
            Utilities.listAll(new File(model.getSrcPath()), model.getSrcFuzzyName(), 0);
        List<TaskMeta> lstTask = new ArrayList<>();
        Optional.ofNullable(lstFilesInfo).ifPresent(
            filesInfo -> filesInfo.forEach(
                pair -> {
                    String srcFilePath = pair.getKey().getPath();
                    String nameAs = model.getSrcFuzzyName();
                    String destFilePath = Utilities.compose(pair, model.getDestPath());
                    String nameTo = model.getDestRenameTo();
                    String destFileType = cboDestFileFormat.getValue().getExtension();
                    Boolean header = model.isIsWithHeader();
                    lstTask.add(new TaskMeta(srcFilePath, nameAs, destFilePath, nameTo, destFileType, header));
                }
            )
        );
        return lstTask;
    }

    @SuppressWarnings(value = {"unused"})
    public void cancelWork(ActionEvent keyEvent) {
        cancel();
        if (btnStart.isDisabled()) {
            btnStart.setDisable(false);
        }
    }

    private void cancel() {
        Optional.ofNullable(classifier).ifPresent(ParallelGroup::destroy);

        Optional.ofNullable(service).ifPresent((service) -> {
            try {
                logger.info("Attempt to shutdown outer executor!");
                service.shutdown();
                service.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("Outer tasks interrupted!");
            } finally {
                if (!service.isTerminated()) {
                    logger.info("Cancel non-finished outer tasks!");
                }
                service.shutdownNow();
                logger.info("Shutdown outer thread finished!");
            }
        });
    }

    @SuppressWarnings(value = {"unused"})
    public void validate(ActionEvent actionEvent) {
        if (cbxIndicatorForBatch.isSelected()) {
            txtFuzzySrcFileName.setEditable(true);
            final StringProperty srcPathProperty = srcPath.textProperty();
            if (!Files.isDirectory().apply(new File(srcPathProperty.getValue()))) {
                srcPathProperty.setValue(StringUtils.EMPTY);
            }

            final StringProperty destPathProperty = destPath.textProperty();
            if (!Files.isDirectory().apply(new File(destPathProperty.getValue()))) {
                destPathProperty.setValue(StringUtils.EMPTY);
            }

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
            if (StringUtils.isEmpty(txtFuzzySrcFileName.textProperty().getValue())) {
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

    private TreeItem<File> createTree(File file) {
        TreeItem<File> item = new TreeItem<>(file);
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                item.getChildren().add(createTree(child));
            }
            item.setGraphic(new ImageView(
                Objects.requireNonNull(contextClassLoader.getResource("picture/folder.png")).
                    toExternalForm()));
        } else {
            item.setGraphic(new ImageView(
                Objects.requireNonNull(contextClassLoader.getResource("picture/text-x-generic.png")).
                    toExternalForm()));
        }
        return item;
    }
}
