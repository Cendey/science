package com.netex.apps.ctrl;

import com.google.common.io.Files;
import com.netex.apps.exts.ParallelGroup;
import com.netex.apps.intf.Effect;
import com.netex.apps.intf.Result;
import com.netex.apps.meta.CSSMeta;
import com.netex.apps.meta.FileExtensions;
import com.netex.apps.meta.TaskMeta;
import com.netex.apps.mods.Model;
import com.netex.apps.util.Utilities;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
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

import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Stream;

import static javafx.application.Platform.exit;

public class Controller implements Initializable {

    private static final Logger logger = LogManager.getLogger(Controller.class);
    private static ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    private static final FileSystemView fileSystemView = FileSystemView.getFileSystemView();

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
    public TreeTableView<File> logTreeViewer;
    public ImageView appLogo;
    public TreeTableColumn<File, String> nameColumn;
    public TreeTableColumn<File, Long> sizeColumn;
    public TreeTableColumn<File, Date> modifiedColumn;
    public TreeTableColumn<File, String> typeColumn;


    private Stage stage;
    private Model model;

    private ParallelGroup classifier;
    private ExecutorService service;

    private Function<Model, Result<Node, String>> validation = (model) -> {
        if (cbxIndicatorForBatch.isSelected()) {
            if (StringUtils.isEmpty(model.getSrcPath())) {
                return Result.failure(srcPath, "Source directory is required!");
            } else if (!Files.isDirectory().apply(new File(model.getSrcPath()))) {
                return Result.failure(srcPath, "Source directory is not existed!");
            } else if (StringUtils.isEmpty(model.getSrcFuzzyName())) {
                return Result.failure(txtFuzzySrcFileName, "The matched source file name is required!");
            } else if (StringUtils.isNotEmpty(model.getDestPath()) && !Files.isDirectory().apply(new File(model.getDestPath()))) {
                return Result.failure(destPath, "Target directory is not existed!");
            } else if (cboDestFileFormat.getValue() == null || StringUtils.isEmpty(cboDestFileFormat.getValue().getExtension())) {
                return Result.failure(cboDestFileFormat, "Target file format is required!");
            } else {
                return Result.success(null, "Successfully!");
            }
        } else {
            if (StringUtils.isEmpty(model.getSrcPath())) {
                return Result.failure(srcPath, "Source file is required!");
            } else if (!Files.isFile().apply(new File(model.getSrcPath()))) {
                return Result.failure(srcPath, "Source file is not existed!");
            } else if (cboDestFileFormat.getValue() == null || StringUtils.isEmpty(cboDestFileFormat.getValue().getExtension())) {
                return Result.failure(cboDestFileFormat, "Target file format is required!");
            } else {
                return Result.success(null, "Successfully!");
            }
        }
    };

    private Effect<String> success = valid -> logger.info("All required check is passed!");
    private Effect<String> failure = logger::error;

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
        addTooltip(srcPath, tipsForSrc);
        txtFuzzySrcFileName.textProperty().bindBidirectional(model.srcFuzzyNameProperty());
        String tipsForSrcFuzzyName =
                "Specify the source file name to match, which is only available for batch files conversion.\n"
                        .concat("In single file conversion, the source name to matched will be ignored!");
        addTooltip(txtFuzzySrcFileName, tipsForSrcFuzzyName);
        txtFuzzySrcFileName.setEditable(false);
        destPath.textProperty().bindBidirectional(model.destPathProperty());
        String tipsForDestPath = "1. For batch operation, tick batch checkbox first, then choose a work directory.\n"
                .concat("2. Or, choose a single file to conversion, you can ignore the source file name.");
        addTooltip(destPath, tipsForDestPath);
        txtDestPrefixName.textProperty().bindBidirectional(model.destRenameToProperty());
        String tipsForDestPrefixName =
                "Specify the prefix file name to generate target files, which is only available for batch files conversion.\n"
                        .concat("In single file conversion, the target prefix file name is optional!");
        addTooltip(txtDestPrefixName, tipsForDestPrefixName);
        cbxNeedFileHeader.indeterminateProperty().bindBidirectional(model.isWithHeaderProperty());
        cbxIndicatorForBatch.indeterminateProperty().bindBidirectional(model.isForBatchProperty());
        cboDestFileFormat.setConverter(new FileExtensions.FileExtensionConvert());
        cboDestFileFormat.getItems().addAll(model.getDestFormat());
        appLogo.setImage(new Image(
                Objects.requireNonNull(contextClassLoader.getResource("picture/conversion.png")).toExternalForm()));
        centerImage(appLogo);
        nameColumn.setCellValueFactory(
                item -> new SimpleStringProperty(fileSystemView.getSystemDisplayName(item.getValue().getValue())));
        sizeColumn.setCellValueFactory(
                item -> new SimpleObjectProperty<>((item.getValue().getValue().length() >>> 10) | 1));
        modifiedColumn.setCellValueFactory(
                item -> new SimpleObjectProperty<>(new Date(item.getValue().getValue().lastModified())));
        typeColumn.setCellValueFactory(
                item -> new SimpleStringProperty(FilenameUtils.getExtension(item.getValue().getValue().getPath())));
        progress();
    }

    private void progress() {
        progressIndicator.progressProperty().addListener((observable, oldValue, newValue) -> {
            double progress = newValue == null ? 0 : newValue.doubleValue();
            if (progress < 0.2) {
                setBarStyleClass(progressIndicator, CSSMeta.RED_BAR);
            } else if (progress < 0.4) {
                setBarStyleClass(progressIndicator, CSSMeta.ORANGE_BAR);
            } else if (progress < 0.6) {
                setBarStyleClass(progressIndicator, CSSMeta.YELLOW_BAR);
            } else {
                setBarStyleClass(progressIndicator, CSSMeta.GREEN_BAR);
            }
        });
    }

    private void setBarStyleClass(ProgressBar bar, String barStyleClass) {
        bar.getStyleClass().removeAll(CSSMeta.BAR_COLOR_STYLE_CLASSES);
        bar.getStyleClass().add(barStyleClass);
    }

    private void centerImage(ImageView imageView) {
        Image img = imageView.getImage();
        if (img != null) {
            double width, height;

            double ratioX = imageView.getFitWidth() / img.getWidth();
            double ratioY = imageView.getFitHeight() / img.getHeight();

            double deduceCoEffect;
            if (ratioX >= ratioY) {
                deduceCoEffect = ratioY;
            } else {
                deduceCoEffect = ratioX;
            }

            width = img.getWidth() * deduceCoEffect;
            height = img.getHeight() * deduceCoEffect;

            imageView.setX((imageView.getFitWidth() - width) / 2);
            imageView.setY((imageView.getFitHeight() - height) / 2);
        }
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
            logTreeViewer.getColumns().forEach(column -> logTreeViewer.resizeColumn(column, delta));
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

    private void addTooltip(TextField instance, String toolTips) {
        instance.tooltipProperty().setValue(new Tooltip(toolTips));
    }

    //http://fxexperience.com/controlsfx/
    private void showMessage(String message) {
        //http://code.makery.ch/blog/javafx-dialogs-official/
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(String.format("%s!%n", message));
        alert.showAndWait();
    }

    private void available(Node node) {
        if (node instanceof TextField) {
            TextField instance = TextField.class.cast(node);
            StringProperty property = instance.textProperty();
            final File path = new File(property.getValue().trim());
            if (instance.equals(srcPath)) {
                polish(instance, path);
            } else if (instance.equals(txtFuzzySrcFileName)) {
                polish(instance, property);
            } else if (instance.equals(destPath)) {
                polish(instance, path);
            } else if (instance.equals(txtDestPrefixName)) {
                polish(instance, property);
            }
        }
    }

    private void polish(TextField instance, StringProperty property) {
        Optional.of(Utilities.isValidName(property.getValue())).ifPresent(decision -> {
            if (decision) {
                stylish(instance, true);
            } else {
                stylish(instance, false);
            }
        });
    }

    private void polish(TextField instance, File path) {
        if (cbxIndicatorForBatch.isSelected()) {
            Optional.of(Files.isDirectory().apply(path)).ifPresent(decision -> {
                if (decision) {
                    stylish(instance, true);
                } else {
                    stylish(instance, false);
                }
            });
        } else {
            Optional.of(Files.isFile().apply(path)).ifPresent(decision -> {
                if (decision) {
                    stylish(instance, true);
                } else {
                    stylish(instance, false);
                }
            });
        }
    }

    private void stylish(TextField instance, boolean isValid) {
        final ObservableList<String> styleClass = instance.getStyleClass();
        if (isValid) {
            while (styleClass.contains(CSSMeta.INVALID)) {
                styleClass.remove(CSSMeta.INVALID);
            }
            if (!styleClass.contains(CSSMeta.VALID)) {
                styleClass.add(CSSMeta.VALID);
            }
        } else {
            while (styleClass.contains(CSSMeta.VALID)) {
                styleClass.remove(CSSMeta.VALID);
            }
            if (!styleClass.contains(CSSMeta.INVALID)) {
                styleClass.add(CSSMeta.INVALID);
            }
        }
    }


    public void exitApp() {
        stage.hide();
        exit();
    }

    @SuppressWarnings(value = {"unused"})
    public void startWork(ActionEvent keyEvent) {
        if (!isReady()) return;
        Runnable runnable = () -> {
            btnStart.setDisable(true);
            logTreeViewer.setRoot(null);
            progressIndicator.progressProperty().unbind();
            final List<TaskMeta> taskMetas = prepare();
            classifier = new ParallelGroup(taskMetas, 1);
            try {
                List<Future<List<String>>> result = classifier.classify(progressIndicator);
                Optional.of(result).ifPresent(
                        futures -> Platform
                                .runLater(
                                        () -> {
                                            TreeItem<File> treeItem =
                                                    createTree(new File(Paths.get(
                                                            StringUtils.isNotEmpty(model.getDestPath()) ?
                                                                    model.getDestPath() : model.getSrcPath()).getRoot().toUri()));
                                            logTreeViewer.setRoot(treeItem);
                                        }
                                )
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
                    showMessage("Source file name is illegal.");
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
                    showMessage("Target file name is illegal.");
                }
            }
        }
    }

    private TreeItem<File> createTree(File file) {
        TreeItem<File> item = new TreeItem<>(file);
        File[] children = file.listFiles();
        if (children != null) {
            Stream.of(children).filter(
                    child -> child.getPath().contains(model.getDestPath()) || model.getDestPath().contains(child.getPath()))
                    .forEach(child -> item.getChildren().add(createTree(child)));
            item.setGraphic(new ImageView(
                    Objects.requireNonNull(contextClassLoader.getResource("picture/folder-modern.png")).
                            toExternalForm()));
        } else {
            item.setGraphic(new ImageView(
                    Objects.requireNonNull(contextClassLoader.getResource("picture/text-x-generic.png")).
                            toExternalForm()));
        }
        return item;
    }

    public void openDirectoryOrFile(MouseEvent mouseEvent) {
        int clickCount = mouseEvent.getClickCount();
        if (clickCount == 2) {
            Desktop desktop = Desktop.getDesktop();

            if (!Desktop.isDesktopSupported()) {
                logger.error("Desktop is not supported in current OS!");
            } else {
                Object source = mouseEvent.getSource();
                if (source != null && ClassUtils.isAssignable(TreeTableView.class, source.getClass())) {
                    @SuppressWarnings("unchecked") TreeTableView<File> treeTableView = TreeTableView.class.cast(source);
                    TreeItem<File> treeItem = treeTableView.getSelectionModel().selectedItemProperty().getValue();
                    File file = treeItem.getValue();

                    if (file.exists() && file.canExecute()) {
                        try {
                            stage.getScene().setCursor(Cursor.WAIT);
                            desktop.open(file);
                            stage.getScene().setCursor(Cursor.DEFAULT);
                        } catch (IOException e) {
                            logger.error(e.getCause().getMessage());
                        }
                    }
                }
            }
        }
    }

    private boolean isReady() {
        Result<Node, String> result = validation.apply(model);
        result.bind(success, failure);
        Node indicator = result.indicator();
        if (indicator != null) {
            showMessage(result.message());
            available(indicator);
            indicator.requestFocus();
        } else {
            Stream.of(srcPath, txtFuzzySrcFileName, destPath, txtDestPrefixName).forEach(this::available);
        }
        return indicator == null;
    }
}