package cn.com.nettex.apps.ctrl.convert;

import cn.com.nettex.apps.ctrl.strategy.Director;
import cn.com.nettex.apps.exts.ParallelGroup;
import cn.com.nettex.apps.i18n.I18NManager;
import cn.com.nettex.apps.i18n.MessageMeta;
import cn.com.nettex.apps.intf.Assign;
import cn.com.nettex.apps.intf.Effect;
import cn.com.nettex.apps.intf.Result;
import cn.com.nettex.apps.meta.CSSMeta;
import cn.com.nettex.apps.meta.ElemMeta;
import cn.com.nettex.apps.meta.FileMeta;
import cn.com.nettex.apps.meta.TaskMeta;
import cn.com.nettex.apps.mods.convert.ConvertModel;
import cn.com.nettex.apps.util.Utilities;
import com.google.common.io.Files;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
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
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Stream;

public class ConvertController implements Assign<Director>, Initializable {

    private static final Logger logger = LogManager.getLogger(ConvertController.class);
    private static ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    private static final FileSystemView fileSystemView = FileSystemView.getFileSystemView();

    public TextField srcPath;
    public Button btnSrcPath;
    public Label lblSrcFile;
    public TextField destPath;
    public Button btnDestPath;
    public ChoiceBox<FileMeta> cboDestFileFormat;
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
    private ConvertModel _convertModel;

    private ParallelGroup classifier;
    private ExecutorService service;

    private Function<ConvertModel, Result<Node, String>> validation = (convertModel) -> {
        if (cbxIndicatorForBatch.isSelected()) {
            if (StringUtils.isEmpty(convertModel.getSrcPath())) {
                return Result.failure(srcPath, I18NManager.get(MessageMeta.MESSAGE_SOURCE_DIRECTORY_REQUIRED));
            } else if (!Files.isDirectory().apply(new File(convertModel.getSrcPath()))) {
                return Result.failure(srcPath, I18NManager.get(MessageMeta.MESSAGE_SOURCE_DIRECTORY_NOT_EXIST));
            } else if (StringUtils.isEmpty(convertModel.getSrcFuzzyName())) {
                return Result.failure(txtFuzzySrcFileName, I18NManager.get(MessageMeta.MESSAGE_SOURCE_NAME_REQUIRED));
            } else if (StringUtils.isNotEmpty(convertModel.getDestPath()) && !Files.isDirectory()
                .apply(new File(convertModel.getDestPath()))) {
                return Result.failure(destPath, I18NManager.get(MessageMeta.MESSAGE_TARGET_DIRECTORY_NOT_EXIST));
            } else if (cboDestFileFormat.getValue() == null || StringUtils
                .isEmpty(cboDestFileFormat.getValue().getExtension())) {
                return Result
                    .failure(cboDestFileFormat, I18NManager.get(MessageMeta.MESSAGE_TARGET_FILE_FORMAT_REQUIRED));
            } else {
                return Result.success(null, I18NManager.get(MessageMeta.MESSAGE_VALIDATE_STATUS_SUCCESS));
            }
        } else {
            if (StringUtils.isEmpty(convertModel.getSrcPath())) {
                return Result.failure(srcPath, I18NManager.get(MessageMeta.MESSAGE_SOURCE_FILE_REQUIRED));
            } else if (!Files.isFile().apply(new File(convertModel.getSrcPath()))) {
                return Result.failure(srcPath, I18NManager.get(MessageMeta.MESSAGE_SOURCE_FILE_NOT_EXIST));
            } else if (cboDestFileFormat.getValue() == null || StringUtils
                .isEmpty(cboDestFileFormat.getValue().getExtension())) {
                return Result.failure(cboDestFileFormat, MessageMeta.MESSAGE_TARGET_FILE_FORMAT_REQUIRED);
            } else {
                return Result.success(null, I18NManager.get(MessageMeta.MESSAGE_VALIDATE_STATUS_SUCCESS));
            }
        }
    };

    private Effect<String> success = valid -> logger.info(ElemMeta.ALL_REQUIRED_CHECK_IS_PASSED);
    private Effect<String> failure = logger::error;

    public void change(Stage stage) {
        this.stage = stage;
        stage.titleProperty().bindBidirectional(_convertModel.windowsTitleProperty());
        addResizeListener();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _convertModel = new ConvertModel();
        srcPath.textProperty().bindBidirectional(_convertModel.srcPathProperty());
        txtFuzzySrcFileName.textProperty().bindBidirectional(_convertModel.srcFuzzyNameProperty());
        txtFuzzySrcFileName.setEditable(false);

        destPath.textProperty().bindBidirectional(_convertModel.destPathProperty());
        txtDestPrefixName.textProperty().bindBidirectional(_convertModel.destRenameToProperty());

        cbxNeedFileHeader.indeterminateProperty().bindBidirectional(_convertModel.isWithHeaderProperty());
        cbxIndicatorForBatch.indeterminateProperty().bindBidirectional(_convertModel.isForBatchProperty());
        cboDestFileFormat.setConverter(new FileMeta.FileExtensionConvert());
        cboDestFileFormat.getItems().addAll(_convertModel.getDestFormat());
        appLogo.setImage(new Image(
            Objects.requireNonNull(contextClassLoader.getResource(ElemMeta.PICTURE_CONVERSION_PNG))
                .toExternalForm()));
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
            ObservableList<Node> components = ((Pane) root).getChildren();
            if (components != null && components.size() > 0) {
                components.parallelStream().filter(Node::isResizable)
                    .forEach(node -> adjust(node, ElemMeta.WIDTH, delta));
            }
            logTreeViewer.getColumns().forEach(column -> logTreeViewer.resizeColumn(column, delta));
        });

        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            double delta = newValue.doubleValue() - oldValue.doubleValue();
            ObservableList<Node> components = ((Pane) root).getChildren();
            if (components != null && components.size() > 0) {
                components.parallelStream().filter(Node::isResizable)
                    .forEach(node -> adjust(node, ElemMeta.HEIGHT, delta));
            }
        });
    }


    private void adjust(Node node, String propertyName, double delta) {
        if (node.isResizable()) {
            if (ClassUtils.isAssignable(node.getClass(), Pane.class)) {
                List<Node> children = ((Pane) node).getChildren();
                for (Node child : children) {
                    adjust(child, propertyName, delta);
                }
            } else {
                //Adjust orientation only for TextField.class or TextArea.class
                if (StringUtils.equalsIgnoreCase(propertyName, ElemMeta.WIDTH)) {
                    if (ClassUtils.isAssignable(node.getClass(), TextInputControl.class)) {
                        Utilities.adjustSize(node, propertyName, delta);
                    }
                } else if (StringUtils.equalsIgnoreCase(propertyName, ElemMeta.HEIGHT)) {
                    if (ClassUtils.isAssignable(node.getClass(), TextArea.class)) {
                        Utilities.adjustSize(node, propertyName, delta);
                    }
                }
            }
        }
    }

    private String initDirectory(String fullFilePath) {
        if (fullFilePath == null || fullFilePath.trim().length() == 0) return System.getProperty(ElemMeta.USER_HOME);

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

    //http://fxexperience.com/controlsfx/
    private void showMessage(String message) {
        //http://code.makery.ch/blog/javafx-dialogs-official/
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(ElemMeta.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(String.format("%s!%n", message));
        alert.showAndWait();
    }

    private void notify(Node node) {
        if (node instanceof TextField) {
            TextField instance = (TextField) node;
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
        final String name = property.getValue();
        if (StringUtils.isNotEmpty(name)) {
            Optional.of(Utilities.isValidName(name)).ifPresent(decision -> {
                if (decision) {
                    stylish(instance, true);
                } else {
                    stylish(instance, false);
                }
            });
        } else {
            stylish(instance, true);
        }
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
            if (instance.equals(srcPath)) {
                Optional.of(Files.isFile().apply(path)).ifPresent(decision -> {
                    if (decision) {
                        stylish(instance, true);
                    } else {
                        stylish(instance, false);
                    }
                });
            } else if (instance.equals(destPath)) {
                Optional.of(Files.isDirectory().apply(path)).ifPresent(decision -> {
                    if (decision) {
                        stylish(instance, true);
                    } else {
                        stylish(instance, false);
                    }
                });
            }
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

    @SuppressWarnings(value = {"unused"})
    public void startWork(ActionEvent keyEvent) {
        if (!isReady()) return;
        btnStart.setDisable(true);
        logTreeViewer.setRoot(null);
        progressIndicator.progressProperty().unbind();
        Runnable runnable = () -> {
            final List<TaskMeta> taskMetas = prepare();
            classifier = new ParallelGroup(taskMetas, 1);
            try {
                List<Future<List<String>>> result = classifier.classify(progressIndicator);
                Optional.of(result).ifPresent(futures -> Platform.runLater(
                    () -> {
                        TreeItem<File> treeItem =
                            createTree(new File(Paths.get(
                                StringUtils.isNotEmpty(_convertModel.getDestPath()) ?
                                    _convertModel.getDestPath() : _convertModel.getSrcPath()).getRoot()
                                .toUri()));
                        logTreeViewer.setRoot(treeItem);
                    }
                ));
            } catch (InterruptedException e) {
                logger.error(ElemMeta.TASK_S_INTERRUPTED_EXCEPTION);
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
            Utilities.listAll(new File(_convertModel.getSrcPath()), _convertModel.getSrcFuzzyName(), 0);
        List<TaskMeta> lstTask = new ArrayList<>();
        Optional.ofNullable(lstFilesInfo).ifPresent(
            filesInfo -> filesInfo.forEach(
                pair -> {
                    String srcFilePath = pair.getKey().getPath();
                    String nameAs = _convertModel.getSrcFuzzyName();
                    String destFilePath = Utilities.compose(pair, _convertModel.getDestPath());
                    String nameTo = _convertModel.getDestRenameTo();
                    String destFileType = cboDestFileFormat.getValue().getExtension();
                    Boolean header = _convertModel.isIsWithHeader();
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
                logger.info(ElemMeta.ATTEMPT_TO_SHUTDOWN_OUTER_EXECUTOR);
                service.shutdown();
                service.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error(ElemMeta.OUTER_TASKS_INTERRUPTED);
            } finally {
                if (!service.isTerminated()) {
                    logger.info(ElemMeta.CANCEL_NON_FINISHED_OUTER_TASKS);
                }
                service.shutdownNow();
                logger.info(ElemMeta.SHUTDOWN_OUTER_THREAD_FINISHED);
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
            _convertModel.setWindowsTitle(I18NManager.get(MessageMeta.MESSAGE_BATCH_CONVERSION));
        } else {
            txtFuzzySrcFileName.setEditable(false);
            _convertModel.setWindowsTitle(I18NManager.get(MessageMeta.MESSAGE_SINGLE_CONVERSION));
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
            directoryChooser.setTitle(I18NManager.get(MessageMeta.MESSAGE_VIEW_DIRECTORY));
            directoryChooser.setInitialDirectory(new File(workDirectory));
            File parent = directoryChooser.showDialog(stage);
            Optional.ofNullable(parent).ifPresent(directory -> receiver.textProperty().setValue(directory.getPath()));
        } else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(I18NManager.get(MessageMeta.MESSAGE_VIEW_FILE));
            fileChooser.setInitialDirectory(new File(workDirectory));
            fileChooser.getExtensionFilters()
                .addAll(
                    new FileChooser.ExtensionFilter(ElemMeta.ALL, ElemMeta.ALL_TYPE),
                    new FileChooser.ExtensionFilter(ElemMeta.FILES, ElemMeta.TEXT_TYPE, ElemMeta.CSV_TYPE,
                        ElemMeta.EXCEL97_TYPE, ElemMeta.EXCEL07_TYPE),
                    new FileChooser.ExtensionFilter(
                        ElemMeta.NORMAL_TEXT_FILE, ElemMeta.TEXT_TYPE, ElemMeta.ASCII_TEXT_TYPE),
                    new FileChooser.ExtensionFilter(ElemMeta.COMMA_SEPARATED_VALUES_TEXT_FILE, ElemMeta.CSV_TYPE),
                    new FileChooser.ExtensionFilter(
                        ElemMeta.MICROSOFT_EXCEL_SPREADSHEET, ElemMeta.EXCEL97_TYPE),
                    new FileChooser.ExtensionFilter(ElemMeta.OFFICE_OPEN_XML_WORKBOOK, ElemMeta.EXCEL07_TYPE)
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
        fileChooser.setTitle(I18NManager.get(MessageMeta.MESSAGE_VIEW_FILE));
        fileChooser.setInitialDirectory(new File(initDirectory));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(ElemMeta.FILES, ElemMeta.ALL_TYPE));
        File file = fileChooser.showOpenDialog(stage);
        Optional.ofNullable(file)
            .ifPresent(path -> instance.textProperty().setValue(FilenameUtils.removeExtension(path.getName())));
    }

    @SuppressWarnings(value = {"unused"})
    public void chooseSrcFuzzyName(MouseEvent actionEvent) {
        if (txtFuzzySrcFileName.isEditable() && actionEvent.getClickCount() == 2) {
            if (StringUtils.isEmpty(txtFuzzySrcFileName.textProperty().getValue())) {
                fuzzyFileName(System.getProperty(ElemMeta.USER_HOME), txtFuzzySrcFileName);
            } else {
                String fileName = FilenameUtils.normalize(txtFuzzySrcFileName.textProperty().getValue());
                if (!StringUtils.equals(txtFuzzySrcFileName.textProperty().getValue(), fileName)) {
                    showMessage(I18NManager.get(MessageMeta.MESSAGE_SOURCE_FILE_NAME_ILLEGAL));
                }
            }
        }
    }

    @SuppressWarnings(value = {"unused"})
    public void chooseDestPrefixName(MouseEvent actionEvent) {
        if (actionEvent.getClickCount() == 2) {
            if (StringUtils.isEmpty(txtDestPrefixName.textProperty().getValue())) {
                fuzzyFileName(System.getProperty(ElemMeta.USER_HOME), txtDestPrefixName);
            } else {
                String fileName = FilenameUtils.normalize(txtDestPrefixName.textProperty().getValue());
                if (!StringUtils.equals(txtDestPrefixName.textProperty().getValue(), fileName)) {
                    showMessage(I18NManager.get(MessageMeta.MESSAGE_TARGET_FILE_NAME_ILLEGAL));
                }
            }
        }
    }

    private TreeItem<File> createTree(File file) {
        TreeItem<File> item = new TreeItem<>(file);
        File[] children = file.listFiles();
        if (children != null) {
            Stream.of(children).filter(
                child -> child.getPath().contains(_convertModel.getDestPath()) || _convertModel.getDestPath()
                    .contains(child.getPath()))
                .forEach(child -> item.getChildren().add(createTree(child)));
            item.setGraphic(new ImageView(
                Objects.requireNonNull(contextClassLoader.getResource(ElemMeta.PICTURE_FOLDER_MODERN_PNG)).
                    toExternalForm()));
        } else {
            item.setGraphic(new ImageView(
                Objects.requireNonNull(contextClassLoader.getResource(ElemMeta.PICTURE_TEXT_X_GENERIC_PNG)).
                    toExternalForm()));
        }
        return item;
    }

    public void openDirectoryOrFile(MouseEvent mouseEvent) {
        int clickCount = mouseEvent.getClickCount();
        if (clickCount == 2) {
            Desktop desktop = Desktop.getDesktop();

            if (!Desktop.isDesktopSupported()) {
                logger.error(ElemMeta.DESKTOP_IS_NOT_SUPPORTED_IN_CURRENT_OS);
            } else {
                Object source = mouseEvent.getSource();
                if (source != null && ClassUtils.isAssignable(TreeTableView.class, source.getClass())) {
                    @SuppressWarnings("unchecked") TreeTableView<File> treeTableView = (TreeTableView) source;
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
        Result<Node, String> result = validation.apply(_convertModel);
        result.bind(success, failure);
        Node indicator = result.indicator();
        if (indicator != null) {
            showMessage(result.message());
            notify(indicator);
            indicator.requestFocus();
        } else {
            Stream.of(srcPath, txtFuzzySrcFileName, destPath, txtDestPrefixName).forEach(this::notify);
        }
        return indicator == null;
    }
}